package com.tzj.providers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class InputStreamRunnable implements Runnable {
    private BufferedReader bReader = null;
    private StringBox sBuffer = null;

    public InputStreamRunnable(InputStream is, StringBox sb) {
        try {
            this.sBuffer = sb;
            this.bReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        String line = null;
        try {
            while ((line = bReader.readLine()) != null) {
                sBuffer.append(line).append("\n");
            }
            sBuffer.toString();
            bReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
