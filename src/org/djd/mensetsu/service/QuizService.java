package org.djd.mensetsu.service;

import java.util.ArrayList;

import org.djd.mensetsu.QuizActivity;
import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.data.QuestionAnswerEntity;
import org.djd.mensetsu.data.QuizEntity;
import org.djd.mensetsu.global.BooleanUtil;
import org.djd.mensetsu.provider.CategoryContentProvider;
import org.djd.mensetsu.provider.QuestionAnswerContentProvider;
import org.djd.mensetsu.provider.QuizContentProvider;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class QuizService extends IntentService {

	private static final String TAG = QuizService.class.getSimpleName();

	public QuizService() {
		super(TAG);

	}

	/**
	 * long running job in back-ground thread.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		// TODO write a better join using Joiner API.
		
		ContentResolver contentResolver = getContentResolver();

		int deleteCount = contentResolver.delete(QuizContentProvider.CONTENT_URI, null, null);

		Log.i(TAG, String.format("%d Quiz entries deleted.", deleteCount));

		Cursor categoryCursor = contentResolver.query(CategoryContentProvider.CONTENT_URI,
		      CategoryEntity.Columns.FULL_PROJECTION, CategoryEntity.Columns.IS_SELECTED_BY_USER + "=?",
		      new String[] { String.valueOf(BooleanUtil.SELECTED) }, CategoryEntity.Columns._ID);

		ArrayList<CategoryEntity> selectedCategories = CategoryEntity.Helper.convertToCategoryEntity(categoryCursor);

		for (CategoryEntity categoryEntity : selectedCategories) {

			Cursor qaCursor = contentResolver.query(QuestionAnswerContentProvider.CONTENT_URI,
			      QuestionAnswerEntity.Columns.JUST_ID_PROJECTION, QuestionAnswerEntity.Columns.CATEGORY_ID + "=?",
			      new String[] { String.valueOf(categoryEntity.id) }, QuestionAnswerEntity.Columns._ID);

			long[] questionAnswerIds = QuestionAnswerEntity.Helper.extractJustIds(qaCursor);
			for (long questionAnswerId : questionAnswerIds) {
				contentResolver.insert(
						QuizContentProvider.CONTENT_URI, 
						QuizEntity.Helper.createQuizEntity(questionAnswerId).getContentValuesForInsert());
			}
		}
		broadcastSuccess();
	}

	private void broadcastSuccess() {
		 Intent responseIntent = new Intent();
		
		 responseIntent.setAction(QuizActivity.QuizBroadcastReceiver.ACTION_RESPONSE);
		 responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
		
		 sendBroadcast(responseIntent);
	}

	

}
