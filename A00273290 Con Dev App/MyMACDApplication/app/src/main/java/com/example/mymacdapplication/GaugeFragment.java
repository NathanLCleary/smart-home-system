package com.example.mymacdapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;

public class GaugeFragment extends Fragment {
    private StateViewModel model;

    public GaugeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_gauge, container, false);

        HalfGauge gauge = view.findViewById(R.id.halfGauge);

        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0);
        range.setTo(500);

        gauge.addRange(range);

        gauge.setMinValue(0);
        gauge.setMaxValue(2000);
        gauge.setValue(0);

        model.pressure.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.e("MACD", "New entry to gauge: " + integer);
                gauge.setValue(integer);
            }
        });

        return view;
    }
}