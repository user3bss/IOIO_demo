package com.bmt.customviews;

import com.bmt.ioio_demo.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class TestCustomControlsActivity extends Activity {
	Context content = null;
	String tag = getClass().getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_controls);
		content = this;
	}
}
