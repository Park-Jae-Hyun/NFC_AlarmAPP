package kr.androidteam.alarm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class findWoeid {
	LocationManager manager;
	Location location;
	String locationProvider;
	Thread myThread;
	 String woeidResult;
	 String woeid;
	 Context context;
	 private String WOEID_QUERY_PREFIX_FIND_BY_GPS = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text%3D%22";
		private String WOEID_QUERY_CONSECTION_FIND_BY_GPS = "%2C";
		private String WOEID_QUERY_SUFFIX_FIND_BY_GPS = "%22%20and%20gflags%3D%22R%22&";
	
		public findWoeid(Context context){
		
			this.context=context;
			loadYahooWeather();
		
			
	}
	
	
	protected void loadYahooWeather(){

        		
        		String weatherString = QueryYahooWeather();
        		
        		Document weatherDoc = convertStringToDocument(weatherString);
        	      
        		 woeidResult = parsewoeid(weatherDoc);

	}
	public String getWoeid(){
		return woeidResult;
	}

private String QueryYahooWeather(){
    	
    	String qResult = "";
		String latitude="37.6216493";
    	String longitude="126.913107";
    	manager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location _location) {
				location=_location;
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};	


		//gps를 사용해 해당 위치를 받을 수 있을 시
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

			location=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		}
		
		//gps를 사용하지 못할 시 네트워크를 사용해 위치를 받아오게 만든다.
		if(location==null)
			location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	

		if (location != null) {

			latitude=Double.toString(location.getLatitude());
			
			longitude=Double.toString(location.getLongitude());
		}
		
		
		Log.e("latitude", latitude);
		Log.e("longitude",longitude);
	
		String queryString = WOEID_QUERY_PREFIX_FIND_BY_GPS + latitude +
				 WOEID_QUERY_CONSECTION_FIND_BY_GPS + longitude +
				 WOEID_QUERY_SUFFIX_FIND_BY_GPS;
		
		HttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(queryString);
    	  
    	try {
    		HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
    		
    		if (httpEntity != null){
    			InputStream inputStream = httpEntity.getContent();
    			Reader in = new InputStreamReader(inputStream);
    			BufferedReader bufferedreader = new BufferedReader(in);
    			StringBuilder stringBuilder = new StringBuilder();
    	     
    			String stringReadLine = null;

    			while ((stringReadLine = bufferedreader.readLine()) != null) {
    				stringBuilder.append(stringReadLine + "\n");	
    			}
    	     
    			qResult = stringBuilder.toString();	
    		}	
    	}
    	
    	catch (ClientProtocolException e) {
    		e.printStackTrace();	
    	} 
    	
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	Log.e("asd",qResult);
    	return qResult;	
    }


private Document convertStringToDocument(String src){
	Document dest = null;
	DocumentBuilderFactory dbFactory =
			DocumentBuilderFactory.newInstance();
	DocumentBuilder parser;
	
	try {
		parser = dbFactory.newDocumentBuilder();
		dest = parser.parse(new ByteArrayInputStream(src.getBytes()));	
	} catch (ParserConfigurationException e1) {
		e1.printStackTrace();
		Toast.makeText(context,
				e1.toString(), Toast.LENGTH_LONG).show();	
	} catch (SAXException e) {
		e.printStackTrace();
			} catch (IOException e) {
		e.printStackTrace();
			}
	
	return dest;	
}


public String parsewoeid(Document srcDoc){

	String woeid;

	woeid = srcDoc.getElementsByTagName("woeid").item(0).getTextContent();
	return woeid;
}


}
