/*
 * @(#)MainActivity.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import java.util.ArrayList;
import java.util.List;
import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.GisMeteoWeather;
import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.GisDbHelper;
import com.voskalenko.weather.datebase.IChosenCities;
import com.voskalenko.weather.datebase.ICities;
import com.voskalenko.weather.datebase.IForecastJournal;
import com.voskalenko.weather.services.ContentParser;
import com.voskalenko.weather.services.WeatherService;
import com.voskalenko.weather.services.WeatherUpload;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.app.LoaderManager;

/**This is the main activity of the program, 
 * where a list of forecasts for chosen cities. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class MainActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor>, Button.OnClickListener,
		ListView.OnItemClickListener,IForecastJournal,ICities {
	private GisMeteoWeather weather;
	private JournalCursorAdapter adapter; 
	private ListView lstFr;
	private Context ctx;
	private List<String> xmlLst;
	private WeatherUpload uploader;
	private ContentParser parser;
	private boolean isStopped;
	private static final int LOADER_ID = 0;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	private LoaderManager loaderMng;
	private ProgressDialog prgDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx = this;
		prgDialog = new ProgressDialog(this);
		prgDialog.setTitle(R.string.wait_Please);
		prgDialog.setMessage(ctx.getString(R.string.forming_weather_list));
		weather = new GisMeteoWeather();
		Button btnAddCity = (Button) findViewById(R.id.btnAddCity);
		Button btnGetWeather = (Button) findViewById(R.id.btnGetWeather);
		lstFr = (ListView) findViewById(R.id.lstForecast);
		mCallbacks = this;
		btnAddCity.setOnClickListener(this);
		btnGetWeather.setOnClickListener(this);
		lstFr.setOnItemClickListener(this);
		isStopped = stopService(new Intent(this, WeatherService.class)); // Temporarily
		String[] from = { ICities.FCITY_NAME,FTOD,FDATE,FTEMPERATURE_MAX,FRELWET_MAX,FPRESSURE_MAX,FWIND_MAX};
	    int[] to = {R.id.city, R.id.tod,R.id.date,R.id.temperature,R.id.relwet,R.id.pressure,R.id.wind};
	    adapter = new JournalCursorAdapter(this, R.layout.weather_item, null, from, to);
		lstFr.setAdapter(adapter);
		loaderMng = getLoaderManager();
		loaderMng.initLoader(LOADER_ID, null, mCallbacks);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnAddCity:
			Intent intent = new Intent(ctx, CitySearchActivity.class);
			startActivity(intent);
			break;
		case R.id.btnGetWeather:
			prgDialog.setTitle(R.string.server_connecting);
			prgDialog.setMessage(ctx.getString(R.string.loading_content));
			uploader = new WeatherUpload(MainActivity.this);
			parser = ContentParser.getInstatnce();
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					prgDialog.show();
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					xmlLst = uploader.uploadContent();
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if (!xmlLst.isEmpty()) {
						weather.getlstTowns().clear();
						for (String xml : (ArrayList<String>) xmlLst) {
							GisMeteoWeather.TownData town = parser.parse(xml);
							weather.getlstTowns().add(town);
						}
						GisMeteoOper.saveToDb(ctx,weather.getlstTowns());
						loaderMng.restartLoader(LOADER_ID, null, mCallbacks);
						prgDialog.hide();
					}
				}
			};
			task.execute();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long city_code) {
		Intent intent = new Intent(MainActivity.this, DetailsActivity.class);		
		intent.putExtra(IChosenCities.FCITY_CODE, city_code);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		prgDialog.show();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI,"forecast_journal");
		String where = FTOD +"=" +GisMeteoOper.getCurrTod();
		return new CursorLoader(this, uri,null, where, null,null);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
		adapter.swapCursor(cur);
		prgDialog.hide();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isStopped)
			startService(new Intent(this, WeatherService.class)); // Temporarily
	}

}
