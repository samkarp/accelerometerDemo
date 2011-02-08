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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MixedGPSUse extends Activity {
	private Button startButton;
	private Button stopButton;
	private String path = Environment.getExternalStorageDirectory().toString();
	private String FILENAME = "mixed.log";
	private BufferedWriter fos;
	private SensorManager sensorMan;
	private int updateCount;
	private LocationManager locMan;
	
	/** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.mixedscreen);
        
        startButton = (Button) findViewById(R.id.startMixButton);
    	stopButton = (Button) findViewById(R.id.stopMixButton);
    	
    	startButton.setOnClickListener(startListener);
    	stopButton.setOnClickListener(stopListener);
    	stopButton.setEnabled(false);
    }
    
    public void startMixed(){
    	try {
        	fos = new BufferedWriter(new FileWriter(path + "/" + FILENAME));
        	fos.write("Created the MIXED LOGGER @ " + 
            		DateFormat.getDateTimeInstance().format(new Date()) + "\n");
        	fos.flush();
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    	
    	startButton.setEnabled(false);
    	stopButton.setEnabled(true);
    	
    	sensorMan = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    	sensorMan.registerListener(listener,
    	   sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    	   SensorManager.SENSOR_DELAY_NORMAL);
    	
    	locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    			60000, 1, gpsListener);
    	
         	
    }
    
    public void stopMixed()
    {
    	stopService(new Intent(this, BatteryLevelService.class));
    	sensorMan.unregisterListener(listener);
    	stopButton.setEnabled(false);
    	startButton.setEnabled(true);
    	
    	try{
			fos.write("Stopping the MIXED LOGGER @ " + 
					DateFormat.getDateTimeInstance().format(new Date())+"\n");
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
     	
    }
    
    private OnClickListener startListener = new OnClickListener(){
    	public void onClick(View v){
    		startMixed();
    	}
    };
    
    private OnClickListener stopListener = new OnClickListener(){
    	public void onClick(View v){
    		stopMixed();    	
    	}
    };
    
    
        
    private SensorEventListener listener = new SensorEventListener(){

		   public void onAccuracyChanged(Sensor arg0, int arg1){}

		   public void onSensorChanged(SensorEvent evt)
		   {
		      float vals[] = evt.values;
		    	      
		      if(evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		      {
		    	  //still not moving
		    	  if ((vals[0] < .5 && vals[1] < .5) && updateCount != 1000)
		    	  {
		    		  updateCount++;
		    	  }
		    	  //just enough data to stop moving
		    	  else if((vals[0] < .5 && vals[1] < .5) && updateCount == 1000)
		    	  {
		    		  updateCount++;
		    		  locMan.removeUpdates(gpsListener);
		    		  
		    	  }
		    	  //moving after stopped, add listener back
		    	  else if ((vals[0] > .5 || vals[1] > .5) && updateCount >= 1000)
		    	  {
		    		  updateCount = 0;
		    		  locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		    	    			60000, 1, gpsListener);
		    	  }
		    	  //else 
		    	  else
		    	  {
		    		  updateCount = 0;
		    	  }
		    	  
		    	  if (updateCount == 2130000000)
		    	  {
		    		  updateCount = 1001;
		    	  }

		      }
		   }
		};
		
		private LocationListener gpsListener = new LocationListener(){
    		Location curLocation;
    	    
    		public void onLocationChanged(Location location)
    		{
    			if(curLocation == null)
    	         {
    	            curLocation = location;
    	         }
    	         
    	         curLocation = location;
    	         
    	         try{
    	 			fos.write("GPS Update @ " + 
    	 					DateFormat.getDateTimeInstance().format(new Date())+"\n");
    	 			fos.write("\t Latitude = " + curLocation.getLatitude() + "\n" +
    	 					"\t Longitude = " + curLocation.getLongitude() + "\n");

    	 		} catch (IOException ex) {
    	 			ex.printStackTrace();
    	 		}
    		}
    		public void onProviderDisabled(String provider){}
    		public void onProviderEnabled(String provider){}
    		public void onStatusChanged(String provider, int status, Bundle extras){};
    		
    	};
    	
}
