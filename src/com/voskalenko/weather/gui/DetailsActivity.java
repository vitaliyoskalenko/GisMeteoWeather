/*
 * @(#)DetailsActivity.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.IChosenCities;
import com.voskalenko.weather.datebase.ICities;
import com.voskalenko.weather.datebase.IForecastJournal;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**This class implements the activity to display 
 * the detail forecasts for the selected city. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class DetailsActivity extends Activity implements IForecastJournal{
	private List<TextView> temperArr;
	private List<TextView> heatArr;
	private List<TextView> pressureArr;
	private List<TextView> relwetArr;
	private List<TextView> cloudinessArr;
	private List<TextView> precipArr;
	private List<TextView> windArr;
	private List<ImageView> iconArr;
	private List<ImageView> windDirectArr;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		temperArr = new ArrayList<TextView>();
		temperArr.add((TextView) findViewById(R.id.temper0));
		temperArr.add((TextView) findViewById(R.id.temper1));
		temperArr.add((TextView) findViewById(R.id.temper2));
		temperArr.add((TextView) findViewById(R.id.temper3));

		heatArr = new ArrayList<TextView>();
		heatArr.add((TextView) findViewById(R.id.heat0));
		heatArr.add((TextView) findViewById(R.id.heat1));
		heatArr.add((TextView) findViewById(R.id.heat2));
		heatArr.add((TextView) findViewById(R.id.heat3));

		pressureArr = new ArrayList<TextView>();
		pressureArr.add((TextView) findViewById(R.id.pressure0));
		pressureArr.add((TextView) findViewById(R.id.pressure1));
		pressureArr.add((TextView) findViewById(R.id.pressure2));
		pressureArr.add((TextView) findViewById(R.id.pressure3));

		relwetArr = new ArrayList<TextView>();
		relwetArr.add((TextView) findViewById(R.id.relwet0));
		relwetArr.add((TextView) findViewById(R.id.relwet1));
		relwetArr.add((TextView) findViewById(R.id.relwet2));
		relwetArr.add((TextView) findViewById(R.id.relwet3));

		cloudinessArr = new ArrayList<TextView>();
		cloudinessArr.add((TextView) findViewById(R.id.cloudiness0));
		cloudinessArr.add((TextView) findViewById(R.id.cloudiness1));
		cloudinessArr.add((TextView) findViewById(R.id.cloudiness2));
		cloudinessArr.add((TextView) findViewById(R.id.cloudiness3));

		precipArr = new ArrayList<TextView>();
		precipArr.add((TextView) findViewById(R.id.precipitation0));
		precipArr.add((TextView) findViewById(R.id.precipitation1));
		precipArr.add((TextView) findViewById(R.id.precipitation2));
		precipArr.add((TextView) findViewById(R.id.precipitation3));

		windArr = new ArrayList<TextView>();
		windArr.add((TextView) findViewById(R.id.wind0));
		windArr.add((TextView) findViewById(R.id.wind1));
		windArr.add((TextView) findViewById(R.id.wind2));
		windArr.add((TextView) findViewById(R.id.wind3));

		iconArr = new ArrayList<ImageView>();
		iconArr.add((ImageView) findViewById(R.id.iconNight));
		iconArr.add((ImageView) findViewById(R.id.iconMorning));
		iconArr.add((ImageView) findViewById(R.id.iconAfternoon));
		iconArr.add((ImageView) findViewById(R.id.iconEvening));

		windDirectArr = new ArrayList<ImageView>();
		windDirectArr.add((ImageView) findViewById(R.id.wind_direct_0));
		windDirectArr.add((ImageView) findViewById(R.id.wind_direct_1));
		windDirectArr.add((ImageView) findViewById(R.id.wind_direct_2));
		windDirectArr.add((ImageView) findViewById(R.id.wind_direct_3));

		ImageView iconTod = (ImageView) findViewById(R.id.iconTod);
		ImageView iconThermometer = (ImageView) findViewById(R.id.thermometer);
		TextView txtCityName = (TextView) findViewById(R.id.city_name);
		TextView txtTod = (TextView) findViewById(R.id.tod);
		TextView txtLocation = (TextView) findViewById(R.id.location);
		TextView txtPrecipitation = (TextView) findViewById(R.id.precipitation);
		TextView txtTemper = (TextView) findViewById(R.id.temper);
		TextView txtWind = (TextView) findViewById(R.id.wind);
		TextView txtPressure = (TextView) findViewById(R.id.pressure);
		TextView txtRelwet = (TextView) findViewById(R.id.relwet);
		
		/* Here we fill components with the necessary information */
		String[] cloudArr = getResources().getStringArray(R.array.cloudiness);
		String[] precipitationArr = getResources().getStringArray(R.array.precipitation);
		String[] directionArr = getResources().getStringArray(R.array.direction);
		String[] todArr = getResources().getStringArray(R.array.tod);
		long cityCode = getIntent().getLongExtra(IChosenCities.FCITY_CODE, 0);

		cursor = DBOper.getForecastJournal(this, cityCode, false);
		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			String city_name =  cursor.getString(cursor.getColumnIndex(ICities.FCITY_NAME));
			txtCityName.setText(city_name);
			for (int i = 0; i < cursor.getCount(); i++) {
				int tod = cursor.getInt(cursor.getColumnIndex(FTOD));
				int temperature_min = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MIN));
				int temperature_max = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MAX));
				int  heat_min = cursor.getInt(cursor.getColumnIndex(FHEAT_MIN));
				int heat_max = cursor.getInt(cursor.getColumnIndex(FHEAT_MAX));
				int pressure_min = cursor.getInt(cursor.getColumnIndex(FPRESSURE_MIN));
				int pressure_max = cursor.getInt(cursor.getColumnIndex(FPRESSURE_MAX));
				int relwet_min = cursor.getInt(cursor.getColumnIndex(FRELWET_MIN));
				int relwet_max = cursor.getInt(cursor.getColumnIndex(FRELWET_MAX));
				int cloudiness = cursor.getInt(cursor.getColumnIndex(FCLOUDINESS));
				int precipitation = cursor.getInt(cursor.getColumnIndex(FPRECIPITATION));
				int wind_min = cursor.getInt(cursor.getColumnIndex(FWIND_MIN));
				int wind_max = cursor.getInt(cursor.getColumnIndex(FWIND_MAX));
				int direction = cursor.getInt(cursor.getColumnIndex(FWIND_DIRECTION));
				temperArr.get(i).setText(String.format("%s...%s", temperature_min,temperature_max));
				heatArr.get(i).setText(String.format("%s...%s",heat_min, heat_max));
				pressureArr.get(i).setText(String.format("%s...%s", pressure_min,pressure_max));
				relwetArr.get(i).setText(String.format("%s...%s", relwet_min,relwet_max));
				cloudinessArr.get(i).setText(cloudArr[cloudiness]);
				precipArr.get(i).setText(precipitationArr[precipitation]);
				windArr.get(i).setText(String.format("%s...%s", wind_min, wind_max));
				iconArr.get(i).setImageResource(GisMeteoOper.geWeatherIconId(tod,cloudiness,precipitation));
				windDirectArr.get(i).setImageResource(direction);
				
				if(GisMeteoOper.getCurrTod() == tod){
					iconTod.setImageResource(GisMeteoOper.geWeatherIconId(tod,cloudiness,precipitation));
					txtTod.setText(String.format("%s %s", getString(R.string.tod),todArr[tod]));
					txtLocation.setText(city_name);
					txtPrecipitation.setText(String.format("%s, %s", cloudArr[cloudiness], 
							precipitationArr[precipitation]));
					txtTemper.setText(String.format("%s C", temperature_max));
					txtWind.setText(String.format(getString(R.string.wind), directionArr[direction], wind_min, wind_max));
					txtPressure.setText(String.format(getString(R.string.pressure), pressure_min, pressure_max));
					txtRelwet.setText(String.format(getString(R.string.relwet),relwet_min, relwet_max," %"));
					iconThermometer.setImageResource((new int[] {
							R.drawable.thermometer_warm, R.drawable.thermometer_cold })[temperature_max > 0 ? 0 : 1]);
				}
                cursor.moveToNext();
			}
	}

		ImageButton btnBack = (ImageButton) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopManagingCursor(cursor);
	}
}
