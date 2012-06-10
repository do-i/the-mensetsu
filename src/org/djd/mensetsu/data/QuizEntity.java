package org.djd.mensetsu.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class QuizEntity implements java.io.Serializable {

	/**
    * 
    */
	public static final String QUIZ_TABLE_NAME = "QUIZ";
	private static final long serialVersionUID = -4283668840140658548L;	
//	private static final String TAG = QuizEntity.class.getSimpleName();

	/**
	 * Key properties for the Expense object. Note they are public to avoid the overhead of virtual method invocation
	 * (Android docs say this is up to 7 times faster!)
	 */
	public long id;
	public long questionAnswerIdFk;
	
	public ContentValues getContentValues() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(QuizEntity.Columns._ID, id);
		contentValues.put(QuizEntity.Columns.QUESTION_ANSWER_ID_FK, questionAnswerIdFk);
		return contentValues;

	}

	public ContentValues getContentValuesForInsert() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(QuizEntity.Columns.QUESTION_ANSWER_ID_FK, questionAnswerIdFk);
		return contentValues;

	}

	

	@Override
   public String toString() {
	   return String.format("QuizEntity [id=%s, questionAnswerIdFk=%s]", id, questionAnswerIdFk);
   }



	public static final class Columns implements BaseColumns {

		public static final String QUESTION_ANSWER_ID_FK = "QUESTION_ANSWER_ID_FK";

		public static final String[] FULL_PROJECTION = { _ID, QUESTION_ANSWER_ID_FK};

		private Columns() {
		}

	}

	public static final class Helper {

		/**
		 * 
		 * @param questionAnswerIdFk
		 * @return new {@link QuizEntity} instance.
		 */
		public static final QuizEntity createQuizEntity(long questionAnswerIdFk) {

			QuizEntity quizEntity = new QuizEntity();			
			quizEntity.questionAnswerIdFk = questionAnswerIdFk;
			return quizEntity;

		}

		
		public static final ArrayList<Long> extractQuestionAnswerIds(Cursor cursor) {

			int rows = cursor.getCount();
			ArrayList<Long> result = new ArrayList<Long>(rows);

			
			while (cursor.moveToNext()) {

				result.add( cursor.getLong(cursor.getColumnIndex(QuizEntity.Columns.QUESTION_ANSWER_ID_FK)));
				
			}
			return result;

		}

	}
}
