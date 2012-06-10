package org.djd.mensetsu.service;

import static org.djd.mensetsu.global.ApplicationCommons.BROADCAST_ERROR_INFO_KEY;

import java.util.ArrayList;

import org.djd.mensetsu.config.QuestionAnswerListActivity;
import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.data.QuestionAnswerEntity;
import org.djd.mensetsu.global.DownloadException;
import org.djd.mensetsu.global.Downloader;
import org.djd.mensetsu.provider.CategoryContentProvider;
import org.djd.mensetsu.provider.QuestionAnswerContentProvider;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class QuestionAnswerFileDownloadService extends IntentService {

	private static final String TAG = QuestionAnswerFileDownloadService.class.getSimpleName();

	public QuestionAnswerFileDownloadService() {
		super(TAG);

	}

	/**
	 * long running job in back-ground thread.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Uri uri = intent.getData();

		long categoryId = ContentUris.parseId(uri);
		String errorMsg = null;
		try {
			ArrayList<String> rawQuestionAnswerData = Downloader.loadTextFileToArrayList(getFileUrl(categoryId));
			if (0 != rawQuestionAnswerData.size()) {
				ContentResolver contentResolver = super.getContentResolver();

				for (String rawDataTxt : rawQuestionAnswerData) {
					QuestionAnswerEntity questionAnswerEntity = QuestionAnswerEntity.Helper.createQuestionAnswerEntity(categoryId,
					      rawDataTxt);

					uri = contentResolver.insert(QuestionAnswerContentProvider.CONTENT_URI, questionAnswerEntity
					      .getContentValuesForInsert());

					Log.i(TAG, "rowId:" + ContentUris.parseId(uri));
				}
			} else {
				errorMsg = "No data is downloaded.";
			}
		} catch (DownloadException e) {
			errorMsg = "Error: " + e.getMessage();
		} catch (SQLException e) {
			errorMsg = e.getMessage();
		} catch (IllegalStateException e) {
			errorMsg = e.getMessage();
		}

		if (null != errorMsg) {
			
			Log.i(TAG, "extraInfo:" + errorMsg);
			broadcastFailure(errorMsg);
		}
		broadcastSuccess();
	}

	private String getFileUrl(long categoryId) {
		String[] selectionArgs = new String[] { String.valueOf(categoryId) };
		// TODO Call ContentProvider to query URL from category table.
		ContentResolver contentResolver = super.getContentResolver();

		Cursor cursor = contentResolver.query(CategoryContentProvider.CONTENT_URI,
		      CategoryEntity.Columns.FULL_PROJECTION, CategoryEntity.Columns._ID + "=?", selectionArgs, null);
		ArrayList<CategoryEntity> categoryEntityList = CategoryEntity.Helper.convertToCategoryEntity(cursor);

		if (1 != categoryEntityList.size()) {
			String err = String.format("Error: There is no category found for the given categoryId (%d).", categoryId);
			Log.e(TAG, err);
			throw new IllegalStateException(err);
		}

		String dataFileUrl = categoryEntityList.get(0).dataFileUrl;

		Log.i(TAG, "dataFileUrl:" + dataFileUrl);
		return dataFileUrl;

	}

	private void broadcastSuccess() {
		Intent responseIntent = new Intent();

		responseIntent.setAction(QuestionAnswerListActivity.QuestionAnswerBroadcastReceiver.ACTION_RESPONSE);
		responseIntent.addCategory(Intent.CATEGORY_DEFAULT);

		sendBroadcast(responseIntent);
	}
	
	private void broadcastFailure(String errorMsg){
		Intent responseIntent = new Intent();

		responseIntent.setAction(QuestionAnswerListActivity.QuestionAnswerBroadcastReceiver.ACTION_RESPONSE);
		responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
		responseIntent.putExtra(BROADCAST_ERROR_INFO_KEY, errorMsg);
		sendBroadcast(responseIntent);
	}

}
