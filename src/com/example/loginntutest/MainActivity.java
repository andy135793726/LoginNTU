package com.example.loginntutest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.BufferedHttpEntity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.layout;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView txtResult;
	private Button btnConnect;
	
	public static final String NTUURL = "https://wl122.cc.ntu.edu.tw/auth/loginnw.html" ;

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
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		
			if (msg.what == 1) {
				toast(msg.getData().getString("toast"));
			}
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtResult = (TextView) findViewById(R.id.txtResult);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		
		btnConnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toast("click");
				
				Thread thread = new Thread() {
					@Override
					public void run() {
						connect();
					}
				};
				thread.start();
				//connect();
			}
		});
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void connect() {
		

		try {
			URL url = new URL(NTUURL);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setHostnameVerifier(hostnameVerifier);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			

			//5toast("start connecting");
			//urlConnection.connect();

			//toast("connected");
			
			/*
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			try {
				if (!url.getHost().equals(urlConnection.getURL().getHost())) {
					// we were redirected! Kick the user out to the browser to sign on?
					toast("!url.getHost().equals...");
				}
			} finally {
				urlConnection.disconnect();
			}
			*/
			
			stringToHandlerMsgToast("hey").sendToTarget();
			
			
			OutputStream os = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery());
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
			
			stringToHandlerMsgToast("finish reading, result: " + resultData).sendToTarget();
			
			/*if (!resultData.equals("")) {
					txtResult.setText(resultData);
			}*/
			reader.close();
			urlConnection.disconnect();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			Log.e("ttt", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
		
		
	}
	
	
	
	private String getQuery() {
		
		EditText editName = (EditText) findViewById(R.id.editName);
		EditText editPwd = (EditText) findViewById(R.id.editPwd); 
		
		String username = editName.getText().toString();
		String pwd = editPwd.getText().toString();
		
		return "username=" + username + "&password=" + pwd;
	}

	
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
}
