package com.isawesome.daniel.sensorrecorder;

/**
 * Created by Daniel on 24/01/2015.
 */
public class GyroData extends SensorData {
    public GyroData(long timestamp, double x, double y, double z){
        super(timestamp, x, y, z, "Gyro");
    }
}
