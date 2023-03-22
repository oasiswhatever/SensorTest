package com.example.sensor_test2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;

    private TextView xValueAccel, yValueAccel, zValueAccel;
    private TextView xValueGyro, yValueGyro, zValueGyro;
    private TextView xValueMagnet, yValueMagnet, zValueMagnet;

    private boolean isAccelerometerEnabled = false;
    private boolean isGyroscopeEnabled = false;
    private boolean isMagnetometerEnabled = false;

    private List<SensorData> sensorDataList;

    private Button saveButton;

    private FileWriter fileWriter;
    private File file;

    private BufferedWriter writer;

    private static final String FILENAME = "sensor_data.csv";
    private static final String DIRECTORY_NAME = "SensorData";

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 가속도 센서 초기화
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // 자이로스코프 센서 초기화
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            sensorManager.registerListener(MainActivity.this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // 지자기 센서 초기화
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer != null) {
            sensorManager.registerListener(MainActivity.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // TextView 초기화
        xValueAccel = findViewById(R.id.x_value_accel);
        yValueAccel = findViewById(R.id.y_value_accel);
        zValueAccel = findViewById(R.id.z_value_accel);

        xValueGyro = findViewById(R.id.x_value_gyro);
        yValueGyro = findViewById(R.id.y_value_gyro);
        zValueGyro = findViewById(R.id.z_value_gyro);

        xValueMagnet = findViewById(R.id.x_value_mag);
        yValueMagnet = findViewById(R.id.y_value_mag);
        zValueMagnet = findViewById(R.id.z_value_mag);

        // CSV 파일 생성 및 FileWriter 초기화
        file = new File(Environment.getExternalStorageDirectory(), "sensor_data.csv");
        try {
            fileWriter = new FileWriter(file);
            fileWriter.append("Timestamp, Accelerometer X, Accelerometer Y, Accelerometer Z, Gyroscope X, Gyroscope Y, Gyroscope Z, Orientation X, Orientation Y, Orientation Z, Magnetometer X, Magnetometer Y, Magnetometer Z\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sensorDataList = new ArrayList<>();

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "sensor_data_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
                File csvFile = new File(getStorageDirectory(), fileName);
                saveSensorDataToCsv(csvFile);
                Toast.makeText(MainActivity.this, "CSV file saved to " + csvFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerEnabled) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isGyroscopeEnabled) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (isMagnetometerEnabled) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used, but required to be implemented
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float[] sensorValues = sensorEvent.values;
        long timestamp = System.currentTimeMillis();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                SensorData accelerometerData = new SensorData(sensorType, timestamp, sensorValues);
                sensorDataList.add(accelerometerData);
                xValueAccel.setText(String.valueOf(sensorValues[0]));
                yValueAccel.setText(String.valueOf(sensorValues[1]));
                zValueAccel.setText(String.valueOf(sensorValues[2]));
                break;
            case Sensor.TYPE_GYROSCOPE:
                SensorData gyroscopeData = new SensorData(sensorType, timestamp, sensorValues);
                sensorDataList.add(gyroscopeData);
                xValueGyro.setText(String.valueOf(sensorValues[0]));
                yValueGyro.setText(String.valueOf(sensorValues[1]));
                zValueGyro.setText(String.valueOf(sensorValues[2]));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                SensorData magnetometerData = new SensorData(sensorType, timestamp, sensorValues);
                sensorDataList.add(magnetometerData);
                xValueMagnet.setText(String.valueOf(sensorValues[0]));
                yValueMagnet.setText(String.valueOf(sensorValues[1]));
                zValueMagnet.setText(String.valueOf(sensorValues[2]));
                break;
        }
    }


    private void saveSensorDataToCsv(File csvFile) {
        String csvData = SensorData.toCsv(sensorDataList);

        try {
            FileWriter fileWriter = new FileWriter(csvFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(csvData);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving csv file: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error saving csv file", Toast.LENGTH_SHORT).show();
        }
    }
    private File getStorageDirectory() {
        File file = new File(getExternalFilesDir(null), DIRECTORY_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}

