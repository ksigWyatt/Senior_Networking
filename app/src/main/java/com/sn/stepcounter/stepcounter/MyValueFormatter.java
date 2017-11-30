package com.sn.stepcounter.stepcounter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by yjj781265 on 11/29/2017.
 */

public class MyValueFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if((int)value!=0) {
            return String.valueOf((int) value);
        }else{
            return"";
        }
    }
}
