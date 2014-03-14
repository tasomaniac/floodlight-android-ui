package com.tasomaniac.floodlight;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.tasomaniac.floodlight.fragments.DashboardFragment;
import com.tasomaniac.floodlight.fragments.FlowModFragment;
import com.tasomaniac.floodlight.fragments.FlowsFragment;
import com.tasomaniac.floodlight.fragments.HostDetailFragment;
import com.tasomaniac.floodlight.fragments.HostsFragment;
import com.tasomaniac.floodlight.fragments.SwitchDetailFragment;
import com.tasomaniac.floodlight.fragments.SwitchesFragment;
import com.tasomaniac.floodlight.fragments.TopologyFragment;
import com.tasomaniac.floodlight.model.DeviceSummary;
import com.tasomaniac.floodlight.model.Flow;
import com.tasomaniac.floodlight.view.MenuScrollView;

public class MainActivity extends SherlockFragmentActivity implements
		ItemListFragment.Callbacks, SwitchesFragment.Callbacks,
		FlowsFragment.Callbacks, DashboardFragment.Callbacks,
		HostsFragment.Callbacks, FlowModFragment.Callbacks {

	private static final String STATE_MENUDRAWER = "com.tasomaniac.beam.it.menuDrawer";

	private MenuDrawerManager mMenuDrawer;

	private int mActiveViewId;
	private boolean mTwoPane;

	private String ip;
	private String port;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		ip = i.getStringExtra("ip");
		port = i.getStringExtra("port");

		mTwoPane = getResources().getBoolean(R.bool.has_two_pane);
		if (mTwoPane) {
			setContentView(R.layout.activity_item_list);
			// ((ItemListFragment) getSupportFragmentManager()
			// .findFragmentById(R.id.item_list))
			// .setActivateOnItemClick(true);
		} else {
			mMenuDrawer = new MenuDrawerManager(this,
					MenuDrawer.MENU_DRAG_CONTENT);

			// FrameLayout frame = new FrameLayout(this);
			// FragmentTransaction ft =
			// getSupportFragmentManager().beginTransaction();
			// ft.add(
			// android.R.id.content,
			// Fragment.instantiate(this,
			// ItemListFragment.class.getName()));
			// ft.commit();
			// mMenuDrawer.setMenuView(frame, new LayoutParams(
			// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mMenuDrawer.setMenuView(R.layout.activity_item_list);

			mMenuDrawer.setContentView(R.layout.activity_item_detail);

			MenuScrollView msv = (MenuScrollView) mMenuDrawer.getMenuView()
					.findViewById(R.id.scrollView);
			msv.setOnScrollChangedListener(new MenuScrollView.OnScrollChangedListener() {
				@Override
				public void onScrollChanged() {
					mMenuDrawer.getMenuDrawer().invalidate();
				}
			});

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			TextView activeView = (TextView) findViewById(mActiveViewId == 0 ? R.id.item_dashboard
					: mActiveViewId);
			if (activeView != null) {
				mMenuDrawer.setActiveView(activeView);
				// TODO: active item degisiyor. contenti de degistir.
				activeView.performClick();
			}

			mMenuDrawer.getMenuDrawer().peekDrawer();
		}
	}

	@Override
	public void onItemSelected(View v) {

		if (!mTwoPane) {
			mMenuDrawer.setActiveView(v);
			mMenuDrawer.closeMenu();
		} else
		{
			((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list))
			.changeMainList(true);
			findViewById(R.id.item_detail_whole2).setVisibility(View.VISIBLE);
			findViewById(R.id.item_detail_whole3).setVisibility(View.GONE);
		}

		int fragment_id = R.id.item_detail_container;// : android.R.id.content;
		if (mActiveViewId != v.getId()) {
			mActiveViewId = v.getId();

			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();

			Bundle args = new Bundle();
			args.putString("ip", ip);

			switch (mActiveViewId) {

			case R.id.item_dashboard:

				ft.replace(
						fragment_id,
						Fragment.instantiate(this,
								DashboardFragment.class.getName(), args));
				break;
			case R.id.item_topology:
				ft.replace(
						fragment_id,
						Fragment.instantiate(this,
								TopologyFragment.class.getName(), args));
				if (mTwoPane)
				{
					((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list))
					.changeMainList(false);
					findViewById(R.id.item_detail_whole2).setVisibility(
							View.GONE);
				}
				break;
			case R.id.item_switches:
				ft.replace(
						fragment_id,
						Fragment.instantiate(this,
								SwitchesFragment.class.getName(), args));
				break;
			case R.id.item_hosts:
				ft.replace(
						fragment_id,
						Fragment.instantiate(this,
								HostsFragment.class.getName(), args));
				break;
			case R.id.item_flow_manager:
				ft.replace(
						fragment_id,
						Fragment.instantiate(this,
								SwitchesFragment.class.getName(), args));
				if (mTwoPane)
				{
					((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list))
					.changeMainList(false);
					findViewById(R.id.item_detail_whole3).setVisibility(View.VISIBLE);
				}
			default:
				break;
			}
			
			if(mTwoPane)
				ft.replace(R.id.item_detail_container2, new Fragment());
			ft.commit();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mMenuDrawer != null)
				mMenuDrawer.toggleMenu();
			return true;
		case R.id.menu_item_search:
			Toast.makeText(getApplicationContext(), "Maybe in the future! :)",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mMenuDrawer != null) {
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN
					|| drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
				return;
			}
		}
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt("active_id", mActiveViewId);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null)
			mActiveViewId = savedInstanceState.getInt("active_id");
	}

	@Override
	public void onSwitchSelected(String dpid) {

		if (mTwoPane) {
			Class<?> c = mActiveViewId == R.id.item_switches ? SwitchDetailFragment.class
					: FlowsFragment.class;
			Bundle arguments = new Bundle();
			arguments.putString("ip", ip);
			arguments.putString("dpid", dpid);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.item_detail_container2,
							Fragment.instantiate(this,
									c.getName(),
									arguments)).commit();
			
		} else {
			Class<?> c = mActiveViewId == R.id.item_switches ? SwitchDetailActivity.class
					: FlowManagerFlowsActivity.class;
			Intent i = new Intent(this, c);
			i.putExtra("ip", ip);
			i.putExtra("dpid", dpid);
			startActivity(i);
		}
	}

	@Override
	public void onFlowSelected(Flow f) {
		openTheFlow(f);
	}

	@Override
	public void onNewFlow(String sw_id) {
		openTheFlow(new Flow(sw_id));
	}

	public void openTheFlow(Flow f) {
		// TODO : do something with the 3 pane
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("ip", ip);
			arguments.putSerializable("flow", f);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.item_detail_container3,
							Fragment.instantiate(this,
									FlowModFragment.class.getName(),
									arguments)).commit();
		}
	}

	@Override
	public void onDashHostSelected(String mac) {

		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("ip", ip);
			arguments.putString("mac", mac);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.item_detail_container2,
							Fragment.instantiate(this,
									HostDetailFragment.class.getName(),
									arguments)).commit();
		} else {
			Intent i = new Intent(this, HostDetailActivity.class);
			i.putExtra("ip", ip);
			i.putExtra("mac", mac);
			startActivity(i);
		}
	}

	@Override
	public void onDashSwitchSelected(String dpid) {

		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("ip", ip);
			arguments.putString("dpid", dpid);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.item_detail_container2,
							Fragment.instantiate(this,
									SwitchDetailFragment.class.getName(),
									arguments)).commit();
		} else {
			Intent i = new Intent(this, SwitchDetailActivity.class);
			i.putExtra("ip", ip);
			i.putExtra("dpid", dpid);
			startActivity(i);
		}
	}

	@Override
	public void onHostSelected(DeviceSummary d) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("ip", ip);
			arguments.putString("mac",
                    d.getMacAddress());
            arguments.putInt("port", d.getSwitchPort());
            arguments.putLong("last_seen", d.getLastSeen().getTime());
            arguments.putString("switch", d.getAttachedSwitch());
			getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.item_detail_container2,
							Fragment.instantiate(this,
									HostDetailFragment.class.getName(),
									arguments)).commit();
		} else {
			// DeviceSummary d = hosts.get(pos);
			Intent i = new Intent(this, HostDetailActivity.class);
			i.putExtra("ip", d.getIpv4());
			i.putExtra("switch", d.getAttachedSwitch());
			i.putExtra("mac", d.getMacAddress());
			i.putExtra("port", d.getSwitchPort());
			i.putExtra("last_seen", d.getLastSeen().getTime());
			// i.putExtra("dpid", hosts.get(pos).getDpid());
			startActivity(i);
		}

	}

	@Override
	public void onFlowPushed(String dpid) {
		
		((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list))
		.changeMainList(true);
		findViewById(R.id.item_detail_whole2).setVisibility(View.VISIBLE);
		FragmentTransaction ft = getSupportFragmentManager()
				.beginTransaction();

		Bundle args = new Bundle();
		args.putString("ip", ip);
		args.putString("dpid", dpid);
		ft.replace(
				R.id.item_detail_container,
				Fragment.instantiate(this,
						SwitchesFragment.class.getName(), args));
		
			((ItemListFragment)getSupportFragmentManager().findFragmentById(R.id.item_list))
			.changeMainList(false);
			findViewById(R.id.item_detail_whole3).setVisibility(View.VISIBLE);
		
		ft.replace(R.id.item_detail_container2, 
				
				Fragment.instantiate(this,
						FlowsFragment.class.getName(), args));
		ft.replace(R.id.item_detail_container3, new Fragment());
	ft.commit();
	}

}
