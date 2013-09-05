/*
 * @(#)ForecastProvider.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */ 

package com.voskalenko.weather.datebase;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/** The class implements the content providers work. 
 *  And returns data in the form of the cursor on the desired URI
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */ 

public class ForecastProvider extends ContentProvider {
	public static final String AUTHORITY = "com.voskalenko.provider.Weather";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final int URI_FORECAST_JOURNAL = 1;
	public static final int URI_CHOSEN_CITIES = 2;
	public static final int URI_CITY_NAME_BY_CODE = 3;
	public static final int URI_CLEAR_JOURNAL = 4;
	public static final int URI_CHOSEN_CITIES_INFO = 5;
	public static final int URI_SEARCH_CITIES = 6;
	public static final int URI_CITIES_BY_NAME = 7;
	public static final int URI_CHOSEN_CITIES_DEL = 8;
	public static final int URI_PREFERENCES = 9;
	public static final String CONTENT_TYPE_JOURNAL = "vnd.android.cursor.dir/forecast_fournal";
	public static final String CONTENT_TYPE_CHOSEN_CITIES = "vnd.android.cursor.dir/chosen_cities";
	public static final String CONTENT_TYPE_CHOSEN_CITIES_INFO = "vnd.android.cursor.dir/chosen_cities_info";
	public static final String CONTENT_TYPE_CITIES_ITEM = "vnd.android.cursor.item/cities";
	public static final String CONTENT_TYPE_CITIES_BY_NAME = "vnd.android.cursor.dir/cities_by_name";
	public static final String CONTENT_TYPE_PREFERENCES = "vnd.android.cursor.dir/preferences";
	private static final UriMatcher uriMatcher;
	private static HashMap<String, String> city_columns;
	private GisDbHelper dbHelper;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "forecast_journal", URI_FORECAST_JOURNAL);
		uriMatcher.addURI(AUTHORITY, "chosen_cities", URI_CHOSEN_CITIES);
		uriMatcher.addURI(AUTHORITY, "city_name_by_code/#", URI_CITY_NAME_BY_CODE);
		uriMatcher.addURI(AUTHORITY, "clear_journal", URI_CLEAR_JOURNAL);
		uriMatcher.addURI(AUTHORITY, "chosen_cities_info", URI_CHOSEN_CITIES_INFO);
		uriMatcher.addURI(AUTHORITY, "cities_by_name/*", URI_CITIES_BY_NAME);
		uriMatcher.addURI(AUTHORITY, "chosen_cities_del/#", URI_CHOSEN_CITIES_DEL);
		uriMatcher.addURI(AUTHORITY, "preferences", URI_PREFERENCES);
		city_columns = new HashMap<String, String>();
		city_columns.put(ICities.FID, ICities.FID);
		city_columns.put(ICities.FCOUNTRY, ICities.FCOUNTRY);
		city_columns.put(ICities.FCITY_NAME, ICities.FCITY_NAME);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case URI_FORECAST_JOURNAL: return CONTENT_TYPE_JOURNAL;
		case URI_CHOSEN_CITIES: return CONTENT_TYPE_CHOSEN_CITIES;
		case URI_CHOSEN_CITIES_INFO: return CONTENT_TYPE_CHOSEN_CITIES_INFO;
		case URI_CITY_NAME_BY_CODE: return CONTENT_TYPE_CITIES_ITEM;
		case URI_CITIES_BY_NAME: return CONTENT_TYPE_CITIES_BY_NAME;
		case URI_PREFERENCES: return CONTENT_TYPE_PREFERENCES;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		dbHelper = new GisDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] pProjection, String pSelection,
			String[] pSelectionArgs, String pSortorder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder query = new SQLiteQueryBuilder();
		switch (uriMatcher.match(uri)) {
		case URI_FORECAST_JOURNAL:
			query.setTables(GisDbHelper.SQL_FORECAST_JOURNAL);
			break;
		case URI_CHOSEN_CITIES:
			query.setTables(IChosenCities.TBL_CHOSEN_CITIES);
			break;
		case URI_CHOSEN_CITIES_INFO:
			query.setTables(GisDbHelper.SQL_CHOSEN_CITIES_INFO);
			query.setProjectionMap(city_columns);
			break;
		case URI_CITY_NAME_BY_CODE:
			String city_code = uri.getPathSegments().get(1);
			query.setTables(ICities.TBL_CITIES);
			query.appendWhere(ICities.FID + "=" + '"' + city_code + '"');
			break;
		case URI_CITIES_BY_NAME:
			String city_name = uri.getPathSegments().get(1);
			query.setTables(ICities.TBL_CITIES);
			String fcity_name = city_name.substring(0, 1).toUpperCase()+city_name.substring(1).toLowerCase();
			query.appendWhere(ICities.FCITY_NAME + " like " + '"' + fcity_name + "%" + '"');
			query.setProjectionMap(city_columns);
			break;
		case URI_PREFERENCES:
			query.setTables(GisDbHelper.SQL_PREFERENCES);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		Cursor c = query.query(db, pProjection, pSelection, pSelectionArgs, null, null, pSortorder, null);
		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Uri resultUri = null;
		long rowId;
		switch (uriMatcher.match(uri)) {
		case URI_FORECAST_JOURNAL:
			rowId = db.insert(IForecastJournal.TBL_FORECAST_JOURNAL, "", values);
			break;
		case URI_CHOSEN_CITIES:
			rowId = db.insert(IChosenCities.TBL_CHOSEN_CITIES, "", values);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		if (rowId != -1) {
			resultUri = ContentUris.withAppendedId(uri, +rowId);
			return resultUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case URI_CLEAR_JOURNAL:
			count = db.delete(IForecastJournal.TBL_FORECAST_JOURNAL, selection, selectionArgs);
			break;
		case URI_CHOSEN_CITIES_DEL:
			String rowId = uri.getPathSegments().get(1);
			count = db.delete(IChosenCities.TBL_CHOSEN_CITIES, IChosenCities.FCITY_CODE + "=" + rowId, selectionArgs);
			getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case URI_PREFERENCES: {
			return db.update(IPreferences.TBL_PREFERENCES, values, selection, selectionArgs);
		}
		}
		throw new UnsupportedOperationException();
	}
}
