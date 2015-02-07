package com.isawesome.daniel.sensorrecorder;

/**
 * Created by Daniel on 24/01/2015.
 */
public class AccelData extends SensorData {

        public AccelData(int sessionId, long timestamp, double x, double y, double z){
            super(sessionId, timestamp, x, y, z, "Accel");
        }


}
