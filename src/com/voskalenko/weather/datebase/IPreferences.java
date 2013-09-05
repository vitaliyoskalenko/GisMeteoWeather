/*
 * @(#)IPreferences.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.datebase;

/**Interface describes the columns of the table Preferences
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public interface IPreferences {
// fields of preferences
	public static final String TBL_PREFERENCES = "preferences";
	public static final String FWIDGET_WIEW_MODE = "widget_view_mode";
	public static final String FFAVORITE_CITY_CODE = "favorite_city_id";
	public static final String FACTIVATE_UPDATE = "activate_update";
	public static final String FUPDATE_FREQUENCY = "update_frequency";
	public static final String FUPDATE_RADIO_POS = "update_radio_pos";
}
