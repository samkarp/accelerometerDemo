package gov.nasa.arc.geocam.accelerometerDemo;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class GPSUse extends Activity {
	
	private TextView screenDisplay;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.gpsscreen);
        
    	screenDisplay = (TextView) findViewById(R.id.screenDisplayText);
        
    	LocationListener gpsListener = new LocationListener(){
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
    		}
    		public void onProviderDisabled(String provider){}
    		public void onProviderEnabled(String provider){}
    		public void onStatusChanged(String provider, int status, Bundle extras){};
    		
    	};
    	LocationManager locMan;
    	locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    			60000, 1, gpsListener);
    	
    	
    	
    }
    
    public void updateDisplay(Boolean isMoving)
    {
    	if (isMoving)
    		screenDisplay.setText("I'm moving");
    	else
    		screenDisplay.setText("I'm not moving anymore");
    		
    }
}