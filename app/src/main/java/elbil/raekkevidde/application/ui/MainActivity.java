package elbil.raekkevidde.application.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.application.adapters.ObdResponseAdapter;
import elbil.raekkevidde.R;
import elbil.raekkevidde.application.event.events.ItemInsertedEvent;
import elbil.raekkevidde.application.service.AbstractBluetoothConnectionService;
import elbil.raekkevidde.application.service.BluetoothConnectionService;
import elbil.raekkevidde.obdConnection.io.ObdCommandJob;
import elbil.raekkevidde.obdConnection.io.ObdProgressListener;
import elbil.raekkevidde.obdJavaApi.enums.AvailableCommandNames;

public class MainActivity extends AppCompatActivity implements ObdProgressListener {
    private static final String TAG = MainActivity.class.getName();
    private static final int NO_BLUETOOTH_ID = 0;
    private static final int BLUETOOTH_DISABLED = 1;
    private static final int START_LIVE_DATA = 2;
    private static final int STOP_LIVE_DATA = 3;
    private static final int SETTINGS = 4;
    private static final int GET_DTC = 5;
    private static final int TABLE_ROW_MARGIN = 7;
    private static final int NO_ORIENTATION_SENSOR = 8;
    private static final int NO_GPS_SUPPORT = 9;
    private static final int TRIPS_LIST = 10;
    private static final int SAVE_TRIP_NOT_AVAILABLE = 11;
    private static final int REQUEST_ENABLE_BT = 1234;
    private static boolean bluetoothDefaultIsEnable = false;

    private RecyclerView mRecyclerView;
    private ObdResponseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ObdResponseAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //Initialise Gateway service
        //Run the setup commands
        //Create and run getDataCommand()
        //Propagate the responses into the list
        //notify with newResponseInserted()
    }

    public void newResponseInserted(String response){
        AppData.obdResponseList.add(response);
        mAdapter.notifyItemChanged(AppData.obdResponseList.size()-1);
    }

    @Subscribe
    public void ItemInsertedEvent(ItemInsertedEvent event){
        newResponseInserted(event.getItem());
    }

//    private Sensor orientSensor = null;
//    private PowerManager.WakeLock wakeLock = null;
//    private boolean preRequisites = true;

    private boolean preRequisites = true;
    private boolean isServiceBound;
    private AbstractBluetoothConnectionService service;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            service = ((AbstractBluetoothConnectionService.AbstractBluetoothConnectionServiceBinder) binder).getService();
            service.setContext(MainActivity.this);
            Log.d(TAG, "Starting live data");
            try {
                service.startService();
                if (preRequisites) {
                    //btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                //btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
                doUnbindService();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.
        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, className.toString() + " service is unbound");
            isServiceBound = false;
        }
    };

    private void doBindService() {
        if (!isServiceBound) {
            Log.d(TAG, "Binding OBD service..");
            if (preRequisites) {
                //btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
                Intent serviceIntent = new Intent(this, BluetoothConnectionService.class);
                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            }
        }
    }

    private void doUnbindService() {
        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
                if (preRequisites) {
                    //btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
                }
            }
            Log.d(TAG, "Unbinding OBD service..");
            unbindService(serviceConn);
            isServiceBound = false;
            //obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    public void stateUpdate(final ObdCommandJob job) {
        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null && isServiceBound) {
                //             obdStatusTextView.setText(cmdResult.toLowerCase());
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound)
                stopLiveData();
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
       /*     if(isServiceBound)
                obdStatusTextView.setText(getString(R.string.status_obd_data));
        }

       /* if (vv.findViewWithTag(cmdID) != null) {
            TextView existingTV = (TextView) vv.findViewWithTag(cmdID);
            existingTV.setText(cmdResult);
        } else addTableRow(cmdID, cmdName, cmdResult);
        commandResult.put(cmdID, cmdResult);
        updateTripStatistic(job, cmdID);*/
        }
    }

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    private void stopLiveData() {
        Log.d(TAG, "Stopping live data..");
        doUnbindService();
     //   releaseWakeLockIfHeld();
    }
}
