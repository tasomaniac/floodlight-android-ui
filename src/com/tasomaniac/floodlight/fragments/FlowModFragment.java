package com.tasomaniac.floodlight.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.model.Action;
import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.utils.JSONException;
import com.tasomaniac.floodlight.utils.flowmanager.FlowManagerPusher;

public class FlowModFragment extends SherlockFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	EditText mFormName;
	EditText mFormPriority;
	EditText mFormCookie;
	EditText mFormIdleTimeout;
	EditText mFormHardTimeout;
	EditText mFormOutport;
	EditText mFormActionPort;
	private Spinner mActionsSpinner;

	private AsyncTask<String, Void, String> downloader;

	private String ip;
	private String dpid;
	private Flow flow;
	
	private boolean tek;
	

	public FlowModFragment() {
	}
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {
		public void onFlowPushed(String dpid);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onFlowPushed(String dpid) {
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//			setActivatedPosition(savedInstanceState
//					.getInt(STATE_ACTIVATED_POSITION));
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
//			dpid = getArguments().getString("dpid");
			flow = (Flow) getArguments().getSerializable("flow");
			tek= getArguments().getBoolean("tek");
			
//			flow.set
		}
		
		getSherlockActivity().getSupportActionBar().setTitle(R.string.title_flows);
		setHasOptionsMenu(true);
		
		if(tek)
		{
		// Inflate a "Done/Discard" custom action bar view.
        LayoutInflater inflater = (LayoutInflater) getSherlockActivity().getSupportActionBar().getThemedContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.actionbar_custom_view_done_discard, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	
                    	setUpDownloader();
                    }	
                });
        customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Discard"
                        //finish();
                    	mFormName.setText("");
                    	mFormPriority.setText("");
                    	mFormCookie.setText("");
                    	mFormIdleTimeout.setText("");
                    	mFormHardTimeout.setText("");
                    	mFormOutport.setText("");
                    	mFormActionPort.setText("");
                    	mActionsSpinner.setSelection(0);
                    	//TODO: clear form
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
		}
           
	}
	
	public void setUpDownloader()
	{
		//TODO: action secilmemeisse uyari belki
    	downloader = new AsyncTask<String, Void, String>() {

			protected void onPreExecute() {
			
				flow.setName(mFormName.getText().toString());
				flow.setPriority(mFormPriority.getText().toString());
				flow.setCookie(mFormCookie.getText().toString());
				flow.setIdleTimeOut(mFormIdleTimeout.getText().toString());
				flow.setHardTimeOut(mFormHardTimeout.getText().toString());
				flow.setOutPort(mFormOutport.getText().toString());
				
				flow.getActions().get(0).setValue(mFormActionPort.getText().toString());
			};
			
			@Override
			protected String doInBackground(String... params) {
				
				try {
					return FlowManagerPusher.push(ip, flow);
				} catch (IOException e) {
				} catch (JSONException e) {
				}
				// Check to see that all the controller information is here
				return null;
			}

			protected void onPostExecute(String result) {
			
				if(!TextUtils.isEmpty(result))
				{
					AlertDialog.Builder b = new  AlertDialog.Builder(getActivity());
					b.setTitle(R.string.res_title)
					.setMessage(result)
					.setPositiveButton(R.string.ok, 
							new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {

									mCallbacks.onFlowPushed(dpid);
								}
							});
					b.create().show();
				}
				

			};

		}.execute(ip);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mFormName.setText(flow.getName());
		mFormPriority.setText(flow.getPriority());
		mFormCookie.setText(flow.getCookie());
		mFormIdleTimeout.setText(flow.getIdleTimeOut());
		mFormHardTimeout.setText(flow.getHardTimeOut());
		mFormOutport.setText(flow.getOutPort());
		
		if(flow.getActions()!=null && flow.getActions().size()>0)
		{
			mFormActionPort.setText(flow.getActions().get(0).getValue());
		
			int pos = 0;
			String type = flow.getActions().get(0).getType();
			String[] type_arr = getResources().getStringArray(R.array.actions_array);
			for(int i=0; i<type_arr.length; i++)
			{
				if(type_arr[i].equals(type))
				{
					pos = i;
					break;
				}
			}
			mActionsSpinner.setSelection(pos);
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.sign_in_form,
				container, false);
		
		mFormName = (EditText) mRootView.findViewById(R.id.form_name);
		mFormPriority = (EditText) mRootView.findViewById(R.id.form_priority);
		mFormCookie = (EditText) mRootView.findViewById(R.id.form_cookie);
		mFormIdleTimeout = (EditText) mRootView.findViewById(R.id.form_idle_timeout);
		mFormHardTimeout = (EditText) mRootView.findViewById(R.id.form_hard_timeout);
		mFormOutport = (EditText) mRootView.findViewById(R.id.form_outport);
		mFormActionPort = (EditText) mRootView.findViewById(R.id.form_action_port);
		
		mActionsSpinner = (Spinner) mRootView.findViewById(R.id.form_actions_spinner);
		
		mActionsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				
				//TODO: bircok action a olanak sagla
				List<Action> a = new ArrayList<Action>();
				a.add(new Action(getResources().getStringArray(R.array.actions_array)[pos]));
				flow.setActions(a);
				
				if(pos==0 || pos ==3)
					mFormActionPort.setVisibility(View.GONE);
				else
					mFormActionPort.setVisibility(View.VISIBLE);
						
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return mRootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);
		if(!tek)
		{
			menu.add(Menu.NONE, 1222, Menu.NONE, "PUSH")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		
		if(item.getItemId() == 1222)
		{
			setUpDownloader();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
