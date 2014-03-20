package com.example.loginntutest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class WifiLoginService extends Service {
 
	public final static String SSID = "ssid";
	public enum NotificationType {
		CONNECTED
	}
	
	
	private static Thread sConnectThread;
	private static WifiLoginService sWifiLoginService;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotifyBuilder;
	private String mSsid;
	
	
	@Override
	public void onCreate() {
		
		if (sWifiLoginService != null) {
			sWifiLoginService.stopSelf();
		}
		if (sConnectThread != null) {
			sConnectThread.interrupt();
		}
		super.onCreate();
		sWifiLoginService = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mSsid = intent.getStringExtra(SSID);
		if (mSsid.equals(getResources().getString(R.string.ssid_ntu)) ||
				mSsid.equals(getResources().getString(R.string.ssid_NTU))) {
			final String username = intent.getStringExtra(MyAccountManager.USERNAME);
			final String password = intent.getStringExtra(MyAccountManager.PASSWORD);
			
		
			sConnectThread = new Thread("ConnectThread"){
				public void run() {
					connect(mSsid, username, password);
					interrupt();
				}
			};
			sConnectThread.start();		
		}
		
		return START_NOT_STICKY;
	}

	
	
	/**
	 * this lets the HttpsUrlConnection object bypass the verifier checking
	 */
	HostnameVerifier hostnameVerifier = new HostnameVerifier() {
	    @Override
	    public boolean verify(String hostname, SSLSession session) {
	        HostnameVerifier hv =
	            HttpsURLConnection.getDefaultHostnameVerifier();
	        //return hv.verify(NTUURL, session);
	        return true;
	    }

	};
	private void connect(String ssid, String username, String password) {
		
		if (isInternetAvailable()) {
			return;
		}

		try {
			String urlResourceName = "url_" + ssid;
			URL url = new URL(getString( getResources().getIdentifier( urlResourceName, "string", getPackageName())));
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setHostnameVerifier(hostnameVerifier);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(5000);
			
			urlConnection.connect();
			
			stringToHandlerMsgToast("hey").sendToTarget();
			
			
			OutputStream os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(ssid, username, password));
			writer.flush();
			writer.close();
			os.close();
			stringToHandlerMsgToast("finish writing").sendToTarget();
			
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine = null;
			String resultData = "";
			while ((inputLine = reader.readLine()) != null) {
				resultData += inputLine + "\n";
			}
			
			//stringToHandlerMsgToast("finish reading, result: " + resultData).sendToTarget();
			showConnectionNotification(NotificationType.CONNECTED, ssid);
			
			reader.close();
			urlConnection.disconnect();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Log.e("ttt", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}	
	}
	
	
	
	private Boolean isInternetAvailable() {
		return false;
	}
	
	
	
	private String getQuery(String ssid, String username, String password) {
		
		//NTU, ntu
		if (ssid.equals(getString(R.string.ssid_ntu)) || 
				ssid.equals(getString(R.string.ssid_NTU))) {
			return "username=" + username + "&password=" + password;
		}
		
		return null;
	}
	
	
	private void showConnectionNotification( NotificationType type, String ssid ) {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int notifyId = 1;
		mNotifyBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_launcher);
		
		switch (type) {
		case CONNECTED:
			mNotifyBuilder.setContentText("Login success: " + ssid);
			break;
		default:
			break;
		}
		
	    mNotificationManager.notify(
	            notifyId,
	            mNotifyBuilder.build());

	}
	
	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		
			if (msg.what == 1) {
				toast(msg.getData().getString("toast"));
			}
			
		}
	};
	
	
	public Message stringToHandlerMsgToast(String text) {
		Message msg = Message.obtain(handler);
		msg.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("toast", text);
		msg.setData(bundle);
		return msg;
	}
	
	public void toast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
