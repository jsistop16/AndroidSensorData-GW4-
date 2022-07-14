package com.example.mystep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import com.example.mystep.databinding.ActivityMainBinding;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private ActivityMainBinding binding;

    //step
    private SensorManager smStep;// access the device's sensors
    private Sensor stepCounter;
    TextView stepTxt;

    //light
    private SensorManager smLight;
    private Sensor light;
    private float lux;
    TextView lightTxt;



    //using the eventListener
    private SensorEventListener accLis;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){//권한 체크 기능 : 디바이스에서 데이터 측정하는것을 허가해주는 팝업창 뜸
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }//step_count & step_detector는 ACTIVITY_RECOGNITION에 대한 허가 필요


        //viewBinding
        stepTxt = binding.tvStepCount;
        lightTxt = binding.tvLight;

        //sensor manager
        smStep = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        smLight = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //sensor
        stepCounter = smStep.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        light = smLight.getDefaultSensor(Sensor.TYPE_LIGHT);
        //stepCounter : 앱 종료와 관계없이 기존의 값유지 + 1씩 증가
        //stepDetector : 리턴값이 무조건 1, 앱이 종료되면 다시 0부터 시작

        accLis = new SensorClass();//create Listener instance

        //register to listener
        smStep.registerListener(accLis, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        smLight.registerListener(accLis, light, SensorManager.SENSOR_DELAY_NORMAL);

        timer.schedule(timerTask, 60000, 60000);

        if(stepCounter != null){
            Toast.makeText(this, "STEP", Toast.LENGTH_SHORT).show();
        }
        if (light != null) {
            Toast.makeText(this, "LIGHT", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(stepCounter != null){
            smStep.registerListener(accLis, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(light != null){
            smLight.registerListener(accLis, light, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sm.unregisterListener(accLis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        smStep.registerListener(accLis, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        smLight.registerListener(accLis, light, SensorManager.SENSOR_DELAY_NORMAL);
    }
    float realData;
    float dataStorage;//0
    float stepData;

    private class SensorClass implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                stepTxt.setText("Step Count : " + (sensorEvent.values[0]));

                System.out.println("qwertyqwert" + (sensorEvent.values[0]));
                realData = sensorEvent.values[0];
            }
            if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
                lux = sensorEvent.values[0];
                lightTxt.setText("Light : " + lux);
            }

        }




        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {// i = accuracy
            //
        }
    }

    Timer timer = new Timer();
    Timestamp timestamp;
    SimpleDateFormat sdf;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            stepData = realData - dataStorage;
            dataStorage = realData;
            System.out.println("확인 : " + stepData);
            runOnUiThread(new Runnable() {//Toast는 ui자원이므로 별도의 thread를 사용해야 접근가능
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), String.valueOf(stepData), Toast.LENGTH_SHORT).show();
                    //현재 시간 출력 - TimeStamp
                    timestamp = new Timestamp(System.currentTimeMillis());
                    sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    Log.d(sdf.format(timestamp), String.valueOf(stepData));
                }
            });
        }
    };

}