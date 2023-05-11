package com.example.mymacdapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MACD";
    private StateViewModel model;

    private int qos = 0;
    private MqttAsyncClient mqttClient;
    private MemoryPersistence persistence = new MemoryPersistence();
    private final static String MQTT_BROKER = "tcp://broker.emqx.io:1883";
    private String clientId;
    EditText messageET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new ViewModelProvider(this).get(StateViewModel.class);

        messageET = findViewById(R.id.MessageED);

        SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Sensor lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mySensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final SensorEventListener lightSensorListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                //textLIGHT_reading.setText("LIGHT: " + event.values[0]);
                Log.i(TAG, "" + event.values[0]);
                String setting = "bright";
                if (event.values[0] < 20000){
                    setting = "dark";
                }else if(event.values[0] >= 20000 & event.values[0] < 30000){
                    setting = "middle";
                }
                MqttMessage message = new MqttMessage();
                message.setPayload(setting.getBytes(StandardCharsets.UTF_8));

                try {
                    mqttClient.publish("A00273290/ESP", message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mqttConnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //generator.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {}

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            Log.d(TAG, String.format("messageArrived from topic %s. Message = %s ",topic,payload));
            String[] stringArray = payload.split(",");

            String tempString = stringArray[0];
            double d = Double.parseDouble(tempString);
            int temp = (int) d;
            Log.e(TAG, "Double: " + d + " int: " + temp);
            model.temperature.postValue(temp);
            model.humidity.postValue(Integer.valueOf(stringArray[1]));
            model.pressure.postValue(Integer.valueOf(stringArray[2]));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {}
    };


    private void mqttConnect() throws MqttException {
        model = new ViewModelProvider(this).get(StateViewModel.class);
        // set mqtt options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        clientId = MqttAsyncClient.generateClientId();

        // connect
        mqttClient = new MqttAsyncClient(MQTT_BROKER, clientId, persistence);
        mqttClient.connect(options, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "onSuccess: Connected");
                // set the callback
                mqttClient.setCallback(mqttCallback);
                // subscribe
                try {
                    mqttClient.subscribe("A00273290/output", qos);
                    /*MqttMessage message = new MqttMessage();
                    message.setPayload("Hi there".getBytes(StandardCharsets.UTF_8));

                    mqttClient.publish("A00273290/input", message);*/

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, "onFailure: " + exception.getMessage(), exception);
            }
        });
    }

    public void sendMessage(View view) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(messageET.getText().toString().getBytes(StandardCharsets.UTF_8));

        mqttClient.publish("A00273290/input", message);
    }
}