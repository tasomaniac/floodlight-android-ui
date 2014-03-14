package com.tasomaniac.floodlight.fragments;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.R;
import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.utils.JSONException;
import com.tasomaniac.floodlight.utils.ServerUtilities;
import com.tasomaniac.floodlight.utils.flowmanager.FlowManagerPusher;


public class FlowsFragment extends SherlockFragment {

	public static final String ARG_ITEM_ID = "item_id";

	private ViewGroup mRootView;
	private ListView mFlowsList;

	private FlowDownloader downloader;

	private String ip;
	private String dpid;
	private List<Flow> flows;
	
	ActionMode mMode;
	private int selected = -1;
//	private ArrayList<Integer> selectedPos = new ArrayList<Integer>();

	public FlowsFragment() {
	}
	
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {
		public void onFlowSelected(Flow f);
		public void onNewFlow(String sw_id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onFlowSelected(Flow f) {
		}

		@Override
		public void onNewFlow(String sw_id) {
		}
		
	};

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			ip = getArguments().getString("ip");
			dpid = getArguments().getString("dpid");
		}
		
		getSherlockActivity().getSupportActionBar().setTitle(R.string.title_flows);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(downloader!=null)
			downloader.cancel(true);
		downloader = null;
		downloader = new FlowDownloader();
		downloader.execute(ip);
	}
	
	public void setupLists() {
		
		if(mFlowsList!=null) {
			mFlowsList.setAdapter(new ArrayAdapter<Flow>(
					getActivity(), android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, flows));
			mFlowsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//			mFlowsList.setMultiChoiceModeListener(new ModeCallback());
//	        setListAdapter(new ArrayAdapter<String>(this,
//	                android.R.layout.simple_list_item_activated_1, Cheeses.sCheeseStrings));
			
			mFlowsList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int pos, long arg3) {
					if(mMode==null)
						mCallbacks.onFlowSelected(flows.get(pos));
				}
			});
			
			mFlowsList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v,
						int pos, long arg3) {
					if(mMode==null)
					{
//						selectedPos.clear();
						mMode = getSherlockActivity().startActionMode(new AnActionModeOfEpicProportions());
//						selectedPos.add(pos);
//						v.setSelected(true);
						v.setActivated(true);
						selected = pos;
						return true;
					}
					
					return false;
				}
			});
		}
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.switches_layout,
				container, false);
		
		mFlowsList = (ListView) mRootView.findViewById(R.id.list);
		
		return mRootView;
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);

	    menu.add(Menu.NONE,
	    		R.id.menu_item_add,
	    		Menu.NONE,
	    		R.string.menu_add)
	    .setIcon(android.R.drawable.ic_menu_add)
	    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

//	    inflater.inflate(R.menu.flow_manager_flows_menu, menu);
	    
	    
	}
	
	@Override
	public boolean onOptionsItemSelected(
			MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_item_add:
			mCallbacks.onNewFlow(dpid);
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar


//            menu.add(Menu.NONE,
//            		123,
//            		Menu.NONE, R.string.menu_select_all)
//                .setIcon(R.drawable.ic_menu_selectall_holo_light)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add(Menu.NONE,
            		124,
            		Menu.NONE, R.string.menu_rename)
                .setIcon(R.drawable.ic_menu_edit)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add(Menu.NONE,
            		125,
            		Menu.NONE, R.string.menu_delete)
                .setIcon(R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            Toast.makeText(ActionModes.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
        	
        	switch (item.getItemId()) {
			
        	case 123:
        		break;
        	case 124:
        		mCallbacks.onFlowSelected(flows.get(selected));
				break;
			case 125:

				if(downloader!=null)
					downloader.cancel(true);
				downloader = null;
				downloader = new FlowDownloader();
				downloader.execute(ip, ""); //2 tane gondererek secili olanlari silmesini sagliyorum
				break;
			default:
				break;
			}
            mode.finish();
            mMode = null;
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	mMode = null;
        	mFlowsList.clearChoices();
//        	selectedPos.clear();
        }
    }
	
	public class FlowDownloader extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {

			mFlowsList.setVisibility(View.GONE);					
			mRootView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
		};
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			if(params!=null && params.length==2)
			{
     			try {
					FlowManagerPusher.remove(ip, flows.get(selected));
				} catch (IOException e) {
				} catch (JSONException e) {
				}	     
			}
			
			try {
				flows = ServerUtilities.getStaticFlows(ip, dpid);
			} catch (IOException e) {
			} catch (JSONException e) {
			}
			
			// Check to see that all the controller information is here
			return null;
		}

		protected void onPostExecute(Boolean result) {
			
			mRootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);

			if(flows!=null && flows.size()>0)
			{
				mFlowsList.setVisibility(View.VISIBLE);
				setupLists();
			}
			else
			{
				TextView empty = (TextView) mRootView.findViewById(android.R.id.empty);
				empty.setVisibility(View.VISIBLE);
				empty.setText("There is no static flow in this switch. You can add one with the button in the actionbar.");				
			}

		};

	}
	
}
