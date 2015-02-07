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

public class MainActivity extends Activity implements
        OnClickListener {

    private static final int DEFAULT_SAMPLE_RESOLUTION = 500;
    private Button btnStart, btnStop, btnTakePic;
    private boolean started = false;
    private TextView tvState;
    private EditText etSampleResolution;
    private CameraHandler m_cameraHandler;

    Intent intent;
    private int sampleResolution;
    private int m_sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitGUIItems();
        intent = new Intent(getApplicationContext(), SensorSampleService.class );
        m_sessionId =0;

        m_cameraHandler = new CameraHandler();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                sampleResolution = Integer.parseInt(etSampleResolution.getText().toString());
                started = true;
                m_sessionId++;
                intent.putExtra("sessionId", m_sessionId);
                intent.putExtra("startListening", true);
                intent.putExtra("sampleResolution", sampleResolution);
                startService(intent);
                btnTakePic.setEnabled(true);
                tvState.setText("Started");
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                started = false;
                intent.putExtra("startListening", false);
                startService(intent);
                btnTakePic.setEnabled(false);
                tvState.setText("Stopped");
                break;
            case R.id.btnTakePic:
                m_cameraHandler.TakePictureAction(m_sessionId, this);
                break;
            default:
                break;
        }

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

}