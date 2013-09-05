/*
 * @(#)GisMeteoOper.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.voskalenko.weather.R;
import com.voskalenko.weather.GisMeteoWeather.TownData;
import com.voskalenko.weather.datebase.DBOper;
import com.voskalenko.weather.datebase.ForecastProvider;
import com.voskalenko.weather.datebase.IForecastJournal;
import com.voskalenko.weather.services.AutoRunService;

/**Class implements static methods to work with weather data.
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class GisMeteoOper implements IForecastJournal{
	private static final String TAG = GisMeteoOper.class.getSimpleName();
	  
	/**
	 * Method saves the weather information into the database by using content
	 * provider
	 */
	public static void saveToDb(Context ctx,
			List<GisMeteoWeather.TownData> lstTowns) {
		ContentValues row = new ContentValues();
		String city_name;
		DBOper.clearJournal(ctx);
		for (TownData town : lstTowns) {
			city_name = DBOper.getCityNameByCode(ctx, town.getCode());
			for (TownData.ForecastData fr : town.getLstForecasts()) {
				row.put(FCITY_CODE, town.getCode());
				row.put(FSNAME, city_name);
				row.put(FLATITUDE, town.getLatitude());
				row.put(FLONGITUDE, town.getLongitude());
				row.put(FDATE, (new SimpleDateFormat("dd.MM.yyyy")).format(fr.getDate().getTime()));
				row.put(FTOD, fr.getTod());
				row.put(FWEEKDAY, fr.getWeekday());
				row.put(FPREDICT, fr.getPredict());
				row.put(FCLOUDINESS, fr.getPhenomena().getCloudiness());
				row.put(FPRECIPITATION, fr.getPhenomena().getPrecipitation());
				row.put(FRPOWER, fr.getPhenomena().getRpower());
				row.put(FSPOWER, fr.getPhenomena().getSpower());
				row.put(FPRESSURE_MAX, fr.getPressure().getMax());
				row.put(FPRESSURE_MIN, fr.getPressure().getMin());
				row.put(FTEMPERATURE_MAX, fr.getTemperature().getMax());
				row.put(FTEMPERATURE_MIN, fr.getTemperature().getMin());
				row.put(FWIND_MAX, fr.getWind().getMax());
				row.put(FWIND_MIN, fr.getWind().getMin());
				row.put(FWIND_DIRECTION, fr.getWind().getDirection());
				row.put(FRELWET_MAX, fr.getRelwet().getMax());
				row.put(FRELWET_MIN, fr.getRelwet().getMin());
				row.put(FHEAT_MAX, fr.getHeat().getMax());
				row.put(FHEAT_MIN, fr.getHeat().getMin());
				DBOper.insertToJournal(ctx, row);
				row.clear();
			}
		}
		ctx.getContentResolver().notifyChange(ForecastProvider.CONTENT_URI, null);
	}

	/**The method returns the picture identifier for the necessary weather
	 * forecast
	 */
	public static int geWeatherIconId(int tod, int cloudiness, int precipitation) {
		Class<R.drawable> cls = R.drawable.class;
		Field field;
		int id = 0;
		String nd = (new String[] { "dark_", "light_", "light_", "dark_" })[tod];
		String cl = (new String[] { "0cloud", "1cloud_", "1cloud_", "3cloud_" })[cloudiness];
		String pr = "";
		if (cloudiness > 0) {
			pr = (new String[] { "", "", "", "", "modrain", "heavyrain",
					"snow", "snow", "thunders", "norain", "norain" })[precipitation];
		}
		String icon_name = nd + cl + pr;
		try {
			field = cls.getField(icon_name);
			id = field.getInt(field);
		} catch (Exception e) {
			Log.e(TAG+" Method geWeatherIconId", String.format("Can not load icon [%1$s]", icon_name), e);
		}
		return id;
	}

	/**The method returns the picture identifier for the necessary wind
	 * direction
	 */
	public static int getWindDirectIconId(int wind_direction) {
		Class<R.drawable> cls = R.drawable.class;
		Field field;
		int id = 0;
		String icon_name = "wind_direct_" + wind_direction;
		try {
			field = cls.getField(icon_name);
			id = field.getInt(field);
		} catch (Exception e) {
			Log.e(TAG+" Method getWindDirectIconId", String.format("Can not load icon [%1$s]", icon_name), e);
		}
		return id;
	}

	/** Returns current daypart by time of day 
	 * */
	public static int getCurrTod() {
		int tod = 0;
		Calendar cl = Calendar.getInstance();
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		if ((hour >= 1) && (hour <= 5))
			tod = 0;
		else if ((hour >= 6) && (hour <= 11))
			tod = 1;
		else if ((hour >= 12) && (hour <= 17))
			tod = 2;
		else if ((hour >= 18) && (hour <= 24))
			tod = 3;
		return tod;
	}
}
