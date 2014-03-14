package com.tasomaniac.floodlight.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.model.Port;
import com.tasomaniac.floodlight.model.Switch;
import com.tasomaniac.floodlight.utils.json.SwitchesJSON;
import com.viewpagerindicator.TabPageIndicator;

public class SwitchDetailFragment extends SherlockFragment {

	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	private TextView mVendor;
	private TextView mRevision;
	private TextView mSerial;
	private TextView mVersion;
	private ListView mPortsList;
	private ListView mFlowsList;
	private ViewPager partPager;

	private AsyncTask<String, Void, Boolean> downloader;

	private String ip;
	private String dpid;
	private Switch mSw;

	public SwitchDetailFragment() {
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
			ip = getArguments().getString("ip");
			dpid = getArguments().getString("dpid");
		}
		
		getSherlockActivity().getSupportActionBar().setTitle("DPID: " + dpid);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		downloader = new AsyncTask<String, Void, Boolean>() {

			protected void onPreExecute() {

				mRootView.findViewById(R.id.main_content).setVisibility(View.GONE);					
				mRootView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
			};
			
			@Override
			protected Boolean doInBackground(String... params) {
				
				mSw = SwitchesJSON.getSwitch(ip, dpid);
				
				// Check to see that all the controller information is here
				return null;
			}

			protected void onPostExecute(Boolean result) {

				mRootView.findViewById(R.id.main_content).setVisibility(View.VISIBLE);					
				mRootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
				
				mVendor.setText(mSw.getManufacturerDescription());
				mRevision.setText(mSw.getHardwareDescription());
				mSerial.setText(mSw.getSerialNumber());
				mVersion.setText(mSw.getSoftwareDescription());

				if(mPortsList == null)
				{
					partPager = (ViewPager) mRootView.findViewById(R.id.pager);
					partPager.setAdapter(new AlterPagerAdapter());
					final TabPageIndicator indicator = (TabPageIndicator) mRootView.findViewById(R.id.indicator);
					indicator.setViewPager(partPager);	
				}
				else
					setupLists();
			};

		}.execute(ip);
	}
	
	public void setupLists() {
		

		if(mPortsList!=null){
			mPortsList.setAdapter(new ArrayAdapter<Port>(
					getActivity(), android.R.layout.simple_list_item_1,
					android.R.id.text1, mSw.getPorts()));
		}
		
		if(mFlowsList!=null) {
			mFlowsList.setAdapter(new ArrayAdapter<Flow>(
					getActivity(), android.R.layout.simple_list_item_1,
					android.R.id.text1, mSw.getFlows()));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_switch_detail,
				container, false);

		mVendor = (TextView) mRootView.findViewById(R.id.switch_vendor);
		mRevision = (TextView) mRootView.findViewById(R.id.switch_revision);
		mSerial = (TextView) mRootView.findViewById(R.id.switch_serial);
		mVersion = (TextView) mRootView.findViewById(R.id.switch_version);
		
		mPortsList = (ListView) mRootView.findViewById(R.id.switch_ports_list);
		mFlowsList = (ListView) mRootView.findViewById(R.id.switch_flows_list);
		
		return mRootView;
	}
	
	private class AlterPagerAdapter extends PagerAdapter {

		ViewPager container;

		private String[] titles = new String[]
				{
				getString(R.string.title_ports), getString(R.string.title_flows)
				};

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View)object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position % titles.length];
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			this.container = (ViewPager) container;
			
			if(position==0)
			{
				mPortsList = new ListView(getActivity());
				setupLists();
				this.container.addView(mPortsList);
				return mPortsList;
			}
			else
			{
				mFlowsList = new ListView(getActivity());
				setupLists();
				this.container.addView(mFlowsList);
				return mFlowsList;
			}

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {}
		@Override
		public Parcelable saveState() {	return null;}
		@Override
		public void startUpdate(View container) {}
	}

}
