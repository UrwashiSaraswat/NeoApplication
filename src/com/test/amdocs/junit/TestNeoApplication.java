/**
 * 
 */
package com.test.amdocs.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import javax.json.JsonObject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.test.amdocs.NeoApplication;
import com.test.amdocs.exceptions.ZeroNeoObjectsRetrievedException;

/**
 * @author urwashi
 * Junit tests the three methods of NeoApplicationClass by checking against
 *  json data retrived for date 29 august 2017
 */
public class TestNeoApplication {

	
	/* The total count of the 
	 * number of near earth objects retrieved.
	 * */
	static int totalNeoObjectsRetrieved=5;
	/*The key object to retrieve the value of the magnitude or the size of a near earth object
	 * */
	static final String magnitude_key="absolute_magnitude_h";
	/*
	 * To retrieve all near earth objects for 29 august 2017*/
	final static String timeStamp="2017-08-29";
	
	static NeoApplication application=new NeoApplication();
	
	/*The largest size value of largest near earth object*/
	double largestObjectSize=28.894;
	
	/*The smallest distance value of the nearest near earth object from earth*/
	double nearestObjectDistanceInKms=37700180;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		application.invokeNeoWebService(timeStamp,timeStamp);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		application=null;
	}

	@Test
	public void testcountAllNearEarthObjects() {
		int count=application.countAllNearEarthObjects();
		assertEquals(totalNeoObjectsRetrieved, count);
		
	}
	@Test
	public void testFindLargestSizeObject() {
		
		try {
			double d=(application.findLargestNearEarthObject().getJsonNumber(magnitude_key)).doubleValue();
			Assert.assertEquals(largestObjectSize, d, 0.01);
		} catch (ZeroNeoObjectsRetrievedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testfindNearestNearEarthObject() {
		JsonObject obj;
		try {
			obj = application.findNearestNearEarthObject();
			System.out.println(nearestObjectDistanceInKms-application.processDistanceFromEarth(obj));
			Assert.assertEquals(nearestObjectDistanceInKms, application.processDistanceFromEarth(obj),0.01);
		} catch (ZeroNeoObjectsRetrievedException e) {
			e.printStackTrace();
		}	
		
	}

}
