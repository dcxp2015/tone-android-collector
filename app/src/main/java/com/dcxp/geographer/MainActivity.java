package com.dcxp.geographer;

import com.google.gson.*;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements SensorEventListener, View.OnClickListener {
    private SensorManager sManager;
    private Sensor accel;
    private ArrayList<Point3D> data = new ArrayList<Point3D>();
    private TextView infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();

        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        infoView = (TextView)findViewById(R.id.infoView);

        infoView.setText("Press 'Start' to begin collecting.");
    }

    public void startPressed(View v){
        startCollection();
    }

    public void finishPressed(View v){
        infoView.setText("Collection paused. Data is still stored.");
        pauseCollection();
    }

    public void clearPressed(View v){
        infoView.setText("Data cleared.");
        clearCollection();
    }

    public void sendPressed(View v){
        sendCollected();
    }

    private void startCollection(){
        if(accel!=null) {
            infoView.setText("Collecting Data...");
            sManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void pauseCollection(){
        if(accel!=null) {
            sManager.unregisterListener(this);
        }
    }

    private void clearCollection(){
        this.pauseCollection();
        data.clear();
    }

    private void sendCollected(){
        Gson gson = new Gson();
        System.out.println("DATA: " + gson.toJson(data));

        String dText = gson.toJson(data);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"drflax29@verizon.net"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Accel Data");
        i.putExtra(Intent.EXTRA_TEXT, dText);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startCollection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.pauseCollection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        infoView.setText("Changed!");
        if(event.values.length == 3){
            infoView.setText("Changed 3");
            Point3D p = new Point3D(event.values[0],event.values[1],event.values[2]);
            System.out.println(p);
            data.add(p);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {

    }

    class Point3D{
        float x, y, z;
        public Point3D(float ix, float iy, float iz){
            x = ix;
            y = iy;
            z = iz;
        }

        @Override
        public String toString(){
            return "[X: "+x+", Y: "+y+", Z: "+z+"]";
        }
    }
}
