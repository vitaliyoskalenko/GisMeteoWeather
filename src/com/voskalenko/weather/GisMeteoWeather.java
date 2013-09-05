/*
 * @(#)GisMeteoWeather.java  1.0 2012/12/25
 *
 * Copyright (C) 2012 Vitaly Oskalenko, oskalenkoVit@ukr.net 
 * This is an open source project that can be used for own purposes
 * but should be released under name of new owner
 */

package com.voskalenko.weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**The class stores information about weather on the chosen cities.
 * The class contains internal subclasses which store all 
 * characteristics of a weather forecast. 
 * @version 1.0 25 Dec 2012
 * @author  Vitaly Oskalenko
 */        

public class GisMeteoWeather {
	private List<TownData> lstTowns;

	public GisMeteoWeather() {
		this.lstTowns = new ArrayList<TownData>();
	}

	public List<TownData> getlstTowns() {
		return lstTowns;
	}

	/**
	 * The class provides information about city and also provides the necessary
	 * subclasses with a detailed forecast
	 */
	public static class TownData {
		private long code;
		private String sname;
		private int latitude;
		private int longitude;
		private List<TownData.ForecastData> lstForecasts;

		public TownData(long code, String sname, int latitude, int longitude) {
			this.code = code;
			this.sname = sname;
			this.latitude = latitude;
			this.longitude = longitude;
			lstForecasts = new ArrayList<ForecastData>();
		}

		
		public long getCode() {
			return code;
		}

		public String getSname() {
			return sname;
		}

		public int getLatitude() {
			return latitude;
		}

		public int getLongitude() {
			return longitude;
		}

		/** The list of weather forecasts for the night, morning, day, evening */
		public List<TownData.ForecastData> getLstForecasts() {
			return lstForecasts;
		}

		/**
		 * Subclass provides detailed information on weather. And provides
		 * information on the temperature, pressure, humidity, precipitation,
		 * and so on.
		 */
		public static class ForecastData {
			public static class PhenomenaData {
				private int cloudiness;
				private int precipitation;
				private int rpower;
				private int spower;

				public PhenomenaData(int cloudiness, int precipitation, int rpower, int spower) {
					this.cloudiness = cloudiness;
					this.precipitation = precipitation;
					this.rpower = rpower;
					this.spower = spower;
				}

				public int getCloudiness() {
					return cloudiness;
				}

				public int getPrecipitation() {
					return precipitation;
				}

				public int getRpower() {
					return rpower;
				}

				public int getSpower() {
					return spower;
				}
			}

			public static class PressureData {
				private int max;
				private int min;

				public PressureData(int max, int min) {
					this.max = max;
					this.min = min;
				}

				public int getMax() {
					return max;
				}

				public int getMin() {
					return min;
				}
			}

			public static class TemperatureData {
				private int max;
				private int min;

				public TemperatureData(int max, int min) {
					this.max = max;
					this.min = min;
				}

				public int getMax() {
					return max;
				}

				public int getMin() {
					return min;
				}
			}

			public static class WindData {
				private int min;
				private int max;
				private int direction;

				public WindData(int max, int min, int direction) {
					this.max = max;
					this.min = min;
					this.direction = direction;
				}

				public int getMin() {
					return min;
				}

				public int getMax() {
					return max;
				}

				public int getDirection() {
					return direction;
				}
			}

			public static class RelwetData {
				private int max;
				private int min;

				public RelwetData(int max, int min) {
					this.max = max;
					this.min = min;
				}

				public int getMax() {
					return max;
				}

				public int getMin() {
					return min;
				}
			}

			public static class HeatData {
				private int max;
				private int min;

				public HeatData(int max, int min) {
					this.max = max;
					this.min = min;
				}

				public int getMax() {
					return max;
				}

				public int getMin() {
					return min;
				}

			}

			private Calendar date;
			private int tod;
			private int weekday;
			private int predict;
			private PhenomenaData phenomena;
			private PressureData pressure;
			private TemperatureData temperature;
			private WindData wind;
			private RelwetData relwet;
			private HeatData heat;

			public ForecastData(Calendar date, int tod, int weekday, int predict) {
				this.date = date;
				this.tod = tod;
				this.weekday = weekday;
				this.predict = predict;
			}

			public Calendar getDate() {
				return date;
			}

			public int getTod() {
				return tod;
			}

			public int getWeekday() {
				return weekday;
			}

			public int getPredict() {
				return predict;
			}

			public void setPhenomena(PhenomenaData phenomena) {
				this.phenomena = phenomena;
			}

			public void setPressure(PressureData pressure) {
				this.pressure = pressure;
			}

			public void setTemperature(TemperatureData temperature) {
				this.temperature = temperature;
			}

			public void setWind(WindData wind) {
				this.wind = wind;
			}

			public void setRelwet(RelwetData relwet) {
				this.relwet = relwet;
			}

			public void setHeat(HeatData heat) {
				this.heat = heat;
			}

			public PhenomenaData getPhenomena() {
				return phenomena;
			}

			public PressureData getPressure() {
				return pressure;
			}

			public TemperatureData getTemperature() {
				return temperature;
			}

			public WindData getWind() {
				return wind;
			}

			public RelwetData getRelwet() {
				return relwet;
			}

			public HeatData getHeat() {
				return heat;
			}
		}
	}
}
