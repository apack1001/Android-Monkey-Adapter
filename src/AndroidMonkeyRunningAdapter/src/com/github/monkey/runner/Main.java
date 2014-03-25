package com.github.monkey.runner;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.github.monkey.runner.helper.DeviceConnectHelper;
import com.github.monkey.runner.scheduler.Console;
import com.github.monkey.runner.scheduler.MonkeyTestDevice;
import com.github.monkey.runner.scheduler.MonkeyTestDeviceFactory;
import com.github.monkey.runner.scheduler.MonkeyTestSeries;
import com.github.monkey.runner.scheduler.MonkeyTestSeriesFactory;

import java.io.File;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		String initFileName = "";
		String rawCommand = "monkey --throttle 300 -c android.intent.category.MONKEY -c android.intent.category.LAUNCHER -c android.intent.category.DEFAULT --monitor-native-crashes --kill-process-after-error --pct-touch 43 --pct-motion 30 --pct-majornav 10 --pct-syskeys 15 --pct-appswitch 2 -v -v -v 99999";
	
		CLIParser cli = new CLIParser();
		boolean success = cli.parse(args);
		if (false == success)
			return;
		
		File adbFile =  new File(getADB());
		if (false == adbFile.exists())
			throw new RuntimeException("ADB is not exist!\nCurrent Localtion is " + getADB());
		DeviceConnectHelper.init(getADB());
		try {
			ArrayList<MonkeyTestSeries> serieslist = new ArrayList<MonkeyTestSeries>();
			DeviceConnectHelper deviceHelper = new DeviceConnectHelper();
			
			for (String serialNumber : cli.deivcesId) {
				IDevice device = deviceHelper.getConnecedDevice(serialNumber);
				if (device == null)
					device = deviceHelper.waitForDeviceConnected(serialNumber, 60000);
				
				if (device == null) {
					Console.printLogMessage(serialNumber, "device " + serialNumber + "cannot be connected");
					continue;
				}

				MonkeyTestDevice monkeyTestDevice = MonkeyTestDeviceFactory
						.newDevice(device);
				monkeyTestDevice.install(cli.pkgPath);
				
				MonkeyTestSeries series = MonkeyTestSeriesFactory.newSeries(
						monkeyTestDevice, 
						cli.pkgName, 
						cli.pkgVersion, 
						cli.pkgPath,
						rawCommand, 
						cli.user, 
						initFileName, 
						(long) (1000 * 3600 * Float.parseFloat(cli.seriesDuration)),
						(long) (1000 * 3600 * Float.parseFloat(cli.singleDuration)));
				series.start();
				serieslist.add(series);
			}
			
			// Wait until the monkey series completed!
			for (MonkeyTestSeries series: serieslist)
				series.join();
			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			AndroidDebugBridge.terminate();
		}

		System.exit(0);
	}

	static boolean isMac() {
		return getPlatform().startsWith("Mac OS"); 
	}
	
	static boolean isWindows() {
		return getPlatform().startsWith("Windows");
	}
	
	static boolean isLinux() {
		return getPlatform().startsWith("Linux");
	}

	private static String getPlatform() {
		return System.getProperty("os.name");
	}
	
	private static String getADB() {
		if (isWindows()) {
			return "." + File.separator + "adb" + File.separator + getPlatform().split(" ")[0] + File.separator + "adb.exe";
		} else if (isMac() || isLinux()){
			return new File("").getAbsolutePath() + File.separator + "adb" + File.separator + getPlatform().split(" ")[0] + File.separator + "adb";
		} else {
			throw new RuntimeException("not yet implement");
		}
	}
}
