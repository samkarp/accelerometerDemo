package gov.nasa.arc.geocam.accelerometerDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class accelerometerDemo extends Activity {
	
	private Button gpsButton;
	private Button accelerometerButton;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	gpsButton = (Button) findViewById(R.id.GPSButton);
    	accelerometerButton = (Button) findViewById(R.id.AccelerometerButton);
        
    	gpsButton.setOnClickListener(accelerometerListener);
        accelerometerButton.setOnClickListener(gpsListener);
    }
    
    private OnClickListener accelerometerListener = new OnClickListener(){
    	public void onClick(View v){
    		Intent gpsIntent = new Intent(v.getContext(), GPSUse.class);
    		startActivity(gpsIntent);
    	}
    };
    
    private OnClickListener gpsListener = new OnClickListener(){
    	public void onClick(View v){
    		Intent accelerometerIntent = new Intent(v.getContext(), AccelerometerUse.class);
    		startActivity(accelerometerIntent);
    		
    	}
    };
}