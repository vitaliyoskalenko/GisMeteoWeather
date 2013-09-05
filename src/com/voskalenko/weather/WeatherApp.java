package com.voskalenko.weather;

import android.app.Application;

public class WeatherApp extends Application {
	private static WeatherApp singleton;

	public WeatherApp getInstance(){
		return singleton;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}


