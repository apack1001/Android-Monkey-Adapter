
package com.github.monkey.runner.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.github.monkey.runner.helper.LocationHelper.FileLocationException;

public class PropertiesHelper {
    public static void setProperties(String id, String section,
            Map<String, String> properties, boolean append) {
        try {
            String file = LocationHelper.getPropertiesLocation(id);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
            bw.newLine();
            bw.write(String.format("[%s]", section));
            bw.newLine();
            for (Map.Entry<String, String> prop : properties.entrySet()) {
                bw.write(String.format("%s=%s", prop.getKey(), prop.getValue()));
                bw.newLine();
            }
            bw.flush();
            bw.close();

        } catch (FileLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
