package com.example.schreiber;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity1 extends ActionBarActivity implements OnItemClickListener {

	private DrawerLayout drawerLayout;
	private ListView listView;
	private ActionBarDrawerToggle drawerListener;
	private MyAdapter myAdapter;
	FragmentManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity1);
		manager = getFragmentManager();
//		manager.addOnBackStackChangedListener(this);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		listView = (ListView) findViewById(R.id.drawerList);
		myAdapter = new MyAdapter(this);
		listView.setAdapter(myAdapter);
		listView.setOnItemClickListener(this);
		drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				TestVocab.closeKeyboard(drawerView.getContext(), getWindow().getDecorView().getRootView().getWindowToken());
			}

			@Override
			public void onDrawerClosed(View drawerView) {
			}

		};
		drawerLayout.setDrawerListener(drawerListener);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	// change of the upper icon
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}

	// item click on the menu icon, slide the menu.. it forward the click to
	// open drawer
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerListener.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// if orientaiton change or something else
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerListener.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		selectItem(position);
		drawerLayout.closeDrawers();
		Bundle bundle = new Bundle();
		bundle.putString("message", myAdapter.menuItems[position]);
		Fragment detail;
		switch (position) {
		case 0:
			detail =  new TopicList();
			 bundle.putInt("root", 0);
			break;
		case 1:
			detail =  new TopicList();
			bundle.putInt("root", 1);
			break;
		case 2:
			detail =  new SettingsFragment();
			break;
		default:
			 detail = new DetailFragment();
		}
		
		detail.setArguments(bundle);
		
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.mainContent, detail);
		transaction.addToBackStack("detail");
		transaction.commit();
	}

	// for changing title
	public void selectItem(int position) {
		listView.setItemChecked(position, true);
	}

	public void setTitle(String title) {
		getSupportActionBar().setTitle(title);
	}

}

class MyAdapter extends BaseAdapter {
	private Context context;
	String[] menuItems;
	int[] images = { R.drawable.ic_facebook, R.drawable.ic_java, R.drawable.ic_sql, R.drawable.ic_tools,R.drawable.ic_facebook };

	public MyAdapter(Context context) {
		this.context = context;
		menuItems = context.getResources().getStringArray(R.array.main_menu);
	}

	@Override
	public int getCount() {
		return menuItems.length;
	}

	@Override
	public Object getItem(int position) {
		return menuItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.custom_row, parent, false);
		} else {
			row = convertView;
		}
		TextView titleTextView = (TextView) row.findViewById(R.id.textView1);
		ImageView titleImageView = (ImageView) row.findViewById(R.id.imageView1);

		titleTextView.setText(menuItems[position]);
		titleImageView.setImageResource(images[position]);

		return row;
	}

}
