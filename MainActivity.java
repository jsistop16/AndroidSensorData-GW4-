package com.example.sensorlistup;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.sensorlistup.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

//to check whether a particular sensor is accessible or not before launching the application.
public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //to request runtime permission from the user to access any sensor data
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 1);
        }else{
            Log.d(TAG, "ALREADY GRANTED");
        }
        //To use the "step counter" or "step detector" sensor,
        //also need the ACTIVITY_RECOGNITION runtime permission.



        //create an object of the SensorManager class and instantiate it
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        //To check the list of sensors integrated with GW4
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Sensor sensor : sensors) {
            arrayList.add(sensor.getName());
        }
        arrayList.forEach((n) -> System.out.println(n));

        mTextView = binding.text;
    }
}