package elbil.raekkevidde.application.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.InterruptedIOException;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.application.commands.DisconnectCommand;
import elbil.raekkevidde.application.commands.ObdCommandReader;
import elbil.raekkevidde.application.commands.ReadAllCommand;
import elbil.raekkevidde.application.commands.SetIDCommand;
import elbil.raekkevidde.application.event.events.ItemInsertedEvent;
import elbil.raekkevidde.application.ui.MainActivity;
import elbil.raekkevidde.obdConnection.activity.ConfigActivity;
import elbil.raekkevidde.obdConnection.io.AbstractGatewayService;
import elbil.raekkevidde.obdConnection.io.BluetoothManager;
import elbil.raekkevidde.obdConnection.io.ObdCommandJob;
import elbil.raekkevidde.obdConnection.io.ObdGatewayService;
import elbil.raekkevidde.obdJavaApi.commands.protocol.EchoOffCommand;
import elbil.raekkevidde.obdJavaApi.commands.protocol.LineFeedOffCommand;
import elbil.raekkevidde.obdJavaApi.commands.protocol.ObdResetCommand;
import elbil.raekkevidde.obdJavaApi.commands.protocol.SelectProtocolCommand;
import elbil.raekkevidde.obdJavaApi.commands.protocol.TimeoutCommand;
import elbil.raekkevidde.obdJavaApi.commands.temperature.AmbientAirTemperatureCommand;
import elbil.raekkevidde.obdJavaApi.enums.ObdProtocols;
import elbil.raekkevidde.obdJavaApi.exceptions.UnsupportedCommandException;

/**
 * Created by Yoghurt Jr on 24-03-2017.
 */

public class BluetoothConnectionService extends AbstractBluetoothConnectionService{

    private static final String TAG = ObdGatewayService.class.getName();

    private ObdCommandReader reader = null;
    private int jobsTaken = 0;
    @Inject
    SharedPreferences prefs;


    @Override
    protected void executeQueue() throws InterruptedException {
        Log.d(TAG, "Executing queue..");
        while (!Thread.currentThread().isInterrupted()) {

            Log.d("jobsTaken = " + jobsTaken, "Numberofjobsinitialised = " + numberOfJobsInitialised);


            if(jobsTaken == numberOfJobsInitialised){
                Log.d("Breaking queue", "now");
                break;
            }
            Log.d("Running", "Execute queue");
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();
                jobsTaken++;
                // log job
                Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");

                if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    Log.d(TAG, "Job state is NEW. Run it..");
                    job.setState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    if (sock.isConnected()) {
                        job.getCommand().run(sock.getInputStream(), sock.getOutputStream());
                    } else {
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                        Log.e(TAG, "Can't run command on a closed socket.");
                    }
                } else
                    // log not new job
                    Log.e(TAG,
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                Log.d(TAG, "Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if(io.getMessage().contains("Broken pipe"))
                        job.setState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    else
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                final ObdCommandJob job2 = job;

                             ((MainActivity) ctx).runOnUiThread(new Runnable() {
                                @Override
                               public void run() {
                                    Log.d("UI Thread", "inserting: " + job2.getCommand().getFormattedResult());
                                    AppData.obdQueryList.add(job2.getCommand().getName());
                                    AppData.event.ItemInsertedEvent(job2.getCommand().getFormattedResult()); //probably have to changed this
                                    Log.d("UI Thread", "has been inserted: " + job2.getCommand().getFormattedResult());
                          }
                     });
            }
        }
    }

    protected void executeQueue(int numberOfJobs) throws InterruptedException, IOException {
        Log.d(TAG, "Executing queue..");
        jobsTaken = 0;
        while (!Thread.currentThread().isInterrupted()) {

            Log.d("jobsTaken = " + jobsTaken, "Numberofjobsinitialised = " + numberOfJobsInitialised);
            /*
            Clear the input stream and prepare the OBD for new ID commands for each ID response read
             */
            if(jobsTaken % 2 == 0) {
                new DisconnectCommand().run(sock.getInputStream(), sock.getOutputStream());
                try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace();}
                clearInputStream();
            }

            if(jobsTaken == numberOfJobs){
                Log.d("Breaking queue", "now");
                break;
            }



            Log.d("Running", "Execute queue");
            ObdCommandJob job = null;
            try {
                job = jobsQueue.take();
                jobsTaken++;
                // log job
                Log.d(TAG, "Taking job[" + job.getId() + "] from queue..");

                if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NEW)) {
                    Log.d(TAG, "Job state is NEW. Run it..");
                    job.setState(ObdCommandJob.ObdCommandJobState.RUNNING);
                    if (sock.isConnected()) {
                        job.getCommand().run(sock.getInputStream(), sock.getOutputStream());
                    } else {
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                        Log.e(TAG, "Can't run command on a closed socket.");
                    }
                } else
                    // log not new job
                    Log.e(TAG,
                            "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
            } catch (UnsupportedCommandException u) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED);
                }
                Log.d(TAG, "Command not supported. -> " + u.getMessage());
            } catch (IOException io) {
                if (job != null) {
                    if(io.getMessage().contains("Broken pipe"))
                        job.setState(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE);
                    else
                        job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "IO error. -> " + io.getMessage());
            } catch (Exception e) {
                if (job != null) {
                    job.setState(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR);
                }
                Log.e(TAG, "Failed to run command. -> " + e.getMessage());
            }

            if (job != null) {
                final ObdCommandJob job2 = job;

                ((MainActivity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("UI Thread", "inserting: " + job2.getCommand().getFormattedResult());
                        AppData.obdQueryList.add(job2.getCommand().getName());
                        AppData.event.ItemInsertedEvent(job2.getCommand().getFormattedResult()); //probably have to changed this
                        Log.d("UI Thread", "has been inserted: " + job2.getCommand().getFormattedResult());
                    }
                });
            }
        }
    }

    @Override
    protected void processInput() throws IOException, InterruptedException{
        if(reader == null){
            reader = new ObdCommandReader(this.getApplicationContext());
        }
        reader.run(sock.getInputStream());
    }

    @Override
    protected void processAllInput() throws IOException, InterruptedException{
        if(reader == null){
            reader = new ObdCommandReader(this.getApplicationContext());
        }
        reader.run(sock.getInputStream(), true);
    }

    @Override
    public void startService() throws IOException {
        Log.d(TAG, "Starting service..");

        // get the remote Bluetooth device
        if(prefs == null) {
            Log.d("Preferences = ", "null");
        } else {
            Log.d("Preferences = ", "NOT NULL");
        }
        final String remoteDevice = prefs.getString(ConfigActivity.BLUETOOTH_LIST_KEY, "Idiot");
        if (remoteDevice == null || "".equals(remoteDevice)) {
            //TODO   Toast.makeText(ctx, getString(R.string.text_bluetooth_nodevice), Toast.LENGTH_LONG).show();

            // log error
            Log.e(TAG, "No Bluetooth device has been selected.");

            // TODO kill this service gracefully
            stopService();
            throw new IOException();
        } else {

            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            dev = btAdapter.getRemoteDevice(remoteDevice);


    /*
     * Establish Bluetooth connection
     *
     * Because discovery is a heavyweight procedure for the Bluetooth adapter,
     * this method should always be called before attempting to connect to a
     * remote device with connect(). Discovery is not managed by the Activity,
     * but is run as a system service, so an application should always call
     * cancel discovery even if it did not directly request a discovery, just to
     * be sure. If Bluetooth state is not STATE_ON, this API will return false.
     *
     * see
     * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
     * .html#cancelDiscovery()
     */
            Log.d(TAG, "Stopping Bluetooth discovery.");
            btAdapter.cancelDiscovery();

            //TODO      showNotification(getString(R.string.notification_action), getString(R.string.service_starting), R.drawable.ic_btcar, true, true, false);

            try {
                startObdConnection();
            } catch (Exception e) {
                Log.e(
                        TAG,
                        "There was an error while establishing connection. -> "
                                + e.getMessage()
                );

                // in case of failure, stop this service.
                stopService();
                throw new IOException();
            }
            //TODO   showNotification(getString(R.string.notification_action), getString(R.string.service_started), R.drawable.ic_btcar, true, true, false);
        }
    }

    @Override
    public void stopService() {
        Log.d(TAG, "Stopping service..");

        notificationManager.cancel(AbstractGatewayService.NOTIFICATION_ID);
        jobsQueue.clear();
        isRunning = false;

        if (sock != null)
            // close socket
            try {
                sock.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        // kill service
        stopSelf();
    }

    private void startObdConnection() throws IOException {
        Log.d(TAG, "Starting OBD connection..");
        isRunning = true;
        try {
            sock = BluetoothManager.connect(dev);
        } catch (Exception e2) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Stopping app..", e2);
            stopService();
            throw new IOException();
        }

        // Let's configure the connection.
        Log.d(TAG, "Queueing jobs for connection configuration..");
        queueJob(new ObdCommandJob(new ObdResetCommand()));

        //Below is to give the adapter enough time to reset before sending the commands,
        //otherwise the first startup commands could be ignored.
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        queueJob(new ObdCommandJob(new EchoOffCommand()));

    /*
     * Will send second-time based on tests.
     */
        queueJob(new ObdCommandJob(new EchoOffCommand()));
        //queueJob(new ObdCommandJob(new LineFeedOffCommand()));
        queueJob(new ObdCommandJob(new TimeoutCommand(62)));

//        queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.ISO_15765_4_CAN_B)));
// Get protocol from preferences
final String protocol = prefs.getString(ConfigActivity.PROTOCOLS_LIST_KEY, "AUTO");
        queueJob(new ObdCommandJob(new SelectProtocolCommand(ObdProtocols.valueOf(protocol))));

        queueJob(new ObdCommandJob(new SetIDCommand("374")));
        queueJob(new ObdCommandJob(new ReadAllCommand(getApplicationContext(), "374")));

        numberOfJobsInitialised = 7;
        queueCounter = 0L;

        jobsInitialised = true;
        Log.d(TAG, "Initialization jobs queued.");
    }
}




