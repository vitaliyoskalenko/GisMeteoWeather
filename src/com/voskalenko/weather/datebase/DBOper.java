/*
 * @(#)DBOper.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */ 

package com.voskalenko.weather.datebase;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.IForecastJournal;
import com.voskalenko.weather.datebase.IPreferences;

/**Class helps ease the work with the database by calling necessary methods
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */ 

public class DBOper implements IForecastJournal,IPreferences{
	
	/* These methods return data from tables by means of interaction with a
	 * content provider
	 */
	
	public static void insertToJournal(Context ctx, ContentValues row) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "forecast_journal");
		cr.insert(uri, row);
	}

	public static void insertToChosenCities(Context ctx, ContentValues row) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "chosen_cities");
		cr.insert(uri, row);
	}

	public static Cursor getForecastJournal(Context ctx, long city_code, boolean get_favorite_city) {
		ContentResolver cr = ctx.getContentResolver();
		String where = null;
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "forecast_journal");
		if (get_favorite_city) {
			String favorite_city_code = getPreferences(ctx, FFAVORITE_CITY_CODE);
			where = "j."+FCITY_CODE + "=" + favorite_city_code;
		} else
			where = "j."+FCITY_CODE + "=" + city_code;
		return cr.query(uri, null,where, null, FTOD);
	}

	public static Cursor getChosenCities(Context ctx, boolean get_favorite_city) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri;
		String city_code = null;
		String where = null;
		if (get_favorite_city) {
			uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "preferences");
			Cursor cur = cr.query(uri, null, null, null, null);
			if (cur.moveToFirst())
				city_code = cur.getString(cur.getColumnIndexOrThrow(FFAVORITE_CITY_CODE));
			where = IChosenCities.FCITY_CODE + "=" + city_code;
		}
		uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI,
				"chosen_cities");
		return cr.query(uri, null, where, null, null);
	}

	public static Cursor getChosenCitiesInfo(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "chosen_cities_info");
		return cr.query(uri, null, null, null, null);
	}

	public static String getCityNameByCode(Context ctx, long city_code) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "city_name_by_code/" + city_code);
		Cursor c = cr.query(uri, null, null, null, null);
		String city_id = null;
		if (c.moveToFirst()) {
			city_id = c.getString(0);
		}
		c.close();
		return city_id;
	}

	public static String getPreferences(Context ctx, String field) {
		ContentResolver cr = ctx.getContentResolver();
		String result = null;
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "preferences");
		Cursor cursor = cr.query(uri, null, null, null, null);
		if (cursor.moveToFirst()) {
			result = cursor.getString(cursor.getColumnIndexOrThrow(field));
			cursor.close();
		}
		return result;
	}
	
	public static Cursor getPreferences(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "preferences");
		return cr.query(uri, null, null, null, null);
	}
	public static void updPreferences(Context ctx, ContentValues row) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "preferences");
		cr.update(uri, row, null, null);
	}

	public static void clearJournal(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "clear_journal");
		cr.delete(uri, null, null);

	}

	public static void delChosenCity(Context ctx, long city_code) {
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "chosen_cities_del/" + city_code);
		cr.delete(uri, null, null);
	}
}
