package org.djd.mensetsu.data.table;

import static android.provider.BaseColumns._ID;

import static org.djd.mensetsu.data.QuizEntity.QUIZ_TABLE_NAME;
import static org.djd.mensetsu.data.QuizEntity.Columns.QUESTION_ANSWER_ID_FK;

import org.djd.mensetsu.data.QuestionAnswerEntity;
import org.djd.mensetsu.data.DatabaseHelper.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuizTable implements Table {

	private static final String TAG = QuizTable.class.getSimpleName();
	private static final String QUIZ_TABLE_DROP_SQL = String.format("DROP TABLE IF EXISTS  %s", QUIZ_TABLE_NAME);
	private static final String QUIZ_TABLE_CREATE_SQL = String
	      .format(
	            "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER , FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE);",
	            QUIZ_TABLE_NAME, _ID, QUESTION_ANSWER_ID_FK, QUESTION_ANSWER_ID_FK,
	            QuestionAnswerEntity.QUESTION_ANSWER_TABLE_NAME, QuestionAnswerEntity.Columns._ID);

	public QuizTable() {
		Log.i(TAG, "table is created.");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Create db table if not yet exists." + QUIZ_TABLE_CREATE_SQL);
		db.execSQL(QUIZ_TABLE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Android called onUpgrade() method.");
		db.execSQL(QUIZ_TABLE_DROP_SQL);
		onCreate(db);
	}

}
