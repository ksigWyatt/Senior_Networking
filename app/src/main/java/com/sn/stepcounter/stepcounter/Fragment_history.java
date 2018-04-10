package com.sn.stepcounter.stepcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by LoLJay on 4/4/2018.
 */

public class Fragment_history extends Fragment {

    private View fragview;
    private RecyclerView recyclerView;
    private ArrayList<TraceDialog> traceDialogs;
    private TraceDialogAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragview = inflater.inflate(R.layout.fragment_history,container,false);
        recyclerView = fragview.findViewById(R.id.history_recyclerView);
        traceDialogs = new ArrayList<>();
        adapter = new TraceDialogAdapter(traceDialogs,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        loadHistoryData();

        return fragview;
    }

    private void loadHistoryData(){
        Date date = new Date();
        TraceDialog dialog = new TraceDialog(UUID.randomUUID().toString(),date.getTime(),"40 miles",200);
        for(int i = 0; i<10 ; i++){
            traceDialogs.add(dialog);
        }
        adapter.notifyDataSetChanged();
    }

}
