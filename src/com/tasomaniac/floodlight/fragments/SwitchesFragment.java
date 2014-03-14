package com.tasomaniac.floodlight.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.SwitchDetailActivity;
import com.tasomaniac.floodlight.ItemListFragment.Callbacks;
import com.tasomaniac.floodlight.fragments.DashboardFragment.DashboardDownloader;
import com.tasomaniac.floodlight.model.Switch;
import com.tasomaniac.floodlight.utils.json.SwitchesJSON;

public class SwitchesFragment extends SherlockFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	private ListView mSwitchesList;

	private AsyncTask<String, Void, Boolean> downloader;

	private String ip;
	private List<Switch> switches;

	public SwitchesFragment() {
	}

	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {
		public void onSwitchSelected(String dpid);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onSwitchSelected(String dpid) {
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
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
		}

		getSherlockActivity().getSupportActionBar().setTitle(
				R.string.title_switches);

		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(downloader!=null)
			downloader.cancel(true);
		downloader = null;
		downloader = new SwitchesDownloader();
		downloader.execute(ip);
	}

	public void setupLists() {
		if (mSwitchesList != null) {
			mSwitchesList.setAdapter(new ArrayAdapter<Switch>(getActivity(),
					android.R.layout.simple_list_item_1, android.R.id.text1,
					switches));
			mSwitchesList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					mCallbacks.onSwitchSelected(switches.get(pos).getDpid());
				}

			});
		}

	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {

		if (mSwitchesList != null) {
			mSwitchesList.setChoiceMode(activateOnItemClick
					? ListView.CHOICE_MODE_SINGLE
					: ListView.CHOICE_MODE_NONE);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.switches_layout,
				container, false);

		mSwitchesList = (ListView) mRootView.findViewById(R.id.list);

		return mRootView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_item_refresh:
			
			if(downloader!=null)
				downloader.cancel(true);
			downloader = null;
			downloader = new SwitchesDownloader();
			downloader.execute(ip);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class SwitchesDownloader extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {

			mRootView.findViewById(R.id.list).setVisibility(View.GONE);
			mRootView.findViewById(R.id.progress_bar).setVisibility(
					View.VISIBLE);
		};

		@Override
		protected Boolean doInBackground(String... params) {
			String IP = params[0];
			switches = SwitchesJSON.getSwitches(IP);

			// Check to see that all the controller information is here
			return null;
		}

		protected void onPostExecute(Boolean result) {

			mRootView.findViewById(R.id.progress_bar).setVisibility(
					View.GONE);

			if (switches != null && switches.size() > 0) {

				mRootView.findViewById(R.id.list).setVisibility(
						View.VISIBLE);

				setupLists();

			} else {
				// TODO: give error
				// displayError("Failed to display controller, no controller found!");
			}
		};

	}

}
