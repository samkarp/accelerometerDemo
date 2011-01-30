package gov.nasa.arc.geocam.accelerometerDemo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;

public class BatteryLevelService extends Service {
	private BroadcastReceiver batteryLevelReceiver;
	private String path = Environment.getExternalStorageDirectory().toString();
	private String FILENAME = "samDemo.log";
	private BufferedWriter fos;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
    /**
     * Called when the current activity is first created.
     */
    public void onCreate() {
        try {
        	fos = new BufferedWriter(new FileWriter(path + "/" + FILENAME));
        	fos.write("Created the Battery LOGGER @ " + 
            		DateFormat.getDateTimeInstance().format(new Date()) + "\n");
    	} catch (IOException ex){
    		ex.printStackTrace();
    	}
    }
	
	@Override
	public void onDestroy(){
		try{
			fos.write("Stopping the Battery LOGGER @ " + 
					DateFormat.getDateTimeInstance().format(new Date()) + "\n");
			fos.flush();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
        		
		//unregisterReceiver(batteryLevelReceiver);
		stopSelf();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID){

		batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                
                try {
                	fos.write("Battery Level Remaining: " + level + "% \n");
            	} catch (IOException ex){
            		ex.printStackTrace();
            	}
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        
        return START_STICKY;
	}
	
}
