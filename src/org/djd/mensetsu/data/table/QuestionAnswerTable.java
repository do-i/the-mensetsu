package org.djd.mensetsu.data.table;

import static android.provider.BaseColumns._ID;
import static org.djd.mensetsu.data.CategoryEntity.CATEGORY_TABLE_NAME;
import static org.djd.mensetsu.data.QuestionAnswerEntity.QUESTION_ANSWER_TABLE_NAME;
import static org.djd.mensetsu.data.QuestionAnswerEntity.Columns.ANSWER;
import static org.djd.mensetsu.data.QuestionAnswerEntity.Columns.CATEGORY_ID;
import static org.djd.mensetsu.data.QuestionAnswerEntity.Columns.QUESTION;
import static org.djd.mensetsu.data.QuestionAnswerEntity.Columns.QUESTION_ID;

import org.djd.mensetsu.data.DatabaseHelper.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuestionAnswerTable implements Table {

	private static final String TAG = QuestionAnswerTable.class.getSimpleName();
	private static final String QUESTION_ANSWER_TABLE_DROP_SQL = String.format("DROP TABLE IF EXISTS  %s",
	      QUESTION_ANSWER_TABLE_NAME);

	public static final String QUESTION_ANSWER_TABLE_CREATE_SQL = String.format(
	      "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, "
	            + "UNIQUE(%s, %s) ON CONFLICT REPLACE, FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
	      QUESTION_ANSWER_TABLE_NAME, _ID, QUESTION_ID, QUESTION, ANSWER, CATEGORY_ID, QUESTION_ID, CATEGORY_ID,
	      CATEGORY_ID, CATEGORY_TABLE_NAME, _ID);

	public QuestionAnswerTable() {
		Log.i(TAG, "table is created.");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Create db table if not yet exists." + QUESTION_ANSWER_TABLE_CREATE_SQL);
		db.execSQL(QUESTION_ANSWER_TABLE_CREATE_SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Android called onUpgrade() method.");

		db.execSQL(QUESTION_ANSWER_TABLE_DROP_SQL);
		onCreate(db);

	}

}
