package com.pitz.findmybus.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BaseDao {

	private String url;
	private ArrayList<NameValuePair> params;
	private String response;
	private String errorMessage;
	private int responseCode;
	
	final NameValuePair contentType = new BasicNameValuePair("Content-Type", "application/json");
	final NameValuePair authorization = new BasicNameValuePair("Authorization", "Basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA== X-AppGlu-Environment: staging");
	final NameValuePair environment = new BasicNameValuePair("X-AppGlu-Environment", "staging");
	final String jsonParam = "params";
	
	public BaseDao(String url) 
	{
		this.url = url;
		this.params = new ArrayList<NameValuePair>();
	}

	public void executePost() 
	{
		HttpPost request = new HttpPost(url);

		request.addHeader(contentType.getName(), contentType.getValue());
		request.addHeader(authorization.getName(), authorization.getValue());
		request.addHeader(environment.getName(), environment.getValue());
		
		if (!params.isEmpty()) 
		{
			String message = createJSon();
	            
			try 
			{
				request.setEntity(new StringEntity(message, HTTP.UTF_8));
			} 
			catch (UnsupportedEncodingException e) {
				Log.d("Connection: ", e.getMessage());
				e.printStackTrace();
			}
		}
		
		executeRequest(request, url);
	}

	private void executeRequest(HttpUriRequest request, String utl) 
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			errorMessage = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);

				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			Log.d("Connection: ", e.getMessage());
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			Log.d("Connection: ", e.getMessage());
		}
	}

	private static String convertStreamToString(InputStream content) 
	{
		BufferedReader buff = new BufferedReader(new InputStreamReader(content));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = buff.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				content.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private String createJSon(){
		JSONObject jSonObj = new JSONObject();
		JSONObject child = new JSONObject();
		
		try 
		{
			for (NameValuePair pair : params) {
				child.put(pair.getName().toString(), pair.getValue().toString());
			}
			
			jSonObj.put(jsonParam, child);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return jSonObj.toString();
	}
	
	public void addParam(String name, String value) {
		this.params.add(new BasicNameValuePair(name, value));
	}

	public int getReponseCode() {
		return responseCode;
	}

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
