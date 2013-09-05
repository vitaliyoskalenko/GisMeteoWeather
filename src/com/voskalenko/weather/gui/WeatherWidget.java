/*
 * @(#)WeatherWidget.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.gui;

import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.ICities;
import com.voskalenko.weather.datebase.IForecastJournal;
import com.voskalenko.weather.services.WeatherService;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

/**Class implements a weather widget. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class WeatherWidget extends AppWidgetProvider implements IForecastJournal {
	private static final String TAG = WeatherWidget.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		if (intent.getAction().equals(WeatherService.WEATHER_REFRESHED))
			updateWeather(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager ,int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateWeather(context, appWidgetManager, appWidgetIds);
	}

	public void updateWeather(Context context) {
		ComponentName thisWidget = new ComponentName(context, WeatherWidget.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		updateWeather(context, appWidgetManager, appWidgetIds);
	}

	public void updateWeather(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "updateWeather");
		int[] viewsIds = { R.id.txtWidgetTemper0, R.id.txtWidgetTemper1, R.id.txtWidgetTemper2, R.id.txtWidgetTemper3 };
		int[] iconsIds = { R.id.iconWidgetNight, R.id.iconWidgetMorning, R.id.iconWidgetAfternoon, R.id.iconWidgetEvening };
		Cursor cursor = DBOper.getForecastJournal(context, 0, true);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
			if (cursor.moveToFirst()) {
				String cityName = cursor.getString(cursor.getColumnIndex(ICities.FCITY_NAME));
				int j = 0;	
				do {
					int temperature_min = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MIN));
					int temperature_max = cursor.getInt(cursor.getColumnIndex(FTEMPERATURE_MAX));
					int tod = cursor.getInt(cursor.getColumnIndex(FTOD));
					int cloudiness = cursor.getInt(cursor.getColumnIndex(FCLOUDINESS));
					int precipitation = cursor.getInt(cursor.getColumnIndex(FPRECIPITATION));
					views.setTextViewText(viewsIds[j], String.format("%s/%s C",temperature_min, temperature_max));
					views.setImageViewResource(iconsIds[j], GisMeteoOper.geWeatherIconId(tod, cloudiness, precipitation));
					j++;
				} while(cursor.moveToNext());
				views.setTextViewText(R.id.txtHomeCity,
						String.format(context.getString(R.string.widget_home_city), cityName));
				Intent intent = new Intent(context, ConfiguratorActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, 0);
				views.setOnClickPendingIntent(R.id.btnConfigure, pendingIntent);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}
		cursor.close();
	}
}
