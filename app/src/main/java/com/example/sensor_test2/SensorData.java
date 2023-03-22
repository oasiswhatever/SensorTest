package com.example.sensor_test2;

import java.util.List;

public class SensorData {
    private int sensorType;
    private long timestamp;
    private float[] sensorValues;

    public SensorData(int sensorType, long timestamp, float[] sensorValues) {
        this.sensorType = sensorType;
        this.timestamp = timestamp;
        this.sensorValues = sensorValues;
    }

    public int getSensorType() {
        return sensorType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float[] getSensorValues() {
        return sensorValues;
    }

    public static String toCsv(List<SensorData> sensorDataList) {
        StringBuilder sb = new StringBuilder();
        for (SensorData data : sensorDataList) {
            sb.append(data.getTimestamp())
                    .append(",")
                    .append(data.getSensorValues()[0])
                    .append(",")
                    .append(data.getSensorValues()[1])
                    .append(",")
                    .append(data.getSensorValues()[2])
                    .append("\n");
        }
        return sb.toString();
    }
}
