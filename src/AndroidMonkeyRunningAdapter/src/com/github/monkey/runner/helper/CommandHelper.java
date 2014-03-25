
package com.github.monkey.runner.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHelper {

    public static ArrayList<String> exec(String command) throws InterruptedException {
        ArrayList<String> out = new ArrayList<String>();
        Process pro = null;
        Runtime runTime = Runtime.getRuntime();
        if (runTime == null) {
            System.err.println("Create runtime false!");
        }
        try {
            pro = runTime.exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                out.add(line);
            }
            input.close();
            output.close();
            pro.destroy();
        } catch (IOException ex) {
            Logger.getLogger(CommandHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
}
