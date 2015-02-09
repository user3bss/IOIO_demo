package com.bmt.custom_classes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
	public static String encodeBitmapBase64(Bitmap image){		
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);	    
	}
	public static String getMD5EncryptedString(byte[] _encTarget){
		String encTarget = "";
		try {
			encTarget = new String(_encTarget, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("error converting byte[] to string");
		}
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }	
	public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }	
}
