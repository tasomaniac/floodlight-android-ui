package com.tasomaniac.floodlight.fragments;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.fragments.SwitchesFragment.Callbacks;
import com.tasomaniac.floodlight.model.DeviceSummary;
import com.tasomaniac.floodlight.model.Switch;
import com.tasomaniac.floodlight.utils.ServerUtilities;
import com.tasomaniac.floodlight.utils.json.SwitchesJSON;
import com.viewpagerindicator.TabPageIndicator;

public class DashboardFragment extends SherlockFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	private TextView mHostName;
	private TextView mHealth;
	private TextView mMemory;
	private TextView mModules;
	private ListView mSwitchesList;
	private ListView mHostsList;
	private ViewPager partPager;

	private DashboardDownloader downloader;
	
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {
		public void onDashSwitchSelected(String dpid);
		public void onDashHostSelected(String mac);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onDashSwitchSelected(String dpid) {
		}

		@Override
		public void onDashHostSelected(String mac) {			
		}
	};

	private String ip;
	private List<String> controllerInfo;
	private List<Switch> switches;
	private List<DeviceSummary> hosts;

	public DashboardFragment() {
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mSwitchesList.setItemChecked(mActivatedPosition, false);
		} else {
			mSwitchesList.setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			ip = getArguments().getString("ip");
		}
		
		getSherlockActivity().getSupportActionBar().setTitle(R.string.title_dashboard);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(downloader!=null)
			downloader.cancel(true);
		downloader = null;
		downloader = new DashboardDownloader();
		downloader.execute(ip);
	}
	
	public void setupLists()
	{
		if(mSwitchesList!=null)
		{
			mSwitchesList.setAdapter(new ArrayAdapter<Switch>(
					getActivity(), android.R.layout.simple_list_item_1,
					android.R.id.text1, switches));
			mSwitchesList.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					
					mCallbacks.onDashSwitchSelected(switches.get(pos).getDpid());
				}
				
			});
		}
		
		if(mHostsList!=null)
		{
			mHostsList.setAdapter(new ArrayAdapter<DeviceSummary>(
					getActivity(), android.R.layout.simple_list_item_1,
					android.R.id.text1, hosts));
			
			mHostsList.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					
					mCallbacks.onDashHostSelected(hosts.get(pos).getMacAddress());
				}
				
			});
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.dashboard_layout,
				container, false);

		mHostName = (TextView) mRootView.findViewById(R.id.controller_hostname);
		mHealth = (TextView) mRootView.findViewById(R.id.controller_healthy);
		mMemory = (TextView) mRootView.findViewById(R.id.controller_memory);
		mModules = (TextView) mRootView.findViewById(R.id.controller_modules);
		
		mSwitchesList = (ListView) mRootView.findViewById(R.id.switches_list);
		mHostsList = (ListView) mRootView.findViewById(R.id.hosts_list);
		
		return mRootView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:
			
			if(downloader!=null)
				downloader.cancel(true);
			downloader = null;
			downloader = new DashboardDownloader();
			downloader.execute(ip);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class AlterPagerAdapter extends PagerAdapter {

		ViewPager container;

		private String[] titles = new String[]
				{
				getString(R.string.title_switches), getString(R.string.title_hosts)
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
				mSwitchesList = new ListView(getActivity());
				setupLists();
				this.container.addView(mSwitchesList);
				return mSwitchesList;
			}
			else
			{
				mHostsList = new ListView(getActivity());
				setupLists();
				this.container.addView(mHostsList);
				return mHostsList;
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
	
	public class DashboardDownloader extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {

			mRootView.findViewById(R.id.main_content).setVisibility(View.GONE);					
			mRootView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
		};
		
		@Override
		protected Boolean doInBackground(String... params) {
			String IP = params[0];
			controllerInfo = ServerUtilities.getControllerInfo(IP);
			switches = SwitchesJSON.getSwitches(IP);
			hosts = ServerUtilities.getDeviceSummaries(IP);

			// Check to see that all the controller information is here
			return controllerInfo.size() == 4;
		}

		protected void onPostExecute(Boolean result) {

			if (result) {

				mRootView.findViewById(R.id.main_content).setVisibility(View.VISIBLE);					
				mRootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
				
				mHostName.setText(controllerInfo.get(0));
				mHealth.setText(controllerInfo.get(1));
				mMemory.setText(controllerInfo.get(2));
				mModules.setText(controllerInfo.get(3));

				if(mSwitchesList==null)
				{
					partPager = (ViewPager) mRootView.findViewById(R.id.pager);
					partPager.setAdapter(new AlterPagerAdapter());
					final TabPageIndicator indicator = (TabPageIndicator) mRootView.findViewById(R.id.indicator);
					indicator.setViewPager(partPager);
				}
				else
				{
					setupLists();
				}
				
				
			} else {				
				mRootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
				// TODO: give error
				// displayError("Failed to display controller, no controller found!");
			}
		};

	}

}
