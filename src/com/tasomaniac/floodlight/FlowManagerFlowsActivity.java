package com.tasomaniac.floodlight;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.fragments.FlowsFragment;
import com.tasomaniac.floodlight.model.Flow;

public class FlowManagerFlowsActivity extends SherlockFragmentActivity implements
	FlowsFragment.Callbacks {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString("ip",
                    getIntent().getStringExtra("ip"));
            arguments.putString("dpid",
                    getIntent().getStringExtra("dpid"));
            FlowsFragment fragment = new FlowsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        	finish();
            return true;
        }
        //TODO: add the other items here (add, refresh, delete all)

        return super.onOptionsItemSelected(item);
    }

    @Override
	public void onFlowSelected(Flow f) {
		openTheFlow(f);
	}

	@Override
	public void onNewFlow(String sw_id) {
		openTheFlow(new Flow(sw_id));
	}
	
	public void openTheFlow(Flow f)
	{
		Intent i = new Intent(this,
				FlowModActivity.class);
		i.putExtra("ip", getIntent().getStringExtra("ip"));
		i.putExtra("flow", f);
		startActivity(i);
	}
}
