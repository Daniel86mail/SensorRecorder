package com.isawesome.daniel.sensorrecorder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener,
        OnClickListener {
    private SensorManager sensorManager;
    private Button btnStart, btnStop;
    private boolean started = false;
    private ArrayList sensorData;
    private TextView tvState, tvReading, tvRecordedData, tvRecordedItems;
    private long mAccelTimeStamp, mGyroTimeStamp;
    int counter = 0;
    Logger log;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        InitGUIItems();
        log = new Logger();


    }

    @Override
    protected void onResume() {
        super.onResume();

        log.InitCSVWriter();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
        log.CloseCSVFile();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            int sensorType = event.sensor.getType();
            switch(sensorType){
                case Sensor.TYPE_ACCELEROMETER:
                    HandleAccel(event);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    HandleGyro(event);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public  void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                counter = 0 ;
                // save prev data if available
                started = true;
                Sensor accel = sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_FASTEST);
                Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
                mAccelTimeStamp = System.currentTimeMillis();
                mGyroTimeStamp = System.currentTimeMillis();
                tvState.setText("Started");
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                started = false;
                sensorManager.unregisterListener(this);
                tvState.setText("Stopped");
                break;
            default:
                break;
        }

    }

    private void InitGUIItems() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        tvState = (TextView) findViewById((R.id.tvState));
        tvReading = (TextView) findViewById(R.id.tvReading);
        tvRecordedData = (TextView) findViewById(R.id.tvRecordedData);
        tvRecordedItems = (TextView) findViewById(R.id.tvRecordedItems);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }

    private void HandleAccel(SensorEvent event){

        AccelData data = new AccelData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);

        if(data.getTimestamp() > mAccelTimeStamp + 1000) {
            //record every 1 second
            String output = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",data.getTimestamp(), data.getX(), data.getY(), data.getZ() );
            tvRecordedData.setText(output);
            mAccelTimeStamp = data.getTimestamp();
            counter++;
            String counterOutput = String.format("Number of Recorded Items is: %d",counter);
            tvRecordedItems.setText(counterOutput);
            //save data to file
            log.RecordDataToFile(data);
        }

        String output1 = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",data.getTimestamp(), data.getX(), data.getY(), data.getZ() );
        tvReading.setText(output1);
    }

    private void HandleGyro(SensorEvent event){

        GyroData data = new GyroData(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2]);

        if(data.getTimestamp() > mGyroTimeStamp + 1000) {
            //record every 1 second
            String output = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",data.getTimestamp(), data.getX(), data.getY(), data.getZ() );
            tvRecordedData.setText(output);
            mGyroTimeStamp = data.getTimestamp();
            counter++;
            String counterOutput = String.format("Number of Recorded Items is: %d",counter);
            tvRecordedItems.setText(counterOutput);
            //save data to file
            log.RecordDataToFile(data);
        }

        String output1 = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",data.getTimestamp(), data.getX(), data.getY(), data.getZ() );
        tvReading.setText(output1);
    }
}