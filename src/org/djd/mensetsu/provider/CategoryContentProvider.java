package org.djd.mensetsu.provider;


import static  org.djd.mensetsu.global.ApplicationCommons.SCHEME;
import org.djd.mensetsu.data.CategoryEntity;
import org.djd.mensetsu.data.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CategoryContentProvider extends ContentProvider {

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.djd.mensetsu.category";

	
	private static final String AUTHORITY = "org.djd.mensetsu.provider.categorycontentprovider";
	private static final String PATH_CATEGORY = "/category/";
   public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CATEGORY);
   
	private SQLiteDatabase database;

	private static final int CATEGORIES = 1;

	private static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, PATH_CATEGORY, CATEGORIES);
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case CATEGORIES:
			return CONTENT_TYPE;		
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);

		database = dbHelper.getWritableDatabase();
		return (null != database);

	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {

		long id = database.replace(CategoryEntity.CATEGORY_TABLE_NAME, null, contentValues);
		if (0 <= id) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		return database.query(CategoryEntity.CATEGORY_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
	
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
		
		long id = ContentUris.parseId(uri);		
		
		int updatedRowCount = database.update(CategoryEntity.CATEGORY_TABLE_NAME,  values, CategoryEntity.Columns._ID + "=?", new String[] { String.valueOf(id) });
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updatedRowCount;
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		return database.delete(CategoryEntity.CATEGORY_TABLE_NAME, whereClause, whereArgs);
	}

}
