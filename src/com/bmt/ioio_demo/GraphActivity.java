package com.bmt.ioio_demo;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bmt.custom_classes.AnalogPin;
import com.bmt.custom_classes.FileIO;
import com.bmt.customviews.UIGraphView;


public class GraphActivity extends IOIOActivity{
	private int numConnected_ = 0;
	boolean show_toast_connection_info = true;
	//UIKnobSwitch sw_knob0 = null;
	UIGraphView graph0 = null;
	String tag = getClass().getSimpleName();
	FileIO[] OutputStreams = null;
	FileIO[] InputStreams = null;
	AnalogPinFile[] analog_pins = null;
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
			true,	//0
			false,	//1
			false,	//2
			false,	//3
			false,	//4
			false,	//5
			false,	//6
			false,	//7
			false,	//8
			false,	//9
			false,	//10
			false,	//12
			false,	//13
			false,	//14
			false	//15			
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
		if(OutputStreams != null && InputStreams != null && OutputStreams.length == InputStreams.length){
			for(int i=0;i<OutputStreams.length;i++){
				InputStreams[i].closeFile();
				OutputStreams[i].emptyFile();
				InputStreams[i].openFile();
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
			
		//graph0.drawSamplesLineChart(p.samples, color); //pass the data directly
		//drawSamplesLineChart(p.samples, color, int start, int end);
		
		//TODO need to filter samples according to scroll here??		
		//graph0.drawLineChart(p.getSamples(), color); //convert arraylist<float> to float[] 
		//TODO use paging for render frames???

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
		OutputStreams = new FileIO[getNumEnabledChannels()];
		InputStreams = new FileIO[getNumEnabledChannels()];
		Application app = getApplication();
		
		for(int i=0;i<enabled_channels.length;i++){
			if(enabled_channels[i]){
				//FileIO.file_location.TEMP is ReadOnly on emulator
				OutputStreams[i] = new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.WRITE, "PIN"+(i+31));
				InputStreams[i] = new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.READ, "PIN"+(i+31));
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
		/*graph0.SetListener(new UIGraphViewListener(){
			@Override
			public void onScrollUpdate(float x, float y, float xScroll, float yScroll) {
				//using callback for fling bc this instance 
				//has a referance to graph0 and the sample data
			}			
		});*/
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
			if(ioiopina != null){
				printDroppedSamples();
				int numSampleToRead = ioiopina.available();
				for(int i=0;i<numSampleToRead;i++){	//reads all available samples
					float v = (float) ioiopina.getVoltageBuffered();
					byte[] b = ByteBuffer.allocate(4).putFloat(v).array();
					try {
						fs.write(b);
					} catch (IOException e) {
						Log.e(tag, "Error writing to stream: "+e.getLocalizedMessage());
					}
				}				
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
				analog_pins = new AnalogPinFile[getNumEnabledChannels()];
				Log.i(tag, "numEnabledChannels"+getNumEnabledChannels());
				for(int i=0;i<enabled_channels.length;i++){
					if(enabled_channels[i])
						analog_pins[i] = new AnalogPinFile(ioio_, i+31, OutputStreams[i].getOutputStream());
				}
				//OutputPins.put("led", new OutputPin(0,3,false));  //mode 3 = open_collector
				//Thread.sleep(250);
				//OutputPins.get("led").writeBit(true);  //mode 3 = OPEN_DRAIN
				//InputPin9 = new InputPin(9, 0); //0:pullup, 1:pulldn, 2:float			
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
				for(int i=0;i<analog_pins.length;i++){
					if(enabled_channels[i])
						analog_pins[i].readAnalogInBufferedToFile();
				}
				graph0.filesUpdated();
				Thread.sleep(100);
				//Thread.sleep((1/60) * 1000);				
			} catch (Exception e) {
				toast("loop Error: "+e.getMessage()+" , "+e.getLocalizedMessage() );
			}
		}
	}
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}
}
