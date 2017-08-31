package com.test.amdocs;

import javax.json.JsonObject;

public class NearEarthObjectBean {

	public NearEarthObjectBean(double magnitude, JsonObject obj, double distanceFromEarth_KM) {
		super();
		this.magnitude = magnitude;
		this.obj = obj;
		this.distanceFromEarth_KM = distanceFromEarth_KM;
	}
	/**
	 * @return the magnitude
	 */
	public double getMagnitude() {
		return magnitude;
	}
	/**
	 * @param magnitude the magnitude to set
	 */
	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}
	/**
	 * @return the obj
	 */
	public JsonObject getObj() {
		return obj;
	}
	/**
	 * @param obj the obj to set
	 */
	public void setObj(JsonObject obj) {
		this.obj = obj;
	}
	/**
	 * @return the close_approach_distanceFromEarth
	 */
	public double getdistanceFromEarth_KM() {
		return distanceFromEarth_KM;
	}
	/**
	 * @param close_approach_distanceFromEarth the close_approach_distanceFromEarth to set
	 */
	public void setdistanceFromEarth_KM(double distanceFromEarth_KM) {
		this.distanceFromEarth_KM = distanceFromEarth_KM;
	}
	double magnitude;
	JsonObject obj;
	double distanceFromEarth_KM;
	

}
