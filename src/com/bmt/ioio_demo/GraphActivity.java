package com.bmt.ioio_demo;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bmt.custom_classes.AnalogPin;
import com.bmt.custom_classes.FileIO;
import com.bmt.custom_classes.OutputPin;
import com.bmt.custom_classes.Util;
import com.bmt.customviews.UIGraphView;



public class GraphActivity extends IOIOActivity{
	//private int numConnected_ = 0;
	boolean show_toast_connection_info = true;
	//UIKnobSwitch sw_knob0 = null;
	UIGraphView graph0 = null;
	String tag = getClass().getSimpleName();
	ArrayList<FileIO> OutputStreams = null;
	ArrayList<FileIO> InputStreams = null;
	ArrayList<AnalogPinFile> analog_pins = null;
	int sizeOfFloat = 0;
	int [] pin_colors = {
			Color.parseColor("#FF0000"),	//0
			Color.parseColor("#FF5500"),	//1
			Color.parseColor("#FFAA00"),	//2
			Color.parseColor("#FFFF00"),	//3
			Color.parseColor("#AAFF00"),	//4
			Color.parseColor("#55FF00"),	//5
			Color.parseColor("#00FF00"),	//6
			Color.parseColor("#00FF55"),	//7
			Color.parseColor("#00FFAA"),	//8
			Color.parseColor("#00FFFF"),	//9
			Color.parseColor("#00AAFF"),	//10
			Color.parseColor("#0055FF"),	//12
			Color.parseColor("#0000FF"),	//13
			Color.parseColor("#5500FF"),	//14
			Color.parseColor("#AA00FF")		//15
	};
	boolean [] enabled_channels = {
					//AN  Pin#
			false,	//0 - 31
			true,	//1 - 32
			false,	//2 - 33
			false,	//3 - 34
			false,	//4 - 35
			false,	//5 - 36
			false,	//6 - 37
			false,	//7 - 38
			false,	//8 - 39
			false,	//9 - 40
			false,	//10 - 41
			false,	//12 - 42
			false,	//13 - 43
			false,	//14 - 44
			false	//15 - 45		
	};
	private int getNumEnabledChannels(){
		int numEnabledChannels = 0;
		for(int i=0;i<enabled_channels.length;i++){
			if(enabled_channels[i])
				numEnabledChannels++;
		}
		return numEnabledChannels;
	}
	public void clearFiles(){
		if(OutputStreams != null){
			Iterator<FileIO> InputIt = InputStreams.iterator();			
			Iterator<FileIO> OutputIt = OutputStreams.iterator();
			//int i = 0;
			while(OutputIt.hasNext() && InputIt.hasNext()){
				FileIO fi = InputIt.next();
				fi.closeFile();
				OutputIt.next().emptyFile();
				fi.openFile();
				//Log.i(tag, "clear index "+i);
				//i++;
			}
		}
	}
	private void clear_graph(){	
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				graph0.clear();
			}
		});		
	}

	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		/*runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						graph0.setEnabled(true);
						//sw_knob0.setEnabled(true);
					}
				} else {
					if (--numConnected_ == 0) {
						graph0.setEnabled(false);
						//sw_knob0.setEnabled(false);						
					}
				}
			}
		});*/
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_activity);
		OutputStreams = new ArrayList<FileIO>();
		InputStreams = new ArrayList<FileIO>();
		Application app = getApplication();
		
		Util u = new Util();
		sizeOfFloat = u.sizeOfFloat();	  //4
		//int sizeOfInt = u.sizeOfInt();  //4
		//Log.i(tag, "sizeOfFloat: "+sizeOfFloat+", sizeOfInt:"+sizeOfInt);
		
		for(int i=0;i<enabled_channels.length;i++){
			if(enabled_channels[i]){
				//FileIO.file_location.TEMP is ReadOnly on emulator
				OutputStreams.add( new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.WRITE, "PIN"+(i+31)) );
				InputStreams.add( new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.READ, "PIN"+(i+31)) );
			}
		}
		clearFiles();
		
		/*sw_knob0 = (UIKnobSwitch) findViewById(R.id.sw_knob0);
		sw_knob0.SetListener(new UIKnobSwitchListener(){
			@Override
			public void onChange(int position) {
				//toast("Knob position: "+position);
			}			
		});*/		
		graph0 = (UIGraphView) findViewById(R.id.graph0);
		graph0.setFileInputStreams(InputStreams, pin_colors);		
	}	
	@Override
	protected void onResume() {
		super.onResume();
	}	
	private void showVersions(IOIO ioio, String title) {
		if(show_toast_connection_info){
			toast(String.format("%s\n" +
					"IOIOLib: %s\n" +
					"Application firmware: %s\n" +
					"Bootloader firmware: %s\n" +
					"Hardware: %s",
					title,
					ioio.getImplVersion(VersionType.IOIOLIB_VER),
					ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
					ioio.getImplVersion(VersionType.BOOTLOADER_VER),
					ioio.getImplVersion(VersionType.HARDWARE_VER)));
		}
	}
	
	private void toast(final String message) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	public class AnalogPinFile extends AnalogPin {
		FileOutputStream fs = null;
		String fn = null;
		public AnalogPinFile(IOIO ioio, int PinNum, FileOutputStream _f) {
			super(ioio, PinNum);
			fs = _f;
		}
		public void readAnalogInBufferedToFile() throws InterruptedException, ConnectionLostException{
			try {
				if(ioiopina != null){
					if(ioiopina.getOverflowCount() > 0){
						toast("dropped "+ioiopina.getOverflowCount() + " samples");
					}
					int numSampleToRead = ioiopina.available();
					for(int i=0;i<numSampleToRead;i++){	//reads all available samples
						float v = ioiopina.getVoltageBuffered();						
						try {
							fs.write(ByteBuffer.allocate(sizeOfFloat).putFloat(v).array());
						} catch (IOException e) {
							toast("Error writing to stream: "+e.getLocalizedMessage());
						}
					}				
				}
			} catch (Exception e){
				toast(e.getLocalizedMessage());
			}
		}
	}
	public class _IOIOLooper extends BaseIOIOLooper {
		String tag = getClass().getSimpleName();
		public void interrupted() {
			enableUi(false);        //interrupted
			if(show_toast_connection_info) toast("IOIO interrupted"); 		
		}
	
		@Override
		protected void setup() {
			try{
				enableUi(true);
				showVersions(ioio_, "IOIO connected!");	 
				analog_pins = new ArrayList<AnalogPinFile>();
				Log.i(tag, "numEnabledChannels"+getNumEnabledChannels());
				Iterator<FileIO> osi = OutputStreams.iterator();
				String msg = new String();
				for(int i=0;i<enabled_channels.length;i++){
					if(enabled_channels[i] && osi.hasNext()){
						analog_pins.add( new AnalogPinFile(ioio_, i+31, osi.next().getOutputStream()) );
						msg += "adding analog pin "+(i+31)+"\n";
					}
				}
				toast(msg);
				OutputPin led = new OutputPin(ioio_, IOIO.LED_PIN, 0, false);  //mode 3 = open_collector
				led.writeBit(false); 										   //led is inverted
				//InputPin InputPin9 = new InputPin(ioio_, 9, 0); 			   //0:pullup, 1:pulldn, 2:float
				PwmOutput pwmOutput_ = ioio_.openPwmOutput(12, 50);
				toast("50% duty cycle on pin #12");
			} catch (Exception e) {
				toast("setup_Error: "+e.getMessage()+" "+e.getLocalizedMessage());
			}			
		}
		
		@Override
		public void incompatible() {
			showVersions(ioio_, "Incompatible firmware version!");
		}
		@Override
		public void disconnected() {
			enableUi(false);		
			if(show_toast_connection_info) toast("IOIO disconnected");
		}	
		@Override
		public void loop() {		
			try {
				//read the analog pins
				Iterator<AnalogPinFile> pin = analog_pins.iterator();
				while(pin.hasNext()){
					pin.next().readAnalogInBufferedToFile();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						graph0.filesUpdated();
					}
				});		
				Thread.sleep(200);
				//Thread.sleep((1/60) * 1000);				
			} catch (Exception e) {
				toast("loop Error: "+e.getLocalizedMessage() );
			}
		}
	}
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}

}
