package com.bmt.custom_classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alert_Dialog {
	public Alert_Dialog(String MSG, String Title, Context content) {		
		new AlertDialog.Builder(content)
	    .setTitle(Title)
	    .setMessage(MSG)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        }
	     })
	    .show();		
	}
}

