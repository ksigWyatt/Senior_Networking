package com.sn.stepcounter.stepcounter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by yjj781265 on 11/28/2017.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter{
    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return mValues[(int) value];
    }
}
