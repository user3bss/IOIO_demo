package com.bmt.ioio_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class launcher extends Activity implements OnClickListener {
	TextView button0 = null;
	TextView button1 = null;
	TextView button2 = null;
	TextView button3 = null;
	Context content = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		content = this;
		button0 = (TextView) findViewById(R.id.button1);
		button1 = (TextView) findViewById(R.id.button2);
		button2 = (TextView) findViewById(R.id.button3);
		button3 = (TextView) findViewById(R.id.button4);
		
		button0.setOnClickListener(this);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch(v.getId()){
			case R.id.button1:
				intent = new Intent(content, FM_Activity.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				break;
			case R.id.button2:
				intent = new Intent(content, GraphActivity.class);				
				break;
			case R.id.button3:
				intent = new Intent(content, MainActivity.class);				
				break;
			case R.id.button4:
				intent = new Intent(content, IOIOSimpleApp.class);				
				break;
		}
		startActivity(intent);		
	}	
}
