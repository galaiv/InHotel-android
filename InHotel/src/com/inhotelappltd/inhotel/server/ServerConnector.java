package com.inhotelappltd.inhotel.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

import com.inhotelappltd.inhotel.http.MultipartEntityBuilder;
import com.inhotelappltd.inhotel.utils.CommonActions;

public class ServerConnector {
	Context context;
	HttpPost httppost;
	
	public ServerConnector(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}


	/**
	 * Returns the response from the server by calling the given url
	 * @param url
	 * @return
	 */
	public String getResponseFromServer(String url,String function,String parameters,String token) {
		HttpParams params = new BasicHttpParams();
		//params.setParameter("data", auth);
		HttpClient httpclient = new DefaultHttpClient(params);

		HttpPost httpPost = new HttpPost(url);
		String serverResponse="";
		try {
			String json = "{\"function\":\"" + function + "\",\"parameters\":"
					+ parameters + ",\"token\":" + token + "}";
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			Log.e("TO SERVER ", json);
			StringBuilder response_string_builder = new StringBuilder();

			// JSONObject json_object = null;

			HttpResponse response = httpclient.execute(httpPost);
			InputStream input_stream = response.getEntity().getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					input_stream));
			String line;
			while ((line = r.readLine()) != null) {
				response_string_builder.append(line);
			}
			// json_object = new JSONObject(response_string_builder.toString());
			serverResponse = response_string_builder.toString();
			Log.e("FROM SERVER", response_string_builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	
	
	
	public String  doRegistration(String type,MultipartEntityBuilder list) {
	
		Log.e("Lll",type+")))");
		String serverResponse="";
		StringBuilder response_string_builder = new StringBuilder();
		
		
		Log.e("doRegistration", list.toString()+"**");
       
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
		if (type.equalsIgnoreCase("registration")) {
			httppost = new HttpPost(CommonActions.appURL+ "/register");
		} else if (type.equalsIgnoreCase("login")) {
			httppost = new HttpPost(CommonActions.appURL + "/login");
		} else if (type.equalsIgnoreCase("account")) {
			httppost = new HttpPost(CommonActions.appURL + "/register");
		} else if(type.equalsIgnoreCase("drink")){
			httppost = new HttpPost(CommonActions.appURL + "/send_message");
		}
			
	    
	   
	    try {
	    	httppost.setEntity(list.build());
	    	//httppost.setEntity(mpEntity);
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        InputStream input_stream = response.getEntity().getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(
					input_stream));
			String line;
			while ((line = r.readLine()) != null) {
				response_string_builder.append(line);
			}
			//json_object = new JSONObject(response_string_builder.toString());
			serverResponse=response_string_builder.toString();
			Log.e("FROM SERVER", response_string_builder.toString() + "");
	      
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    }
	    return serverResponse;
	} 
	
}
