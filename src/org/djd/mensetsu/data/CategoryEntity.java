package org.djd.mensetsu.data;

import static org.djd.mensetsu.global.ApplicationCommons.DATA_FILE_ITEM_DELIMITER;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class CategoryEntity implements java.io.Serializable {


	public static final String CATEGORY_TABLE_NAME = "CATEGORY";
	private static final String TAG = CategoryEntity.class.getSimpleName();

	/**
    * 
    */
	private static final long serialVersionUID = -4507169399753821714L;
	public static final int SELECTED = 1;
	public static final int UNSELECTED = 0;

	/**
	 * Key properties for the Expense object. Note they are public to avoid the overhead of virtual method invocation
	 * (Android docs say this is up to 7 times faster!)
	 */
	public long id;
	public String categoryTitle;
	public String dataFileUrl;
	public int isSelectedByUser;

	
	public ContentValues getContentValues() {

		ContentValues v = new ContentValues();

		v.put(CategoryEntity.Columns._ID, id);
		v.put(CategoryEntity.Columns.CATEGORY_TITLE, categoryTitle);
		v.put(CategoryEntity.Columns.DATA_FILE_URL, dataFileUrl);
		v.put(CategoryEntity.Columns.IS_SELECTED_BY_USER, isSelectedByUser);

		return v;

	}

	public ContentValues getContentValuesForInsert() {

		ContentValues v = new ContentValues();

		v.put(CategoryEntity.Columns.CATEGORY_TITLE, categoryTitle);
		v.put(CategoryEntity.Columns.DATA_FILE_URL, dataFileUrl);
		v.put(CategoryEntity.Columns.IS_SELECTED_BY_USER, isSelectedByUser);

		return v;

	}
	
	@Override
   public String toString() {
	   return String.format("CategoryEntity [categoryTitle=%s, dataFileUrl=%s, id=%s, isSelectedByUser=%s]",
	         categoryTitle, dataFileUrl, id, isSelectedByUser);
   }


	public static final class Columns implements BaseColumns {

		public static final String CATEGORY_TITLE = "CATEGORY_TITLE";
		public static final String DATA_FILE_URL = "DATA_FILE_URL";
		public static final String IS_SELECTED_BY_USER = "IS_SELECTED_BY_USER";

		public static final String[] FULL_PROJECTION = { _ID, CATEGORY_TITLE, DATA_FILE_URL, IS_SELECTED_BY_USER };
//		public static final String[] CATEGORY_SELECTION_PROJECTION = { _ID, CATEGORY_TITLE, IS_SELECTED_BY_USER };
		public static final String[] LIST_VIEW_PROJECTION = {  CATEGORY_TITLE };

		private Columns() {
		}

	}

	public static final class Helper {
		/**
		 * 
		 * @param rawDataTxt
		 * @return <code>null</code> if passed in string is invalid. Otherwise new {@link CategoryEntity} instance.
		 */
		public static final CategoryEntity createCategoryEntity(String rawDataTxt) {
			final int NUMBER_OF_ITEMS = 2;
			if (null == rawDataTxt || rawDataTxt.trim().isEmpty()) {
				return null;
			}

			String[] categoryDataTokens = rawDataTxt.split(DATA_FILE_ITEM_DELIMITER);

			if (NUMBER_OF_ITEMS != categoryDataTokens.length) {
				return null;
			}

			CategoryEntity categoryEntity = new CategoryEntity();
			categoryEntity.categoryTitle = categoryDataTokens[0];
			categoryEntity.dataFileUrl = categoryDataTokens[1];
			categoryEntity.isSelectedByUser = (4<categoryEntity.categoryTitle.length())?SELECTED:UNSELECTED;
			return categoryEntity;

		}
		
		public static final CategoryEntity convertToCategoryEntity(Cursor cursor, int possition){
//			cursor.move(offset);
			cursor.moveToPosition(possition);
			CategoryEntity categoryEntity = new CategoryEntity();
			
			categoryEntity.id = cursor.getLong(cursor.getColumnIndex(CategoryEntity.Columns._ID));
			categoryEntity.categoryTitle = cursor.getString(cursor
			      .getColumnIndex(CategoryEntity.Columns.CATEGORY_TITLE));
			categoryEntity.dataFileUrl = cursor.getString(cursor.getColumnIndex(CategoryEntity.Columns.DATA_FILE_URL));
			categoryEntity.isSelectedByUser = cursor.getInt(cursor.getColumnIndex(CategoryEntity.Columns.IS_SELECTED_BY_USER));
			
			return  categoryEntity ;
		}

		public static final ArrayList<CategoryEntity> convertToCategoryEntity(Cursor cursor) {

			int rows = cursor.getCount();

			ArrayList<CategoryEntity> resultList = new ArrayList<CategoryEntity>(rows);

			while (cursor.moveToNext()) {

				CategoryEntity categoryEntity = new CategoryEntity();
				categoryEntity.id = cursor.getLong(cursor.getColumnIndex(CategoryEntity.Columns._ID));
				categoryEntity.categoryTitle = cursor.getString(cursor
				      .getColumnIndex(CategoryEntity.Columns.CATEGORY_TITLE));
				categoryEntity.dataFileUrl = cursor.getString(cursor.getColumnIndex(CategoryEntity.Columns.DATA_FILE_URL));
				categoryEntity.isSelectedByUser = cursor.getInt(cursor.getColumnIndex(CategoryEntity.Columns.IS_SELECTED_BY_USER));
				resultList.add(categoryEntity);

			}

			return resultList;

		}

	}
}
