package com.tasomaniac.floodlight;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.fragments.SwitchDetailFragment;

public class SwitchDetailActivity extends SherlockFragmentActivity {

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
            SwitchDetailFragment fragment = new SwitchDetailFragment();
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

        return super.onOptionsItemSelected(item);
    }
}
