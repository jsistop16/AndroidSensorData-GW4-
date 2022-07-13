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

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private ActivityMainBinding binding;

    private SensorManager smStep;// access the device's sensors
    private SensorManager smHeart;
    private Sensor stepCounter;
    private Sensor heartCounter;
    TextView stepTxt;
    TextView heartTxt;

    //using the accelerometer
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
        }

        stepTxt = binding.tvStepCount;
        heartTxt = binding.tvHeartRate;

        smStep = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        smHeart = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        stepCounter = smStep.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //stepCounter : 앱 종료와 관계없이 기존의 값유지 + 1씩 증가
        //stepDetector : 리턴값이 무조건 1, 앱이 종료되면 다시 0부터 시작
        heartCounter = smHeart.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        accLis = new SensorClass();//create accelometerListener instance

        smStep.registerListener(accLis, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);//register to listener
        smHeart.registerListener(accLis, heartCounter, SensorManager.SENSOR_DELAY_NORMAL);

        timer.schedule(timerTask, 0, 6000);

        if(stepCounter != null){
            Toast.makeText(this, "STEP", Toast.LENGTH_SHORT).show();
        }else if(heartCounter != null) {
            Toast.makeText(this, "HEARTRATE", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(stepCounter != null){
            smStep.registerListener(accLis, stepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }else if (heartCounter != null) {
            smHeart.registerListener(accLis, heartCounter, SensorManager.SENSOR_DELAY_NORMAL);
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
        smHeart.registerListener(accLis, heartCounter, SensorManager.SENSOR_DELAY_NORMAL);//Sensor_Delay_Normal : 값을 받는 주기 NORMAL = 0.2초당 한번
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



                /*
                //현재 시간 출력 - TimeStamp
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                Log.d(sdf.format(timestamp), minVal[0]);*/

            }else if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                Log.d("심장센서", String.valueOf(sensorEvent.values[0]));
                if((int)sensorEvent.values[0] > 0){
                    heartTxt.setText(String.valueOf(sensorEvent.values[0]));
                }
            }
        }




        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {// i = accuracy
            //
        }
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(dataStorage != 0){
                stepData = realData - dataStorage;
            }
            dataStorage = realData;
            System.out.println("확인 : " + stepData);
            // Toast.makeText(getApplicationContext(), "증가", Toast.LENGTH_SHORT).show();
        }
    };

}