package org.djd.mensetsu.config;

import org.djd.mensetsu.R;
import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.provider.CategoryContentProvider;
import org.djd.mensetsu.service.CategoryFileDownloadService;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ConfigActivity extends ListActivity {

	private static final String TAG = ConfigActivity.class.getSimpleName();
	private ConfigActivityBroadcastReceiver receiver;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_view);

		receiver = new ConfigActivityBroadcastReceiver();

		this.displayListItems();
	}

	@Override
	protected void onResume() {
		super.onResume();
		super.registerReceiver(receiver, receiver.intentFilter);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != receiver) {
			super.unregisterReceiver(receiver);
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.category_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.id_item_category_register:
			startActivity(new Intent(this, RegisterUrlActivity.class));
			break;
		case R.id.id_item_download_category:
			startService(new Intent(this, CategoryFileDownloadService.class));
			break;
		case R.id.id_item_delete:
			// TODO use async call
			ContentResolver contentResolver = getContentResolver();
			contentResolver.delete(CategoryContentProvider.CONTENT_URI, null, null);			
			displayListItems();
			Toast.makeText(this, getString(R.string.msg_all_categories_deleted), Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, getString(R.string.msg_sys_no_case_defined_error), Toast.LENGTH_SHORT).show();
			Log.e(TAG, getString(R.string.msg_sys_no_case_defined_error));
		}

		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);
				
		Intent intent = new Intent(this,QuestionAnswerListActivity.class);
		Uri data = ContentUris.withAppendedId(Uri.EMPTY, id); // not sure if this works EMPTY + id.
		intent.setData(data);	
		
		startActivity(intent);
		// Toast.makeText(this, String.format("position: %d,  id: %d", position, id), Toast.LENGTH_SHORT).show();
//		Log.i(TAG, String.format("position: %d,  id: %d", position, id));
	}

	/**
	 * TODO run db query in background thread.
	 */
	private void displayListItems() {
			
		Cursor cursor = managedQuery(CategoryContentProvider.CONTENT_URI, CategoryEntity.Columns.FULL_PROJECTION, null, null, CategoryEntity.Columns.CATEGORY_TITLE);
		int[] viewIds = new int[] { R.id.category_title };

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.category_entry_view, cursor,
		      CategoryEntity.Columns.LIST_VIEW_PROJECTION, viewIds);

		setListAdapter(listAdapter);
	}

	/**
	 * Callback handler receives category list data from database.
	 * 
	 * 
	 * 
	 */
	public class ConfigActivityBroadcastReceiver extends BroadcastReceiver {
		public static final String ACTION_RESPONSE = "org.djd.mensetsu.config.ConfigActivityBroadcastReceiver";
		public final IntentFilter intentFilter;

		public ConfigActivityBroadcastReceiver() {
			intentFilter = new IntentFilter(ACTION_RESPONSE);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		}
	

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = "Completed Download and Updated Database.";
			Toast.makeText(ConfigActivity.this, result, Toast.LENGTH_SHORT).show();
			displayListItems();
			
		}
	}

}
