package org.djd.mensetsu.data;

import static org.djd.mensetsu.global.ApplicationCommons.DATA_FILE_ITEM_DELIMITER;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class QuestionAnswerEntity implements java.io.Serializable {

	public static final String QUESTION_ANSWER_TABLE_NAME = "QUESTION_ANSWER";
	private static final String TAG = QuestionAnswerEntity.class.getSimpleName();
	private static final long serialVersionUID = 2982848555614317450L;

	/**
	 * Key properties for the Expense object. Note they are public to avoid the overhead of virtual method invocation
	 * (Android docs say this is up to 7 times faster!)
	 */
	public long id;
	public int questionId;
	public String question;
	public String answer;
	public long categoryId; // FK to Category table

	public ContentValues getContentValues() {

		ContentValues contentValues = new ContentValues();

		contentValues.put(QuestionAnswerEntity.Columns._ID, id);
		contentValues.put(QuestionAnswerEntity.Columns.QUESTION_ID, questionId);
		contentValues.put(QuestionAnswerEntity.Columns.QUESTION, question);
		contentValues.put(QuestionAnswerEntity.Columns.ANSWER, answer);
		contentValues.put(QuestionAnswerEntity.Columns.CATEGORY_ID, categoryId);

		return contentValues;

	}

	public ContentValues getContentValuesForInsert() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(QuestionAnswerEntity.Columns.QUESTION_ID, questionId);
		contentValues.put(QuestionAnswerEntity.Columns.QUESTION, question);
		contentValues.put(QuestionAnswerEntity.Columns.ANSWER, answer);
		contentValues.put(QuestionAnswerEntity.Columns.CATEGORY_ID, categoryId);

		return contentValues;

	}

	@Override
	public String toString() {
		return String.format("QuestionAnswerEntity [answer=%s, categoryId=%s, id=%s, question=%s, questionId=%s]",
		      answer, categoryId, id, question, questionId);
	}

	public static final class Columns implements BaseColumns {

		public static final String QUESTION_ID = "QUESTION_ID";
		public static final String QUESTION = "QUESTION";
		public static final String ANSWER = "ANSWER";
		public static final String CATEGORY_ID = "CATEGORY_ID";

		public static final String[] FULL_PROJECTION = { _ID, QUESTION_ID, QUESTION, ANSWER, CATEGORY_ID };
		public static final String[] LIST_VIEW_PROJECTION = { QUESTION_ID, QUESTION, ANSWER };
		public static final String[] JUST_ID_PROJECTION = { _ID };

		private Columns() {
		}

	}

	public static final class Helper {

		private static final String QUESTION_ID_PATTERN = "[0-9]{1,3}"; // Limit number of questions per category to 1000.

		/**
		 * 
		 * @param rawDataTxt
		 * @return <code>null</code> if passed in string is invalid. Otherwise new {@link QuestionAnswerEntity} instance.
		 */
		public static final QuestionAnswerEntity createQuestionAnswerEntity(long categoryId, String rawDataTxt) {
			final int NUMBER_OF_ITEMS = 3;
			if (null == rawDataTxt || rawDataTxt.trim().isEmpty()) {
				return null;
			}

			String[] categoryDataTokens = rawDataTxt.split(DATA_FILE_ITEM_DELIMITER);

			if (NUMBER_OF_ITEMS != categoryDataTokens.length) {
				return null;
			}

			QuestionAnswerEntity categoryEntity = new QuestionAnswerEntity();
			String questionIdTxt = categoryDataTokens[0];
			if (!questionIdTxt.matches(QUESTION_ID_PATTERN)) {
				Log.w(TAG, "questionIdTxt is not 3 digit number. " + questionIdTxt);
				return null;
			}
			categoryEntity.questionId = Integer.parseInt(questionIdTxt);
			categoryEntity.question = categoryDataTokens[1];
			categoryEntity.answer = categoryDataTokens[2];
			categoryEntity.categoryId = categoryId;
			return categoryEntity;

		}

		public static final long[] extractJustIds(Cursor cursor) {
			int rows = cursor.getCount();
			long[] result = new long[rows];

			int index = 0;
			while (cursor.moveToNext()) {

				result[index] = cursor.getLong(cursor.getColumnIndex(QuestionAnswerEntity.Columns._ID));

				++index;
			}
			return result;
		}
		
		public static final QuestionAnswerEntity createQuestionAnswerEntity(Cursor cursor) {
			
			QuestionAnswerEntity result = new QuestionAnswerEntity();
			
			if(1 != cursor.getCount()){
				throw new IllegalStateException("Result is not exactly one row.");
			}
			
			if(!cursor.moveToFirst()){
				throw new IllegalStateException("Result is not exactly one row.");
			}

			result.id =	cursor.getLong(cursor.getColumnIndex(QuestionAnswerEntity.Columns._ID));
			result.question =	cursor.getString(cursor.getColumnIndex(QuestionAnswerEntity.Columns.QUESTION));
			result.answer =	cursor.getString(cursor.getColumnIndex(QuestionAnswerEntity.Columns.ANSWER));
			result.categoryId = cursor.getLong(cursor.getColumnIndex(QuestionAnswerEntity.Columns.CATEGORY_ID));
			result.questionId = cursor.getInt(cursor.getColumnIndex(QuestionAnswerEntity.Columns.QUESTION_ID));
			
			return result;
		}

	}
}
