
package com.github.monkey.runner.scheduler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmuilib.log.event.BugReportImporter;
import com.github.monkey.runner.helper.LocationHelper;
import com.github.monkey.runner.helper.PropertiesHelper;
import com.github.monkey.runner.helper.SharedProperties;
import com.github.monkey.runner.helper.ZipHelper;
import com.github.monkey.runner.helper.LocationHelper.FileLocationException;

/**
 * Represents a item of monkey test.
 * 
 */
public class MonkeyTest {

    final String mTestId;
    final String mSeriesId;
    final MonkeyTestDevice mDevice;
    final String mCommand;
    final String mUserName;
    final String mPkgName;
    final String mPkgVersion;
    final String mInitFilePath;
    final long mSeriesExpectDuration;
    final long mSingleExpectDuration;

    long mActualDuration = 0;
    long mStart = 0;
    long mFinish = 0;
    boolean mIsStarted = false;
    boolean mIsFinished = false;

    MonkeyTestItemExecutor mExecutor;

    public MonkeyTest(
            MonkeyTestDevice device,
            String seriesId,
            String pkgName,
            String pkgVersion,
            String command,
            String userName,
            String initFilePath,
            long singleExpectDuration,
            long seriesExpectDuration) {
        mTestId = UUID.randomUUID().toString();
        mSeriesId = seriesId;
        mDevice = device;
        mPkgName = pkgName;
        mPkgVersion = pkgVersion;
        mCommand = command;
        mUserName = userName;
        mInitFilePath = initFilePath;
        mSingleExpectDuration = singleExpectDuration;
        mSeriesExpectDuration = seriesExpectDuration;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void start() {
        if (mExecutor != null) {
            return;
        }
        mExecutor = new MonkeyTestItemExecutor(mTestId);
        mExecutor.start();
        mIsStarted = true;
    }

    public void interrupt() {
        mIsFinished = true;
        if (!mExecutor.isInterrupted()) {
            mExecutor.interrupt();
        }
    }

    public class MonkeyTestItemExecutor extends Thread {

        private LogOutputReceiver mMonkeyLogOutputReceiver;
        private LogOutputReceiver mLogcatLogOutputReceiver;
		private LogOutputReceiver mTracesLogOutputReceiver;
		private LogOutputReceiver mBugreportOutputReceiver;

        public MonkeyTestItemExecutor(String name) {
            super(name);
        }

        void log(String msg) {
            Console.printLogMessage(mDevice.getSerialNumber(), msg);
        }

        void startMonkeyThread() {
            try {
                String f = LocationHelper.getMonkeyLogLocation(mTestId);
                mMonkeyLogOutputReceiver = new LogOutputReceiver(f);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDevice.monkey(mCommand, mMonkeyLogOutputReceiver);
                        mMonkeyLogOutputReceiver.cancel();
                    }
                });
                t.start();
            } catch (Exception ex) {
                // Pass
            }
        }

        void startLogcatThread() {
            try {
                String f = LocationHelper.getLogcatLogLocation(mTestId);
                mLogcatLogOutputReceiver = new LogOutputReceiver(f);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDevice.logcat(mLogcatLogOutputReceiver);
                        mLogcatLogOutputReceiver.cancel();
                    }
                });
                t.start();
            } catch (Exception ex) {
                // Pass
            }
        }
        
        void startBugreport() {
        	try {
        		String bugreportPath = LocationHelper.getBugreportLocation(mTestId);
        		mBugreportOutputReceiver = new LogOutputReceiver(bugreportPath);
        		mDevice.bugreport(mBugreportOutputReceiver);
        	} catch (Exception e) {
        		
        	}
        }
        
        void startTraces() {
        	try {
        		String tracesPath = LocationHelper.getTracesLocation(mTestId);
                mTracesLogOutputReceiver = new LogOutputReceiver(tracesPath);
                mDevice.traces(mTracesLogOutputReceiver);
        	} catch (Exception e) {
        		
        	}
        	
        }

        @Override
        public void run() {
            try {
                log("Monkey test started.");

                // wait for device
                log("Wating for device 60 seconds");
                TimeUnit.SECONDS.sleep(60);
                if (mDevice.connect()) {
                    log("Device connected success");
                } else {
                    log("Device not found, monkey test not started");
                    mIsFinished = true;
                    return;
                }

                // unlock screen for device
                String msg = mDevice.initialize(mInitFilePath);
                log("Execute init script (" + msg + ").");
               
                // monkey
                mStart = System.currentTimeMillis();
                saveProperties(false);
                startMonkeyThread();
                startLogcatThread();
                log("Monkey test started");
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    if (mMonkeyLogOutputReceiver.isCancelled()) {
                        log("Monkey test finished, since monkey log output break.");
                        break;
                    }
                    if (mLogcatLogOutputReceiver.isCancelled()) {
                        log("Monkey test finished, since logcat log output break.");
                        break;
                    }
                    mFinish = System.currentTimeMillis();
                    mActualDuration = mFinish - mStart;
                    if (mActualDuration >= mSingleExpectDuration) {
                        mActualDuration = mSingleExpectDuration;
                        log("Monkey test finished, since reach ecpect duration");
                        break;
                    }
                }
                mMonkeyLogOutputReceiver.cancel();
                mLogcatLogOutputReceiver.cancel();
                log(String.format("Monkey test lasts for %s ms",
                        mActualDuration));

                Map<String, String> props = saveProperties();
            	// save bugreport and trace
                startBugreport();
            	startTraces();
                // zip & upload
                String zipSrc = LocationHelper.getLogsLocation(mTestId);
                String zipDes = LocationHelper.getZipLocation(mTestId);
                ZipHelper.zip(zipSrc, zipDes);
                
                SharedProperties.getInstance().add(props);
                // TODO: Upload

            } catch (InterruptedException ie) {
                // Pass
            } catch (Exception ex) {
                log(ex.getMessage());
            } finally {
//                // reboot
//                log(String.format("Rebooted! (%s)", mDevice.reboot()));
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    // Pass
                }
                mIsFinished = true;
                log("Monkey test finished.");
            }
        }
        
        private Map<String, String> saveProperties()
				throws FileLocationException {
        	return saveProperties(true);
        }
        
		private Map<String, String> saveProperties(boolean shouldSaveFinishTime)
				throws FileLocationException {
			// store monkey properties
			Map<String, String> props = new HashMap<String, String>();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			props.put("test.id", mTestId);//$NON-NLS-1$
			props.put("test.start", df.format(new Date(mStart)));//$NON-NLFFS-1$
			props.put("test.finish", df.format(new Date(mFinish)));//$NON-NLS-1$
			props.put("test.duration.expect", String.valueOf(mSingleExpectDuration / 1000));//$NON-NLS-1$
			
			if (shouldSaveFinishTime)
				props.put("test.duration.actual", String.valueOf(mActualDuration / 1000));//$NON-NLS-1$

			props.put("series.id", mSeriesId);//$NON-NLS-1$
			props.put("series.duration.expect", String.valueOf(mSeriesExpectDuration / 1000));//$NON-NLS-1$

			props.put("apk.package", mPkgName);//$NON-NLS-1$
			props.put("apk.version", mPkgVersion);//$NON-NLS-1$

			props.put("command", mCommand);//$NON-NLS-1$
			props.put("executor", mUserName);//$NON-NLS-1$
			props.put("status", mActualDuration < mSingleExpectDuration ? "FAIL" : "PASS");//$NON-NLS-1$

			PropertiesHelper.setProperties(mTestId, "monkey", props, false);

			// store device properties
			props = mDevice.getProperties();
			PropertiesHelper.setProperties(mTestId, "device", props, true);

			log(String.format("Log store at %s",
			        LocationHelper.getLogsLocation(mTestId)));
			return props;
		}

        /**
         * LogOutputReceiver implements
         * {@link MultiLineReceiver#processNewLines(String[])}, which is called
         * whenever there is output from log. This class is expected to be used
         * from a different thread, and the only way to stop that thread is by
         * using the {@link LogOutputReceiver#mIsCancelled} variable. See
         * {@link IDevice#executeShellCommand(String, IShellOutputReceiver, int)}
         * for more details.
         */
        private class LogOutputReceiver extends MultiLineReceiver {

            private boolean mIsCancelled;
            private String mFileName;

            public LogOutputReceiver(String fileName) {
                mFileName = fileName;
                setTrimLine(false);
            }

            /** Implements {@link IShellOutputReceiver#isCancelled() }. */
            @Override
            public boolean isCancelled() {
                return mIsCancelled;
            }

            @Override
            public void processNewLines(String[] lines) {
                if (!mIsCancelled) {
                    try {
                        FileWriter fw = new FileWriter(mFileName, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        for (String line : lines) {
                            bw.write(line.toString());
                            bw.newLine();
                        }
                        bw.close();
                        fw.close();
                    } catch (Exception ex) {
                        // Pass
                    }
                }
            }

            public void cancel() {
                mIsCancelled = true;
            }
        }
    }
}
