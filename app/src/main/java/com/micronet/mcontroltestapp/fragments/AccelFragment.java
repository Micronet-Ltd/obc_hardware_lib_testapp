package com.micronet.mcontroltestapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.micronet.mcontroltestapp.Accelerometer;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by brigham.diaz on 10/26/2016.
 */

public class AccelFragment extends Fragment implements OnChartValueSelectedListener {

    private LineChart mChart;
    //Typeface mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
    //Typeface mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.fragment_section_canbus, container, false);
        View rootView = inflater.inflate(com.micronet.mcontroltestapp.R.layout.activity_realtime_linechart, container, false);

        mChart = (LineChart) rootView.findViewById(com.micronet.mcontroltestapp.R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(6f);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        //xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(1.1f);
        leftAxis.setAxisMinimum(-1.1f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        feedMultiple();

        return rootView;
    }


    private void addEntry() throws FileNotFoundException {

        LineData data = mChart.getData();

        LineDataSet setX, setY, setZ;

        if (data != null) {

            setX = (LineDataSet) data.getDataSetByIndex(0);
            setY = (LineDataSet) data.getDataSetByIndex(1);
            setZ = (LineDataSet) data.getDataSetByIndex(2);
            // set.addEntry(...); // can be called as well

            if (setX == null) {
                setX =  createSet("AccelX" , ColorTemplate.getHoloBlue() , ColorTemplate.getHoloBlue());
                data.addDataSet(setX);
            }
            if (setY == null) {
                setY = createSet("AccelY", Color.RED, Color.RED);
                data.addDataSet(setY);
            }
            if (setZ == null) {
                setZ = createSet("AccelZ" , ColorTemplate.colorWithAlpha(Color.YELLOW, 200), ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
                data.addDataSet(setZ);
            }

            Accelerometer accel = new Accelerometer();
            try {
                accel.getAccel();
            } catch (IOException e) {
                e.printStackTrace();
            }

            data.addEntry(new Entry(setX.getEntryCount(), accel.accelData[0]), 0);
            data.addEntry(new Entry(setY.getEntryCount(), accel.accelData[1]), 1);
            data.addEntry(new Entry(setZ.getEntryCount(), accel.accelData[2]), 2);

//            if (data.getEntryCount() > 5000) {
//                //int totalCount = data.getEntryCount();
//                for (int i = 0; i < 1000 ; i++) {
//                    data.removeEntry(setX.getEntryForIndex(i), 0);
//                }
//            }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(240);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet(String strDataTitle, int lineColor , int fillColor) {
        LineDataSet set =  new LineDataSet(null, strDataTitle);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(lineColor);
        set.setCircleColor(Color.TRANSPARENT);
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setFillColor(fillColor);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextSize(9f);
        set.setDrawCircleHole(false);

        return set;
    }

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    addEntry();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {

                    // Don't generate garbage runnables inside the loop.
                    getActivity().runOnUiThread(runnable);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

}
