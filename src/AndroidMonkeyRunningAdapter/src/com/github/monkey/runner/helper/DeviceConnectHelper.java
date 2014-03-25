package com.github.monkey.runner.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice.DeviceState;
import com.android.ddmlib.IDevice;
import com.github.monkey.runner.scheduler.Console;

public final class DeviceConnectHelper implements IDebugBridgeChangeListener, IDeviceChangeListener{
	private List<IDevice> devices = Collections
			.synchronizedList(new ArrayList<IDevice>());
	private String id = null;
	
	public static void init(final String adbLocation) {
		AndroidDebugBridge.init(false);
    	AndroidDebugBridge.createBridge(adbLocation, true);

	}
	
    public IDevice getConnecedDevice(final String serialNumber) {
        for (IDevice device : AndroidDebugBridge.getBridge().getDevices()) {
            if (serialNumber.equals(device.getSerialNumber())
                    && device.getState() == DeviceState.ONLINE) {
                return device;
            }
        }
        return null;
    }
    
	public IDevice waitForDeviceConnected(final String serialNumber, final long timeout) throws InterruptedException {
		id = serialNumber;
		Console.printLogMessage(serialNumber, "waitForDeviceConnected(" + serialNumber + " to be connected start");
    	
	    IDevice [] devices = AndroidDebugBridge.getBridge().getDevices();
	    if (devices != null) {
		   	for (IDevice device : devices) {
		   		if (device.getSerialNumber() == serialNumber) {
		   			Console.printLogMessage("", device.getSerialNumber());
		   			return device;
		   		}
		   	}
	    }
    	
		AndroidDebugBridge.addDebugBridgeChangeListener(DeviceConnectHelper.this);
    	AndroidDebugBridge.addDeviceChangeListener(DeviceConnectHelper.this);    	
    	synchronized (this) {
    		while (getConnecedDevice(serialNumber) == null) {
    			Console.printLogMessage("", "wait");
    			wait(60000);
    		}
		}
    	Console.printLogMessage(serialNumber, "waitForDeviceConnected(" + serialNumber + " to be connected end");
    	
    	return getConnecedDevice(serialNumber);
	}

	@Override
	public void deviceChanged(IDevice arg0, int arg1) {
//		Console.printLogMessage(""arg0.getSerialNumber());
		
	}

	@Override
	public void deviceConnected(IDevice device) {
		Console.printLogMessage(device.getSerialNumber(), "device [" + device.getSerialNumber() + "] connected");

		if (device.getSerialNumber().equals(id)) {
			Console.printLogMessage(id, "device [" + device.getSerialNumber() + "] matching expected! ");
			Console.printLogMessage(id, "notify");
			synchronized (this) {
				devices.add(device);
				notifyAll();
			}
		}
			
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		synchronized (devices) {
			synchronized (this) {
				devices.remove(device);
			}
		}
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge bridge) {
		
	}

}
