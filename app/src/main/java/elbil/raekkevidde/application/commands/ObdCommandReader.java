/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package elbil.raekkevidde.application.commands;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import elbil.raekkevidde.application.AppData;

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
