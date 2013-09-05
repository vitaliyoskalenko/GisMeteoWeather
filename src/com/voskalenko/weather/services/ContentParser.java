/*
 * @(#)ContentParser.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */
  
package com.voskalenko.weather.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.voskalenko.weather.GisMeteoWeather;
import com.voskalenko.weather.GisMeteoWeather.TownData.ForecastData.*;

/**An instance of the Class "ContentParser"  processes 
 * the received data from the server of gismeteo.ru. 
 * Based on the data it creates an instance of TownData. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */

public class ContentParser extends DefaultHandler {
	private GisMeteoWeather.TownData town;
	private GisMeteoWeather.TownData.ForecastData forecast;
	private PhenomenaData phenomena;
	private PressureData pressure;
	private TemperatureData temperature;
	private WindData wind;
	private RelwetData relwet;
	private HeatData heat;
	private static ContentParser instance;
	private static final String TAG = ContentParser.class.getSimpleName();

	public static ContentParser getInstatnce() {
		if (instance == null) {
			instance = new ContentParser();
		}
		return instance;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {
		if (qName.equalsIgnoreCase("TOWN")) {
			town = new GisMeteoWeather.TownData(Integer.parseInt(attr
					.getValue("index")),
			/* attr.getValue("sname") */null, // I take city name from table
												// "cities"
					Integer.parseInt(attr.getValue("latitude")),
					Integer.parseInt(attr.getValue("longitude")));
		}
		if (qName.equalsIgnoreCase("FORECAST")) {
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(attr.getValue("year")),
					Integer.parseInt(attr.getValue("month"))-1,
					Integer.parseInt(attr.getValue("day")),
					Integer.parseInt(attr.getValue("hour")), 0);
			forecast = new GisMeteoWeather.TownData.ForecastData(cal,
					Integer.parseInt(attr.getValue("tod")),
					Integer.parseInt(attr.getValue("weekday")),
					Integer.parseInt(attr.getValue("predict")));
		}
		if (qName.equalsIgnoreCase("PHENOMENA")) {
			phenomena = new PhenomenaData(Integer.parseInt(attr
					.getValue("cloudiness")), Integer.parseInt(attr
					.getValue("precipitation")), Integer.parseInt(attr
					.getValue("rpower")), Integer.parseInt(attr
					.getValue("spower")));
		}
		if (qName.equalsIgnoreCase("PRESSURE")) {
			pressure = new PressureData(Integer.parseInt(attr.getValue("max")),
					Integer.parseInt(attr.getValue("min")));
		}
		if (qName.equalsIgnoreCase("TEMPERATURE")) {
			temperature = new TemperatureData(Integer.parseInt(attr
					.getValue("max")), Integer.parseInt(attr.getValue("min")));
		}
		if (qName.equalsIgnoreCase("WIND")) {
			wind = new WindData(Integer.parseInt(attr.getValue("max")),
					Integer.parseInt(attr.getValue("min")),
					Integer.parseInt(attr.getValue("direction")));
		}
		if (qName.equalsIgnoreCase("RELWET")) {
			relwet = new RelwetData(Integer.parseInt(attr.getValue("max")),
					Integer.parseInt(attr.getValue("min")));
		}
		if (qName.equalsIgnoreCase("HEAT")) {
			heat = new HeatData(Integer.parseInt(attr.getValue("max")),
					Integer.parseInt(attr.getValue("min")));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("FORECAST")) {
			town.getLstForecasts().add(forecast);
		}
		if (qName.equalsIgnoreCase("PHENOMENA")) {
			forecast.setPhenomena(phenomena);
		}
		if (qName.equalsIgnoreCase("PRESSURE")) {
			forecast.setPressure(pressure);
		}
		if (qName.equalsIgnoreCase("TEMPERATURE")) {
			forecast.setTemperature(temperature);
		}
		if (qName.equalsIgnoreCase("WIND")) {
			forecast.setWind(wind);
		}
		if (qName.equalsIgnoreCase("RELWET")) {
			forecast.setRelwet(relwet);
		}
		if (qName.equalsIgnoreCase("HEAT")) {
			forecast.setHeat(heat);
		}
	}

	public GisMeteoWeather.TownData parse(String XML) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			reader.parse(new InputSource(new StringReader(XML)));
		} catch (ParserConfigurationException e) {
			Log.e(TAG,"ParserConfig error");
		} catch (SAXException e) {
			Log.e(TAG,"SAXException : xml not well formed");
		} catch (IOException e) {
			Log.e(TAG,"IO error");
		}
		return town;
	}
}
