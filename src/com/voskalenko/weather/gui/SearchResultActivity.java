/*
 * @(#)SearchResultActivity.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import com.voskalenko.weather.R;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.IChosenCities;
import com.voskalenko.weather.datebase.ICities;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**This activity displays city search results. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class SearchResultActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor>, Button.OnClickListener,
		ListView.OnItemClickListener {
	private ListView lstFoundCities;
	private Button btnConfirm;
	private Button btnCancel;
	private Cursor cursor;
	private int listItemPos;
	private ProgressDialog prgDialog;
	private SimpleCursorAdapter adapter;
	private String cityName;
	private String chosenCity;
	private Activity ctx;
	private static final int LOADER_ID = 0;
	private LoaderManager loaderMng;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
		ctx = this;
		mCallbacks = this;
		lstFoundCities = (ListView) findViewById(R.id.lstFoundCities);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		lstFoundCities.setOnItemClickListener(this);
		cityName = getIntent().getStringExtra(ICities.FCITY_NAME);
		prgDialog = new ProgressDialog(this);
		prgDialog.setTitle(getString(R.string.search));
		prgDialog.setMessage(cityName);
		String[] fromColumns = new String[] { ICities.FCOUNTRY,
				ICities.FCITY_NAME };
		int[] toLayoutIDs = new int[] { R.id.txtCountry, R.id.txtCity_name };
		adapter = new SimpleCursorAdapter(ctx, R.layout.cities_list_item, null, fromColumns, toLayoutIDs);
		lstFoundCities.setAdapter(adapter);
		loaderMng = getLoaderManager();
		loaderMng.initLoader(LOADER_ID, null, mCallbacks);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void finishSearchResultActivity(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra(ICities.FCITY_NAME, cityName);
		intent.putExtra("CHOSEN_CITY", chosenCity);
		setResult(resultCode, intent);
		finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnConfirm:
			ContentValues row = new ContentValues();
			cursor.moveToPosition(listItemPos);
			chosenCity = cursor.getString(cursor.getColumnIndexOrThrow(ICities.FCITY_NAME));
			int city_code = cursor.getInt(cursor.getColumnIndexOrThrow(ICities.FID));
			row.put(IChosenCities.FCITY_CODE, city_code);
			DBOper.insertToChosenCities(ctx, row);
			finishSearchResultActivity(Activity.RESULT_OK);
			break;
		case R.id.btnCancel:
			finishSearchResultActivity(Activity.RESULT_CANCELED);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		listItemPos = pos;
		btnConfirm.setEnabled(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		prgDialog.show();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI,
				"cities_by_name/" + cityName);
		return new CursorLoader(this, uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
		prgDialog.hide();
		if (cur.getCount() == 0) {
			new AlertDialog.Builder(ctx)
					.setMessage(String.format(getString(R.string.unsuccessful_search), cityName))
					.setPositiveButton(android.R.string.ok, 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface pDialog, int pWhich) {
									finishSearchResultActivity(Activity.RESULT_CANCELED);

								}
							}).show();
		}
		else {
			adapter.swapCursor(cur);
			cursor = cur;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}
