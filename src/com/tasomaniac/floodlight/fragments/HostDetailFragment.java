package com.tasomaniac.floodlight.fragments;

import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.model.DeviceSummary;

public class HostDetailFragment extends SherlockFragment {

	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	private TextView mMac;
	private TextView mIP;
	private TextView mLastSeen;
	private TextView mSwitchPort;

	private AsyncTask<String, Void, Boolean> downloader;

	private String ip;
	private String mac;
	private DeviceSummary mDevice;

	public HostDetailFragment() {
	}

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

		if (getArguments() != null) {
			mDevice = new DeviceSummary(getArguments().getString("mac"));
			ip = getArguments().getString("ip");
			mDevice.setIpv4(ip);
			mDevice.setAttachedSwitch(getArguments().getString("switch"));
			mDevice.setLastSeen(new Date(getArguments().getLong("last_seen")));
			mDevice.setSwitchPort(getArguments().getInt("port"));
		}
		
		getSherlockActivity().getSupportActionBar().setTitle("Host: " + mac);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_host_detail,
				container, false);

		mMac = (TextView) mRootView.findViewById(R.id.host_mac);
		mIP = (TextView) mRootView.findViewById(R.id.host_ip);
		mLastSeen = (TextView) mRootView.findViewById(R.id.host_last_seen);
		mSwitchPort = (TextView) mRootView.findViewById(R.id.host_attachments);

		mMac.setText(mDevice.getMacAddress());
		mIP.setText(mDevice.getIpv4());
		mLastSeen.setText(mDevice.getLastSeen().toString());
		mSwitchPort.setText(mDevice.getAttachedSwitch() + " - " + mDevice.getSwitchPort());

		
		return mRootView;
	}

}
