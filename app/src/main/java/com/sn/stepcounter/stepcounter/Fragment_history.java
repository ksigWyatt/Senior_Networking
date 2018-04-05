package com.sn.stepcounter.stepcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LoLJay on 4/4/2018.
 */

public class Fragment_history extends Fragment {

    private View fragview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragview = inflater.inflate(R.layout.fragment_history,container,false);
        return fragview;
    }
}
