package csail.mit.edu;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


public class Bluetooth extends Service{
	private static BluetoothAdapter bluetoothAdapter;
    private static BroadcastReceiver receiver;
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 
		 System.out.println("Bluetooth OnStartCommand()");
		 bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 if (!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
		 //Global.context.registerReceiver(receiver , new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		 IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	     Global.context.registerReceiver(receiver, filter);

	     filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	     Global.context.registerReceiver(receiver, filter);
	        
	     
	     
		 bluetoothAdapter.startDiscovery();
		 stopSelf();

		 
		 return super.onStartCommand(intent, flags, startId);
	 }
	  class BTReceiver extends BroadcastReceiver 
	  {	   
			@Override
			public void onReceive(Context c, Intent intent) {
				String action = intent.getAction();
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
	            {       
	            	
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                String name = device.getName();
	                String add = device.getAddress();
	                int state = device.getBondState();
	                int dev_class = device.getBluetoothClass() == null ? -1 : device.getBluetoothClass().getDeviceClass();
	                int dev_maj_class = device.getBluetoothClass() == null ? -1 : device.getBluetoothClass().getMajorDeviceClass();
	                System.out.println(name + Global.PayloadFieldDelimiter + add + Global.PayloadFieldDelimiter + state + Global.PayloadFieldDelimiter + dev_class + Global.PayloadFieldDelimiter + dev_maj_class);
	            } 
	            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
	            {     	
	            	
	            	bluetoothAdapter.cancelDiscovery();
	            	stopSelf();
	            }
	          
			}
		}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onDestroy(){
		System.out.println("Bluetooth OnDestroy()");
		
		super.onDestroy();  
	}
	

}
