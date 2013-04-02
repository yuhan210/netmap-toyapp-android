package csail.mit.edu;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class GPS{
	private LocationManager locationManager;
    private LocationListener locationListener;
    private int scanIntervalInMs;
    private double lat = -1;
    private double lon = -1;
    private double alt = -1;
    private float speed = -1;
    private float bearing = -1;
    private float accuracy = -1;
    private String provider = "";
    public GPS()
	{
		this.locationManager = (LocationManager)Global.context.getSystemService(Context.LOCATION_SERVICE);
		this.locationListener = new GPSLocationListener();		
		this.scanIntervalInMs = 1000;		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, scanIntervalInMs, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, scanIntervalInMs, 0, locationListener);
	}
	private class GPSLocationListener implements LocationListener 
	{
	     public void onLocationChanged(Location loc) 
	     {		
	         lat = loc.getLatitude();
	         lon = loc.getLongitude();
	         alt = loc.getAltitude();
	         speed = loc.getSpeed();
	         bearing = loc.getBearing();
	         accuracy = loc.getAccuracy();
	         provider = loc.getProvider();
	    	 System.out.println("GPS received: " + loc.getLatitude() + Global.PayloadFieldDelimiter + loc.getLongitude() + Global.PayloadFieldDelimiter +  loc.getAltitude() + Global.PayloadFieldDelimiter + loc.getSpeed() + Global.PayloadFieldDelimiter +  loc.getBearing() + Global.PayloadFieldDelimiter + loc.getAccuracy());  	
	     }
	           
	     public void onProviderDisabled(String provider) 
	     {}

	     public void onProviderEnabled(String provider) 
	     {}

	     public void onStatusChanged(String provider, int status, Bundle extras) 
	     {}
	 }
	 public String toString(){
		 
		return lat+","+lon+","+alt+","+speed+","+bearing+","+accuracy+","+provider; 
	 }
     public void stop()
	 {	
		locationManager.removeUpdates(locationListener);		
	 }
		
		
}
