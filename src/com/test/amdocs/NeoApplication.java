package com.test.amdocs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.test.amdocs.exceptions.InvalidDateException;
import com.test.amdocs.exceptions.ZeroNeoObjectsRetrievedException;

/*
 * Java Application that processes the list of near earth objects retrieved from rest service for the configured date.
 * It prints the total number of near earth objects retrieved , the details of the Largest size
 * near earth object and the details of the near earth object which is nearest to earth.
 * 
 * The distance from earth is processed by retrieving the key value of "miss_distance" object
 * inside the close_approach_date object of a near earth object retrieved.
 * 
 * */
public class NeoApplication implements NeoApplicationInterface{
	

	
	/*The Root object of the json data structure retrieved from the neo webservice
	 * */
	JsonObject topJsonObject=null;
	
	/*
	 * Array of the near earth objects retrieved from the root json data retrieved from neo webservice
	 * */
	List<JsonArray> listFinal=new ArrayList<JsonArray>();;
	
	/* The total count of the 
	 * number of near earth objects retrieved.
	 * */
	int totalNeoObjectsRetrieved=0;
	
	/*The key object to retrieve the value of the magnitude or the size of a near earth object
	 * */
	static final String magnitude_key="absolute_magnitude_h";
	/*
	 * The list of processed near earth objects
	 * */
	ArrayList<NearEarthObjectBean> list=null;
	/*
	 * The timestamp for start date for which user can retrieve the near earth objects from webservice
	 * */
	String starttimeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	/*
	 * The timestamp for end date for which user can retrieve the near earth objects from webservice
	 * */
	String endtimeStamp=starttimeStamp;
	
	public static void main(String[] args) throws ZeroNeoObjectsRetrievedException,InvalidDateException {
		Scanner sc = null;
		try{
		sc=new Scanner(System.in);
		System.out.print("Enter start date in YYYY-MM-DD format to invoke neo webservice");
		String starttimeStamp=sc.nextLine();

		System.out.print("Enter end date in YYYY-MM-DD format to invoke neo webservice");
		
		String endtimeStamp=sc.nextLine();
		NeoApplication app=getInstance();
		app=app.invokeNeoWebService(starttimeStamp,endtimeStamp);
		
		System.out.println("Total number of near earth objects retrieved = "+app.totalNeoObjectsRetrieved);
		JsonObject neo=app.findLargestNearEarthObject();
		if(neo!=null)
		{	System.out.println("\nDetails of the largest size near earth object retrieved =\n");
			printNeo(neo);
		}
			
		neo=app.findNearestNearEarthObject();
		if(neo!=null){
			System.out.println("\nDetails of the nearest to earth, near eath object retrieved =\n");
			printNeo(neo);
		}
		List<JsonObject> beanList=app.listNearEarthObjects();
		int ctr=0;
		while(ctr<beanList.size()){
			JsonObject neoObject=beanList.get(ctr);
			System.out.println("Neo Object referenceId="+neoObject.getString("neo_reference_id")+" has size= "+neoObject.getJsonNumber(magnitude_key).doubleValue()+" and distance from earth in kms is="+app.processDistanceFromEarth(neoObject));
			ctr++;
		}
	}finally{
		if(sc!=null)
		sc.close();
	}
		
		
	}
	/* Invokes the neo webservice for the configured time stamp date
	 * and populates the json root object and array of near earth objects
	 * @returns NeoApplication object if no exception adn error is found else returns null
	 * @throws InvalidDateException if the entered startdate or enddate is invalid
	 * */
	public NeoApplication invokeNeoWebService(String startTimeStamp ,String endTimeStamp) throws InvalidDateException{
		JsonReader reader=null;
		NeoApplication application;
		try {
			if(isValidDate(startTimeStamp) && isValidDate(endTimeStamp)){
				this.starttimeStamp=startTimeStamp;
				this.endtimeStamp=endTimeStamp;
			}else{
				throw new InvalidDateException("The date entered is invalid.It should be valid date in format yyyy-MM-dd");
			}
			System.out.println("Retrieving near earth objects for the start date==="+startTimeStamp+" and end date ="+endTimeStamp);
			URL	url = new URL(
					    "https://api.nasa.gov/neo/rest/v1/feed?start_date="+startTimeStamp+"&end_date="+endTimeStamp+"&api_key="+API_KEY);
			
			HttpURLConnection	conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
			
				throw new RuntimeException("Failed : HTTP error code : "+conn.getResponseCode()+
				   ": " + conn.getResponseMessage()+" : "+ readStream(conn.getErrorStream()));
			
			}
			reader=Json.createReader(conn.getInputStream());
			topJsonObject=reader.readObject();
			reader.close();
			JsonObject neoObjects=topJsonObject.getJsonObject("near_earth_objects");
			this.totalNeoObjectsRetrieved=topJsonObject.getInt("element_count");
			
			Iterator<String> itr=neoObjects.keySet().iterator();			
			while(itr.hasNext()){
				listFinal.add(neoObjects.getJsonArray(itr.next()));
			}
			
			
			application=this;
			
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
				return null;
			}			
			catch (IOException e) {
				
				e.printStackTrace();
				return null;
			}finally{
				if(reader!=null)
				reader.close();
			}
		return application;
		
	}
	
	/*
	 * @return List of the JsonObjects of all near earth objects retrieved 
	 * @throws ZeroNeoObjectsRetrievedException
	 * */
	public List<JsonObject> listNearEarthObjects() throws ZeroNeoObjectsRetrievedException
	{
		if(totalNeoObjectsRetrieved==0){
			throw new ZeroNeoObjectsRetrievedException("No neo objects retrieved");
		}
		List<JsonObject> neoObjects=new ArrayList<JsonObject>(this.totalNeoObjectsRetrieved);
		int counter=0;
		while(counter<listFinal.size()){
			
			JsonArray arr=(JsonArray) listFinal.get(counter);
			int innerCtr=0;
			while(innerCtr<arr.size()){
				neoObjects.add((JsonObject) arr.get(innerCtr));
				innerCtr++;
			}
			counter++;
		}
		return neoObjects;
		
	}
	/*
	 * @return The JsonObject of the Largest near earth object retrieved 
	 * */
	public JsonObject findLargestNearEarthObject() throws ZeroNeoObjectsRetrievedException{
		if(totalNeoObjectsRetrieved==0){
			throw new ZeroNeoObjectsRetrievedException("No neo objects retrieved");
		}
		List<NearEarthObjectBean> neos=processListOfNEO();
		Collections.sort(neos, new SizeComparator());		
		return neos.get(totalNeoObjectsRetrieved-1).getObj();
		
	}
	/*
	 * @return The JsonObject of the Nearest near earth object retrieved 
	 * */
	public JsonObject findNearestNearEarthObject() throws ZeroNeoObjectsRetrievedException{
		if(totalNeoObjectsRetrieved==0){
			throw new ZeroNeoObjectsRetrievedException("No neo objects retrieved");
		}
		List<NearEarthObjectBean> neos=processListOfNEO();
		Collections.sort(neos, new DistanceFromEarthComparator());
		return neos.get(0).getObj();
		
	}
	/*
	 * @return The count of the total number of near earth objects retrieved 
	 * */
	public int countAllNearEarthObjects(){
		return totalNeoObjectsRetrieved;
		
	}
	/*
	 * The method processes the list of near earth objects by 
	 * retrieving the size attribute, the distance from earth in kms from near
	 * earth object retrieved.It then creates new NearEarThObject java object with 
	 * these both attributes and the reference to JsonObject and adds to an Arraylist
	 * 
	 * @returns the ArrayList of newly created NearEarthObjects
	 * */
	List<NearEarthObjectBean> processListOfNEO(){
		
		if(list!=null){
			return list;
		}
		list=new ArrayList<NearEarthObjectBean>(totalNeoObjectsRetrieved);

		try{
			int counter=0;
			while(counter<listFinal.size()){
			
			JsonArray arr=(JsonArray) listFinal.get(counter);
			int innerCtr=0;
			while(innerCtr<arr.size()){
				JsonObject obj=(JsonObject) arr.get(innerCtr);
				double distance=processDistanceFromEarth(obj);
				list.add(new NearEarthObjectBean(obj.getJsonNumber(magnitude_key).doubleValue(),obj,distance));
				
				innerCtr++;
				
				}
			counter++;
		}
		}finally{
			
		}
		return list;		
	}
	
	/*Prints the details of all keys and corresponding values inside the Near earth json object
	 * */
	 static void printNeo(JsonObject object){
		Iterator<String> itr=object.keySet().iterator();
		while(itr.hasNext()){
			String key=itr.next();
			System.out.println(key+"::"+object.get(key));
		}
		
	}

	class SizeComparator implements Comparator<NearEarthObjectBean>{

		@Override
		public int compare(NearEarthObjectBean o1, NearEarthObjectBean o2) {
			double val1=o1.getMagnitude();
			double val2=o2.getMagnitude();
			if(val1>val2){
				return 1;
			}else if(val1==val2){
				return 0;
			}else{
				return -1;
			}
		}
		
	}
	class DistanceFromEarthComparator implements Comparator<NearEarthObjectBean>{


		@Override
		public int compare(NearEarthObjectBean o1, NearEarthObjectBean o2) {
			double val1=o1.getdistanceFromEarth_KM();
			double val2=o2.getdistanceFromEarth_KM();
			if(val1>val2){
				return 1;
			}else if(val1==val2){
				return 0;
			}else{
				return -1;
			}
		}
		
	}
	/*
	 * @returns the distance from earth in kms for a specific 
	 * close approach data object of a near earth object
	 * */
	public double processDistanceFromEarth(JsonObject obj){
		JsonArray val=(JsonArray) obj.get("close_approach_data");
		Iterator<JsonValue> itr=val.iterator();
		while(itr.hasNext()){
			JsonObject object=(JsonObject) itr.next();
			JsonString kms=object.getJsonObject("miss_distance").getJsonString("kilometers");
			return Double.parseDouble(kms.getString());
		}
		
		return 0;
	}
	/*
	 * Utility method to check the validity of specified date in format yyyy-MM-dd
	 * */
	static boolean isValidDate(String dateString) {
	    if (dateString == null || !dateString.matches("\\d{4}-[01]\\d-[0-3]\\d"))
	        return false;
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    df.setLenient(false);
	    try {
	        df.parse(dateString);
	        return true;
	    } catch (ParseException ex) {
	        return false;
	    }
	}
	
	static NeoApplication getInstance(){
		return new NeoApplication();
	}
	/* Utility method to read the error stream when httpresponse code is not 200 returned
	 * by the neowebservice
	 * */
	private String readStream(InputStream stream) throws IOException {
	    StringBuilder builder = new StringBuilder();
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
	        String line;
	        while ((line = in.readLine()) != null) {
	            builder.append(line); 
	        }
	        in.close();
	    }
	    return builder.toString();
	}

}
