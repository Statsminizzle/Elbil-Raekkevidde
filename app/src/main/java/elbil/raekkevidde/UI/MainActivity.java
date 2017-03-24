package elbil.raekkevidde.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import elbil.raekkevidde.Adapters.ObdResponseAdapter;
import elbil.raekkevidde.R;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> obdResponseList = new ArrayList<>();
    private ArrayList<String> obdQueryList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ObdResponseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ObdResponseAdapter(obdResponseList, obdQueryList);
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
        obdResponseList.add(response);
        mAdapter.notifyItemChanged(obdResponseList.size()-1);
    }


}
