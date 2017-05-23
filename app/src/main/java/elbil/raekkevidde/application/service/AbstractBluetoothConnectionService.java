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
package elbil.raekkevidde.application.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.application.commands.DisconnectCommand;
import elbil.raekkevidde.application.commands.ReadAllCommand;
import elbil.raekkevidde.application.commands.SetIDCommand;
import elbil.raekkevidde.application.ui.MainActivity;
import elbil.raekkevidde.application.utils.ObdCommandJob;
import roboguice.service.RoboService;

public abstract class AbstractBluetoothConnectionService extends RoboService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = AbstractBluetoothConnectionService.class.getName();
    private final IBinder binder = new AbstractBluetoothConnectionService.AbstractBluetoothConnectionServiceBinder();
    @Inject
    protected NotificationManager notificationManager;
    protected Context ctx;
    protected boolean isRunning = false;
    protected Long queueCounter = 0L;
    protected boolean jobsInitialised = false;
    protected int numberOfJobsInitialised = 7;
    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();
    protected BluetoothDevice dev = null;
    protected BluetoothSocket sock = null;

    // Run the executeQueue in a different thread to lighten the UI thread
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
                try {
                        /* Execute the initialisation commands */
                        executeQueue();
                        while(!t.isInterrupted()) {
                            /* Add needed commands to the queue and execute them afterwards.
                             * When the commands have been executed we send an updateUI event to any
                             * subscribers
                             */
                            executeQueue(addQueueLoopCommands());
                            updateUI();
                            t.sleep(5000);
                        }
                } catch (InterruptedException|IOException e) {
                    t.interrupt();
                }
            }
    });

    private int addQueueLoopCommands() throws IOException, InterruptedException {
        int numberOfJobs;
        queueJob(new ObdCommandJob(new SetIDCommand("374")));
        queueJob(new ObdCommandJob(new ReadAllCommand(getApplicationContext(), "374")));
        queueJob(new ObdCommandJob(new SetIDCommand("346")));
        queueJob(new ObdCommandJob(new ReadAllCommand(getApplicationContext(), "346")));
        queueJob(new ObdCommandJob(new SetIDCommand("412")));
        queueJob(new ObdCommandJob(new ReadAllCommand(getApplicationContext(), "412")));
        numberOfJobs = 6;
        return numberOfJobs;
    }

    private void updateUI(){
        Log.d("Mainlooper = ", getApplicationContext().getMainLooper().getThread().getName());
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                AppData.event.UpdateUIEvent();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void loopCommands() throws IOException, InterruptedException {
        new DisconnectCommand().run(sock.getInputStream(),sock.getOutputStream());
        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace();}
        clearInputStream();
        new SetIDCommand("412").run(sock.getInputStream(), sock.getOutputStream());
        new ReadAllCommand(getApplicationContext(), "412").run(sock.getInputStream(), sock.getOutputStream());
        new DisconnectCommand().run(sock.getInputStream(),sock.getOutputStream());
        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace();}
        clearInputStream();
        new SetIDCommand("346").run(sock.getInputStream(), sock.getOutputStream());
        new ReadAllCommand(getApplicationContext(), "346").run(sock.getInputStream(), sock.getOutputStream());
        new DisconnectCommand().run(sock.getInputStream(),sock.getOutputStream());
        clearInputStream();
    }

    protected void clearInputStream() throws IOException{
        sock.getInputStream().skip(sock.getInputStream().available());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating service..");
        t.start();
        Log.d(TAG, "Service created.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying service...");
        notificationManager.cancel(NOTIFICATION_ID);
        t.interrupt();
        Log.d(TAG, "Service destroyed.");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean queueEmpty() {
        return jobsQueue.isEmpty();
    }

    /**
     * This method will add a job to the queue while setting its ID to the
     * internal queue counter.
     *
     * @param job the job to queue.
     */
    public void queueJob(ObdCommandJob job) {
        queueCounter++;
        Log.d(TAG, "Adding job[" + queueCounter + "] to queue..");

        job.setId(queueCounter);
        try {
            jobsQueue.put(job);
            Log.d(TAG, "Job queued successfully.");
        } catch (InterruptedException e) {
            job.setState(ObdCommandJob.ObdCommandJobState.QUEUE_ERROR);
            Log.e(TAG, "Failed to queue job.");
        }
    }

    /**
     * Show a notification while this service is running.
     */
    protected void showNotification(String contentTitle, String contentText, int icon, boolean ongoing, boolean notify, boolean vibrate) {
        final PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, MainActivity.class), 0);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx);
        notificationBuilder.setContentTitle(contentTitle)
                .setContentText(contentText).setSmallIcon(icon)
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis());
        // can cancel?
        if (ongoing) {
            notificationBuilder.setOngoing(true);
        } else {
            notificationBuilder.setAutoCancel(true);
        }
        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (notify) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.getNotification());
        }
    }

    public void setContext(Context c) {
        ctx = c;
    }

    protected abstract void executeQueue() throws InterruptedException;
    protected abstract void executeQueue(int numberOfJobs) throws InterruptedException, IOException;

    protected abstract void processInput() throws IOException, InterruptedException;

    protected abstract void processAllInput() throws IOException, InterruptedException;


    public abstract void startService() throws IOException;

    public abstract void stopService();

    public class AbstractBluetoothConnectionServiceBinder extends Binder {
        public AbstractBluetoothConnectionService getService() {
            return AbstractBluetoothConnectionService.this;
        }
    }

}
