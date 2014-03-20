package com.example.loginntutest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.example.loginntutest.MyAccountManager.AccountType;

public class WifiReceiver extends BroadcastReceiver {

	
	private static WifiReceiver mReceiver;
	public static void registerWifiReceiver(Context context) {
		
		/*
		Toast.makeText(context, "Registering WifiReceiver", Toast.LENGTH_SHORT).show();
		WifiReceiver receiver = new WifiReceiver();
		IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		context.registerReceiver(receiver, filter);
		mReceiver = receiver;
		
		*/
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i("NTU", "WifiReceiver onReceive called");
		
	    if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())){
        	SupplicantState suplState=((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
        	Log.i("NTU", "Suuplicant State Changed Action");
            switch(suplState){
	            case ASSOCIATED:Log.i("SupplicantState", "ASSOCIATED");
	            	break;
		        case ASSOCIATING:Log.i("SupplicantState", "ASSOCIATING");
		            break;
		        case AUTHENTICATING:Log.i("SupplicantState", "Authenticating...");
		            break;
		        case COMPLETED:
		        	Log.i("SupplicantState", "Connected");
		        	
		        	startWifiLoginService(context);
		            break;
		        case DISCONNECTED:Log.i("SupplicantState", "Disconnected");
		            break;
		        case DORMANT:Log.i("SupplicantState", "DORMANT");
		            break;
		        case FOUR_WAY_HANDSHAKE:Log.i("SupplicantState", "FOUR_WAY_HANDSHAKE");
		            break;
		        case GROUP_HANDSHAKE:Log.i("SupplicantState", "GROUP_HANDSHAKE");
		            break;
		        case INACTIVE:Log.i("SupplicantState", "INACTIVE");
		            break;
		        case INTERFACE_DISABLED:Log.i("SupplicantState", "INTERFACE_DISABLED");
		            break;
		        case INVALID:Log.i("SupplicantState", "INVALID");
		            break;
		        case SCANNING:Log.i("SupplicantState", "SCANNING");
		            break;
		        case UNINITIALIZED:Log.i("SupplicantState", "UNINITIALIZED");
		            break;
		        default:Log.i("SupplicantState", "Unknown");
		            break;
            }
	    }
	    else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
	    	startWifiLoginService(context);
	    }
	}
	
	private void startWifiLoginService(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	String ssid = wifiManager.getConnectionInfo().getSSID();
    	Toast.makeText(context, "extra supplicant connected, ssid = " + ssid, Toast.LENGTH_SHORT).show();
    	Log.i("NTU", "extra supplicant connected, ssid = " + ssid);
    
    	if (	ssid.equals(context.getString(R.string.ssid_NTU)) ||
    			ssid.equals(context.getString(R.string.ssid_ntu))) {
    		Toast.makeText(context, "ssid = " + ssid +" loginning", Toast.LENGTH_SHORT).show();
    		Log.i("NTU", "ssid = " + ssid +" loginning");
    		
    		MyAccountManager acctMgr = new MyAccountManager(context);
    		String username = acctMgr.getUsername(AccountType.NTU);
    		String password = acctMgr.getPassword(AccountType.NTU);

    		Intent intentOut = new Intent();
    		intentOut.setClass(context, WifiLoginService.class);
    		intentOut.putExtra(WifiLoginService.SSID, ssid);
    		intentOut.putExtra(MyAccountManager.USERNAME, username);
    		intentOut.putExtra(MyAccountManager.PASSWORD, password);
    		context.startService(intentOut);
    	}
	}

}



