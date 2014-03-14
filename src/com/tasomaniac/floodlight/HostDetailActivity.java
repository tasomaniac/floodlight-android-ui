package com.tasomaniac.floodlight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.fragments.HostDetailFragment;

public class HostDetailActivity extends SherlockFragmentActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString("ip",
                    getIntent().getStringExtra("ip"));
            arguments.putString("mac",
                    getIntent().getStringExtra("mac"));
            arguments.putInt("port", getIntent().getIntExtra("port", -1));
            arguments.putLong("last_seen", getIntent().getLongExtra("last_seen", -1));
            arguments.putString("switch", getIntent().getStringExtra("switch"));
            HostDetailFragment fragment = new HostDetailFragment();
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
