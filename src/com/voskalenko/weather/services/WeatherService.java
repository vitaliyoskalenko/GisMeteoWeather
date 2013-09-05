/*
 * @(#)WeatherService.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */

package com.voskalenko.weather.services;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import com.voskalenko.weather.GisMeteoOper;
import com.voskalenko.weather.GisMeteoWeather;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.IPreferences;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**Class implements a service that downloads weather with internet service. 
 * It starts an instance of the loader and then processes the data with parser
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */  

public class WeatherService extends Service implements Observer {
	private static final String TAG = WeatherService.class.getSimpleName();
	private WeatherUpload uploader;
	private ContentParser parser;
	private WeatherBinder binder;
	public final static String WEATHER_REFRESHED = "com.voskalenko.weather.WEATHER_REFRESHED";

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		binder = new WeatherBinder();
		uploader = new WeatherUpload((Observer)this);
		parser = ContentParser.getInstatnce();
		int update_frequency = Integer.parseInt(DBOper.getPreferences(this,IPreferences.FUPDATE_FREQUENCY));
		Log.i(TAG, "has been started");
		uploader.start(update_frequency);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uploader.stop();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object xmlLst) {
		synchronized (this) {
			Log.i(TAG, "Receiving and data processing");
			GisMeteoWeather weather = new GisMeteoWeather();
			weather.getlstTowns().clear();
			for (String xml : (ArrayList<String>) xmlLst) {
				GisMeteoWeather.TownData town = parser.parse(xml);
				weather.getlstTowns().add(town);
			}
			GisMeteoOper.saveToDb(this, weather.getlstTowns());
			sendBroadcast(new Intent(WEATHER_REFRESHED));
		}
	}

	class WeatherBinder extends Binder {
		WeatherService getService() {
			return WeatherService.this;
		}
	}
}
