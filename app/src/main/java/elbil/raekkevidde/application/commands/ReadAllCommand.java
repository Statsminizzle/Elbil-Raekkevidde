package elbil.raekkevidde.application.commands;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.obdJavaApi.commands.protocol.ObdProtocolCommand;

public class ReadAllCommand extends ObdProtocolCommand {

    private Context context;
    private String id;

    public ReadAllCommand(Context context, String id) {
        super("AT MA");
        this.context = context;
        this.id = id;
    }

    @Override
    public String getFormattedResult() {
        final String result = rawData;

        switch (id) {
            case "374": {
                AppData.batteryCharge = calculateBatteryCharge(rawData);
                break;
            }
            case "346": {
                double usingWattage = calculateUsingWattage(rawData);
                AppData.usingWattage.add(usingWattage);
                break;
            }
            case "412": {
                AppData.drivedKilometers = calculateDrivenKilometers(rawData);
                break;
            }
        }

        return result;
    }

    private static double calculateBatteryCharge(String rawData) {
        String data;
        if (rawData.contains(":")) {
            data = rawData.substring(3,5);
        } else {
            data = rawData.substring(2, 4);
        }
        int hex2decimal = hexToDecimal(data);

        return hex2decimal * 0.5 - 5;
    }

    private static double calculateUsingWattage(String rawData){
        // Response will be e.g. 7:10112100000009 so we need to look at the 7 and the two post :
        String data = rawData.substring(0, 1) + rawData.substring(2, 4);
        int hex2decimal = hexToDecimal("2"+data);

        return hex2decimal * 10 - 100000;
    }

    private static int calculateDrivenKilometers(String rawData){
        String data = rawData.substring(4, 10);

        return hexToDecimal(data);
    }

    private static int hexToDecimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }

        return val;
    }

    @Override
    public String getName() {
        return "Read All";
    }

    @Override
    protected void readRawData(InputStream in) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached
        char c;
        int i = 0;
        // -1 if the end of the stream is reached
        while (((b = (byte) in.read()) > -1)) {
            c = (char) b;
            i++;
            if (c == '>' | i == 24) // read until '>' arrives
            {
                break;
            }
            res.append(c);
        }

    /*
     * Imagine the following response 41 0c 00 0d.
     *
     * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
     * attention to the fact that I've put the word byte in quotes, because 41
     * is actually TWO bytes (two chars) in the socket. So, we must do some more
     * processing..
     */
        rawData = removeAll(SEARCHING_PATTERN, res.toString());

    /*
     * Data may have echo or informative text like "INIT BUS..." or similar.
     * The response ends with two carriage return characters. So we need to take
     * everything from the last carriage return before those two (trimmed above).
     */
        //kills multiline.. rawData = rawData.substring(rawData.lastIndexOf(13) + 1);
        rawData = removeAll(WHITESPACE_PATTERN, rawData);//removes all [ \t\n\x0B\f\r]

        /*
        Remove the ATMA command data
         */
        rawData = removeAll(ATMA_PATTERN, rawData);
    }
}
