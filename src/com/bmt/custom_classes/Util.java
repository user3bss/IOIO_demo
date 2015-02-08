package com.bmt.custom_classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;

public class Util {
	String tag = getClass().getSimpleName();
	public int sizeOfFloat(){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(baos);
        float f = 0f;
        try {
			dos.writeFloat(f);
		} catch (IOException e) {
			Log.e(tag, e.getLocalizedMessage());
		}
        System.err.println(baos.toByteArray().length);
        return baos.toByteArray().length;
	}
	public int sizeOfInt(){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(baos);
        int i = 0;
        try {
			dos.writeInt(i);
		} catch (IOException e) {
			Log.e(tag, e.getLocalizedMessage());
		}
        System.err.println(baos.toByteArray().length);
        return baos.toByteArray().length;
	}	
	public int sizeOfDouble(){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(baos);
        double i = 0.0;
        try {
			dos.writeDouble(i);
		} catch (IOException e) {
			Log.e(tag, e.getLocalizedMessage());
		}
        System.err.println(baos.toByteArray().length);
        return baos.toByteArray().length;
	}
	public int sizeOfLong(){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(baos);
        long i = 0;
        try {
			dos.writeLong(i);
		} catch (IOException e) {
			Log.e(tag, e.getLocalizedMessage());
		}
        System.err.println(baos.toByteArray().length);
        return baos.toByteArray().length;
	}	
}
