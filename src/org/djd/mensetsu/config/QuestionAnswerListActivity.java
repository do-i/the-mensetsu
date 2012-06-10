package org.djd.mensetsu.config;

import org.djd.mensetsu.R;
import org.djd.mensetsu.data.QuestionAnswerEntity;
import org.djd.mensetsu.global.ApplicationCommons;
import org.djd.mensetsu.provider.QuestionAnswerContentProvider;
import org.djd.mensetsu.service.QuestionAnswerFileDownloadService;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class QuestionAnswerListActivity extends ListActivity {

	private static final String TAG = QuestionAnswerListActivity.class.getSimpleName();

	private static final int[] VIEW_IDS = new int[] { R.id.qa_data_entry_question_id, R.id.qa_data_entry_question,
	      R.id.qa_data_entry_answer };
	private long categoryId;

	private QuestionAnswerBroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.qa_data_view);
		receiver = new QuestionAnswerBroadcastReceiver();
		Log.i(TAG, "Register receiver");
		super.registerReceiver(receiver, receiver.intentFilter);

		Intent intent = super.getIntent();
		if (null != intent) {

			Uri data = intent.getData();

			categoryId = ContentUris.parseId(data);

			Log.d(TAG, "categoryId: " + categoryId);
			Toast.makeText(this, "selected categoryId is " + categoryId, Toast.LENGTH_SHORT).show();
			displayListItems();
		} else {
			Log.d(TAG, "intent is null.");
		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (null != receiver) {
			Log.i(TAG, "Un-Register receiver");
			super.unregisterReceiver(receiver);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_answer_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		Log.i(TAG, "onButtonClicked()...");

		switch (item.getItemId()) {
		case R.id.id_item_delete_question_answer:

			// TODO: move this to IntentService do in background thread.
			int purgedRowCount = getContentResolver().delete(QuestionAnswerContentProvider.CONTENT_URI,
			      QuestionAnswerEntity.Columns.CATEGORY_ID + "=?", new String[] { String.valueOf(categoryId) });
			displayListItems();
			Toast.makeText(this, String.format("%d rows deleted.", purgedRowCount), Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_item_download_question_answer:
			Intent intent = new Intent(this, QuestionAnswerFileDownloadService.class);
			Uri data = ContentUris.withAppendedId(Uri.EMPTY, categoryId);
			intent.setData(data);
			startService(intent);
			Toast.makeText(this, R.string.msg_download_in_progress, Toast.LENGTH_LONG).show();
			break;

		default:
			Toast.makeText(this, getString(R.string.msg_sys_no_case_defined_error), Toast.LENGTH_SHORT).show();
			Log.e(TAG, getString(R.string.msg_sys_no_case_defined_error));
		}

		return true;
	}

	/**
	 * TODO run db query in background thread.
	 * 
	 * @param categoryId
	 */
	private void displayListItems() {

		String[] selectionArgs = new String[] { String.valueOf(categoryId) };
		Cursor cursor = managedQuery(QuestionAnswerContentProvider.CONTENT_URI,
		      QuestionAnswerEntity.Columns.FULL_PROJECTION, QuestionAnswerEntity.Columns.CATEGORY_ID + "=?",
		      selectionArgs, QuestionAnswerEntity.Columns.QUESTION_ID);

		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.qa_data_entry_view, cursor,
		      QuestionAnswerEntity.Columns.LIST_VIEW_PROJECTION, VIEW_IDS);

		setListAdapter(listAdapter);
	}

	/**
	 * Callback handler receives category list data from database.
	 * 
	 * 
	 * 
	 */
	public class QuestionAnswerBroadcastReceiver extends BroadcastReceiver {
		public static final String ACTION_RESPONSE = "org.djd.mensetsu.config.QuestionAnswerBroadcastReceiver";

		public final IntentFilter intentFilter;

		public QuestionAnswerBroadcastReceiver() {
			intentFilter = new IntentFilter(ACTION_RESPONSE);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// check for error
			String errorMsg = intent.getStringExtra(ApplicationCommons.BROADCAST_ERROR_INFO_KEY);

			if (null != errorMsg) {
				Toast.makeText(QuestionAnswerListActivity.this, R.string.msg_download_error, Toast.LENGTH_SHORT).show();

			} else {
				Log.i(TAG, "Download completed.");
				displayListItems();
			}
		}
	}

}
