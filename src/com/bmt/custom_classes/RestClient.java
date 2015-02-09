package com.bmt.custom_classes;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {
	private static String BASE_URL = "";
	private static AsyncHttpClient client = null;
	static RequestParams params = null;
	static StringBuilder pathParams = null;
	
	public RestClient(String baseurl) {
		client = new AsyncHttpClient();	
		client.setMaxRetriesAndTimeout(5, 9000);
		BASE_URL = baseurl;
		pathParams = new StringBuilder();
		params = new RequestParams();
	}
	public String getAbsoluteUrl() {
		if(!pathParams.toString().contentEquals(""))
			return BASE_URL + pathParams.toString();
		if(!params.toString().contentEquals(""))
			return BASE_URL + params.toString();
		return BASE_URL;
	}
	public void get(AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(), params, responseHandler);
	}
	public void post(AsyncHttpResponseHandler responseHandler) {
	    client.post(BASE_URL, params, responseHandler);
	}
	public void addParam(String Key, String Value){
		if(Value == null)
			params.put(Key, " ");
		else
			params.put(Key, Value);
	}	
	/*public void addParam(String Key, String fileName, byte[] Value){
		createParamsObj();
		params.put(Key, new ByteArrayInputStream(Value), fileName);		
	}
	public void addParam(String Key, File f){
		//File f = new File("/path/to/file.png");
		createParamsObj();
		try {
		    params.put(Key, f);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}		
	}*/
	/*public void addParam(String Key, String fileName, InputStream Value){
		createParamsObj();
		params.put(Key, Value, fileName);		
	}*/
	/*public void addParams(HashMap<String, String> hm){
		params = new RequestParams(hm);
	}*/
	//used to insert variables via the url
	// example www.bmt.com/key/value
	public void addPathParam(String Key, String Value){
		pathParams.append('/');
		pathParams.append(Key);
		pathParams.append('/');
		pathParams.append(Value);
	}
	/*public void addPathParam(String key, int i) {
		pathParams.append('/');
		pathParams.append(key);
		pathParams.append('/');
		pathParams.append(i);
	}*/
}	