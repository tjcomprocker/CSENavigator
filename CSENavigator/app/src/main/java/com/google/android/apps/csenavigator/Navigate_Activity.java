package com.google.android.apps.csenavigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

class PathStep {
    int i, j;
    PathStep prev;

    public PathStep(int i, int j, PathStep prev) {
        this.i = i;
        this.j = j;
        this.prev = prev;
    }

    public String toString() {
        return "[" + i + ", " + j + "]";
    }
}


public class Navigate_Activity extends AppCompatActivity implements SensorEventListener{

    public String pathString="";
    int pathpos = 0;
    int[] path_array = new int[100];
    int[][] matrix = {

            {0,1,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,1,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,1,1,1,1,1,0,1},
            {0,1,0,1,1,1,1,1,0,1},
            {0,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {0,1,0,1,1,1,1,1,0,1},
            {0,1,0,1,1,1,1,1,0,1},
            {0,1,0,1,1,1,1,1,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,0,0,0,0,0,0,0,1},
            {0,1,1,1,1,1,1,1,1,1}
    };

    public  void shortestPath(int sx,int sy, int dx, int dy) {
        sx = sx-1;
        sy = sy-1;
        dx = dx-1;
        dy = dy-1;
        int xGrid = matrix.length;
        int yGrid = matrix[0].length;
        // initial
        PathStep step = new PathStep(sx, sy, null);
        LinkedList<PathStep> queue = new LinkedList<PathStep>();
        queue.add(step);
        // using set to check if already traversed
        HashSet<Integer> set = new HashSet<Integer>();
        boolean findDest = false;
        while(!queue.isEmpty() && !findDest) {
            LinkedList<PathStep> tmpQueue = new LinkedList<PathStep>();
            while(!queue.isEmpty()) {
                step = queue.remove();
                int i = step.i, j = step.j, id;
                if(i == dx && j == dy) {	// find dest
                    findDest = true;
                    break;
                }
                PathStep next;
                // move left
                if(j > 0 && matrix[i][j - 1] != 0) {
                    id = xGrid * i + (j - 1);
                    if(!set.contains(id)) {
                        set.add(id);
                        next = new PathStep(i, j - 1, step);
                        tmpQueue.add(next);
                    }
                }
                // move right
                if(j < (yGrid-1) && matrix[i][j + 1] != 0) {
                    id = xGrid * i + (j + 1);
                    if(!set.contains(id)) {
                        set.add(id);
                        next = new PathStep(i, j + 1, step);
                        tmpQueue.add(next);
                    }
                }
                // move up
                if(i > 0 && matrix[i - 1][j] != 0) {
                    id = xGrid * (i - 1) + j;
                    if(!set.contains(id)) {
                        set.add(id);
                        next = new PathStep(i - 1, j, step);
                        tmpQueue.add(next);
                    }
                }
                // move down
                if(i < (xGrid-1) && matrix[i + 1][j] != 0) {
                    id = xGrid * (i + 1) + j;
                    if(!set.contains(id)) {
                        set.add(id);
                        next = new PathStep(i + 1, j, step);
                        tmpQueue.add(next);
                    }
                }
            }
            queue = tmpQueue;
        }
        if(findDest) {
            // build path
            ArrayList<PathStep> path = new ArrayList<PathStep>();
            while(step != null) {
                path.add(step);
                step = step.prev;
            }
            Collections.reverse(path);
            // print path
			/*for(int i = 0; i < path.size(); i++) {
				if(i == path.size() - 1) {
					System.out.println(path.get(i));
				}
				else {
					System.out.print(path.get(i) + " -> ");
				}
			}*/
            int k;
            int x=0;
            for(int i = 0; i < (path.size()-1); i++) {
                k = 0;
                if(path.get(i).i == path.get(i+1).i){ //y increase
                    while(i<(path.size()-1) && path.get(i).i==path.get(i+1).i){
                        k++;
                        i++;

                    }
                    if(k>0){
                        //System.out.println("Go straight" + k +" grids:");
                        path_array[x++] = 1000*k;
                    }
                }
                else{
                    while(i<(path.size()-1) && path.get(i).j==path.get(i+1).j){
                        k++;
                        i++;

                    }
                    if(k>0){
                        //System.out.println("Go straight" + k +" grids:");
                        path_array[x++] = 1000*k;
                    }
                }
                if(i>0 && i<(path.size()-1)){
                    if(path.get(i-1).j < path.get(i).j){//y increase
                        if(path.get(i).i < path.get(i+1).i){//x increase
                            //System.out.println("Take right");
                            path_array[x++] = 1;
                        }
                        else{//x decrease
                            //System.out.println("Take left");
                            path_array[x++] = 0;
                        }
                    }
                    else if(path.get(i-1).j > path.get(i).j){ //y decrease
                        if(path.get(i).i < path.get(i+1).i){//x increase
                            //System.out.println("Take left");
                            path_array[x++] = 0;
                        }
                        else{//x decrease
                            //System.out.println("Take right");
                            path_array[x++] = 1;
                        }
                    }
                    else if(path.get(i-1).i < path.get(i).i){ //x increase
                        if(path.get(i).j < path.get(i+1).j){//y increase
                            //System.out.println("Take left");
                            path_array[x++] = 0;
                        }
                        else{//y decrease
                            //System.out.println("Take right");
                            path_array[x++] = 1;
                        }
                    }
                    else if(path.get(i-1).i < path.get(i).i){ //x decrease
                        if(path.get(i).j < path.get(i+1).j){//y increase
                            //System.out.println("Take right");
                            path_array[x++] = 1;
                        }
                        else{//y decrease
                            //System.out.println("Take left");
                            path_array[x++] = 0;
                        }
                    }
                }
                else{
                    break;
                }
                i--;
            }
            path_array[x]=-1;
            while(path_array[pathpos]!=-1) {
                if(path_array[pathpos+1]==0){//straight with turn
                    pathString += "Go straight "+ path_array[pathpos]/476 + " meters and turn left"+"\n";
                    pathpos+=2;
                }
                else if(path_array[pathpos+1]==1){//straight with turn
                    pathString += "Go straight " + path_array[pathpos]/476 + " meters and turn right"+"\n";
                    pathpos+=2;
                }
                else {//straight
                    pathString += " Go straight " + path_array[pathpos]/476 + " meters"+"\n";
                    pathpos+=1;
                }

            }
            if(path.get(0).i == path.get(1).i){ //y-axis changes
                if(path.get(0).j<path.get(1).j){//y increases
                    move_along_sign = 1;
                    move_along_axis = 2;
                }
                else{ //y-decreases
                    move_along_sign = 2;
                    move_along_axis = 2;
                }
            }
            else if(path.get(0).i<path.get(1).i){//x-axis increases
                move_along_sign = 1;
                move_along_axis = 1;
            }
            else{//x-axis decreases
                move_along_sign = 2;
                move_along_axis = 1;
            }


        }
    }

        ImageView dot_source;
        ImageView dot_destination;

        //xGrid : grid pos x
        //yGrid : grid pos y
        //xOff : x offset of image in pixel
        //yOff : y offset of image in pixel
        //hImg : height of image
        //wImg : width of image
        //hGrid : grid height
        //wGrid : grid width
        //xGridTot : total horizontal grids
        //yGridTot : total vertical grids
        //xPixel : pixel pos x
        //yPixel : pixel pos y

        int xOff = 0; //fenil:0  ch:0
        int yOff = 220; //fenil:360 ch:280

        int hImg = 500; //fenil:720 ch:650
        int wImg = 650; //fenil:980 ch:770

        int xGrid = 18;
        int yGrid = 10;

        int xGridTot = 20;
        int yGridTot = 12;

        int hGrid = hImg / yGridTot;
        int wGrid = wImg / xGridTot;

        int hDot = 20;
        int wDot = 20;


    /*##############################################################*/
    private SensorManager sensorManager;
    public int flag = 0;
    public float count = 0;
    public float prevCount = 0;
    public float previous_angel = 0;
    public float distance = (float) 0;
    public float[] accel;
    public float[] magnet;
    public int source_x = 0;
    public int source_y = 0;
    public int destination_x = 0;
    public int destination_y = 0;
    public float average_y= 0;
    public float current_x = 0;
    public float current_y = 0;
    public float previous_x = 0;
    public float previous_y = 0;
    public int move_along_sign = 2;
    //1: +ve
    public int move_along_axis = 1;
    //1: X-Axis
    public int reached = 0;
     /*##############################################################*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_);

        Intent intent = getIntent();
        source_x  = (int) intent.getFloatExtra("source_x",source_x);
        source_y  = (int) intent.getFloatExtra("source_y",source_y);
        destination_x  = (int) intent.getFloatExtra("destination_x",destination_x);
        destination_y  = (int) intent.getFloatExtra("destination_y",destination_y);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        update_marker_source(source_x,source_y);
        update_marker_destination(destination_x,destination_y);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelSensor == null || magSensor == null)
        {
            Toast.makeText(getApplicationContext(),"Sorry. But you can't use this app.(Necessary Sensors Not Available)", Toast.LENGTH_LONG).show();
        }
        else
        {
            sensorManager.registerListener(Navigate_Activity.this, accelSensor,sensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(Navigate_Activity.this, magSensor,2000000);
        }

        shortestPath(source_x,source_y,destination_x,destination_y);
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(pathString);

    }

    public void update_marker_source(float x, float y)
    {
        dot_source = (ImageView) findViewById(R.id.dot_source);

        if (Math.abs((int)x - destination_y) <= 1 && Math.abs((int)x - destination_y) <= 1 && reached == 0)
        {
            //Toast.makeText(getApplicationContext(),"Destination Reached....!!!", Toast.LENGTH_SHORT).show();
            reached = 1;
        }

        int xGrid = (int) x;
        int yGrid = (int) y;

        //*************
        //Display Image
        //*************
        dot_source.setY(100);
        dot_source.setX(100);

        //**********
        //Scaling : converting grid (xGrid,yGrid) to pixels (xPixel,yPixel)
        //**********

        xGrid = xGrid - 1;
        yGrid = 10 - (yGrid - 1 );

        int xPixel = (int) (xOff + ( wGrid * xGrid  ) + ( (wGrid-wDot) * 0.5  ));
        int yPixel = (int) (yOff + ( hGrid * yGrid  ) + ( (hGrid-hDot) * 0.5  ));


        //*************
        //End Scling
        //*************

        //back = (ImageView) findViewById(R.id.gfloor);
        //int [] a = getBitmapPositionInsideImageView(back);
        //(x1,y1) = (0,360)
        //(x2,y2) = (980,1080)
        //w = 980
        // h = 720

        //*************
        // Reposition of Image
        //*************
        dot_source.setX(xPixel);//0
        dot_source.setY(yPixel);//280
    }

    public void update_marker_destination(float x, float y)
    {
        dot_destination = (ImageView) findViewById(R.id.dot_destination);
        int xGrid = (int) x;
        int yGrid = (int) y;

        //*************
        //Display Image
        //*************
        dot_destination.setY(100);
        dot_destination.setX(100);

        //**********
        //Scaling : converting grid (xGrid,yGrid) to pixels (xPixel,yPixel)
        //**********

        xGrid = xGrid - 1;
        yGrid = 10 - (yGrid - 1 );

        int xPixel = (int) (xOff + ( wGrid * xGrid  ) + ( (wGrid-wDot) * 0.5  ));
        int yPixel = (int) (yOff + ( hGrid * yGrid  ) + ( (hGrid-hDot) * 0.5  ));


        //*************
        //End Scling
        //*************

        //back = (ImageView) findViewById(R.id.gfloor);
        //int [] a = getBitmapPositionInsideImageView(back);
        //(x1,y1) = (0,360)
        //(x2,y2) = (980,1080)
        //w = 980
        // h = 720

        //*************
        // Reposition of Image
        //*************
        dot_destination.setX(xPixel);//0
        dot_destination.setY(yPixel);//280
    }


    /*############################################################################################*/
    @Override
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
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnet = event.values;
        if (accel != null && magnet != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accel,magnet);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut = orientation[0];
                float azimuthInDegress = ((float)Math.toDegrees(azimut)+360)%360;

                if (flag == 0)
                {
                    previous_angel = azimuthInDegress;
                    flag = 1;
                }
                else
                {
                    if (Math.abs(previous_angel - azimuthInDegress) > 180)
                    {
                        if ((360-(previous_angel - azimuthInDegress) >= 50) && (previous_angel - azimuthInDegress) > 0)
                        {
                            Toast.makeText(getApplicationContext(),"Turned Right", Toast.LENGTH_SHORT).show();
                            distance = 0;
                            if (move_along_axis == 1)
                            {
                                if(move_along_sign == 1){
                                    move_along_sign = 2;
                                    move_along_axis = 2;
                                }
                                else{
                                    move_along_sign = 1;
                                    move_along_axis = 2;
                                }
                                //move_along_axis = 2;
                            }
                            else if(move_along_sign == 1)
                            {
                                move_along_sign = 1;
                                move_along_axis = 1;
                                //move_along_axis = 1;
                            }
                            else{
                                move_along_sign = 2;
                                move_along_axis = 1;
                            }
                        }
                        else if (360-(previous_angel - azimuthInDegress) >= 50 && (previous_angel - azimuthInDegress) < 0)
                        {
                            Toast.makeText(getApplicationContext(),"Turned Left", Toast.LENGTH_SHORT).show();
                            distance = 0;
                            if (move_along_axis == 1)
                            {
                                if(move_along_sign == 1){
                                    move_along_sign = 1;
                                    move_along_axis = 2;
                                }
                                else{
                                    move_along_sign = 2;
                                    move_along_axis = 2;
                                }
                                //move_along_axis = 2;
                            }
                            else if(move_along_sign == 1)
                            {
                                move_along_sign = 2;
                                move_along_axis = 1;
                                //move_along_axis = 1;
                            }
                            else{
                                move_along_sign = 1;
                                move_along_axis = 1;
                            }
                        }
                    }
                    else if (previous_angel - azimuthInDegress <= -50)
                    {
                        Toast.makeText(getApplicationContext(),"Turned Right", Toast.LENGTH_SHORT).show();
                        distance = 0;
                        if (move_along_axis == 1)
                        {
                            if(move_along_sign == 1){
                                move_along_sign = 2;
                                move_along_axis = 2;
                            }
                            else{
                                move_along_sign = 1;
                                move_along_axis = 2;
                            }
                            //move_along_axis = 2;
                        }
                        else if(move_along_sign == 1)
                        {
                            move_along_sign = 1;
                            move_along_axis = 1;
                            //move_along_axis = 1;
                        }
                        else{
                            move_along_sign = 2;
                            move_along_axis = 1;
                        }
                    }
                    else if (previous_angel - azimuthInDegress >= 50)
                    {
                        Toast.makeText(getApplicationContext(),"Turned Left", Toast.LENGTH_SHORT).show();
                        distance = 0;
                        if (move_along_axis == 1)
                        {
                            if(move_along_sign == 1){
                                move_along_sign = 1;
                                move_along_axis = 2;
                            }
                            else{
                                move_along_sign = 2;
                                move_along_axis = 2;
                            }
                            //move_along_axis = 2;
                        }
                        else if(move_along_sign == 1)
                        {
                            move_along_sign = 2;
                            move_along_axis = 1;
                            //move_along_axis = 1;
                        }
                        else{
                            move_along_sign = 1;
                            move_along_axis = 1;
                        }
                    }
                    previous_angel = azimuthInDegress;
                }
            }
        }
        if (accel != null)
        {
            if (accel[2] > 10.0)
            {
                distance += (accel[2]-10)*0.4;
                count = (float) Math.ceil(distance/2.1);
                if (count != prevCount)
                {
                    if (move_along_sign == 1 && move_along_axis == 1)
                    {
                        source_x++;
                        if (source_x > 20)
                        {
                            source_x = 20;
                        }
                    }
                    else if (move_along_sign == 1 && move_along_axis == 2)
                    {
                        source_y++;
                        if (source_y > 12)
                        {
                            source_y = 12;
                        }
                    }
                    else if (move_along_sign == 2 && move_along_axis == 1)
                    {
                        source_x--;
                        if (source_x < 0)
                        {
                            source_x = 0;
                        }
                    }
                    else if (move_along_sign == 2 && move_along_axis == 2)
                    {
                        source_y--;
                        if (source_y < 0)
                        {
                            source_y = 0;
                        }
                    }
                    prevCount = count;
                }
                update_marker_source(source_x,source_y);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*############################################################################################*/
}
