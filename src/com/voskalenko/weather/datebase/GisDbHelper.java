/*
 * @(#)GisDbHelper.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */ 

package com.voskalenko.weather.datebase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**Class implements to work with the database through the content provider
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */ 

public class GisDbHelper extends SQLiteOpenHelper implements IForecastJournal,ICities,IChosenCities,IPreferences{
	public static final String DB_PATH = "/data/data/com.voskalenko.weather/databases/";
	public static final String DB_NAME = "GisMeteoDB.sqlite";
	private static final int DB_VER = 1;
	
/*Metadata for the new database structure*/
	public static final String SQL_CITIES_DDL = "CREATE TABLE IF NOT EXISTS "
												+ TBL_CITIES + " ( " + FID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ FCOUNTRY + " TEXT," + FCITY_NAME + " TEXT" + ")";
	public static final String SQL_CHOSEN_CITIES_DDL = "CREATE TABLE IF NOT EXISTS " + TBL_CHOSEN_CITIES 
			                                           + " (" + IChosenCities.FCITY_CODE + " NUMBER)";
	public static final String SQL_FORECAST_JOURNAL_DDl = "CREATE TABLE IF NOT EXISTS " + TBL_FORECAST_JOURNAL
														  + " ( "+ IForecastJournal.FCITY_CODE + " NUMBER," + FSNAME + " TEXT," + FLATITUDE + " NUMBER,"
														  + FLONGITUDE + " NUMBER," + FDATE + " TEXT," + FTOD + " NUMBER," + FWEEKDAY
														  + " NUMBER," + FPREDICT + " NUMBER," + FCLOUDINESS + " NUMBER," + FPRECIPITATION
														  + " NUMBER," + FRPOWER + " NUMBER," + FSPOWER + " NUMBER," + FPRESSURE_MAX
														  + " NUMBER," + FPRESSURE_MIN + " NUMBER," + FTEMPERATURE_MAX + " NUMBER,"
														  + FTEMPERATURE_MIN + " NUMBER," + FWIND_MAX + " NUMBER," + FWIND_MIN + " NUMBER,"
														  + FWIND_DIRECTION + " NUMBER," + FRELWET_MAX + " NUMBER," + FRELWET_MIN
														  + " NUMBER," + FHEAT_MAX + " NUMBER," + FHEAT_MIN + " NUMBER)";
	public static final String SQL_PREFERENCES_DDL = "CREATE TABLE IF NOT EXISTS "
													+ TBL_PREFERENCES + " ( " + FFAVORITE_CITY_CODE + " NUMBER,"
													+ FWIDGET_WIEW_MODE + " TEXT," + FACTIVATE_UPDATE + " TEXT,"
													+ FUPDATE_FREQUENCY + " NUMBER," + FUPDATE_RADIO_POS +" NUMBER)";
	public static final String SQL_FORECAST_JOURNAL = "forecast_journal j Inner join cities c on c._id=j.city_id "+
			                                          "Inner join chosen_cities ch on ch.city_id=j.city_id" ;
	public static final String SQL_CHOSEN_CITIES_INFO = "chosen_cities ch Left join cities c on c._id=ch.city_id";
	public static final String SQL_PREFERENCES = "preferences p Inner join cities c on c._id=p.favorite_city_id";
	private static Context ctx;
	private static final String TAG = GisDbHelper.class.getSimpleName();

	public GisDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		ctx = context;
		try {
			createDB();
		} catch (IOException e) {
			Log.e(TAG, "checkDB: IOException = " + e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CITIES_DDL);
		db.execSQL(SQL_CHOSEN_CITIES_DDL);
		db.execSQL(SQL_FORECAST_JOURNAL_DDl);
		db.execSQL(SQL_PREFERENCES_DDL);
	}

	
/**The method creates a database structure or copying an existing ones*/
	
	public void createDB() throws IOException {
		boolean dbExist = checkDB();
		if (!dbExist) {
			getReadableDatabase();
			try {
				copyDB();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		} else {
			Log.i(TAG, "GisDB exist");
		}
	}

/**The method checks the existence of the database*/	
	
	private boolean checkDB() {
		SQLiteDatabase checkBase = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.e(TAG, "checkDB: Exception = " + e);
		}
		if (checkBase != null) {
			checkBase.close();
		}
		return checkBase != null ? true : false;
	}

/**Method copies the previously created database in a folder "databases"*/
	
	private void copyDB() throws IOException {
		InputStream input = ctx.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream output = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		output.flush();
		output.close();
		input.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub	
	}
}
