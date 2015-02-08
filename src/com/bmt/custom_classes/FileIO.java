package com.bmt.custom_classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class FileIO {
	private String tag = getClass().getSimpleName();
	//private String previousExternalStorageState;
	private boolean APPEND_MODE = true;
	public enum file_location {
	    APPTEMP, APPDATA, EXTSTORAGE, APPRESOURCES
	}
	public enum file_mode {
		READ, WRITE
	}
	file_mode fmode;
	file_location flocation;
	String fname = null;
	private String path = null;
	File _file = null;
	FileOutputStream fOStream = null;
	FileInputStream fIStream = null;
	boolean isopen = false;
	
	private void Init(Application app){
		File tempDir = null;
		if(flocation == file_location.APPTEMP){
			//SETUP Temp file location
			String TEMPDIR = "_tmp";
			String appPackageName = app.getPackageName();
			File internalCacheDir = app.getCacheDir();
			
			String extState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(extState)) {
				// See http://developer.android.com/guide/topics/data/data-storage.html#ExternalCache
				// getExternalCacheDir() isn't available until API 8
				File extStorage = Environment.getExternalStorageDirectory();
				File dataDir = new File(new File(extStorage, "Android"), "data");
				File externalCacheDir = new File(new File(dataDir, appPackageName), "cache");
				Log.i(tag, "Using external CacheDir for temp");
				tempDir = new File(externalCacheDir, TEMPDIR);
			} else {
				// Use internal storage cache if SD card is removed
				Log.i(tag, "Using internal CacheDir for temp");
				tempDir = new File(internalCacheDir, TEMPDIR);
			}
		}
		// go ahead and make sure the temp directory exists
		//Log.i(tag, "tmpDir: "+tempDir.getAbsolutePath());
		
		//Set File path
		switch(flocation){
			case APPTEMP:
				path = tempDir.getAbsolutePath();	//extstorage if present
				//TempDir: /storage/sdcard/Android/data/com.bmt.ioio_demo/cache/_tmp
				//path = "file://";
				break;
			case APPDATA:
				path = Environment.getDataDirectory().toString();
				//path = "appdata-private://";
				break;
			case EXTSTORAGE:				
				String extState = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(extState)) {				
					path = Environment.getExternalStorageDirectory().toString();
				} else {
					path = "/mnt/sdcard";
					Log.e(tag, "EXTSTORAGE UNAVAILABLE, TRYING /mnt/sdcard");
				}
				//path = "appdata://";
			case APPRESOURCES:
				//path = "app://";
				break;
			default:
				break;
		}
		File f = new File(path);
		if (!f.exists()) {
			Log.i(tag, "creating dirs: " + path);
			f.mkdirs();
		}
		if(!path.endsWith(File.separator))
			path += File.separator;
		_file = new File(path+fname);
		openFile();
	}
	
	public FileIO(Application app, file_location _fl, file_mode _fm, String file_name){
		flocation = _fl;
		fmode = _fm;
		fname = file_name;
		Init(app);
	}
	public FileIO(Application app, file_location _fl, file_mode _fm, String file_name, boolean _append){
		APPEND_MODE = _append;
		flocation = _fl;
		fmode = _fm;
		fname = file_name;
		Init(app);		
	}
	public void emptyFile(){
		closeFile();
		_file.delete();
		openFile();
	}
	public FileInputStream getInputStream(){
		return fIStream;
	}
	public FileOutputStream getOutputStream(){
		return fOStream;
	}
	private void createFileIfNotExist(){
		if(!_file.exists()){
			Log.i(tag, "creating file: "+path+fname);
			try {
				_file.createNewFile();
			} catch (IOException e) {
				Log.e(tag, e.getMessage());
			}
		}
		//Log.i(tag, "File Length: "+_file.length());		
	}
	public boolean openFile(){
		boolean openError = false;
		if(!isopen && !_file.isDirectory()){
			createFileIfNotExist();		
			switch(fmode){
				case READ:
					try {
						fIStream = new FileInputStream(_file);
						isopen = true;
					} catch (FileNotFoundException e) {
						Log.e(tag, "ERROR opening file for READ: "+e.getLocalizedMessage()+ ", file:"+path+fname);
						openError = true;
					}
					break;
				case WRITE:
					try {
						fOStream = new FileOutputStream(_file, APPEND_MODE);
						isopen = true;
					} catch (FileNotFoundException e) {
						Log.e(tag, "ERROR opening file for WRITE: "+e.getLocalizedMessage()+ ", file:"+path+fname);
						openError = true;
					}
					break;
			}
		}
		return openError;
	}	
	public void closeFile(){
		if(isopen && !_file.isDirectory()){
			switch(fmode){
				case READ:
					if(fIStream != null){
						try {
							fIStream.close();
						} catch (IOException e) {
							Log.e(tag, "ERROR: "+e.getLocalizedMessage());
						}
					}
					break;
				case WRITE:
					if(fOStream != null){
						try {
							fOStream.close();
						} catch (IOException e) {
							Log.e(tag, "ERROR: "+e.getLocalizedMessage());
						}
					}					
					break;
			}
		}
		isopen = false;
	}
	public boolean writeByte(int b){
		boolean didWriteData = false;
		if(isopen && _file.canWrite()){				
			try {
				fOStream.write(b);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getMessage());
			}
			didWriteData = true;
		} else {
			Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
		}
		return didWriteData;
	}	
	public boolean writeByteBuffer(byte[] buffer){
		boolean didWriteData = false;
		if(isopen && _file.canWrite()){				
			try {
				fOStream.write(buffer);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getMessage());
			}
			didWriteData = true;
		} else {
			Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
		}
		return didWriteData;
	}
	public boolean writeByteBuffer(byte[] buffer, int byteOffset, int byteCount){
		boolean didWriteData = false;
		if(isopen && _file.canWrite()){				
			try {
				fOStream.write(buffer, byteOffset, byteCount);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getMessage());
			}
			didWriteData = true;
		} else {
			Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
		}
		return didWriteData;
	}	
	public byte[] readByteBuffer(){
		byte[] buffer = null;
		if(isopen && _file.canRead()){				
			try {
				fIStream.read(buffer);
			} catch (IOException e) {
				Log.d(tag, "Error: "+e.getMessage());
			}
		} else {
			Log.d(tag, "can't read file: "+_file.getAbsolutePath());
		}
		return buffer;
	}
	public byte[] readByteBuffer(int byteOffset, int byteCount){
		byte[] buffer = null;
		if(isopen && _file.canRead()){				
			try {
				fIStream.read(buffer, byteOffset, byteCount);
			} catch (IOException e) {
				Log.d(tag, "Error: "+e.getMessage());
			}
		} else {
			Log.d(tag, "can't read file: "+_file.getAbsolutePath());
		}
		return buffer;
	}
	
	public int readByte(){
		int b = -2^31;
		if(isopen && _file.canRead()){				
			try {
				b = fIStream.read();
			} catch (IOException e) {
				Log.d(tag, "Error: "+e.getMessage());
			}
		} else {
			Log.d(tag, "can't read file: "+_file.getAbsolutePath());
		}
		return b;
	}	
	
	public long fileLength(){
		return _file.length();
	}
	public void printDir(){
		if( _file.isDirectory() ){
			File[] dirList = _file.listFiles();
			for(int i=0; i<dirList.length;i++){
				if(dirList[i].isFile())
					Log.i(tag, "file: "+dirList[i].getAbsolutePath());
			}
		} else {
			Log.d(tag, "PrintDir - supplied path: "+fname+" isn't directory");
		}
	}	
}
