package csail.mit.edu;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;


public class WiFi {
	public static WifiManager wifiManager;
	private WifiLock wifiLock;
	private WifiReceiver wifiReceiver = new WifiReceiver();
	private Timer timer;
	private List<ScanResult> scanResults;
	private String record = "";
	
	public WiFi(){
		wifiManager = (WifiManager)Global.context.getSystemService(Context.WIFI_SERVICE);
         
         if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
         
         wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "WiFiSilentScanLock");
         wifiLock.acquire();
         
         Global.context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
         this.timer = new Timer();
		 TimerTask ts = new TimerTask() { public void run() { wifiManager.startScan(); } };					
		 timer.schedule(ts, 0, 1000);
      
	}
	public String toString(){
		
		return record;
	}
	
	class WifiReceiver extends BroadcastReceiver {
	  	 public void onReceive(Context c, Intent intent) 
		    {
	  		record = "";	    	
	    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	    	List<ScanResult> scanResults = wifiManager.getScanResults();
	    	record =  wifiInfo.getSSID() + Global.PayloadFieldDelimiter + 
     		wifiInfo.getBSSID() + Global.PayloadFieldDelimiter + 
     		wifiInfo.getRssi() + Global.PayloadFieldDelimiter + wifiInfo.getLinkSpeed() + Global.PayloadFieldDelimiter + scanResults.size() + Global.PayloadFieldDelimiter;

	  		
	  		System.out.println("wifi received + " + scanResults.size());
	  		
	  		
	  		for (int i = 0; i < scanResults.size(); i++)
	    	{
	  			if (i == scanResults.size() -1){
	  				record += scanResults.get(i).SSID + Global.PayloadFieldDelimiter + scanResults.get(i).BSSID + Global.PayloadFieldDelimiter + scanResults.get(i).frequency + Global.PayloadFieldDelimiter + scanResults.get(i).level;
	  		    	
	  			}else{
	  				record += scanResults.get(i).SSID + Global.PayloadFieldDelimiter + scanResults.get(i).BSSID + Global.PayloadFieldDelimiter + scanResults.get(i).frequency + Global.PayloadFieldDelimiter + scanResults.get(i).level + Global.PayloadFieldDelimiter;
	  		    	
	  			}
	  		}
	  		System.out.println(record);
	  		
	    	 
	  }

	}
	 public void stop()
	 {	
		 Global.context.unregisterReceiver(wifiReceiver);
		 wifiLock.release();
		 timer.cancel();
	 }
			
}