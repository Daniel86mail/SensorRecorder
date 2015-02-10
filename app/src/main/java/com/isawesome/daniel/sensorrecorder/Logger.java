package com.isawesome.daniel.sensorrecorder;

import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Daniel on 24/01/2015.
 */
public class Logger {

    File m_csvPath;
    CSVWriter m_csvWriter;

    public void InitCSVWriter(int sessionId){
        try{
            String sessionFolder = String.format("Session_%d" , sessionId);
            File folder = new File(Environment.getExternalStorageDirectory() + "/SensorRecorder/"+sessionFolder);//make this common to Logger and camera Handler
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                m_csvPath = folder;
                File file = new File(m_csvPath, "SensorRecorder"+"_"+sessionFolder+".csv");
                if(m_csvPath.canWrite()) {
                    FileWriter writer = new FileWriter(file, true);
                    m_csvWriter = new CSVWriter(writer);
                }
            } else {
                // Do something else on failure
            }

            if (m_csvWriter == null)
                throw new Exception("null");
        }
        catch(java.io.IOException e) {

            //dostuff

        }
        catch(Exception e) {
            //do stuff
        }

    }

    public void CloseCSVFile(){
        try{
            if(m_csvWriter != null) {
                m_csvWriter.flush();
                m_csvWriter.close();
            }
        }
        catch(java.io.IOException e) {
        }
    }
///
    public void RecordDataToFile(SensorData data){
        String[] dataToWrite;
        String time, x, y, z, sensorName;
        time =  String.format("%d",data.getTimestamp());
        x = String.format("%.3f",data.getX());
        y = String.format("%.3f",data.getY());
        z = String.format("%.3f",data.getZ());
        sensorName = data.GetSensorName();
        dataToWrite = (new String[]{time, sensorName, x, y, z});

        if(m_csvWriter != null) {
            m_csvWriter.writeNext(dataToWrite);
        }
    }

    public void RecordEventToFile(String eventToLog) {
        String[] dataToWrite = (new String[]{String.valueOf(System.currentTimeMillis()), eventToLog});
        if (m_csvWriter != null) {
            m_csvWriter.writeNext(dataToWrite);
        }
    }
}
