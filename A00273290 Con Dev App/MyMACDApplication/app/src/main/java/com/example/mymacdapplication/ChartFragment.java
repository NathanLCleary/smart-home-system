package com.example.mymacdapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class ChartFragment extends Fragment {
    private StateViewModel model;
    private LineChart lineChart;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        lineChart = view.findViewById(R.id.chart1);

        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);

        LineData data = new LineData();
        data.addDataSet(createSet());
        data.addDataSet(createSet2());

        lineChart.setData(data);

        lineChart.setNoDataText("No chart data available, wait for refresh...");
        lineChart.invalidate();

        model.humidity.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                addEntry(integer,0);
            }
        });

        model.temperature.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                addEntry(integer,1);
            }
        });

        return view;
    }

    private void addEntry(int value,int index) {
        LineData data = lineChart.getData();
        ILineDataSet randomSet = data.getDataSetByIndex(index);
        data.addEntry(new Entry(randomSet.getEntryCount(), value), index);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();

        lineChart.setVisibleXRangeMaximum(20);
        lineChart.moveViewTo(
                data.getEntryCount() - 21,
                50f,
                YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Humidity");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
    private LineDataSet createSet2() {
        LineDataSet set = new LineDataSet(null, "Temperature");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(0, 0, 255));
        set.setCircleColor(Color.rgb(240, 0, 0));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
}