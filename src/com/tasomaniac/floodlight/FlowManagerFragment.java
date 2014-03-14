package com.tasomaniac.floodlight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class FlowManagerFragment extends SherlockFragment
         {

    private boolean mTwoPane;

    private ViewGroup mRootView;
    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_flow_manager);
//
//        if (findViewById(R.id.switch_detail_container) != null) {
//            mTwoPane = true;
//            ((SwitchListFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.switch_list))
//                    .setActivateOnItemClick(true);
//        }
//    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	return super.onCreateView(inflater, container, savedInstanceState);
    }

//    @Override
    public void onItemSelected(String id) {
//        if (mTwoPane) {
//            Bundle arguments = new Bundle();
//            arguments.putString(SwitchDetailFragment.ARG_ITEM_ID, id);
//            SwitchDetailFragment fragment = new SwitchDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.switch_detail_container, fragment)
//                    .commit();
//
//        } else {
//            Intent detailIntent = new Intent(this, SwitchDetailActivity.class);
//            detailIntent.putExtra(SwitchDetailFragment.ARG_ITEM_ID, id);
//            startActivity(detailIntent);
//        }
    }
}
