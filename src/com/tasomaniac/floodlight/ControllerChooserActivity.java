package com.tasomaniac.floodlight;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ControllerChooserActivity extends SherlockFragmentActivity {

	private ControllerCheckTask downloader;
	private ProgressDialog pd;
	private String ip, port;

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (downloader != null
				&& downloader.getStatus() == AsyncTask.Status.RUNNING) {
			downloader.cancel(true);
			downloader = null;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_chooser);
        
        getSupportActionBar().hide();
        
        Button login = (Button) findViewById(R.id.sign_in_button);
        
        login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(downloader!=null)
					downloader.cancel(true);
				downloader = null;
				downloader = new ControllerCheckTask();
				downloader.execute();
				
			}
		});
        
        login.performClick();
    }
    
    public class ControllerCheckTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if(pd==null)
				pd = ProgressDialog.show(ControllerChooserActivity.this, null, getString(R.string.wait), true, false);
			pd.show();
			
	        final EditText ip_text = (EditText) findViewById(R.id.sign_in_ip);
	        final EditText port_text = (EditText) findViewById(R.id.sign_in_port);
	        
			ip = //"http://openflow.marist.edu/proxy?url=";//
					"192.168.1.65"; //ip.getText().toString());
			port = port_text.getText().toString();
		}
		@Override
		protected Integer doInBackground(String... params) {
			int s= -1;
			

			int timeOut = 1000;
			try {
				if (InetAddress.getByName(ip).isReachable(timeOut)) {
						s=0;
				} else {
					s=3;
				}
			} catch (UnknownHostException e1) {
				s=1;
			} catch (IOException e1) {
				s=2;
			}
			return 0;//s;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			if(pd!=null && pd.isShowing())
			{	
				pd.cancel();
				pd=null;
			}
			
			switch (result) {
			case 0:
				Intent i = new Intent(ControllerChooserActivity.this, MainActivity.class);
				i.putExtra("ip", ip);
				i.putExtra("port", port);
				startActivity(i);
				finish();
				break;

				//TODO: give the errors
			default:
				AlertDialog.Builder b = new  AlertDialog.Builder(ControllerChooserActivity.this);
				b.setTitle(R.string.error)
				.setMessage(R.string.controller_error)
				.setPositiveButton(R.string.again, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloader.cancel(true);
						downloader = null;
						downloader = new ControllerCheckTask();
						downloader.execute();
						
					}
				})
				.setNegativeButton(R.string.cancel, null);
				b.create().show();
				break;
			}
		}
		
	}
}
