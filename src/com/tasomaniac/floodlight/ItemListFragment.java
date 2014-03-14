package com.tasomaniac.floodlight;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class ItemListFragment extends SherlockFragment implements View.OnClickListener{

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    private ViewGroup mRootView;

    public interface Callbacks {
        public void onItemSelected(View id);
    }
    
    public void changeMainList(final boolean open)
    {
    	mRootView.findViewById(R.id.scrollView).setVisibility(open ? View.VISIBLE : View.GONE);
    	View a = mRootView.findViewById(R.id.menu_hider);
    	a.setVisibility(open ? View.GONE : View.VISIBLE);
    	
    	a.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeMainList(!open);
			}
		});
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(View id) {
        }
    };

    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	
    	mRootView = (ViewGroup) inflater.inflate(R.layout.menu_scrollview, container, false);
		
		
    	mRootView.findViewById(R.id.item_dashboard).setOnClickListener(this);
    	mRootView.findViewById(R.id.item_topology).setOnClickListener(this);
    	mRootView.findViewById(R.id.item_switches).setOnClickListener(this);
    	mRootView.findViewById(R.id.item_hosts).setOnClickListener(this);
    	mRootView.findViewById(R.id.item_flow_manager).setOnClickListener(this);
    	mRootView.findViewById(R.id.item_patcher).setOnClickListener(this);
		
		return mRootView;
    }
    

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

//    @Override
//    public void onListItemClick(ListView listView, View view, int position, long id) {
//        super.onListItemClick(listView, view, position, id);
//        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

//    public void setActivateOnItemClick(boolean activateOnItemClick) {
//        getListView().setChoiceMode(activateOnItemClick
//                ? ListView.CHOICE_MODE_SINGLE
//                : ListView.CHOICE_MODE_NONE);
//    }

    public void setActivatedPosition(int position) {
    	//TODO: secilen seyi aktive etme burda
//        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position;
    }

	@Override
	public void onClick(View v) {
		mCallbacks.onItemSelected(v);
	}
}
