package gov.nasa.arc.geocam.accelerometerDemo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class GPSUse extends Activity {
	
	private TextView screenDisplay;
	private Button startButton;
	private Button stopButton;
	private String path = Environment.getExternalStorageDirectory().toString();
	private String FILENAME = "gpsDemo.log";
	private BufferedWriter fos;
	private LocationManager locMan;
	private LocationListener gpsListener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gpsscreen);
        
        startButton = (Button) findViewById(R.id.startButton);
    	stopButton = (Button) findViewById(R.id.stopButton);
        
    	startButton.setOnClickListener(startListener);
    	stopButton.setOnClickListener(stopListener);
    	stopButton.setEnabled(false);
        
    	screenDisplay = (TextView) findViewById(R.id.screenDisplayText);
    	
    }
    public void startGPS(){    	
    	try {
        	fos = new BufferedWriter(new FileWriter(path + "/" + FILENAME));
        	fos.write("Created the GPS LOGGER @ " + 
            		DateFormat.getDateTimeInstance().format(new Date()) + "\n");
        	fos.flush();
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    	startButton.setEnabled(false);
    	stopButton.setEnabled(true);
    	
    	startService(new Intent(this, BatteryLevelService.class));
        
    	gpsListener = new LocationListener(){
    		Location curLocation;
    	    
    		public void onLocationChanged(Location location)
    		{
    			if(curLocation == null)
    	         {
    	            curLocation = location;
    	            updateDisplay(true);
    	         }
    	         
    	         if(curLocation.getLatitude() == location.getLatitude() &&
    	               curLocation.getLongitude() == location.getLongitude())
    	        	 updateDisplay(false);
    	         else{
    	         	 updateDisplay(true);
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
    	
    	locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    			60000, 1, gpsListener);  	
    }
    
    public void stopGPS()
    {
    	stopService(new Intent(this, BatteryLevelService.class));
    	stopButton.setEnabled(false);
    	startButton.setEnabled(true);
    	
    	locMan.removeUpdates(gpsListener);
    	try{
			fos.write("Stopping the GPS LOGGER @ " + 
					DateFormat.getDateTimeInstance().format(new Date())+"\n");
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}    	
    }
    
    private OnClickListener startListener = new OnClickListener(){
    	public void onClick(View v){
    		startGPS();
    	}
    };
    
    private OnClickListener stopListener = new OnClickListener(){
    	public void onClick(View v){
    		stopGPS();    	
    	}
    };
    
    public void updateDisplay(Boolean isMoving)
    {
    	if (isMoving)
    		screenDisplay.setText("I'm moving");
    	else
    		screenDisplay.setText("I'm not moving anymore");
    		
    }
}