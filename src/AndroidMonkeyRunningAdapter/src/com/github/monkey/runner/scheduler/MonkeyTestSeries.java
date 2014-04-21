
package com.github.monkey.runner.scheduler;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.android.ddmlib.IShellOutputReceiver;

public class MonkeyTestSeries {

    final MonkeyTestDevice mDevice;
    final String mSeriesId;
    final String mPkgName;
    final String mPkgVersion;
    final String mPkgFilePath;
    final String mRawCommand;
    final String mUserName;
    final String mInitFilePath;
    final long mSeriesExpectDuration;
    final long mSingleExpectDuration;
    long mSeriesActualDuration = 0;
    long mSeriesStart = 0;
    long mSeriesFinish = 0;
    boolean mIsSeriesStarted = false;
    boolean mIsSeriesFinished = false;
    public MonkeyTestSeriesExecutor mExecutor;
    MonkeyTest mCurrentTest;

    public MonkeyTestSeries(
            MonkeyTestDevice device,
            String pkgName,
            String pkgVersion,
            String pkgFilePath,
            String rawCommand,
            String userName,
            String initFilePath,
            long seriesDuration,
            long singleDuration) {
        mSeriesId = UUID.randomUUID().toString();
        mDevice = device;
        mPkgName = pkgName;
        mPkgVersion = pkgVersion;
        mPkgFilePath = pkgFilePath;
        mRawCommand = rawCommand;
        mUserName = userName;
        mInitFilePath = initFilePath;
        mSeriesExpectDuration = seriesDuration;
        mSingleExpectDuration = singleDuration;
    }

    public boolean isStarted() {
        return mIsSeriesStarted;
    }

    public boolean isFinished() {
        return mIsSeriesFinished;
    }

    public void cleanUp() {
        if (mIsSeriesFinished) {
            mExecutor = null;
            mCurrentTest = null;
            mIsSeriesStarted = false;
            mIsSeriesFinished = false;
        }
    }

    public void start() {
        if (mExecutor != null) {
            return;
        }
        mExecutor = new MonkeyTestSeriesExecutor(mSeriesId);
        mExecutor.start();
        mIsSeriesStarted = true;
    }

    public void interrupt() {
        if (mExecutor == null || mCurrentTest == null) {
            return;
        }
        mExecutor.interrupt();
        mCurrentTest.interrupt();
    }

    public MonkeyTestDevice getDevice() {
        return mDevice;
    }

    public class MonkeyTestSeriesExecutor extends Thread {

        public MonkeyTestSeriesExecutor(String name) {
            super(name);
        }

        void log(String msg) {
            Console.printLogMessage(mDevice.getSerialNumber(), msg);
        }

        String getCommand()
        {
            String command = mRawCommand;
            if (!command.contains(" -s "))
            {
                Random r = new Random();
                command = command.replace("monkey", String.format("monkey -s %s", r.nextLong()));
            }
            if (!command.contains(" -p "))
            {
                command = command.replace("monkey", String.format("monkey -p %s", mPkgName));
            }
            return command;
        }

        @Override
        public void run() {
            try {
                log("Monkey test series started.");
                String msg = mDevice.install(mPkgFilePath);
                if (msg == null || msg.isEmpty())
                {
                    log("Install package success.");
                } else
                {
                    log("Install package fail (" + msg + ").");
                }
                mSeriesStart = System.currentTimeMillis();
                while (!mIsSeriesFinished) {
                    mCurrentTest = new MonkeyTest(mDevice, mSeriesId, mPkgName,
                            mPkgVersion, getCommand(),
                            mUserName, mInitFilePath, mSingleExpectDuration, mSeriesExpectDuration);
                    mCurrentTest.start();
                   
                    while (true) {
                        TimeUnit.SECONDS.sleep(1);
                        mSeriesFinish = System.currentTimeMillis();
                        mSeriesActualDuration = mSeriesFinish - mSeriesStart;
//                        System.out.println("mSeriesActualDuration" +  mSeriesActualDuration +  "mSeriesExpectDuration" + mSeriesExpectDuration);
                        if (mCurrentTest.isFinished()) {
                            break;
                        }
                        if (mSeriesActualDuration >= mSeriesExpectDuration) {
                            mSeriesActualDuration = mSeriesExpectDuration;
                            mIsSeriesFinished = true;
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (mCurrentTest != null && mCurrentTest.isStarted() && !mCurrentTest.isFinished()) {
                    mCurrentTest.interrupt();
                }
                mIsSeriesFinished = true;
                // disable reboot
                //mDevice.reboot();
                log("Monkey test series finished.");
            }
        }
    }
}
