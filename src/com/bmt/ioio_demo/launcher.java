package com.bmt.ioio_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bmt.customviews.TestCustomControlsActivity;


public class launcher extends Activity{
	Context content = null;
	String tag = getClass().getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		content = this;
		ListView apps = (ListView) findViewById(R.id.listView1);
		apps.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final String[] values = {"FM Paralxx 27984","Volt Graph","Simple App","Switches", "Test Custom Controls"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, values );
		apps.setAdapter(adapter);
		apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {
				Log.i(tag, "onTouchListener: "+values[position]);	
				Intent intent = null;
				switch(position){
					case 0:
						intent = new Intent(content, FM_Activity.class);
						break;
					case 1:
						intent = new Intent(content, GraphActivity.class);				
						break;
					case 2:
						intent = new Intent(content, IOIOSimpleApp.class);				
						break;
					case 3:
						intent = new Intent(content, MainActivity.class);				
						break;
					case 4:
						intent = new Intent(content, TestCustomControlsActivity.class);
				}
				startActivity(intent);
			}
	   });
	}
}
