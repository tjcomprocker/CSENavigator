package com.google.android.apps.csenavigator;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    /*##############################################################*/
    public String[] arraySpinner;
     /*##############################################################*/

    /*##############################################################*/
    WifiManager mainWifi;
    List<ScanResult> wifiList;
    int i = 0,y,d;
    String [] nextLine;
    String [] firstLine;
    CSVReader reader;
    float allVec [][];
    float locations [][];
    int vectors;
    /*##############################################################*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*##############################################################*/
        this.arraySpinner = new String[]{"101", "102", "103", "104", "105", "126", "127", "124", "123", "122", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "118", "119", "120"};
        Spinner spinner_destinations = (Spinner) findViewById(R.id.destinations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, arraySpinner);
        spinner_destinations.setAdapter(adapter);

        /*sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelSensor == null || magSensor == null) {
            Toast.makeText(getApplicationContext(), "Sorry. But you can't use this app.(Necessary Sensors Not Available)", Toast.LENGTH_LONG).show();
        } else {
            sensorManager.registerListener(this, accelSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }*/

        Spinner spinner_sources = (Spinner) findViewById(R.id.sources);
        spinner_sources.setAdapter(adapter);
        /*##########################################################z####*/

        /*##############################################################*/
        try {
            AssetManager assetManager = getAssets();
            InputStream csvStream = assetManager.open("locations.txt");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);

            reader = new CSVReader(csvStreamReader);


            firstLine = reader.readNext();
            locations = new float [23][3];
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                for (int j = 0; j < 3; j++)
                {
                    locations[i][j] = Float.parseFloat(nextLine[j]);
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*##############################################################*/

        /*##############################################################*/
        try {
            AssetManager assetManager = getAssets();
            InputStream csvStream = assetManager.open("o100.txt");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);

            reader = new CSVReader(csvStreamReader);


            firstLine = reader.readNext();
            allVec = new float [500][];
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                allVec[vectors] = new float[firstLine.length];
                for (int j = 0; j < nextLine.length; j++) {
                    allVec[vectors][j] = Float.parseFloat(nextLine[j]);
                }
                vectors++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        try {
            scanloc();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*##############################################################*/
    }

    public void navigate(View v)
    {
        Spinner destination_spinner=(Spinner) findViewById(R.id.destinations);
        Spinner sources_spinner=(Spinner) findViewById(R.id.sources);
        Intent intent = new Intent(getBaseContext(), Navigate_Activity.class);
        float [] temp  = new float[2];
        temp = loctoxy(Float.parseFloat((sources_spinner.getSelectedItem().toString())));
        float source_x = temp[0];
        float source_y = temp[1];
        temp = loctoxy(Float.parseFloat((destination_spinner.getSelectedItem().toString())));
        float destination_x = temp[0];
        float destination_y = temp[1];
        intent.putExtra("source_x",source_x);
        intent.putExtra("source_y",source_y);
        intent.putExtra("destination_x",destination_x);
        intent.putExtra("destination_y",destination_y);
        //intent.putExtra("AverageY",average_y);
        startActivity(intent);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accel = event.values;
        if (accel != null)
        {
            count++;
            if (count == 5)
            {
                count = 4;
            }
            else if (count < 4)
            {
                average_y = average_y + accel[1];
                return;
            }
            else if (count == 4) {
                average_y = average_y + accel[1];
                average_y = average_y / 5;
            }
        }
        Log.d("shu bhai","kai nai lya...!!!!!");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    */

    public void scanloc() throws IOException {
        TextView loc = (TextView) findViewById(R.id.tx_predicted);
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();
        //ArrayList<float> newVec = new ArrayList<float>();
        float newVec[] = new float[firstLine.length];
        newVec[0]=-1;
        newVec[1]=-1;
        newVec[2]=-1;
        int isset =0,th=-75;
        for(int i=3;i<firstLine.length;i++){
            for(int j=0;j<wifiList.size();j++){
                if(wifiList.get(j).BSSID.toString().equals(firstLine[i])) {
                    newVec[i] = wifiList.get(j).level;
                  /*
                  if(newVec[i]<th){
                      newVec[i]=0;
                  }
                  */
                    if(newVec[i]!=0){
                        newVec[i]+=100;
                    }
                    break;
                }
            }
        }
        float select_x=-1,select_y=-1;
        float min_dist=100000000,dist=0;
        for(int i=0;i<vectors;i++){
            dist = 0;
            for(int j=3;j<firstLine.length;j++){
                dist += (allVec[i][j] - newVec[j])*(allVec[i][j] - newVec[j]);
            }
            if (dist < min_dist){
                min_dist = dist;
                select_x = allVec[i][0];
                select_y = allVec[i][1];
            }
        }

        loc.setText( (int)(xytoloc(select_x, select_y)) + "");
    }

    public float[] loctoxy(float loc)
    {
        float[] xy = new float[2];

        for (int i=0;i<23;i++)
        {
            if (loc == locations[i][0])
            {
                xy[0] = locations[i][1];
                xy[1] = locations[i][2];
                return xy;
            }
        }

        return xy;
    }

    public float xytoloc(float x, float y)
    {
        float loc = 0;
        float min=100000;
        for (int i=0;i<23;i++)
        {
            if(Math.sqrt(((x-locations[i][1])*(x-locations[i][1]))+((y-locations[i][2])*(y-locations[i][2]))) < min)
            {
                loc = locations[i][0];
                min = (float) Math.sqrt(((x-locations[i][1])*(x-locations[i][1]))+((y-locations[i][2])*(y-locations[i][2])));
            }
        }
        return loc;
    }
}
