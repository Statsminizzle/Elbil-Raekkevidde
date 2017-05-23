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
package elbil.raekkevidde.application.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.pires.obd.enums.AvailableCommandNames;
import com.google.inject.Inject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import elbil.raekkevidde.application.AppData;
import elbil.raekkevidde.application.adapters.ObdResponseAdapter;
import elbil.raekkevidde.R;
import elbil.raekkevidde.application.event.events.BatteryChargeEvent;
import elbil.raekkevidde.application.event.events.DrivedKilometersEvent;
import elbil.raekkevidde.application.event.events.ItemInsertedEvent;
import elbil.raekkevidde.application.event.events.UpdateUIEvent;
import elbil.raekkevidde.application.event.events.UsingWattageEvent;
import elbil.raekkevidde.application.service.AbstractBluetoothConnectionService;
import elbil.raekkevidde.application.service.BluetoothConnectionService;
import elbil.raekkevidde.application.utils.ObdCommandJob;
import elbil.raekkevidde.application.utils.ObdProgressListener;

public class MainActivity extends AppCompatActivity implements ObdProgressListener {
    private static final String TAG = MainActivity.class.getName();

    private ObdResponseAdapter mAdapter;
    private Button mButton;
    private Button startLiveDataButton;
    private Button stopLiveDataButton;
    @Inject
    private SharedPreferences prefs;
    private RecyclerView mRecyclerView;
    private TextView batteryChargeTextView;
    private TextView usingWattageTextView;
    private TextView drivedKilometersTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ObdResponseAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mButton = (Button) findViewById(R.id.ConfigButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateConfig();
            }
        });

        startLiveDataButton = (Button) findViewById(R.id.StartLiveData);
        startLiveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button", "Button clicked");
                startLiveData();
            }
        });

        stopLiveDataButton = (Button) findViewById(R.id.StopLiveData);
        stopLiveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Stop data", "clicked");
                stopLiveData();
            }
        });

        batteryChargeTextView = (TextView) findViewById(R.id.BatteryChargeTextView);
        batteryChargeTextView.setText("");

        usingWattageTextView = (TextView) findViewById(R.id.UsingWattageTextView);
        usingWattageTextView.setText("");

        drivedKilometersTextView = (TextView) findViewById(R.id.DrivedKilometersTextView);
        drivedKilometersTextView.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity", getMainLooper().getThread().getName());
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MAIN", isServiceBound+"");
        if(!isServiceBound) {

        }
    }

    private void startLiveData() {
        Log.d(TAG, "Starting live data..");
        doBindService();
    }


    private void updateConfig() {
        startActivity(new Intent(this, ConfigurationActivity.class));
    }

    public void newResponseInserted(String response){
        AppData.obdResponseList.add(response);
        //mAdapter.notifyItemChanged(AppData.obdResponseList.size()-1);
        mAdapter.notifyDataSetChanged();
    }

    private void debugData() {
        for (int i = 0; i < AppData.obdResponseList.size(); i++) {
            Log.d("Query = "  + AppData.obdQueryList.get(i) + "\n Response = " + AppData.obdResponseList.get(i), "");
        }
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
                    Log.d(TAG, "Connected with Bluetooth");
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
                try {
                    Intent serviceIntent = new Intent(this, BluetoothConnectionService.class);
                    bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
                } catch(SecurityException e) {
                    Log.d("Service", "SecurityException");
                }
                Log.d("Service", "Service intent started");
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

    @Subscribe
    public void BatteryChargeEvent(BatteryChargeEvent event) {
        Log.d("BatteryCharge = " + AppData.batteryCharge, "");
        batteryChargeTextView.setText("BatteryCharge = " + String.format("%.0f", AppData.batteryCharge) + " %");
    }

    @Subscribe
    public void UsingWattageEvent(UsingWattageEvent event) {
        String text = "Using Wattage = " + AppData.usingWattage.get(AppData.usingWattage.size()-1) + "W";
        if (!usingWattageTextView.getText().equals(text)) {
            usingWattageTextView.setText(text);
        }
    }

    @Subscribe
    public void DrivedKilometersEvent(DrivedKilometersEvent event) {
        Log.d("Drived Kilometers", "");
        drivedKilometersTextView.setText("Drived Kilometers = " + event.getKilometers() + " km");
    }

    @Subscribe
    public void UpdateUIEvent(UpdateUIEvent event) {
        updateUI();
    }

    public void updateUI() {
        batteryChargeTextView.setText("Battery Charge = " + String.format("%.0f", AppData.batteryCharge) + " %");
        String text = "Using Wattage = " + String.format("%.0f", AppData.usingWattage.get(AppData.usingWattage.size()-1)) + " W";
        usingWattageTextView.setText(text);
        drivedKilometersTextView.setText("Drived Kilometers = " + AppData.drivedKilometers + " km");
    }
}
