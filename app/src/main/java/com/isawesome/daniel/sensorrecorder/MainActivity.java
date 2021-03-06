package com.isawesome.daniel.sensorrecorder;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends Activity implements
        OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int DEFAULT_SAMPLE_RESOLUTION = 500;
    private Button btnStart, btnStop, btnTakePic;
    private boolean started = false;
    private TextView tvState;
    private EditText etSampleResolution;
    private CameraHandler m_cameraHandler;

    Intent intent;
    private int sampleResolution;
    private int m_sessionId;
    private SharedPreferences m_pref;
    private Editor m_prefEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitGUIItems();
        intent = new Intent(getApplicationContext(), SensorSampleService.class );
        InitSharedPreferences();
        m_cameraHandler = new CameraHandler();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(started && !m_cameraHandler.GetActivityCallStatus())
            StopClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(started)
            StopClick();
    }

    //need to do the override here because m_cameraHandler is not an activity and cannot get the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((m_cameraHandler != null) && (requestCode == REQUEST_TAKE_PHOTO))
            m_cameraHandler.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                StartClick();
                break;
            case R.id.btnStop:
                StopClick();
                break;
            case R.id.btnTakePic:
                m_cameraHandler.TakePictureAction(GetSessionId(), this);
                break;
            default:
                break;
        }

    }

    private void StartClick(){
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        sampleResolution = Integer.parseInt(etSampleResolution.getText().toString());
        started = true;
        intent.putExtra("sessionId", GetSessionId());
        intent.putExtra("startListening", true);
        intent.putExtra("sampleResolution", sampleResolution);
        startService(intent);
        btnTakePic.setEnabled(true);
        tvState.setText("Started");
    }

    private void StopClick(){
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        started = false;
        intent.putExtra("startListening", false);
        startService(intent);
        btnTakePic.setEnabled(false);
        IncrementSessionId();
        tvState.setText("Stopped");
    }

    private void InitGUIItems() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnTakePic = (Button) findViewById(R.id.btnTakePic);
        tvState = (TextView) findViewById((R.id.tvState));
        etSampleResolution = (EditText) findViewById(R.id.etSampleResolution);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        btnTakePic.setEnabled(false);

        sampleResolution = DEFAULT_SAMPLE_RESOLUTION;
        etSampleResolution.setText(String.valueOf(sampleResolution));
    }

    private void InitSharedPreferences(){
        m_pref = getApplicationContext().getSharedPreferences("SensorRecorderPref", 0);
        m_prefEditor = m_pref.edit();
    }

    private int GetSessionId(){
        return m_pref.getInt("sessionId", 0);
    }

    private void IncrementSessionId(){
        int currentValue = GetSessionId();
        currentValue++;
        m_prefEditor.putInt("sessionId", currentValue);
        m_prefEditor.commit();
    }

}