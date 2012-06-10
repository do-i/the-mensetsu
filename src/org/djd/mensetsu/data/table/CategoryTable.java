package org.djd.mensetsu.data.table;

import static android.provider.BaseColumns._ID;
import static org.djd.mensetsu.data.CategoryEntity.CATEGORY_TABLE_NAME;
import static org.djd.mensetsu.data.CategoryEntity.Columns.CATEGORY_TITLE;
import static org.djd.mensetsu.data.CategoryEntity.Columns.DATA_FILE_URL;
import static org.djd.mensetsu.data.CategoryEntity.Columns.IS_SELECTED_BY_USER;

import org.djd.mensetsu.data.DatabaseHelper.Table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CategoryTable implements Table {

	private static final String TAG = CategoryTable.class.getSimpleName();
	private static final String CATEGORY_TABLE_DROP_SQL = String.format("DROP TABLE IF EXISTS  %s", CATEGORY_TABLE_NAME);
	private static final String CATEGORY_TABLE_CREATE_SQL = String.format(
	      "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT UNIQUE, %s TEXT, %s INTEGER);",
	      CATEGORY_TABLE_NAME, _ID, CATEGORY_TITLE, DATA_FILE_URL, IS_SELECTED_BY_USER);

	public CategoryTable() {
		Log.i(TAG, "table is created.");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Create db table if not yet exists." + CATEGORY_TABLE_CREATE_SQL);
		db.execSQL(CATEGORY_TABLE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Android called onUpgrade() method.");
		db.execSQL(CATEGORY_TABLE_DROP_SQL);
		onCreate(db);
	}

}
