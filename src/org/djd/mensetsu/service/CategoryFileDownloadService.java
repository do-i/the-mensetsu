package org.djd.mensetsu.service;

import static org.djd.mensetsu.global.ApplicationCommons.BROADCAST_ERROR_INFO_KEY;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_KEY_CATEGORY_LIST_FILE_URL;
import static org.djd.mensetsu.global.ApplicationCommons.DEFAULT_CATEGORY_FILE_LIST_URL;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_FILE_NAME;

import java.util.ArrayList;

import org.djd.mensetsu.config.ConfigActivity.ConfigActivityBroadcastReceiver;
import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.global.DownloadException;
import org.djd.mensetsu.global.Downloader;
import org.djd.mensetsu.provider.CategoryContentProvider;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class CategoryFileDownloadService extends IntentService {

	private static final String TAG = CategoryFileDownloadService.class.getSimpleName();

	

	public CategoryFileDownloadService() {
		super(TAG);

		
	}

	/**
	 * long running job in back-ground thread.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		String extraInfo = null;
		try {
			ArrayList<String> rawCategoryData = Downloader.loadTextFileToArrayList(getCategoryListFileUrl());
			if (0 != rawCategoryData.size()) {
				ContentResolver contentResolver = super.getContentResolver();

				for (String aLine : rawCategoryData) {
					CategoryEntity categoryEntity = CategoryEntity.Helper.createCategoryEntity(aLine);

					Uri uri = contentResolver.insert(CategoryContentProvider.CONTENT_URI, categoryEntity
					      .getContentValuesForInsert());

					Log.i(TAG, "rowId:" + ContentUris.parseId(uri));
				}
			} else {
				extraInfo = "No data is downloaded.";
			}
		} catch (DownloadException e) {
			extraInfo = "Error: " + e.getMessage();
		} catch (SQLException e) {
			extraInfo = e.getMessage();
		}

		
		broadcase(extraInfo);
	}

	private void broadcase(String extraInfo) {
		Intent responseIntent = new Intent();

		responseIntent.setAction(ConfigActivityBroadcastReceiver.ACTION_RESPONSE);
		responseIntent.addCategory(Intent.CATEGORY_DEFAULT);

		if (null != extraInfo) {
			responseIntent.putExtra(BROADCAST_ERROR_INFO_KEY, extraInfo);
		}

		sendBroadcast(responseIntent);
	}

	private String getCategoryListFileUrl() {
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
		return settings.getString(PREFERENCE_KEY_CATEGORY_LIST_FILE_URL, DEFAULT_CATEGORY_FILE_LIST_URL);
	}


}
