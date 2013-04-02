package csail.mit.edu;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class GSM{
	private TelephonyManager telephonyManager;
	private String record = "";
	private int connectedTowerRSSI = 0;
	private int connectedTowerBER = 0;
	private Timer timer;
	 public GSM(){
		 telephonyManager = (TelephonyManager)Global.context.getSystemService(Context.TELEPHONY_SERVICE);
		
		 timer = new Timer();
		 timer.schedule(new GSMTask(), 0, 1000);
		 telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_LOCATION);
		
		
	 }
	 public String toString(){
		 return record;
		 
	 }
	 class GSMTask extends TimerTask 
		{		
			public void run() 
		    {
				 record = "";
				 record = getConnectedCell();
				 record += getNeighboringCellInfo();
		    }
		}
	 private String getNeighboringCellInfo(){
		 List<NeighboringCellInfo> neighbors = telephonyManager.getNeighboringCellInfo();
		 if (neighbors != null)	{
			    String str = "";
			    int valid_cellnum = 0;
			 	for (int i = 0; i < neighbors.size(); i++){
					
					if (neighbors.get(i).getCid() != NeighboringCellInfo.UNKNOWN_CID){
						//nCell.cell = (neighbors.get(i).getCid()&0xFFFF)+"";
						//nCell.lac = ((neighbors.get(i).getCid()&0xFFFF0000)>>16)+"";					
						int type = neighbors.get(i).getNetworkType();
						int cid = (neighbors.get(i).getCid()&0xFFFF);
						int lac = neighbors.get(i).getLac();
						int rssi = neighbors.get(i).getRssi();
						int psc = neighbors.get(i).getPsc();
						++valid_cellnum;
						
						str += type + Global.PayloadFieldDelimiter + cid + Global.PayloadFieldDelimiter + lac + Global.PayloadFieldDelimiter + rssi + Global.PayloadFieldDelimiter + psc + Global.PayloadFieldDelimiter;
						
					}
					return valid_cellnum + Global.PayloadFieldDelimiter + str;
				}
			}
		 return "";
	 }
	 private String getConnectedCell(){

		  GsmCellLocation gloc = (GsmCellLocation)telephonyManager.getCellLocation();	
		   String connected_cell_info = "";
		   if (gloc != null && gloc.getCid() != -1){
				int type = telephonyManager.getNetworkType();
				int cid = gloc.getCid();
				int lac = gloc.getLac();
				int rssi = connectedTowerRSSI;
				int ber = connectedTowerBER;
				connected_cell_info = type + Global.PayloadFieldDelimiter + cid + Global.PayloadFieldDelimiter + lac + Global.PayloadFieldDelimiter + rssi + Global.PayloadFieldDelimiter + ber + Global.PayloadFieldDelimiter;
			}
		    System.out.println(connected_cell_info);
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);				
			return connected_cell_info;
	}
	 
	 
	private PhoneStateListener phoneStateListener = new PhoneStateListener()
	{
		public void  onSignalStrengthsChanged(SignalStrength ss)
		{
			connectedTowerRSSI = ss.getGsmSignalStrength();
			connectedTowerBER = ss.getGsmBitErrorRate();
		}
		
		public void  onCellLocationChanged  (CellLocation location)
		{
		}
	};
	
	
	public void stop()
	 {	
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	 }
			

}