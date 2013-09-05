/*
 * @(#)CitySearchActivity.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import com.voskalenko.weather.R;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.ICities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**This activity provides an opportunity to search for a city and also removing. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class CitySearchActivity extends Activity implements
LoaderManager.LoaderCallbacks<Cursor>,OnClickListener,ListView.OnItemClickListener {
	private ProgressDialog prgDialog;
	private Button btnSearchCity;
	private Button btnDelCity;
	private ImageButton btnBack;
	private ImageButton btnSearch;
	private EditText edCity;
	private ListView lstCities;
	private RelativeLayout citySearchLayout1;
	private TextView txtUnsuccessfulSearch;
	private TextView txtSuccessfulSearch;
	private Context ctx;
	private int cityCode;
	private SimpleCursorAdapter adapter;
	private static final int LOADER_ID = 0;
	private LoaderManager loaderMng;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_search);
		ctx = this;
		prgDialog = new ProgressDialog(this);
		prgDialog.setTitle(R.string.wait_Please);
		prgDialog.setMessage(ctx.getString(R.string.forming_cities_list));
		btnSearchCity = (Button) findViewById(R.id.btnSearchCity);
		btnDelCity = (Button) findViewById(R.id.btnDelCity);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		edCity = (EditText) findViewById(R.id.edCity);
		lstCities = (ListView) findViewById(R.id.lstCities);
		citySearchLayout1 = (RelativeLayout) findViewById(R.id.city_search_layout1);
		txtUnsuccessfulSearch = (TextView) findViewById(R.id.txtUnsuccessfulSearch);
		txtSuccessfulSearch = (TextView) findViewById(R.id.txtSuccessfulSearch);
		btnSearchCity.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnDelCity.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		mCallbacks = this;
		String[] fromColumns = new String[] { ICities.FCOUNTRY, ICities.FCITY_NAME };
		int[] toLayoutIDs = new int[] { R.id.txtCountry, R.id.txtCity_name };
		adapter = new SimpleCursorAdapter(this, R.layout.cities_list_item, null, fromColumns, toLayoutIDs);
		lstCities.setAdapter(adapter);
		loaderMng =  getLoaderManager();
		loaderMng.initLoader(LOADER_ID, null, mCallbacks);
		lstCities.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Cursor cur = (Cursor) parent.getItemAtPosition(pos);
		cityCode = cur.getInt(cur.getColumnIndexOrThrow(ICities.FID));
		btnDelCity.setEnabled(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Activity.RESULT_OK:
			txtUnsuccessfulSearch.setVisibility(View.GONE);
			txtSuccessfulSearch.setVisibility(View.VISIBLE);
			txtSuccessfulSearch.setText(String.format(
					getString(R.string.successful_search),
					data.getStringExtra("CHOSEN_CITY")));
			loaderMng.restartLoader(LOADER_ID, null, mCallbacks);
			break;
		case Activity.RESULT_CANCELED:
			txtSuccessfulSearch.setVisibility(View.GONE);
			txtUnsuccessfulSearch.setVisibility(View.VISIBLE);
			txtUnsuccessfulSearch.setText(String.format(
					getString(R.string.unsuccessful_search),
					data.getStringExtra(ICities.FCITY_NAME)));
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnSearchCity:
			citySearchLayout1.setVisibility(View.VISIBLE);
			btnSearchCity.setEnabled(false);
			btnDelCity.setEnabled(false);
			break;
		case R.id.btnDelCity:
			new AlertDialog.Builder(ctx)
					.setMessage(R.string.del_city)
					.setTitle(R.string.alarm)
					.setPositiveButton(R.string.confirm_delete,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface pDialog,
										int pWhich) {
									DBOper.delChosenCity(ctx,cityCode);
									btnDelCity.setEnabled(false);
									loaderMng.restartLoader(LOADER_ID, null, mCallbacks);
								}
							})
					.setNegativeButton(R.string.cancel_delete,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface pDialog,
										int pWhich) {
									//

								}
							})

					.show();
			break;
		case R.id.btnBack:
			citySearchLayout1.setVisibility(View.GONE);
			txtUnsuccessfulSearch.setVisibility(View.GONE);
			txtSuccessfulSearch.setVisibility(View.GONE);
			btnSearchCity.setEnabled(true);
			break;
		case R.id.btnSearch:
			Intent intent = new Intent(CitySearchActivity.this, SearchResultActivity.class);
			intent.putExtra(ICities.FCITY_NAME, edCity.getText().toString());
			startActivityForResult(intent, 0);
			break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		prgDialog.show();
		Uri uri = Uri.withAppendedPath(ForecastProvider.CONTENT_URI, "chosen_cities_info");
		return new CursorLoader(this, uri,null, null, null,null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
		prgDialog.hide();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cur) {
		adapter.swapCursor(null);
	}
}
