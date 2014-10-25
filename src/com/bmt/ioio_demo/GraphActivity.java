package com.bmt.ioio_demo;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.bmt.customviews.UIGraphView;
import com.bmt.customviews.UIGraphView.UIGraphViewListener;
import com.bmt.customviews.UIKnobSwitch;
import com.bmt.customviews.UIKnobSwitch.UIKnobSwitchListener;
import com.bmt.ioio_demo.GraphActivity._IOIOLooper.AnalogPin;


public class GraphActivity extends IOIOActivity{
	HashMap<String, com.bmt.ioio_demo.GraphActivity._IOIOLooper.AnalogPin> AnalogPins = null;
	private int numConnected_ = 0;
	boolean show_toast_connection_info = true;
	UIKnobSwitch sw_knob0 = null;
	UIGraphView graph0 = null;
	
	private void clear_graph(){	
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				graph0.clear();
			}
		});		
	}
	private void graph_pin(AnalogPin p){
		int color = Color.BLACK;			
		switch(p.pin_num){
			case 31:
				color = Color.parseColor("#FF0000");
				break;
			case 32:
				color = Color.parseColor("#FF5500");
				break;
			case 33:
				color = Color.parseColor("#FFAA00");
				break;				
			case 34:
				color = Color.parseColor("#FFFF00");
				break;				
			case 35:
				color = Color.parseColor("#AAFF00");
				break;
			case 36:
				color = Color.parseColor("#55FF00");
				break;
			case 37:
				color = Color.parseColor("#00FF00");
				break;
			case 38:
				color = Color.parseColor("#00FF55");
				break;
			case 39:
				color = Color.parseColor("#00FFAA");
				break;
			case 40:
				color = Color.parseColor("#00FFFF");
				break;
			case 41:
				color = Color.parseColor("#00AAFF");
				break;
			case 42:
				color = Color.parseColor("#0055FF");
				break;
			case 43:
				color = Color.parseColor("#0000FF");
				break;
			case 44:
				color = Color.parseColor("#5500FF");
				break;
			case 45:
				color = Color.parseColor("#AA00FF");
				break;
			case 46:
				color = Color.parseColor("#FF00FF");
				break;
		
		}
		graph0.drawSamplesLineChart(p.samples, color); //pass the data directly
		//drawSamplesLineChart(p.samples, color, int start, int end);
		
		//TODO need to filter samples according to scroll here??		
		//graph0.drawLineChart(p.getSamples(), color); //convert arraylist<float> to float[] 
		//TODO use paging for render frames???
	}
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						graph0.setEnabled(true);
						sw_knob0.setEnabled(true);
					}
				} else {
					if (--numConnected_ == 0) {
						graph0.setEnabled(false);
						sw_knob0.setEnabled(false);						
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
		//private float RefVolts = (float) 3.3;
		public ArrayList<Float> samples = null;
		
		AnalogPin(int PinNum) {
			pin_num = PinNum;
			try {					
				if(PinNum <= 46 && PinNum >= 31){
					samples = new ArrayList<Float>();					
					ioiopina = ioio_.openAnalogInput(PinNum);
					ioiopina.setBuffer(256);					
					//float sr = ioiopina.getSampleRate();			//in Hz units.				
				}
				
			} catch (ConnectionLostException e) {
				
			}		
		}
		public float[] getSamples(){
			float[] f = new float[samples.size()];
			for(int i=0;i<samples.size();i++){
				f[i] = samples.get(i);
			}
			return f;
		}
		public void readAnalogInBuffered() throws InterruptedException, ConnectionLostException{
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				samples.add( ioiopina.getVoltageBuffered() );		
			}
		}
		public void readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				samples.add( ioiopina.getVoltage() );
			}			
		}
		/*public void setRefVolts(float volts){
			RefVolts = (float) volts;
			if (volts > 3.3) 
				Log.d(tag,"RefVolts set to higher than 3.3");
		}*/
		public void printDroppedSamples() throws ConnectionLostException{
			int droppedSamples = ioiopina.getOverflowCount();
			if(droppedSamples > 0){
				toast("DroppedSamples: "+droppedSamples);
			}			
		}	
	}
	public void interrupted() {
		enableUi(false);        //interrupted
		if(show_toast_connection_info) toast("IOIO interrupted"); 		
	}

	@Override
	protected void setup() {
		try{
			enableUi(true);
			showVersions(ioio_, "IOIO connected!");	 
			AnalogPins = new HashMap<String, AnalogPin>();
			
			//OutputPins.put("led", new OutputPin(0,3,false));  //mode 3 = open_collector
			//Thread.sleep(250);
			//OutputPins.get("led").writeBit(true);  //mode 3 = open_collector
			//for(int i=0;i<16;i++){
			for(int i=0;i<4;i++){ //try to limit pins until graph works
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
			clear_graph();
			//read the analog pins			
			for( Entry<String, AnalogPin> entry : AnalogPins.entrySet()){	
				entry.getValue().readAnalogInBuffered();
				graph_pin(entry.getValue());				
			}
			Thread.sleep((1/60) * 1000); 
			
		} catch (Exception e) {
			toast("loop Error: "+e.getMessage()+" , "+e.getLocalizedMessage() );
			//interrupted();
		}
	}
}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_activity);
		sw_knob0 = (UIKnobSwitch) findViewById(R.id.sw_knob0);
		sw_knob0.SetListener(new UIKnobSwitchListener(){
			@Override
			public void onChange(int position) {
				//toast("Knob position: "+position);
			}			
		});
		graph0 = (UIGraphView) findViewById(R.id.graph0);
		graph0.SetListener(new UIGraphViewListener(){
			@Override
			public void onScrollUpdate(float x, float y, float xScroll, float yScroll) {
				//using callback for fling bc this instance 
				//has a referance to graph0 and the sample data
			}			
		});
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
}
