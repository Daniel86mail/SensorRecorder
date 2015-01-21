package com.isawesome.daniel.sensorrecorder;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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

import com.opencsv.CSVWriter;

import org.w3c.dom.Text;

public class MainActivity extends Activity implements SensorEventListener,
        OnClickListener {
    private SensorManager sensorManager;
    private Button btnStart, btnStop;
    private boolean started = false;
    private ArrayList sensorData;
    private TextView tvState, tvReading, tvRecordedData, tvRecordedItems;
    private long mTimeStamp;
    String m_csvPath;
    CSVWriter m_csvWriter;

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
                //sensorData.add(data);
                //String recordedItemNumber = String.format("Item Number: %d", sensorData.size());
                //tvRecordedItems.setText(recordedItemNumber);
                mTimeStamp = timestamp;

                //save data to file
                RecordDataToFile(data);
            }

            String output = String.format("Time(ms): %d X val: %.3f, Y val: %.3f, Z val: %.3f",timestamp, x, y, z );
            tvReading.setText(output);
        }

    }

    private void RecordDataToFile(AccelData data){
            String[] dataToWrite;
            String time, x, y, z;
            time =  String.format("%d",data.getTimestamp());
            x = String.format("%.3f",data.getX());
            y = String.format("%.3f",data.getY());
            z = String.format("%.3f",data.getZ());
            dataToWrite = (new String[]{time, x, y, z});

        if(m_csvWriter != null)
            m_csvWriter.writeNext(dataToWrite);
    }

    @Override
    public  void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                sensorData = new ArrayList();
                // save prev data if available
                started = true;
                InitCSVWriter();
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
                CloseCSVFile();
                sensorManager.unregisterListener(this);
                tvState.setText("Stopped");
                break;
            default:
                break;
        }

    }

    private void InitCSVWriter(){
        try{
            m_csvPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            m_csvWriter = new CSVWriter(new FileWriter(m_csvPath));
        }
        catch(java.io.IOException e) {
        }
    }

    private void CloseCSVFile(){
        try{
            if(m_csvWriter != null)
                 m_csvWriter.close();
        }
        catch(java.io.IOException e) {
        }
    }
}