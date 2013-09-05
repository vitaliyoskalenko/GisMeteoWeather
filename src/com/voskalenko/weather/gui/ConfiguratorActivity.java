/*
 * @(#)ConfiguratorActivity.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.GisMeteoWeather;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.ICities;
import com.voskalenko.weather.datebase.IPreferences;
import com.voskalenko.weather.services.ContentParser;
import com.voskalenko.weather.services.WeatherService;
import com.voskalenko.weather.services.WeatherUpload;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**Activity for the widget settings. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class ConfiguratorActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnClickListener, ListView.OnItemClickListener {
	private CheckBox chkActiveUpdate;
	private RadioGroup rGrUpdateChoice;
	private RadioButton rbtnUpdateDaily;
	private RadioButton rbtnUpdateTwice;
	private ListView lstFavoriteCity;
	private Button btnManualUpdate;
	private Button btnSaveConfigure;
	private Button btnCancelConfigure;
	private TextView txtFavoriteCity;
	private Cursor prefCursor;
	private SimpleCursorAdapter adapter;
	private int chosenCityCode;
	private Activity ctx;
	private ProgressDialog prgDialog;
	private WeatherUpload uploader;
	private ContentParser parser;
	private List<String> xmlLst;
	private GisMeteoWeather weather;
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private boolean isStopped;
	private static final int LOADER_ID = 0;
	private LoaderManager loaderMng;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	public ConfiguratorActivity() {
		super();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configurator);
		ctx = this;
		mCallbacks = this;
		prgDialog = new ProgressDialog(this);
		prgDialog.setTitle(R.string.wait_Please);
		prgDialog.setMessage(ctx.getString(R.string.forming_cities_list));
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		chkActiveUpdate = (CheckBox) findViewById(R.id.chkActiveUpdate);
		rGrUpdateChoice = (RadioGroup) findViewById(R.id.rGrUpdateChoice);
		rbtnUpdateDaily = (RadioButton) findViewById(R.id.rbtnUpdateDaily);
		rbtnUpdateTwice = (RadioButton) findViewById(R.id.rbtnUpdateTwice);
		lstFavoriteCity = (ListView) findViewById(R.id.lstFavoriteCity);
		btnManualUpdate = (Button) findViewById(R.id.btnManualUpdate);
		btnSaveConfigure = (Button) findViewById(R.id.btnSaveConfigure);
		btnCancelConfigure = (Button) findViewById(R.id.btnCancelConfigure);
		txtFavoriteCity = (TextView) findViewById(R.id.txtFavoriteCity);
		btnManualUpdate.setOnClickListener(this);
		btnSaveConfigure.setOnClickListener(this);
		btnCancelConfigure.setOnClickListener(this);
		isStopped = stopService(new Intent(this, WeatherService.class)); // Temporarily
		weather = new GisMeteoWeather();
		prefCursor = DBOper.getPreferences(this);
		if (prefCursor.moveToFirst()) {
			boolean isChecked = Boolean.parseBoolean(prefCursor.getString(prefCursor.getColumnIndexOrThrow(IPreferences.FACTIVATE_UPDATE)));
			String favoriteCity = prefCursor.getString(prefCursor.getColumnIndexOrThrow(ICities.FCITY_NAME));
			chosenCityCode = prefCursor.getInt(prefCursor.getColumnIndexOrThrow(IPreferences.FFAVORITE_CITY_CODE));
			int update_radio_pos = prefCursor.getInt(prefCursor.getColumnIndexOrThrow(IPreferences.FUPDATE_RADIO_POS));
			chkActiveUpdate.setChecked(isChecked);
			rbtnUpdateDaily.setEnabled(isChecked);
			rbtnUpdateTwice.setEnabled(isChecked);
			btnManualUpdate.setEnabled(!isChecked);
			if (update_radio_pos > 0)
				rGrUpdateChoice.check(update_radio_pos);
			txtFavoriteCity.setText(String.format(getString(R.string.choose_favorite_city), favoriteCity));
			prefCursor.close();
		}
		String[] fromColumns = new String[] { ICities.FCOUNTRY, ICities.FCITY_NAME };
		int[] toLayoutIDs = new int[] { R.id.txtCountry, R.id.txtCity_name };
		adapter = new SimpleCursorAdapter(this, R.layout.cities_list_item, null, fromColumns, toLayoutIDs);
		lstFavoriteCity.setAdapter(adapter);
		loaderMng = getLoaderManager();
		loaderMng.initLoader(LOADER_ID, null, mCallbacks);
		lstFavoriteCity.setOnItemClickListener(this);
		/* Handlers of pressing buttons */
		chkActiveUpdate
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
						rbtnUpdateDaily.setEnabled(isChecked);
						rbtnUpdateTwice.setEnabled(isChecked);
						btnManualUpdate.setEnabled(!isChecked);
						btnSaveConfigure.setEnabled(true);
						btnCancelConfigure.setEnabled(true);
					}
				});

		rGrUpdateChoice
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						btnSaveConfigure.setEnabled(true);
						btnCancelConfigure.setEnabled(true);
					}
				});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnManualUpdate:
			prgDialog = new ProgressDialog(ConfiguratorActivity.this);
			prgDialog.setTitle(R.string.server_connecting);
			prgDialog.setMessage(ctx.getString(R.string.loading_content));
			uploader = new WeatherUpload(ctx);
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
						GisMeteoOper.saveToDb(ctx, weather.getlstTowns());
					}
					prgDialog.hide();
				}
			};
			task.execute();
			break;
		case R.id.btnSaveConfigure:
			ContentValues row = new ContentValues();
			row.put(IPreferences.FACTIVATE_UPDATE,
					Boolean.toString(chkActiveUpdate.isChecked()));
			row.put(IPreferences.FUPDATE_RADIO_POS,
					rGrUpdateChoice.getCheckedRadioButtonId());
			String upd_freq = ((RadioButton) rGrUpdateChoice
					.findViewById(rGrUpdateChoice.getCheckedRadioButtonId()))
					.getTag().toString();
			row.put(IPreferences.FUPDATE_FREQUENCY, upd_freq);
			row.put(IPreferences.FFAVORITE_CITY_CODE, chosenCityCode);
			DBOper.updPreferences(this, row);
			if (chkActiveUpdate.isChecked()) {
				ctx.stopService(new Intent(ctx, WeatherService.class));
				ctx.startService(new Intent(ctx, WeatherService.class));
			} else
				ctx.stopService(new Intent(ctx, WeatherService.class));
			closeConfigurator();
			break;
		case R.id.btnCancelConfigure:
			closeConfigurator();
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Cursor cur = (Cursor) parent.getItemAtPosition(pos);
		chosenCityCode = cur.getInt(cur.getColumnIndexOrThrow(ICities.FID));
		btnSaveConfigure.setEnabled(true);
		btnCancelConfigure.setEnabled(true);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		prgDialog.show();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "chosen_cities_info");
		return new CursorLoader(this, uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		prgDialog.hide();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

/*Make sure we pass back the original appWidgetId*/
	
	private void closeConfigurator() {
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isStopped)
			startService(new Intent(this, WeatherService.class)); // Temporarily
	}

}
