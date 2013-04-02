package csail.mit.edu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NetMapActivity extends Activity {
   
	public static final String TAG= "NetMapActivity";
	private Button start_button;
	private Messenger mService = null;
	private boolean mIsBound;
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Global.setContext(this);
		
        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				start_scanning();
			}
		});
        
        doStartService();
        doBindService();
        
    }
    
    public void start_scanning(){
         
    	if(mService != null){
    		try{
    			Message msg = Message.obtain(null, NetMapService.MSG_STARTSCAN, true);	
    			msg.replyTo = mMessenger;
    			mService.send(msg);
    		}catch(RemoteException e){
    			throw new RuntimeException("fail to send start scanning msg");
    		}
    	}else{
    		Log.e(NetMapActivity.TAG,"fail to connect to Service");
    	}
    	
    	/** WiFi ***/
   	 	//Intent WiFiIntent = new Intent(this, WiFi.class);
   	 	//startService(WiFiIntent);
   	 	
   	 	/** GSM **/
   	 	//Intent GSMIntent = new Intent(this, GSM.class);
   	 	//startService(GSMIntent);
   	 	
   	 	
		/** Bluetooth **/ 
		//Intent BTIntent = new Intent(this, Bluetooth.class);
		//startService(BTIntent);
		
    }
    public void onStart(){
    	super.onStart();	
    }
    
    /** IncomingHandler handles the messages coming from the XTRATAPPService, not being used right now **/
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
       }
    }
    
    /** ServiceConnection sets up the connection to the service **/
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);     
            Log.i(NetMapActivity.TAG, "Service connected.");
            
        }
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.i(NetMapActivity.TAG, "Service disconnected.");
        }
    };
    
    private void doStartService(){ 	
    	ComponentName n = startService(new Intent(NetMapActivity.this, NetMapService.class));
    	if(n != null){
    		System.out.println("doStartService(): service started:" + n);
    	}else{
    		System.out.println("doStartService(): null service returned");
    	}
    }
   private void doStopService(){
	  
	   boolean stopped = stopService(new Intent(NetMapActivity.this,NetMapService.class));   
	   System.out.println("Service stopped:" + stopped);
   }
   
    void doBindService() {
       bindService(new Intent(this, NetMapService.class), mConnection, Context.BIND_AUTO_CREATE);
       
       mIsBound = true;
       Log.i(NetMapActivity.TAG,"binding to service...");
       
        
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                /**
            	try {
                    Message msg = Message.obtain(null, MyService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }**/
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            System.out.println("Service unbinding.");
        }
    }
 public void onDestroy(){
    	
    	/** Stop all the threads and services when the app is destroyed**/
    	super.onDestroy();
    	doUnbindService();
    	doStopService();
    	System.out.println("Activity destroyed.");
    }
    
}