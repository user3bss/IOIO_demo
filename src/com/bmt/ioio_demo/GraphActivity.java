package com.bmt.ioio_demo;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.bmt.customviews.UIGraphView;
import com.bmt.customviews.UIKnobSwitch;


public class GraphActivity extends IOIOActivity implements OnClickListener{
	HashMap<String, com.bmt.ioio_demo.GraphActivity._IOIOLooper.AnalogPin> AnalogPins = null;
	private int numConnected_ = 0;
	boolean show_toast_connection_info = true;
	UIKnobSwitch sw_knob0 = null;
	UIGraphView graph0 = null;
	
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						//ToggleButtons.get("led").setEnabled(true);
						//for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
						//	entry.getValue().setEnabled(true);
						//}
						graph0.setEnabled(true);
						sw_knob0.setEnabled(true);
					}
				} else {
					if (--numConnected_ == 0) {
						graph0.setEnabled(false);
						sw_knob0.setEnabled(false);
						//ToggleButtons.get("led").setEnabled(false);
						//for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
						//	entry.getValue().setEnabled(false);
						//}						
					}
				}
			}
		});
	}	
public class _IOIOLooper extends BaseIOIOLooper {
	String tag = getClass().getSimpleName();
	class AnalogPin {
		public AnalogInput ioiopina;  //31-46
		private int pin_num;
		private float RefVolts = (float) 3.3;
		
		AnalogPin(int PinNum) {
			pin_num = PinNum;
			try {					
				if(PinNum <= 46 && PinNum >= 31){	
					ioiopina = ioio_.openAnalogInput(PinNum);
					ioiopina.setBuffer(256);
					//float sr = ioiopina.getSampleRate();			//in Hz units.		
					//RefVolts = ioiopina.getReference();
					//toast("Reference Voltage: " + RefVolts+", SampleRate: "+sr );				
				}
				
			} catch (ConnectionLostException e) {
				
			}		
		}
		public float readAnalogInBuffered() throws InterruptedException, ConnectionLostException{
			float v = 0;
			//float val = 0;			
			//printAnalogDroppedSamples(aIn0);
			//int numSampleToRead = ioiopina.available();
			//for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				v = ioiopina.getVoltageBuffered();
				//val = ioiopina.readBuffered();
				//toast("Voltage: "+v+", Value: "+val);				
			//}
			return v;
		}
		public float readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
			float v = 0;
			//float val = 0;			
			//printDroppedSamples();
			//int numSampleToRead = ioiopina.available();
			//for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				v = ioiopina.getVoltage();
				//val = ioiopina.read();
				//toast("Voltage: "+v+", Value: "+val);
			//}
			return v;			
		}
		public void setRefVolts(float volts){
			RefVolts = (float) volts;
			if (volts > 3.3) 
				Log.d(tag,"RefVolts set to higher than 3.3");
		}
		public void printDroppedSamples() throws ConnectionLostException{
			int droppedSamples = ioiopina.getOverflowCount();
			if(droppedSamples > 0){
				toast("DroppedSamples: "+droppedSamples);
			}			
		}	
	}
	public void interrupted() {
		//enableUi(false);        //interrupted
		if(show_toast_connection_info) toast("IOIO interrupted"); 		
	}

	@Override
	protected void setup() {
		try{
			enableUi(true);
			showVersions(ioio_, "IOIO connected!");	 
			AnalogPins = new HashMap<String, AnalogPin>();
			//Twi_proxies = new HashMap<String, Twi_proxy>();
			
			//OutputPins.put("led", new OutputPin(0,3,false));  //mode 3 = open_collector
			Thread.sleep(250);
			//OutputPins.get("led").writeBit(true);  //mode 3 = open_collector
			for(int i=0;i<16;i++){
				AnalogPins.put("AN"+i, new AnalogPin(i+31));				
			}
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
		AnalogPins = null;
	}	
	@Override
	public void loop() {		
		try {
			//see if any buttons changed and update output			
			//for( Entry<String, com.bmt.ioio_demo.MainActivity._IOIOLooper.AnalogPin> entry : AnalogPins.entrySet()){								
				//if(entry.getKey().contentEquals("led")){}				
			//}
		} catch (Exception e) {
			toast("loop Error: "+e.getMessage()+" , "+e.getLocalizedMessage() );
			//interrupted();
		}
		//catch (ConnectionLostException e) {	}
	}
}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_activity);
		//sw_knob0 = (UIKnobSwitch) findViewById(R.id.sw_knob0);
		//graph0 = (UIGraphView) findViewById(R.id.graph0);
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
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}
	
	@Override
	public void onClick(View arg0) {
		
		
	}
}
