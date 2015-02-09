package com.bmt.custom_classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
		READ, WRITE, READWRITE, RANDOMRW
	}
	file_mode fmode;
	file_location flocation;
	String fname = null;
	private String path = null;
	File _file = null;
	RandomAccessFile fRandomRW = null;
	FileOutputStream fOStream = null;
	FileInputStream fIStream = null;
	
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
	public FileInputStream getInputStream(){
		return fIStream;
	}
	public FileOutputStream getOutputStream(){
		return fOStream;
	}
	public RandomAccessFile getRandomRWFile(){
		return fRandomRW;
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
	private boolean openRandomRW(){
		boolean openError = false;
		try {
			fRandomRW = new RandomAccessFile(_file, "rw");
		} catch (FileNotFoundException e) {
			Log.e(tag, "ERROR opening file for RandomRW: "+e.getLocalizedMessage()+ ", file:"+path+fname);
			openError = true;
		}
		return openError;
	}
	private boolean openOutputStream(){
		boolean openError = false;
		try {
			fOStream = new FileOutputStream(_file, APPEND_MODE);
		} catch (FileNotFoundException e) {
			Log.e(tag, "ERROR opening file for WRITE: "+e.getLocalizedMessage()+ ", file:"+path+fname);
			openError = true;
		}
		return openError;
	}
	private boolean openInputStream(){
		boolean openError = false;
		try {
			fIStream = new FileInputStream(_file);
		} catch (FileNotFoundException e) {
			Log.e(tag, "ERROR opening file for READ: "+e.getLocalizedMessage()+ ", file:"+path+fname);
			openError = true;
		}
		return openError;
	}
	private void closeRandomRW(){
		if(fRandomRW != null){
			try {
				fRandomRW.close();
				fRandomRW = null;
			} catch (IOException e) {
				Log.e(tag, "ERROR: "+e.getLocalizedMessage());
			}
		}
	}
	private void closeInputStream(){
		if(fIStream != null){
			try {
				fIStream.close();
				fIStream = null;
			} catch (IOException e) {
				Log.e(tag, "ERROR: "+e.getLocalizedMessage());
			}
		}		
	}
	private void closeOutputStream(){
		if(fOStream != null){
			try {
				fOStream.close();
				fOStream = null;
			} catch (IOException e) {
				Log.e(tag, "ERROR: "+e.getLocalizedMessage());
			}
		}		
	}
	public void emptyFile(){
		closeFile();
		_file.delete();
		openFile();
	}	
	public boolean openFile(){
		boolean openError = false;
		if(!_file.isDirectory()){
			createFileIfNotExist();		
			switch(fmode){
				case READ:
					openError = openInputStream();
					break;
				case WRITE:
					openError = openOutputStream();
					break;
				case READWRITE:
					openError = openInputStream() & openOutputStream();
					break;
				case RANDOMRW:
					openError = openRandomRW();
					break;
			}
		}
		return openError;
	}
	public void closeFile(){
		if(!_file.isDirectory()){
			switch(fmode){
				case READ:
					closeInputStream();
					break;
				case WRITE:
					closeOutputStream();
					break;
				case READWRITE:
					closeInputStream();
					closeOutputStream();
					break;
				case RANDOMRW:
					closeRandomRW();
					break;
			}
		}
	}
	public long seek(long offset){
		long byteskipped = 0;
		if(fmode == file_mode.READ || fmode == file_mode.READWRITE){
			if(fIStream != null){				
				try {
					byteskipped = fIStream.skip(offset);
				} catch (IOException e) {
					Log.d(tag, "Error: "+e.getLocalizedMessage());
				}
			}			
		} else if(fRandomRW != null){
			try {
				//Moves this file's file pointer to a new position, from where following read, 
				//write or skip operations are done. The position may be greater 
				//than the current length of the file, but the file's length will only change 
				//if the moving of the pointer is followed by a write operation.
				//
				//this method returns void
				 fRandomRW.seek(offset);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return byteskipped;
	}
	public boolean writeInt(int b){
		boolean didWriteData = false;
		if(fmode != file_mode.RANDOMRW){
			if(_file.canWrite() && fOStream != null){				
				try {
					fOStream.write(b);
				} catch (IOException e) {
					Log.e(tag, "Error: "+e.getLocalizedMessage());
				}
				didWriteData = true;
			} else {
				Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				fRandomRW.writeByte(b);
				didWriteData = true;
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return didWriteData;
	}	
	public boolean writeByteBuffer(byte[] buffer){
		boolean didWriteData = false;
		if(fmode != file_mode.RANDOMRW){
			if(_file.canWrite() && fOStream != null){				
				try {
					fOStream.write(buffer);
				} catch (IOException e) {
					Log.e(tag, "Error: "+e.getLocalizedMessage());
				}
				didWriteData = true;
			} else {
				Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				fRandomRW.write(buffer);
				didWriteData = true;
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return didWriteData;
	}
	public boolean writeByteBuffer(byte[] buffer, int byteOffset, int byteCount){
		boolean didWriteData = false;
		if(fmode != file_mode.RANDOMRW){		
			if(_file.canWrite() && fOStream != null){				
				try {
					fOStream.write(buffer, byteOffset, byteCount);
				} catch (IOException e) {
					Log.e(tag, "Error: "+e.getLocalizedMessage());
				}
				didWriteData = true;
			} else {
				Log.e(tag, "file is READONLY: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				fRandomRW.write(buffer, byteOffset, byteCount);
				didWriteData = true;
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return didWriteData;
	}	
	public int readByteBuffer(byte[] buffer){
		int bytesRead = 0;
		if(fmode != file_mode.RANDOMRW){		
			if(_file.canRead() && fIStream != null){				
				try {
					bytesRead = fIStream.read(buffer);
				} catch (IOException e) {
					Log.d(tag, "Error: "+e.getLocalizedMessage());
				}
			} else {
				Log.d(tag, "can't read file: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				bytesRead = fRandomRW.read(buffer);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return bytesRead;
	}
	public int readByteBuffer(byte[] buffer, int byteOffset, int byteCount){
		int bytesRead = 0;
		if(fmode != file_mode.RANDOMRW){
			if(_file.canRead() && fIStream != null){				
				try {
					bytesRead = fIStream.read(buffer, byteOffset, byteCount);
				} catch (IOException e) {
					Log.d(tag, "Error: "+e.getLocalizedMessage());
				}
			} else {
				Log.d(tag, "can't read file: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				bytesRead = fRandomRW.read(buffer, byteOffset, byteCount);
			} catch (IOException e) {
				Log.e(tag, "Error: "+e.getLocalizedMessage());
			}
		}			
		return bytesRead;
	}
	
	public int readInt(){
		int b = -2^31;
		if(fmode != file_mode.RANDOMRW){
			if(_file.canRead() && fIStream != null){				
				try {
					b = fIStream.read();
				} catch (IOException e) {
					Log.d(tag, "Error: "+e.getLocalizedMessage());
				}
			} else {
				Log.d(tag, "can't read file: "+_file.getAbsolutePath());
			}
		} else if(fRandomRW != null){
			try {
				b = fRandomRW.read();
			} catch (IOException e) {
				Log.d(tag, "Error: "+e.getLocalizedMessage());
			}
		}
		return b;
	}
	
	public long fileLength(){
		//_file.getInputStream().available() == _file.fileLength()
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
