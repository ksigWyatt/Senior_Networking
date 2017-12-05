package com.sn.stepcounter.stepcounter;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class Result extends AppCompatActivity   {
    BarChart chart;
    FirebaseUser currentUser;
    ArrayList<StepData> mDataArrayList;
    TextView result_date,result_distance,result_steps;
    RelativeLayout mRelativeLayout;








    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        chart = findViewById(R.id.chart);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDataArrayList = new ArrayList<>();
        result_date =findViewById(R.id.result_date);
        result_distance = findViewById(R.id.result_distance);
        result_steps = findViewById(R.id.result_steps);
        mRelativeLayout =findViewById(R.id.result_layout);





        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("UsersStepData/"
                +currentUser.getUid()+"/WeeklyStepData");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalSteps =0;
                float totalMiles;

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    StepData data = childSnapshot.getValue(StepData.class);
                    totalSteps+=data.getStepsCount();
                    mDataArrayList.add(data);
                }
                List<BarEntry> entries = new ArrayList<BarEntry>();
                Collections.sort(mDataArrayList);
                updateChart(mDataArrayList,entries);

                totalMiles = totalSteps/5000;

                result_date.setText("Weekly Report");
                result_distance.setText("Total Miles:\t"+totalMiles);
                result_steps.setText("Total Steps:\t"+ (int)totalSteps);





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }



     public void updateChart(ArrayList<StepData> stepDataArr, List<BarEntry> entries){
        final String[] days ={"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        final String[] days2 ={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        ArrayList<Integer>colorArrList = new ArrayList<>();
        int index =0;



         for (StepData data : stepDataArr) {

                 // turn your data into Entry objects

                    entries.add(new BarEntry(data.getDay(), (float) (int) data.getStepsCount()));
                    setColorArr(colorArrList, data, index);
                    index++;




         }
         BarDataSet dataSet = new BarDataSet(entries,"");

         dataSet.setValueTextSize((float)12);
         dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // create color
         dataSet.setColors(colorArrList);
         dataSet.setHighlightEnabled(true);
         dataSet.setValueFormatter(new MyValueFormatter());
         BarData barData = new BarData(dataSet);
         barData.setBarWidth((float)0.5);
         chart.setData(barData);
         chart.setFitBars(true);
         chart.setPinchZoom(false);
         chart.setScaleEnabled(false);
         chart.setDrawGridBackground(false);
         chart.setDrawValueAboveBar(true);
         chart.setNoDataTextColor(getResources().getColor(R.color.blue_pressed));
         XAxis xAxis = chart.getXAxis();

         xAxis.setDrawAxisLine(true);
         xAxis.setDrawGridLines(false);
         xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
         xAxis.setGranularity(1f);
         xAxis.setValueFormatter(new MyXAxisValueFormatter(days));

         YAxis left = chart.getAxisLeft();
         left.setDrawLabels(true); // no axis labels
         left.setDrawAxisLine(true); // no axis line
         left.setDrawGridLines(false); // no grid lines
         left.setDrawZeroLine(false); // draw a zero line
         left.setCenterAxisLabels(false);
         left.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
         left.setStartAtZero(true);
         chart.getAxisRight().setEnabled(false);
         chart.getDescription().setEnabled(false);
         chart.getLegend().setEnabled(false);
         chart.setTouchEnabled(true);
         chart.animateXY(3000, 3000);

         chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
             @Override
             public void onValueSelected(Entry e, Highlight h) {

               // Toast.makeText(getApplicationContext(),String.valueOf((int) e.getX()),Toast.LENGTH_SHORT).show();
                 DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("UsersStepData/"
                         +currentUser.getUid()+"/WeeklyStepData");
                 String day = days[(int)e.getX()];
                 String day2 = days2[(int)e.getX()];
                 mRef.child(day).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         StepData data =dataSnapshot.getValue(StepData.class);
                         if(data.getDate()!=null) {
                             result_date.setVisibility(View.VISIBLE);
                             result_distance.setVisibility(View.VISIBLE);
                             YoYo.with(Techniques.FadeIn).duration(500).repeat(0).playOn(mRelativeLayout);

                             result_date.setText(day2 + " " + data.getDate());
                             result_steps.setText("Steps:   " + String.valueOf((int) e.getY()));
                             result_distance.setText("Miles:   " + String.valueOf(e.getY() / 5000));
                         }else if(data.getDate()==null){
                             YoYo.with(Techniques.FadeIn).duration(500).repeat(0).playOn(mRelativeLayout);

                             result_date.setVisibility(View.INVISIBLE);
                             result_steps.setText("No Data Avaliable");
                             result_distance.setVisibility(View.INVISIBLE);
                         }else{
                             DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("UsersStepData/"
                                     +currentUser.getUid()+"/WeeklyStepData");
                             mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     float totalSteps =0;
                                     float totalMiles;

                                     for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                         StepData data = childSnapshot.getValue(StepData.class);
                                         totalSteps+=data.getStepsCount();

                                     }
                                     totalMiles = totalSteps/5000;
                                     result_date.setVisibility(View.VISIBLE);
                                     result_distance.setVisibility(View.VISIBLE);
                                     result_date.setText("Weekly Report");
                                     result_distance.setText("Total Miles:   "+totalMiles);
                                     result_steps.setText("Total Steps:   "+ (int)totalSteps);

                                 }

                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {

                                 }
                             });
                         }
                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });


             }

             @Override
             public void onNothingSelected() {

                 DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("UsersStepData/"
                         +currentUser.getUid()+"/WeeklyStepData");
                 mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         float totalSteps =0;
                         float totalMiles;

                         for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                             StepData data = childSnapshot.getValue(StepData.class);
                             totalSteps+=data.getStepsCount();

                         }
                         totalMiles = totalSteps/5000;
                         result_date.setVisibility(View.VISIBLE);
                         result_distance.setVisibility(View.VISIBLE);

                         YoYo.with(Techniques.FadeIn).duration(500).repeat(0).playOn(mRelativeLayout);
                         result_date.setText("Weekly Report");
                         result_distance.setText("Total Miles:   "+totalMiles);
                         result_steps.setText("Total Steps:   "+ (int)totalSteps);

                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });

             }
         });
     }


     public void setColorArr(ArrayList<Integer> colorArr,StepData data,int index){
         float stepscount = data.getStepsCount();
         int colorId;
         if(stepscount <3000 && stepscount>=0){
             colorId = R.color.red_error;
             colorArr.add(index,getResources().getColor(colorId));
         }else if(stepscount >=3000&&stepscount<6000 ){
             colorId = R.color.holo_orange_light;
             colorArr.add(index,getResources().getColor(colorId));
         }else if(stepscount >=6000 && stepscount<10000 ){
             colorId = R.color.yellow;
             colorArr.add(index,getResources().getColor(colorId));
         }else{
             colorId = R.color.green;
             colorArr.add(index,getResources().getColor(colorId));
         }

     }



    @Override
    public void onBackPressed() {
     finish();
     overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}
