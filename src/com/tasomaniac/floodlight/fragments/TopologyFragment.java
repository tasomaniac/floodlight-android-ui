package com.tasomaniac.floodlight.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;
import com.tasomaniac.floodlight.R;

public class TopologyFragment extends SherlockFragment {

	public static final String ARG_ITEM_ID = "item_id";

	private String ip;
	private String mac;

	public TopologyFragment() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			ip = getArguments().getString("ip");
		}
		
		getSherlockActivity().getSupportActionBar().setTitle(R.string.title_topology);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		WebView a = new WebView(getActivity());
		a.getSettings().setJavaScriptEnabled(true);
		a.setWebViewClient(new WebViewClient() {  
			  @Override  
			  public boolean shouldOverrideUrlLoading(WebView view, String url)  
			  {  
			    view.loadUrl(url);
			    return true;
			  }  
			}); 
		a.loadUrl("http://"+ip+":8080/ui/index.html");
		return a;
	}

}
