package elbil.raekkevidde.application.commands;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.application.ui.MainActivity;
import elbil.raekkevidde.obdJavaApi.exceptions.BusInitException;
import elbil.raekkevidde.obdJavaApi.exceptions.MisunderstoodCommandException;
import elbil.raekkevidde.obdJavaApi.exceptions.NoDataException;
import elbil.raekkevidde.obdJavaApi.exceptions.NonNumericResponseException;
import elbil.raekkevidde.obdJavaApi.exceptions.ResponseException;
import elbil.raekkevidde.obdJavaApi.exceptions.StoppedException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnableToConnectException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnknownErrorException;
import elbil.raekkevidde.obdJavaApi.exceptions.UnsupportedCommandException;

/**
 * Created by Yoghurt Jr on 18-05-2017.
 */

public class ReadAllResponses {

    /**
     * Error classes to be tested in order
     */
    private final Class[] ERROR_CLASSES = {
            UnableToConnectException.class,
            BusInitException.class,
            MisunderstoodCommandException.class,
            NoDataException.class,
            StoppedException.class,
            UnknownErrorException.class,
            UnsupportedCommandException.class
    };
    protected ArrayList<Integer> buffer = null;
    protected String cmd = null;
    protected boolean useImperialUnits = false;
    protected String rawData = null;
    protected Long responseDelayInMs = null;
    private long start;
    private long end;
    private Context context;

    InputStream in;

    public ReadAllResponses(InputStream in, Context context) {
        this.in = in;
        this.buffer = new ArrayList<>();
        this.context = context;
    }

    public void run() throws IOException {
        while(true) {
            readResult();
            sendResultToMainThread();
            resetData();
        }
    }

    private void sendResultToMainThread(){
        final String result = getFormattedResult();
        Log.d("Result = " + result, "");
        // Get a handler that can be used to post to the main thread
        /*((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("UI Thread", "inserting: " + result);
                AppData.obdQueryList.add("Read All");
                AppData.event.ItemInsertedEvent(result); //probably have to changed this
                Log.d("UI Thread", "has been inserted: " + result);
            }
        });*/
    }

    private void resetData(){
        rawData = null;
    }

    protected void readResult() throws IOException {
        Log.d("Reading raw data " ,"now");
        readRawDataTest(in);
        checkForErrors();
        fillBuffer();
        //performCalculations();
        Log.d("getFResultAll = " ,""+getFormattedResult());
    }

    public static int hex2decimal(String s) {
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

    public String getFormattedResult() {
        String result = rawData;
        //String id = rawData.substring(0, 1);
        //String result = hex2decimal(id) + " " + rawData.substring(2);
        return result;
    }

    protected static Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    protected static Pattern BUSINIT_PATTERN = Pattern.compile("(BUS INIT)|(BUSINIT)|(\\.)");
    protected static Pattern SEARCHING_PATTERN = Pattern.compile("SEARCHING");
    protected static Pattern DIGITS_LETTERS_PATTERN = Pattern.compile("([0-9A-F])+");

    protected String replaceAll(Pattern pattern, String input, String replacement) {
        return pattern.matcher(input).replaceAll(replacement);
    }

    protected String removeAll(Pattern pattern, String input) {
        return pattern.matcher(input).replaceAll("");
    }

    /**
     * <p>fillBuffer.</p>
     */
    protected void fillBuffer() {
        //rawData = removeAll(WHITESPACE_PATTERN, rawData); //removes all [ \t\n\x0B\f\r]
        rawData = removeAll(BUSINIT_PATTERN, rawData);

        if (!DIGITS_LETTERS_PATTERN.matcher(rawData).matches()) {
            throw new NonNumericResponseException(rawData);
        }

        // read string each two chars
        if (buffer != null) {
            buffer.clear();
        } else {
            buffer = new ArrayList<>();
        }
        int begin = 0;
        int end = 2;
        while (end <= rawData.length()) {
            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
            begin = end;
            end += 2;
        }
    }


    private char[] charArray = {'<', 'D', 'A', 'T', 'A', ' ', 'E', 'R', 'R', 'O', 'R', '>'};

    protected void readRawDataTest(InputStream in) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached
        char c;
        int i = 0;
        // -1 if the end of the stream is reached
        while (((b = (byte) in.read()) > -1)) {
            c = (char) b;
            i++;
            Log.d("TEST: ", ""+c);
            if(c == '<' ) {
                Log.d("Found <", " C = " + c);
                String forkert = "";
                for (int k = 1; k < charArray.length; k++) {
                    byte error = (byte) in.read();
                    char errorChar = (char) error;

                    if(charArray[k] != errorChar) {
                        Log.d("Breaking" , " Now with k = " + k + " c = " + c + "  Kchar = " + charArray[k]);
                        break;
                    }

                    Log.d("ErrorChar = " + errorChar, "");
                    forkert += errorChar;
                }
                c = ' ';
                Log.d("Forkert = " + forkert, "");
            }

            if(c == ':'){
                c = ' ';
            }

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
    }

    void checkForErrors() {
        for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
            ResponseException messageError;

            try {
                messageError = errorClass.newInstance();
                messageError.setCommand(this.cmd);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (messageError.isError(rawData)) {
                throw messageError;
            }
        }
    }
}
