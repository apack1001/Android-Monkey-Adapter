
package com.github.monkey.runner.scheduler;

import java.util.HashMap;
import java.util.Map;

public class MonkeyTestSeriesFactory {

    public static Map<MonkeyTestDevice, MonkeyTestSeries> mBuffer = new HashMap<MonkeyTestDevice, MonkeyTestSeries>();

    public static MonkeyTestSeries newSeries(MonkeyTestDevice device, String pkgName,
            String pkgVersion, String pkgFilePath,
            String rawCommand, String userName, String initFileName,
            long seriesDuration, long singleDuration) {
        mBuffer.put(device, new MonkeyTestSeries(device, pkgName, pkgVersion, pkgFilePath,
                rawCommand, userName, initFileName,
                seriesDuration,
                singleDuration));
        MonkeyTestSeries bufferedSeries = mBuffer.get(device);
        return bufferedSeries;
    }

    public static MonkeyTestSeries getSeries(MonkeyTestDevice device) {
        return mBuffer.get(device);
    }
}
