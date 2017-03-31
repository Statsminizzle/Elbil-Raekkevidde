package elbil.raekkevidde.application.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import elbil.raekkevidde.R;
import elbil.raekkevidde.application.AppData;


/**
 * Created by Yoouughurt on 24-03-2017.
 */

public class ObdResponseAdapter extends RecyclerView.Adapter<ObdResponseAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView query, response;

        private MyViewHolder(View view) {
            super(view);
            query = (TextView) view.findViewById(R.id.query);
            response = (TextView) view.findViewById(R.id.response);
        }
    }


    public ObdResponseAdapter() {
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_obd_answers, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String response = AppData.obdResponseList.get(position);
        String query = AppData.obdQueryList.get(position);
        if(query == null) {
            query = "Response from ID " + response.substring(3) + ":"; //substring the ID - find correct substring
        }
        holder.query.setText(query);
        holder.response.setText(response);
    }

    @Override
    public int getItemCount() {
        return AppData.obdResponseList.size();
    }
}
