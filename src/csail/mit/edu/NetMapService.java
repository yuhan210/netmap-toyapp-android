package csail.mit.edu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class NetMapService extends Service{
	
	private final Messenger mMessenger = new Messenger(new IncomingHandler()); 
	static final String DataDir = "/sdcard/NetMapData/";
	static String CurDataDir = "/sdcard/NetMapData";
	static final int MSG_STARTSCAN = 1;
	private BufferedWriter writer;
	
	private WakeLock wl;
	private PowerManager pm;
	
	public GPS gps;
	public WiFi wifi;
	public Bluetooth bt;
	public GSM gsm;
    @Override
    public void onCreate() 
    {   
    
    	/** Acquire power lock **/
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");        
        String currentTime = dateFormat.format(new Date());
        CurDataDir = CurDataDir+"/"+currentTime+"/";
        String filename = CurDataDir + "/" + "scan_results";
    	new File(DataDir).mkdir();    
        new File(CurDataDir).mkdir();
        try { this.writer = new BufferedWriter(new FileWriter(new File(filename)), 10000); }
	    catch (IOException e) { throw new RuntimeException(e); }
		
        
        gps = new GPS();
        wifi = new WiFi();
        gsm = new GSM();
        //bt = new Bluetooth();
        
        
    }
    public void write_sensor_info(){
		 String record = "GPS:" + gps + Global.FieldDelimiter+ "WiFi:" + wifi +  Global.FieldDelimiter + "GSM:" + gsm + "\n";
		 Toast.makeText(this, record, Toast.LENGTH_LONG).show();
		 try {
			writer.write(record);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	class IncomingHandler extends Handler{
		 public void handleMessage(Message msg) {
	           switch(msg.what){
	               case MSG_STARTSCAN:
	            	   write_sensor_info();
	            		
	               break;	
	               
	           	    default: 
	            	    super.handleMessage(msg);       	   
	           }
			  
		 }
		
		
	}
    
	@Override
	public IBinder onBind(Intent intent) {
		 return mMessenger.getBinder();
	}
	
	private void stopSensors(){
		wifi.stop();
		gsm.stop();
		gps.stop();
	}
	public void onDestroy() 
    {
		
		stopSensors();
		wl.release();
		try {
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.runFinalizersOnExit(true);
	    System.exit(0);
    }

	@Override
    public boolean onUnbind(Intent intent)
    {
    	Toast.makeText(this, "Service unbound", Toast.LENGTH_SHORT).show();
    	return false;
    }
}
