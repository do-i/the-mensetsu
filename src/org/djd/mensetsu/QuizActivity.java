package org.djd.mensetsu;

import static org.djd.mensetsu.global.ApplicationCommons.DEFAULT_FONT_SIZE;
import static org.djd.mensetsu.global.ApplicationCommons.FONT_SIZE_FACTOR;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_FILE_NAME;
import static org.djd.mensetsu.global.ApplicationCommons.PREFERENCE_KEY_FONT_SIZE;

import java.util.ArrayList;
import java.util.Collections;

import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.data.QuestionAnswerEntity;
import org.djd.mensetsu.data.QuizEntity;
import org.djd.mensetsu.global.ApplicationCommons;
import org.djd.mensetsu.global.BooleanUtil;
import org.djd.mensetsu.provider.CategoryContentProvider;
import org.djd.mensetsu.provider.QuestionAnswerContentProvider;
import org.djd.mensetsu.provider.QuizContentProvider;
import org.djd.mensetsu.service.QuizService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class QuizActivity extends Activity {

	private static final String TAG = QuizActivity.class.getSimpleName();
	private static final String SAVE_QUESTION_LIST_KEY = "SAVE_QUESTION_LIST_KEY";
	private static final String SAVE_QUESTION_CURR_IDX_KEY = "SAVE_QUESTION_CURR_IDX_KEY";
	private static final String SAVE_ANSWER_VISIBLE_KEY = null;

	private QuizBroadcastReceiver quizBroadcastReceiver;
	private ArrayList<Long> questionAnswerIds;
	private int questionAnswerIdsIndex;
	private boolean answerVisible;

	private TextView questionIdView;
	private TextView qaCountView;
	private TextView questionView;
	private TextView answerView;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_view);
		questionIdView = (TextView) findViewById(R.id.id_question_id);
		qaCountView = (TextView) findViewById(R.id.id_qa_count);

		questionView = (TextView) findViewById(R.id.id_text_view_question);
		questionView.setMovementMethod(ScrollingMovementMethod.getInstance());

		answerView = (TextView) findViewById(R.id.id_text_view_answer);
		answerView.setMovementMethod(ScrollingMovementMethod.getInstance());

		quizBroadcastReceiver = new QuizBroadcastReceiver();
		registerReceiver(quizBroadcastReceiver, quizBroadcastReceiver.intentFilter);

		if (null != savedInstanceState) {
			questionAnswerIdsIndex = savedInstanceState.getInt(SAVE_QUESTION_CURR_IDX_KEY);
			questionAnswerIds = (ArrayList<Long>) savedInstanceState.getSerializable(SAVE_QUESTION_LIST_KEY);
			boolean isVisible = savedInstanceState.getBoolean(SAVE_ANSWER_VISIBLE_KEY);
			if (isVisible) {
				answerView.setVisibility(View.VISIBLE);
			}
		}
		display();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");

		SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
		final float FONT_SIZE = sharedPreferences.getFloat(PREFERENCE_KEY_FONT_SIZE, DEFAULT_FONT_SIZE);

		questionView.setTextSize(FONT_SIZE);
		answerView.setTextSize(FONT_SIZE);

	}

	/**
	 * Callback method in the event of any button is clicked.
	 * 
	 * @param view
	 */
	public void onButtonClicked(View view) {
		switch (view.getId()) {
		case R.id.button_answer:
			ToggleButton btn = (ToggleButton) view;
			if (btn.isChecked()) {
				answerView.setVisibility(View.VISIBLE);
				answerVisible = true;
			} else {
				answerView.setVisibility(View.INVISIBLE);
				answerVisible = false;
			}
			break;
		case R.id.button_prev:

			if (questionAnswerIdsIndex <= 0) {
				Toast.makeText(QuizActivity.this, R.string.msg_at_first_question, Toast.LENGTH_SHORT).show();
			} else {
				--questionAnswerIdsIndex;
				display();

			}

			break;
		case R.id.button_next:
			if (questionAnswerIdsIndex >= questionAnswerIds.size() - 1) {
				Toast.makeText(QuizActivity.this, R.string.msg_at_last_question, Toast.LENGTH_SHORT).show();
			} else {
				++questionAnswerIdsIndex;
				display();

			}
			break;

		default:
			Log.e(TAG, String.format("view.id [%d] is not recognized.", view.getId()));
			throw new UnsupportedOperationException("TODO");
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putSerializable(SAVE_QUESTION_LIST_KEY, questionAnswerIds);
		outState.putInt(SAVE_QUESTION_CURR_IDX_KEY, questionAnswerIdsIndex);
		outState.putBoolean(SAVE_ANSWER_VISIBLE_KEY, answerVisible);
		Log.d(TAG, "Saving activity state.");
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (null != quizBroadcastReceiver) {
			unregisterReceiver(quizBroadcastReceiver);
			Log.d(TAG, "Unregistered Receiver.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quiz_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		Log.i(TAG, "onButtonClicked()...");
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.id_item_preferences_quiz:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			// Toast.makeText(this, "preference", Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_item_category_quiz:
			showDialog(DialogId.CATEGORY_SELECTION_ID, null);
			Toast.makeText(this, "categories selection", Toast.LENGTH_SHORT).show();
			break;

		case R.id.id_item_shuffle_quiz:
			Toast.makeText(this, "Shuffling in progress...", Toast.LENGTH_SHORT).show();
			intent = new Intent(this, QuizService.class);
			startService(intent);
			break;

		default:
			Toast.makeText(this, getString(R.string.msg_sys_no_case_defined_error), Toast.LENGTH_SHORT).show();
			Log.e(TAG, getString(R.string.msg_sys_no_case_defined_error));
		}

		return true;
	}

	/**
	 * TODO change this (use CharSequence[] and boolean[] instead of Cursor) workaround by fixing the update bug in
	 * cursor. setMultiChoiceItems(cursor .... CategoryEntity.Columns.IS_SELECTED_BY_USER,
	 * CategoryEntity.Columns.CATEGORY_TITLE)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogId.CATEGORY_SELECTION_ID:
			Cursor cursor = getContentResolver().query(CategoryContentProvider.CONTENT_URI,
			      CategoryEntity.Columns.FULL_PROJECTION, null, null, CategoryEntity.Columns.CATEGORY_TITLE);

			ArrayList<CategoryEntity> categories = CategoryEntity.Helper.convertToCategoryEntity(cursor);
			int dataSize = categories.size();
			CharSequence[] charSequence = new CharSequence[dataSize];
			boolean[] selection = new boolean[dataSize];
			convertToCharSequenceArray(categories, charSequence, selection);

			return new AlertDialog.Builder(this).setTitle(R.string.lbl_category_title).setMultiChoiceItems(charSequence,
			      selection, new CategoriesOnMultiChoiceClickListener(cursor)).setPositiveButton(R.string.btn_ok,
			      new DialogButtonClickHandler()).create();
		default:
			throw new IllegalArgumentException(String.format("ID %d not a valid argument.", id));
		}

	}

	private void convertToCharSequenceArray(ArrayList<CategoryEntity> categories, CharSequence[] charSequence,
	      boolean[] selection) {
		int index = 0;
		for (CategoryEntity categoryEntity : categories) {
			charSequence[index] = categoryEntity.categoryTitle;
			selection[index] = BooleanUtil.get(categoryEntity.isSelectedByUser);
			++index;
		}
	}

	private class CategoriesOnMultiChoiceClickListener implements OnMultiChoiceClickListener {
		private Cursor cursor;

		public CategoriesOnMultiChoiceClickListener(Cursor cursor) {
			this.cursor = cursor;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int possition, boolean isChecked) {

			CategoryEntity categoryEntity = CategoryEntity.Helper.convertToCategoryEntity(cursor, possition);
			categoryEntity.isSelectedByUser = BooleanUtil.get(isChecked);
			ContentValues contentValues = categoryEntity.getContentValues();

			Uri uri = ContentUris.withAppendedId(CategoryContentProvider.CONTENT_URI, categoryEntity.id);

			int updatedRowCount = getContentResolver().update(uri, contentValues, null, null);

			if (0 == updatedRowCount) {

				Toast.makeText(QuizActivity.this, "Failed to update the change.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void display() {
		if (null == questionAnswerIds) {
			fetchQuestionAnswersIds();
		}
		String question = null;
		String answer = null;
		if (0 != questionAnswerIds.size()) {
			Long questionAnswerId = questionAnswerIds.get(questionAnswerIdsIndex);
			Cursor cursor = managedQuery(QuestionAnswerContentProvider.CONTENT_URI,
			      QuestionAnswerEntity.Columns.FULL_PROJECTION, QuestionAnswerEntity.Columns._ID + "=?",
			      new String[] { questionAnswerId.toString() }, null);

			QuestionAnswerEntity questionAnswerEntity = QuestionAnswerEntity.Helper.createQuestionAnswerEntity(cursor);
			question = questionAnswerEntity.question;
			answer = questionAnswerEntity.answer;
			// Log.d(TAG, String.format("_id=%d, questionid=%d", questionAnswerId, entity.questionId));
			
			cursor = managedQuery(CategoryContentProvider.CONTENT_URI, CategoryEntity.Columns.FULL_PROJECTION, CategoryEntity.Columns._ID + "=?",
					new String[] { String.valueOf(questionAnswerEntity.categoryId)}, null);
			CategoryEntity categoryEntity = CategoryEntity.Helper.convertToCategoryEntity(cursor, 0);
			
			questionIdView.setText(String.format("Question ID: %d (%s)", questionAnswerEntity.questionId, categoryEntity.categoryTitle));

		} else {
			question = "No Quiz";
			answer = "No Answer";
		}

		qaCountView.setText(String.format("%d of %d", 1 + questionAnswerIdsIndex, questionAnswerIds.size()));
		questionView.scrollTo(0, 0);
		questionView.setText(question);
		answerView.scrollTo(0, 0);
		answerView.setText(answer);

	}

	/**
	 * Shuffling is done in this method.
	 */
	private void fetchQuestionAnswersIds() {
		Cursor cursor = managedQuery(QuizContentProvider.CONTENT_URI, QuizEntity.Columns.FULL_PROJECTION, null, null,
		      null);
		questionAnswerIds = QuizEntity.Helper.extractQuestionAnswerIds(cursor);

		// Log.d(TAG, "before shuffle:"+questionAnswerIds);
		Collections.shuffle(questionAnswerIds);
		// Log.d(TAG, "after shuffle:"+questionAnswerIds);

		questionAnswerIdsIndex = 0;

	}

	private class DialogButtonClickHandler implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			// TODO In this callback persists the change into database instead of each and every item selection for a
			// performance reason.
		}

	}

	private static class DialogId {
		public static final int CATEGORY_SELECTION_ID = 0x0001;
		// public static final int PREFERENCE_ID = 0x0002;
	}

	/**
	 * Callback handler receives category list data from database.
	 * 
	 * 
	 * 
	 */
	public class QuizBroadcastReceiver extends BroadcastReceiver {
		public static final String ACTION_RESPONSE = "org.djd.mensetsu.exam.QuizBroadcastReceiver";

		public final IntentFilter intentFilter;

		public QuizBroadcastReceiver() {
			intentFilter = new IntentFilter(ACTION_RESPONSE);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// check for error
			String errorMsg = intent.getStringExtra(ApplicationCommons.BROADCAST_ERROR_INFO_KEY);

			if (null != errorMsg) {
				Toast.makeText(QuizActivity.this, R.string.msg_shuffle_error, Toast.LENGTH_SHORT).show();

			} else {
				Log.i(TAG, "Shuffle completed.");
				questionAnswerIds = null;
				Toast.makeText(QuizActivity.this, R.string.msg_shuffle_completed, Toast.LENGTH_SHORT).show();

				display();
			}
		}

	}

}
