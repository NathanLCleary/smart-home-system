package com.example.mymacdapplication;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class StateViewModel extends ViewModel {
    // define data used in the app
    public MutableLiveData<Integer> temperature = new MutableLiveData<>();
    public MutableLiveData<Integer> humidity = new MutableLiveData<>();
    public MutableLiveData<Integer> pressure = new MutableLiveData<>();
}
