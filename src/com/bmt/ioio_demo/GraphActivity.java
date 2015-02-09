package com.bmt.ioio_demo;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bmt.custom_classes.AnalogPin;
import com.bmt.custom_classes.FileIO;
import com.bmt.custom_classes.OutputPin;
import com.bmt.custom_classes.Util;
import com.bmt.customviews.UIGraphView;



public class GraphActivity extends IOIOActivity implements OnClickListener{
	//private int numConnected_ = 0;
	boolean show_toast_connection_info = true;
	//UIKnobSwitch sw_knob0 = null;
	UIGraphView graph0 = null;
	String tag = getClass().getSimpleName();
	//FileIO[] files = null;
	ToggleButton[] toggleButtons = null;
	AnalogPinFile[] AnalogPinFiles = null;

	int sizeOfFloat = 4;
	String[] fileNames = {
			"PIN31",	//0
			"PIN32",	//1
			"PIN33",	//2
			"PIN34",	//3
			"PIN35",	//4
			"PIN36",	//5
			"PIN37",	//6
			"PIN38",	//7			
			"PIN39",	//8
			"PIN40",	//9
			"PIN41",	//10
			"PIN42",	//11
			"PIN43",	//12
			"PIN44",	//13
			"PIN45",	//14
			"PIN46"		//15
	};
	
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
			Color.parseColor("#0055FF"),	//11
			Color.parseColor("#0000FF"),	//12
			Color.parseColor("#AA00FF"),	//13
			Color.parseColor("#5500FF"),	//14
			Color.parseColor("#AA0055")		//15
	};
	boolean[] enabled_channels = {
			false,	//0
			true,	//1
			false,	//2
			false,	//3
			false,	//4
			false,	//5
			false,	//6
			false,	//7
			false,	//8
			false,	//9
			false,	//10
			false,	//11
			false,	//12
			false,	//13
			false,	//14
			false	//15			
	};
	/*private int getNumEnabledChannels(){
		int numEnabledChannels = 0;
		for(int i=0;i<enabled_channels.length;i++){
			if(enabled_channels[i])
				numEnabledChannels++;
		}
		return numEnabledChannels;
	}*/
	public void clearFiles(){
		/*for(int i=0;i<16;i++){
			files[i].emptyFile();
		}*/
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
					//if (numConnected_++ == 0) {
						//graph0.setEnabled(true);
						//sw_knob0.setEnabled(true);
					//}
				} else {
					//if (--numConnected_ == 0) {
						//graph0.setEnabled(false);
						//sw_knob0.setEnabled(false);						
					//}
				}
			}
		});*/
	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_activity);

		//Application app = getApplication();
		
		Util u = new Util();
		sizeOfFloat = u.sizeOfFloat();	  //4
		//int sizeOfInt = u.sizeOfInt();  //4
		//Log.i(tag, "sizeOfFloat: "+sizeOfFloat+", sizeOfInt:"+sizeOfInt);
		toggleButtons = new ToggleButton[16];
		toggleButtons[0] = (ToggleButton) findViewById(R.id.toggleButton1);
		toggleButtons[0].setTag(new Integer(0));
		toggleButtons[1] = (ToggleButton) findViewById(R.id.toggleButton2);
		toggleButtons[1].setTag(new Integer(1));
		toggleButtons[2] = (ToggleButton) findViewById(R.id.toggleButton3);
		toggleButtons[2].setTag(new Integer(2));
		toggleButtons[3] = (ToggleButton) findViewById(R.id.toggleButton4);
		toggleButtons[3].setTag(new Integer(3));
		toggleButtons[4] = (ToggleButton) findViewById(R.id.toggleButton5);
		toggleButtons[4].setTag(new Integer(4));
		toggleButtons[5] = (ToggleButton) findViewById(R.id.toggleButton6);
		toggleButtons[5].setTag(new Integer(5));
		toggleButtons[6] = (ToggleButton) findViewById(R.id.toggleButton7);
		toggleButtons[6].setTag(new Integer(6));
		toggleButtons[7] = (ToggleButton) findViewById(R.id.toggleButton8);
		toggleButtons[7].setTag(new Integer(7));
		
		toggleButtons[8] = (ToggleButton) findViewById(R.id.toggleButton9);
		toggleButtons[8].setTag(new Integer(8));
		toggleButtons[9] = (ToggleButton) findViewById(R.id.toggleButton10);
		toggleButtons[9].setTag(new Integer(9));
		toggleButtons[10] = (ToggleButton) findViewById(R.id.toggleButton11);
		toggleButtons[10].setTag(new Integer(10));
		toggleButtons[11] = (ToggleButton) findViewById(R.id.toggleButton12);
		toggleButtons[11].setTag(new Integer(11));
		toggleButtons[12] = (ToggleButton) findViewById(R.id.toggleButton13);
		toggleButtons[12].setTag(new Integer(12));
		toggleButtons[13] = (ToggleButton) findViewById(R.id.toggleButton14);
		toggleButtons[13].setTag(new Integer(13));
		toggleButtons[14] = (ToggleButton) findViewById(R.id.toggleButton15);
		toggleButtons[14].setTag(new Integer(14));
		toggleButtons[15] = (ToggleButton) findViewById(R.id.toggleButton16);		
		toggleButtons[15].setTag(new Integer(15));
		for(int i=0;i<toggleButtons.length;i++){
			toggleButtons[i].setOnClickListener(this);
			if(enabled_channels[i]){
				toggleButtons[i].setChecked(true);
			}
			//files[i] = new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.READWRITE, "PIN"+(i+31));
		}
		//clearFiles();
		
		/*sw_knob0 = (UIKnobSwitch) findViewById(R.id.sw_knob0);
		sw_knob0.SetListener(new UIKnobSwitchListener(){
			@Override
			public void onChange(int position) {
				//toast("Knob position: "+position);
			}			
		});*/		
		graph0 = (UIGraphView) findViewById(R.id.graph0);
		graph0.setFilesAndColors(getApplication(), fileNames, pin_colors);	
	}
	@Override
	protected void onStart(){
		super.onStart();
		//graph0.filesUpdated(enabled_channels);
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
		FileIO f = null;
		String fn = null;
		public AnalogPinFile(IOIO ioio, int PinNum, String fileName) {
			super(ioio, PinNum);
			f = new FileIO(getApplication(), FileIO.file_location.APPTEMP, FileIO.file_mode.WRITE, fileName);
		}
		public void closeFile(){
			f.closeFile();
		}
		public void readAnalogInBufferedToFile() throws InterruptedException, ConnectionLostException{
			try {
				if(ioiopina != null){
					if(ioiopina.getOverflowCount() > 0){
						toast("dropped "+ioiopina.getOverflowCount() + " samples");
					}
					try {
						if(f != null)
							f.getOutputStream().write(readAnalogInByteBuffer());
					} catch (IOException e) {
						toast("Error writing to stream: "+e.getLocalizedMessage());
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
				AnalogPinFiles = new AnalogPinFile[16];
				for(int i=0;i<16;i++){
					AnalogPinFiles[i] = new AnalogPinFile(ioio_, i+31, fileNames[i]);
				}
				OutputPin led = new OutputPin(ioio_, IOIO.LED_PIN, 0, false);  //mode 3 = open_collector
				led.writeBit(false); 										   //led is inverted
				//InputPin InputPin9 = new InputPin(ioio_, 9, 0); 			   //0:pullup, 1:pulldn, 2:float
				PwmOutput pwmOutput_ = ioio_.openPwmOutput(12, 50);
				//float sr = AnalogPinFiles[0].getIOIO_Pin().getSampleRate();
				//toast("Analog Sample Rate: "+sr);
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
			for(int i=0;i<16;i++){
				AnalogPinFiles[i].closeFile();
			}
			AnalogPinFiles = null;
			if(show_toast_connection_info) toast("IOIO disconnected");
		}	
		@Override
		public void loop() {		
			try {
				//read the analog pins
				for(int i=0;i<16;i++){
					if(enabled_channels[i])
						AnalogPinFiles[i].readAnalogInBufferedToFile();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						graph0.filesUpdated(enabled_channels);
					}
				});		
				Thread.sleep(10);
				//Thread.sleep((1/60) * 1000);				
			} catch (Exception e) {
				toast("loop Error: "+e.getLocalizedMessage() );
			}
		}
	}
    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "-- ON STOP -- : closing files");
        try{
	        graph0.closeFiles();
	        /*if(AnalogPinFiles != null){
				for(int i=0;i<16;i++){
					AnalogPinFiles[i].closeFile();
				}
	        }*/
        } catch(Exception e){
        	Log.e(tag, e.getLocalizedMessage());
        }
    }
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}
	@Override
	public void onClick(View arg0) {
		int index = (Integer) arg0.getTag();
		if(toggleButtons[index].isChecked()){
			enabled_channels[index] = true;
			Log.i(tag, "enabled channel "+index);
		} else {
			enabled_channels[index] = false;
			Log.i(tag, "disabled channel "+index);
		}
	}

}
