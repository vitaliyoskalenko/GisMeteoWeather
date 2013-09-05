/*
 * @(#)IForecastJournal.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
 
package com.voskalenko.weather.datebase;


/**Interface describes the columns of the table ForecastJournal
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */ 

public interface IForecastJournal {
// Fields of forecast fournal
	public static final String TBL_FORECAST_JOURNAL = "forecast_journal";
	public static final String FCITY_CODE = "city_id";
	public static final String FSNAME = "sname";
	public static final String FLATITUDE = "latitude";
	public static final String FLONGITUDE = "longitude";
	public static final String FDATE = "date";
	public static final String FTOD = "tod";
	public static final String FWEEKDAY = "weekday";
	public static final String FPREDICT = "predict";
	public static final String FCLOUDINESS = "cloudiness";
	public static final String FPRECIPITATION = "precipitation";
	public static final String FRPOWER = "rpower";
	public static final String FSPOWER = "spower";
	public static final String FPRESSURE_MAX = "pressure_max";
	public static final String FPRESSURE_MIN = "pressure_min";
	public static final String FTEMPERATURE_MAX = "temperature_max";
	public static final String FTEMPERATURE_MIN = "temperature_min";
	public static final String FWIND_MAX = "wind_max";
	public static final String FWIND_MIN = "wind_min";
	public static final String FWIND_DIRECTION = "wind_direction";
	public static final String FRELWET_MAX = "relwet_max";
	public static final String FRELWET_MIN = "relwet_min";
	public static final String FHEAT_MAX = "heat_max";
	public static final String FHEAT_MIN = "heat_min";
}
