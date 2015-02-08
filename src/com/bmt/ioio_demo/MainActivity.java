package com.bmt.ioio_demo;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bmt.custom_classes.InputPin;
import com.bmt.custom_classes.OutputPin;
import com.bmt.customviews.LED;

public class MainActivity extends IOIOActivity{	
	String tag = getClass().getSimpleName(); 
	HashMap<String, ToggleButton> ToggleButtons = null;
	LED LEDInput = null;
	HashMap<String, OutputPin> OutputPins = null;
	InputPin InputPin9 = null; 
	boolean show_toast_connection_info = true;

	private int numConnected_ = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);	
		LEDInput = (LED) findViewById(R.id.lED1);
		LEDInput.setIsOn(false);
		
		ToggleButtons = new HashMap<String, ToggleButton>();
		ToggleButtons.put("led", (ToggleButton) findViewById(R.id.toggleButtonLed));		
		ToggleButtons.put("D11", (ToggleButton) findViewById(R.id.toggleButtonD0));
		ToggleButtons.put("D12", (ToggleButton) findViewById(R.id.toggleButtonD1));
		ToggleButtons.put("D13", (ToggleButton) findViewById(R.id.toggleButtonD2));
		ToggleButtons.put("D14", (ToggleButton) findViewById(R.id.toggleButtonD3)); 
		ToggleButtons.put("D15", (ToggleButton) findViewById(R.id.toggleButtonD4));
		ToggleButtons.put("D16", (ToggleButton) findViewById(R.id.toggleButtonD5));
		ToggleButtons.put("D17", (ToggleButton) findViewById(R.id.toggleButtonD6));
		ToggleButtons.put("D18", (ToggleButton) findViewById(R.id.toggleButtonD7)); 
		ToggleButtons.put("D19", (ToggleButton) findViewById(R.id.toggleButtonD8));
		ToggleButtons.put("D20", (ToggleButton) findViewById(R.id.toggleButtonD9));
		ToggleButtons.put("D21", (ToggleButton) findViewById(R.id.toggleButtonD10));
		ToggleButtons.put("D22", (ToggleButton) findViewById(R.id.toggleButtonD11));
		ToggleButtons.put("D23", (ToggleButton) findViewById(R.id.toggleButtonD12));
		ToggleButtons.put("D24", (ToggleButton) findViewById(R.id.toggleButtonD13));
		ToggleButtons.put("D27", (ToggleButton) findViewById(R.id.toggleButtonD14));
		ToggleButtons.put("D28", (ToggleButton) findViewById(R.id.toggleButtonD15));
		
		disableToggleButtons();
	}	
	@Override
	protected void onResume() {
		super.onResume();
	}
	private void disableToggleButtons(){
		for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
			entry.getValue().setEnabled(false);
		}		
	}
	private void enableToggleButtons(){
		for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
			entry.getValue().setEnabled(true);
		}		
	}	
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					//if (numConnected_++ >= 0) {
						enableToggleButtons();						
					//}
				} else {
					//if (--numConnected_ == 0) {
						disableToggleButtons();
						LEDInput.setIsOn(false);
					//}
				}
			}
		});
	}
	
	public void setLed(final boolean bit){
	    runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	try{	        		
	        		LEDInput.setIsOn(bit);
	        	} catch(Exception e){
	        	}
	        }
	    });		
	}

	class Looper extends BaseIOIOLooper {
		@Override
		protected void setup() {
			try{
				enableUi(true);
				showVersions(ioio_, "IOIO connected!");	
				OutputPins = new HashMap<String, OutputPin>(); 
				OutputPins.put("led", new OutputPin(ioio_,IOIO.LED_PIN, 0, false));  //ledPin == 0,  mode 3 = open_collector
				OutputPins.get("led").writeBit(true);  //mode 3 = open_collector				
				OutputPins.put("D11",  new OutputPin(ioio_,11,0,false));
				OutputPins.put("D12",  new OutputPin(ioio_,12,0,false));
				OutputPins.put("D13",  new OutputPin(ioio_,13,0,false));			
				OutputPins.put("D14",  new OutputPin(ioio_,14,0,false));
				OutputPins.put("D15",  new OutputPin(ioio_,15,0,false));
				OutputPins.put("D16",  new OutputPin(ioio_,16,0,false));
				OutputPins.put("D17",  new OutputPin(ioio_,17,0,false));			
				
				OutputPins.put("D18",  new OutputPin(ioio_,18,0,false));
				OutputPins.put("D19",  new OutputPin(ioio_,19,0,false));
				OutputPins.put("D20",  new OutputPin(ioio_,20,0,false));
				OutputPins.put("D21",  new OutputPin(ioio_,21,0,false));			
				OutputPins.put("D22",  new OutputPin(ioio_,22,0,false));
				OutputPins.put("D23",  new OutputPin(ioio_,23,0,false));
				OutputPins.put("D24",  new OutputPin(ioio_,24,0,false));
				OutputPins.put("D27",  new OutputPin(ioio_,27,0,false));
				OutputPins.put("D28",  new OutputPin(ioio_,28,0,false));
				
				//0		IOIO.LEDPIN
				//1		DA1 "TWI1"
				//2 	CL1 "TWI1"
				//3 	?? "TWI0"
				//4 	DA0 "TWI0"
				//5 	??CL0 "TWI0"
				//6 	ssPin	"SPI"
				//7 	misoPin	"SPI"
				//8 	mosiPin "SPI"
				//10 	clkPin	"SPI"
				//25 	CL2 "TWI2"
				//26	DA2 "TWI2"
				//29
				//30

				InputPin9 = new InputPin(ioio_,9, 0); //0:pullup, 1:pulldn, 2:float
				boolean bit = InputPin9.readBit();
				setLed(bit);
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
			OutputPins = null;			
		}	
		@Override
		public void loop() {		
				//see if any buttons changed and update output			
				for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
					if ( OutputPins.containsKey(entry.getKey()) ){
						if(entry.getKey().contains("led")){
							OutputPins.get(entry.getKey()).writeBit(!entry.getValue().isChecked());	
						} else {
							OutputPins.get(entry.getKey()).writeBit(entry.getValue().isChecked());
						}
					} else {
						toast("no key for "+entry.getKey());					
					}
				}
				boolean bit = InputPin9.readBit();
				setLed(bit);
				
				try {
					Thread.sleep(350);
				} catch (InterruptedException e) {
				} 			 //milliseconds, = 50 samples in buffer for analog
		}
	}
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
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
}
