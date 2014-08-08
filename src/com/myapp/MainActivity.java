package com.myapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;


public class MainActivity extends ActionBarActivity {
	String address, ip, device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendMac = (Button) findViewById(R.id.button1);
        sendMac.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				address = info.getMacAddress();
				ip = Formatter.formatIpAddress(info.getIpAddress());
				device = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID); 
				Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
				new SendData().execute();
				System.out.println("called send");
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    private class SendData extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return postData();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			TextView tx= (TextView) findViewById(R.id.textView1);
			tx.setText(result);
		}

		public String postData() {
		    // Create a new HttpClient and Post Header
		    
		    
		    System.out.println("before send");
		    try {

		        
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://infinite-escarpment-6599.herokuapp.com/getIp");

		        // Request parameters and other properties.
		        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		        params.add(new BasicNameValuePair("ip",ip));
		        params.add(new BasicNameValuePair("mac", address));
		        params.add(new BasicNameValuePair("device", device));
		        params.add(new BasicNameValuePair("serial", "serial"));
		        params.add(new BasicNameValuePair("sub", " "));
		        httppost.setEntity(new UrlEncodedFormEntity(params));

		        //Execute and get the response.
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        StringBuilder out = new StringBuilder();
		        if (entity != null) {
		            InputStream instream = entity.getContent();
		            try {
		            	BufferedReader rd = new BufferedReader(new InputStreamReader(instream));
		            	
		            	String line;
		            	while ((line=rd.readLine())!=null){
		            	out.append(line);	
		            	}
		            	
		            } finally {
		                instream.close();
		            }
		        }
		        return out.toString();

		    } catch (ClientProtocolException e) {
		        System.out.println(e.toString());
		    } catch (IOException e) {
		    	System.out.println(e.toString());
		    }
		    System.out.println("return null");
		    return null;
		} 
    	
    }
    
}
