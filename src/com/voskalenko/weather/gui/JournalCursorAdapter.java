/*
 * @(#)JournalCursorAdapter.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.datebase.ICities;
import com.voskalenko.weather.datebase.IForecastJournal;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**The class implements provider for list of weather. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class JournalCursorAdapter extends SimpleCursorAdapter implements IForecastJournal{
 private int layout;
 private Context ctx;
 private static final String TAG = JournalCursorAdapter.class.getSimpleName();
 
 	public JournalCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
 		super(context, layout, cursor, from, to);
 		ctx = context;
 		this.layout = layout;
 	}
 
 	@Override
 	public void bindView(View view, Context _context, Cursor cursor) {
 		TextView txtCity=(TextView)view.findViewById(R.id.city);
 		TextView txtDate=(TextView)view.findViewById(R.id.date);
 		TextView txtTod=(TextView)view.findViewById(R.id.tod);
 		TextView txtTemperature=(TextView)view.findViewById(R.id.temperature);
 		TextView txtRelwet=(TextView)view.findViewById(R.id.relwet);
 		TextView txtPressure=(TextView)view.findViewById(R.id.pressure);
 		TextView txtWind=(TextView)view.findViewById(R.id.wind);
 		TextView txtNotCurrFr=(TextView)view.findViewById(R.id.notCurrFr);
 		String[] directionArr =  ctx.getResources().getStringArray(R.array.direction);
 		String[] todArr =  ctx.getResources().getStringArray(R.array.tod);
 		ImageView icon=(ImageView)view.findViewById(R.id.icon);
 		//notCurrFr.setVisibility(TextView.VISIBLE);
 		String cityName = cursor.getString(cursor.getColumnIndex(ICities.FCITY_NAME)); 
 		String forecastDate = cursor.getString(cursor.getColumnIndex(FDATE)); 
 		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
 		try { 
 			SimpleDateFormat formatter2 = new SimpleDateFormat("MMM dd-yyyy",Locale.getDefault());
 			forecastDate = formatter2.format(formatter.parse(forecastDate));
 		} catch (ParseException e) {
 			Log.e(TAG, e.getMessage());
 		}
 		int tod = cursor.getInt(cursor.getColumnIndex(FTOD)); 
 		int temperature_min = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MIN));
 		int temperature_max = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MAX));
 		int relwet_min = cursor.getInt(cursor.getColumnIndex(FRELWET_MIN));
 		int relwet_max = cursor.getInt(cursor.getColumnIndex(FRELWET_MAX));
 		int pressure_min = cursor.getInt(cursor.getColumnIndex(FPRESSURE_MIN));
 		int pressure_max = cursor.getInt(cursor.getColumnIndex(FPRESSURE_MAX));
 		int wind_direction = cursor.getInt(cursor.getColumnIndex(FWIND_DIRECTION));
 		int wind_min = cursor.getInt(cursor.getColumnIndex(FWIND_MIN));
 		int wind_max = cursor.getInt(cursor.getColumnIndex(FWIND_MAX));
 		int cloudiness = cursor.getInt(cursor.getColumnIndex(FCLOUDINESS));
 		int precipitation = cursor.getInt(cursor.getColumnIndex(FPRECIPITATION)); 
 		txtCity.setText(cityName);
 		txtDate.setText(forecastDate);
 		txtTod.setText(String.format("%s %s", ctx.getString(R.string.tod),todArr[tod]));
 		txtTemperature.setText(String.format(ctx.getString(R.string.temperature),temperature_min,temperature_max));
 		txtRelwet.setText(String.format(ctx.getString(R.string.relwet),relwet_min,relwet_max,"%")); 
 		txtPressure.setText(String.format(ctx.getString(R.string.pressure),pressure_min,pressure_max));
 		txtWind.setText(String.format(ctx.getString(R.string.wind),directionArr[wind_direction],wind_min,wind_max));  
 		icon.setImageResource(GisMeteoOper.geWeatherIconId(tod,cloudiness,precipitation));  
 	}
 
 	@Override
 	public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
 		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(_context.LAYOUT_INFLATER_SERVICE);
 		View view = inflater.inflate(layout, parent, false);
 		return view;
 	}
}
