package com.example.loginntutest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginntutest.MyAccountManager.AccountType;

public class MainActivity extends Activity {

	private TextView mTxtResult;
	private Button mBtnConnect;
	private Button mBtnSave;
	private EditText mEditUsername;
	private EditText mEditPassword;
	
	private Context mContext;
	
	public static final String NTUURL = "https://wl122.cc.ntu.edu.tw/auth/loginnw.html" ;
	private MyAccountManager mAccountManager;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mContext = this;
		findViews();
		
		
		mAccountManager = new MyAccountManager(this);
		final String username = mAccountManager.getUsername(AccountType.NTU);
		final String password = mAccountManager.getPassword(AccountType.NTU);
		mEditUsername.setText(username);
		mEditPassword.setText(password);
		
		WifiReceiver.registerWifiReceiver(this);
		
		
		mBtnConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				

        		Intent intentOut = new Intent();
        		intentOut.setClass(mContext, WifiLoginService.class);
        		intentOut.putExtra(WifiLoginService.SSID, getString(R.string.ssid_ntu));
        		intentOut.putExtra(MyAccountManager.USERNAME, username);
        		intentOut.putExtra(MyAccountManager.PASSWORD, password);
        		mContext.startService(intentOut);
				
				
				
				/*toast("click");
				
				Thread thread = new Thread() {
					@Override
					public void run() {
						connect();
					}
				};
				thread.start();
				//connect();*/
			}
		});
		
		mBtnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mAccountManager.saveAccount(mEditUsername.getText().toString(), mEditPassword.getText().toString(), AccountType.NTU);
			}
		});
	
	
	}
	
	
	private void findViews() {
		mEditUsername = (EditText) findViewById(R.id.editName);
		mEditPassword = (EditText) findViewById(R.id.editPwd);
		mTxtResult = (TextView) findViewById(R.id.txtResult);
		mBtnConnect = (Button) findViewById(R.id.btnConnect);
		mBtnSave = (Button) findViewById(R.id.btnSave);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * this lets the HttpsUrlConnection object bypass the verifier checking
	 */
	
	/*
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
			
			
			//InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			//try {
			//	if (!url.getHost().equals(urlConnection.getURL().getHost())) {
			//		// we were redirected! Kick the user out to the browser to sign on?
			//		toast("!url.getHost().equals...");
			//	}
			//} finally {
			//	urlConnection.disconnect();
			//}
			
			
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
			
			//if (!resultData.equals("")) {
			//		txtResult.setText(resultData);
			//}
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
	
	*/
	
}
