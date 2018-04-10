package com.sn.stepcounter.stepcounter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by LoLJay on 4/9/2018.
 */

public class TraceDialogAdapter extends RecyclerView.Adapter<TraceDialogAdapter.TraceDialogViewHolder>  {
    ArrayList<TraceDialog> traceDialogs;
    Context context;

    public TraceDialogAdapter(ArrayList<TraceDialog> traceDialogs, Context context) {
        this.traceDialogs = traceDialogs;
        this.context = context;
    }

    @Override
    public TraceDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);

        return new TraceDialogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TraceDialogViewHolder holder, int position) {
        TraceDialog dialog = traceDialogs.get(position);
        holder.steps.setText(dialog.getSteps() + " steps");
        holder.distance.setText(dialog.getDistance());
        holder.date.setText(timeStampConverter(dialog.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return traceDialogs.size();
    }

    public class TraceDialogViewHolder extends RecyclerView.ViewHolder {
         TextView date,distance,steps;

        public TraceDialogViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.history_item_date);
            distance = itemView.findViewById(R.id.history_item_distance);
            steps = itemView.findViewById(R.id.history_item_steps);
        }
    }

    public String timeStampConverter(long timeStamp){
        Date date = new Date(timeStamp);
        String pattern = "MM/dd/yyyy h:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

}
