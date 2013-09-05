/*
 * @(#)ICities.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
 
package com.voskalenko.weather.datebase;


/**Interface describes the columns of the table Cities
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */ 

public interface ICities {
// field of cities
	public static final String TBL_CITIES = "cities";
	public static final String FID = "_id";
	public static final String FCOUNTRY = "country";
	public static final String FCITY_NAME = "city_name";
}
