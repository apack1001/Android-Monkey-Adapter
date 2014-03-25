
package com.github.monkey.runner.scheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.IDevice.DeviceState;

public class MonkeyTestDevice {

    private IDevice mDevice;
    private final String mSerialNumber;

    public MonkeyTestDevice(IDevice device) {
        if (device == null) {
            throw new IllegalArgumentException(
                    "MonkeyTestDevice(IDevice device), device should not be null.");
        }
        mDevice = device;
        String serialno = device.getSerialNumber();
        if (serialno == null || serialno.isEmpty() || serialno.contains("?")) {
            throw new IllegalArgumentException(
                    "MonkeyTestDevice(IDevice device), device should not have a illegal serial number, like null, empty or ??????");
        }
        mSerialNumber = serialno;
    }

    /**
     * Returns the properties of device.
     */
    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        try {
            properties.putAll(mDevice.getProperties());
        } catch (NullPointerException npe) {
            // when device rebooting, mDevice will be null
            properties.put("ro.serialno", mSerialNumber);
        }
        return properties;
    }

    /**
     * Returns the serial number of device.
     */
    public String getSerialNumber() {
        return mSerialNumber;
    }

    public boolean connect() {
        boolean result = false;
        for (IDevice device : AndroidDebugBridge.getBridge().getDevices()) {
            if (mDevice.getSerialNumber().equals(device.getSerialNumber())
                    && mDevice.getState() == DeviceState.ONLINE) {
                mDevice = device;
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns if the device is on line.
     * 
     * @return <code>true</code> if {@link IDevice#getState()} returns
     *         {@link DeviceState#ONLINE}.
     */
    public boolean isOnline() {
        if (mDevice == null) {
            return false;
        }
        if (mDevice.getState() == DeviceState.ONLINE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns if the SUT is off line.
     */
    public boolean isOffline() {
        return !isOnline();
    }

    public String install(String pkgFilePath) {
        String msg = "";
        try {
            msg = mDevice.installPackage(pkgFilePath, true);
        } catch (Exception ex) {
            msg = ex.getMessage();
        }
        return msg;
    }

    public String initialize(String shFilePath)
    {
        String msg = "";
        String cmd = String.format("bash %s %s", shFilePath,
                mSerialNumber);
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec(cmd);
            msg = "exit " + process.waitFor();
        } catch (IOException ioe) {
            msg = ioe.getMessage();
        } catch (InterruptedException ie) {
            msg = ie.getMessage();
        }
        return msg;
    }

    public String reboot() {
        String msg = "";
        try {
            mDevice.reboot(null);
        } catch (Exception ex) {
            msg = ex.getMessage();
        }
        return msg;
    }

    /**
     * Executes a monkey command on the device, and sends the result to a
     * <var>receiver</var>.
     * 
     * @param command the shell command to execute
     * @param receiver the {@link IShellOutputReceiver} that will receives the
     *            output of the shell command
     */
    public boolean monkey(String command, IShellOutputReceiver receiver) {
        boolean result = false;
        try {
            mDevice.executeShellCommand(command, receiver, 60000);
            result = true;
        } catch (ShellCommandUnresponsiveException mttor) {
            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    /**
     * Executes a logcat command on the device, and sends the result to a
     * <var>receiver</var>.
     * 
     * @param receiver the {@link IShellOutputReceiver} that will receives the
     *            output of the shell command
     */
    public boolean logcat(IShellOutputReceiver receiver) {
        boolean result = false;
        try {
            mDevice.executeShellCommand("logcat -v threadtime *:V", receiver,
                    60000);
            result = true;
        } catch (ShellCommandUnresponsiveException mttor) {
            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
    
    /**
     * Executes a bugreport command on the device, and sends the result to a
     * <var>receiver</var>.
     * 
     * @param receiver the {@link IShellOutputReceiver} that will receives the
     *            output of the shell command
     */
    public boolean bugreport(IShellOutputReceiver receiver) {
        boolean result = false;
        try {
            mDevice.executeShellCommand("bugreport", receiver,
                    60000);
            result = true;
        } catch (ShellCommandUnresponsiveException mttor) {
            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
    
    
    /**
     * Executes a cat /data/anr/traces.txt command on the device, and sends the result to a
     * <var>receiver</var>.
     * 
     * @param receiver the {@link IShellOutputReceiver} that will receives the
     *            output of the shell command
     */
    public boolean traces(IShellOutputReceiver receiver) {
        boolean result = false;
        try {
            mDevice.executeShellCommand("cat /data/anr/traces.txt", receiver,
                    60000);
            result = true;
        } catch (ShellCommandUnresponsiveException mttor) {
            result = true;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
}
