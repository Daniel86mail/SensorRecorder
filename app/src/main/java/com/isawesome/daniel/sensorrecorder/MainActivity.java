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

import org.w3c.dom.Text;

public class MainActivity extends Activity implements SensorEventListener,
        OnClickListener {
    private SensorManager sensorManager;
    private Button btnStart, btnStop;
    private boolean started = false;
    private ArrayList sensorData;
    private TextView tvState, tvReading, tvRecordedData, tvRecordedItems;
    private long mTimeStamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList();

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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();

            if(timestamp > mTimeStamp + 1000) {
                //record every 1 second
                AccelData data = new AccelData(timestamp, x, y, z);
                String output = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",timestamp, x, y, z );
                tvRecordedData.setText(output);
                sensorData.add(data);
                String recordedItemNumber = String.format("Item Number: %d", sensorData.size());
                tvRecordedItems.setText(recordedItemNumber);
                mTimeStamp = timestamp;
            }

            String output = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",timestamp, x, y, z );
            tvReading.setText(output);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                sensorData = new ArrayList();
                // save prev data if available
                started = true;
                Sensor accel = sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mTimeStamp = System.currentTimeMillis();
                sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_FASTEST);
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
}