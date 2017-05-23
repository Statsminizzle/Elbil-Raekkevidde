package elbil.raekkevidde.application.commands;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.obdJavaApi.commands.ObdCommand;

/**
 * Created by Yoghurt Jr on 31-03-2017.
 */

public class ObdCommandReader extends ObdCommand {

    Context context;

    public ObdCommandReader(Context context){
        this.context = context;
    }

    public void run(InputStream in)throws IOException,
            InterruptedException {
        synchronized (ObdCommandReader.class) {//Only one command can write and read a data in one time.
            readResult(in);
            Log.d("Mainlooper", "incoming");
            // Get a handler that can be used to post to the main thread
            Log.d("Mainlooper = ", context.getMainLooper().getThread().getName());
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    AppData.event.ItemInsertedEvent(rawData); //might need to format result
                } // This is your code
            };
            mainHandler.post(myRunnable);

        }
    }

    public void run(InputStream in, boolean processAll)throws IOException,
            InterruptedException {
        synchronized (ObdCommandReader.class) {//Only one command can write and read a data in one time.
            readResult(in, processAll);
            Log.d("Mainlooper", "incoming");
            // Get a handler that can be used to post to the main thread
            Log.d("Mainlooper = ", context.getMainLooper().getThread().getName());
            Handler mainHandler = new Handler(context.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    AppData.obdQueryList.add("Read All");
                    AppData.event.ItemInsertedEvent(getFormattedResult()); //might need to format result
                } // This is your code
            };
            mainHandler.post(myRunnable);

        }
    }

    @Override
    protected void performCalculations() {

    }

    @Override
    public String getFormattedResult() {
        return null;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
