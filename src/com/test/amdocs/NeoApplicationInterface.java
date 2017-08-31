package com.test.amdocs;

import java.util.List;

import javax.json.JsonObject;

import com.test.amdocs.exceptions.InvalidDateException;
import com.test.amdocs.exceptions.ZeroNeoObjectsRetrievedException;
/*
 * The interface provides the methods to invoke neo webservice 
 * available at https://api.nasa.gov/api.html#NeoWS. 
 * It provides methods to lists and find and retrieved near earth objects from the 
 * webservice 
 * */
public interface NeoApplicationInterface {
	/*
	 * The generated api_key for authentication to the web service	
	 *  */
	static final String API_KEY="x036DohuwjzihfkqjvbY7OOpOa17Q08aUjTTpvAz";
	
	/*Invokes the neo web service
	 * */
	public NeoApplication invokeNeoWebService(String startTimeStamp ,String endTimeStamp) throws InvalidDateException;
	
	/*
	 * @return List of the JsonObjects of all near earth objects retrieved 
	 * @throws ZeroNeoObjectsRetrievedException
	 * */
	public List<JsonObject> listNearEarthObjects() throws ZeroNeoObjectsRetrievedException;
	
	/*
	 * @return The JsonObject of the Largest near earth object retrieved 
	 * */
	public JsonObject findLargestNearEarthObject() throws ZeroNeoObjectsRetrievedException;
	
	/*
	 * @return The JsonObject of the Nearest near earth object retrieved 
	 * */
	public JsonObject findNearestNearEarthObject() throws ZeroNeoObjectsRetrievedException;
	
	/*
	 * @return The count of the total number of near earth objects retrieved 
	 * */
	public int countAllNearEarthObjects();
	
/*	  Sets the startdate in format YYYY-MM-DD for which the neo webservice call is invoked
	 * 
	public void setStartDate(String timestamp);
	
	Returns the start date in format YYYY-MM-DD
	 * 
	public String getStartDate();
	
	  Sets the enddate in format YYYY-MM-DD for which the neo webservice call is invoked
	 * 
	public void setEndDate(String timestamp);
	
	Returns the end date in format YYYY-MM-DD
	 * 
	public String getEndDate();*/

}
