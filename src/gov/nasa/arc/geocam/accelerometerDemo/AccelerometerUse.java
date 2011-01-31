package gov.nasa.arc.geocam.accelerometerDemo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AccelerometerUse extends Activity {
	private Button startButton;
	private Button stopButton;
	private String path = Environment.getExternalStorageDirectory().toString();
	private String FILENAME = "accelerometerDemo.log";
	private BufferedWriter fos;
	private SensorManager sensorMan;
	
	/** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometerscreen);
        
        startButton = (Button) findViewById(R.id.startACCButton);
    	stopButton = (Button) findViewById(R.id.stopACCButton);
    	
    	startButton.setOnClickListener(startListener);
    	stopButton.setOnClickListener(stopListener);
    	stopButton.setEnabled(false);
    }
    
    public void startAccelerometer(){    	
    	try {
        	fos = new BufferedWriter(new FileWriter(path + "/" + FILENAME));
        	fos.write("Created the ACCELEROMETER LOGGER @ " + 
            		DateFormat.getDateTimeInstance().format(new Date()) + "\n");
        	fos.flush();
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    	startButton.setEnabled(false);
    	stopButton.setEnabled(true);
    	
    	startService(new Intent(this, BatteryLevelService.class));
    	
    	sensorMan = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    	sensorMan.registerListener(listener,
    	   sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    	   SensorManager.SENSOR_DELAY_NORMAL);
    	
    	
         	
    }
    
    public void stopAccelerometer()
    {
    	stopService(new Intent(this, BatteryLevelService.class));
    	sensorMan.unregisterListener(listener);
    	stopButton.setEnabled(false);
    	startButton.setEnabled(true);
    	
    	try{
			fos.write("Stopping the ACCELEROMETER LOGGER @ " + 
					DateFormat.getDateTimeInstance().format(new Date())+"\n");
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}    	
    }
    
    private OnClickListener startListener = new OnClickListener(){
    	public void onClick(View v){
    		startAccelerometer();
    	}
    };
    
    private OnClickListener stopListener = new OnClickListener(){
    	public void onClick(View v){
    		stopAccelerometer();    	
    	}
    };
        
    private SensorEventListener listener = new SensorEventListener(){

		   public void onAccuracyChanged(Sensor arg0, int arg1){}

		   public void onSensorChanged(SensorEvent evt)
		   {
		      float vals[] = evt.values;
		    	      
		      if(evt.sensor.getType() == Sensor.TYPE_ORIENTATION)
		      {
		    	  try{
		  	 			fos.write("ACCELEROMETER ORIENTATION Update @ " + 
		  	 					DateFormat.getDateTimeInstance().format(new Date())+"\n");
		  	 			fos.write("\t Orientation X = " + vals[0] + "\n" +
		  	 					  "\t Orientation Y = " + vals[1] + "\n" +
		  	 					  "\t Orientation Z = " + vals[2] + "\n");

		  	 		} catch (IOException ex) {
		  	 			ex.printStackTrace();
		  	 		}
		      }
		      if(evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		      {
		    	  try{
		  	 			fos.write("ACCELEROMETER ACCELERATION @ " + 
		  	 					DateFormat.getDateTimeInstance().format(new Date())+"\n");
		  	 			fos.write("\t Accel X = " + vals[0] + "\n" +
		  	 					  "\t Accel Y = " + vals[1] + "\n" +
		  	 					  "\t Accel Z = " + vals[2] + "\n");

		  	 		} catch (IOException ex) {
		  	 			ex.printStackTrace();
		  	 		}
		      }
		   }
		};
}
