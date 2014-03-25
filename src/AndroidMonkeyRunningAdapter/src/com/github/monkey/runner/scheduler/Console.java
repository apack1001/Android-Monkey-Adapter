package com.github.monkey.runner.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

public final class Console extends Observable {

	private static Console mThis = new Console();

	public Console() {
	}

	public static Console getInstance() {
		return mThis;
	}

	public static void printMessage(String msg) {
		mThis.setChanged();
		mThis.notifyObservers(msg);
	}

	public static void printLogMessage(String sn, String msg) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String log = String.format("%s\t%s\t%s", f.format(new Date()), sn, msg);
		System.out.println(log);
		mThis.setChanged();
		mThis.notifyObservers(log);
	}
}
