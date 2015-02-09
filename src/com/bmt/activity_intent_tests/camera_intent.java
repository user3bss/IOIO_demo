package com.bmt.activity_intent_tests;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.bmt.custom_classes.Alert_Dialog;
import com.bmt.custom_classes.RestClient;
import com.bmt.custom_classes.Util;
import com.bmt.ioio_demo.R;
import com.loopj.android.http.JsonHttpResponseHandler;

public class camera_intent extends Activity {
	String tag = getClass().getSimpleName();
	static final int REQUEST_IMAGE_CAPTURE = 1;
	ImageView iv = null;

	//async vars
	JSONObject jdata = null;
	ProgressDialog dialog;
	Context content = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_intent_test);
		
		iv = (ImageView) findViewById(R.id.imageView1);
		iv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dispatchTakePictureIntent();
			}
		});
	}
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        iv.setImageBitmap(imageBitmap);
	        
	        uploadPhoto(Util.encodeBitmapBase64(imageBitmap));
	    }
	}
	private void cancelActivity(){
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();		
	}	
	private void exitActivityOK(){
		Intent returnIntent = new Intent();
	    setResult(RESULT_OK, returnIntent);		
		finish();		
	}
	
	public void uploadPhoto(String base64Photo){		
		RestClient rc = new RestClient("google.com");
		rc.addParam("photo", base64Photo);
	    rc.post(new JsonHttpResponseHandler() {        
	        @Override
	        public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
	        	Log.i(tag, "json: " + json.toString());
	        	/*if(json.has("status_code")){
					try {
						int status_code = json.getInt("status_code");
						if(status_code == 1){														
							if(json.has("venues")){
								jdata = json;
								JSONArray venues = json.getJSONArray("venues");
								if(venues.length() > 0){
									String[] s = new String[venues.length()];
									for(int i=0;i<venues.length();i++){
										JSONObject venue = venues.getJSONObject(i);
										if(venue.has("venuename") && venue.has("venuestate") && venue.has("mvid")){
											String name = venue.getString("venuename");
											String state = venue.getString("venuestate");
											String mvid = venue.getString("mvid");
											s[i] = name + " - " + state;
										}
									}
								} else {
									new Alert_Dialog("Search returned 0 results.", "Alert", content);
								}
							}
						} else if(json.has("msg")) {
							String msg = json.getString("msg");
							new Alert_Dialog(msg, "Error", content);
						} else {
							new Alert_Dialog("SearchVenue Webservice Error!", "Error", content);
						}		
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					new Alert_Dialog("Webservice Error!", "Error", content);
				}*/
	        }

			@Override
			public void onFailure(int statusCode, Header[] headers,	String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				new Alert_Dialog("Webservice Error", "Error", content);				
			}

			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
			}

			@Override
			public void onStart() {
				super.onStart();
				dialog = new ProgressDialog(content);
				dialog.setTitle("Uploading Photo");
				dialog.show();				
			}	        
	    });			
	}	
}
