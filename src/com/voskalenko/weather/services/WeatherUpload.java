/*
 * @(#)WeatherUpload.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */

package com.voskalenko.weather.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.voskalenko.weather.datebase.DBOper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**The class is designed to download weather in the form of XML
 * Implemented as a timer that runs on a schedule. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */  

public class WeatherUpload extends Observable {
	private Timer timer;
	public static final String URL_GISMETEO_SUITE = "http://informer.gismeteo.ru/xml/";
	private static final String TAG = WeatherUpload.class.getSimpleName();
	private Context ctx;

	public WeatherUpload(Context ctx) {
		this.ctx = ctx;
	}

	public WeatherUpload(Observer observer) {
		super();
		this.addObserver(observer);
		ctx = (Context) observer;
		timer = new Timer();
	}
	
/**This method is caused the getXml method according to the schedule*/
	
	public List<String> uploadContent() {
		List<String> xmlContentLst = new ArrayList<String>();
		Cursor cur = DBOper.getChosenCities(ctx,false);
		//try {
			if (cur.moveToFirst()) {
				do {
					 String content = getXML(URL_GISMETEO_SUITE +cur.getString(0)+ "_1.xml");
					 xmlContentLst.add(content);
					 //I used this code to load from a local folder 
					/*InputStream is = ctx.getAssets().open(cur.getString(0) + "_1.xml");
					int size = is.available();
					byte[] buffer = new byte[size];
					is.read(buffer);
					is.close();
					xmlContentLst.add(new String(buffer));*/
				} while (cur.moveToNext());
			}

		/*} catch (IOException e) {
			e.printStackTrace();
		}*/
		cur.close();
		return xmlContentLst;
	}

	public void uploadContentOnce(){
		setChanged();
		notifyObservers(uploadContent());
	}
	public void start(int uploading_freq) {
		timer.schedule(new TimerTask() {
			public void run() {
				Log.i(TAG,"start upload");
				List<String> xmlContentLst = uploadContent();
				if (xmlContentLst != null) {
					setChanged();
					notifyObservers(xmlContentLst);
				}
			}
		}, 0, uploading_freq);
	}

	public void stop() {
		timer.cancel();
	}

/**The method connects to the data source, and takes necessary XML content*/
	
	private String getXML(String wUrl) {
		String result = null;
		InputStream stream = null;
		BufferedReader reader;
		StringBuilder sb;
		String line;
		URL url = null;
		try {
			url = new URL(wUrl);
			HttpURLConnection connect = (HttpURLConnection) url
					.openConnection();
			if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = connect.getInputStream();
				reader = new BufferedReader(new InputStreamReader(stream));
				sb = new StringBuilder();
				line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
			}

		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return result;
	}
}
