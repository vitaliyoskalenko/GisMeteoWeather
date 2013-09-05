/*
 * @(#)AutoRunService.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.services;

import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.IPreferences;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**The class is designed to run weather service on system startup. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class AutoRunService extends BroadcastReceiver {
  private static final String TAG = AutoRunService.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Log.i(TAG, "start");
			ComponentName service = null;
			boolean isActiveUpdate = Boolean.parseBoolean(DBOper.getPreferences(context,IPreferences.FACTIVATE_UPDATE));
			ComponentName comp = new ComponentName(context.getPackageName(),
					WeatherService.class.getName());
			if (isActiveUpdate) 
				service = context.startService(new Intent().setComponent(comp));
			if (null == service)
				Log.e(TAG, "Could not start service " + comp.toString());
		  else Log.e(TAG, "Received unexpected intent " + intent.toString());
			Log.i(TAG, "stop");
		}
	}

}
