package com.bmt.ioio_demo;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bmt.ioio_demo.MainActivity._IOIOLooper.AnalogPin;
import com.bmt.ioio_demo.MainActivity._IOIOLooper.InputPin;
import com.bmt.ioio_demo.MainActivity._IOIOLooper.OutputPin;
import com.bmt.ioio_demo.MainActivity._IOIOLooper.Spi_proxy;
import com.bmt.ioio_demo.MainActivity._IOIOLooper.Twi_proxy;
//import com.bmt.customviews.UIKnob.UIKnobListener;

public class MainActivity extends IOIOActivity implements OnClickListener{	
	String tag = getClass().getSimpleName(); 
	HashMap<String, ToggleButton> ToggleButtons = null;
	HashMap<String, TextView> TextViews = null;
	HashMap<String, AnalogPin> AnalogPins = null;
	HashMap<String, OutputPin> OutputPins = null;
	HashMap<String, Twi_proxy> Twi_proxies = null;
	public Spi_proxy Spi = null;
	InputPin InputPin9 = null; 
	boolean show_toast_connection_info = true;

	private int numConnected_ = 0;	
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						//ToggleButtons.get("led").setEnabled(true);
						for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							entry.getValue().setEnabled(true);
						}						
					}
				} else {
					if (--numConnected_ == 0) {
						//ToggleButtons.get("led").setEnabled(false);
						for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							entry.getValue().setEnabled(false);
						}						
					}
				}
			}
		});
	}
	
	public void setTextD9(){
	    runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	if(InputPin9.readBit()) 
	        		TextViews.get("D9").setText("D9: true");
	        	else
	        		TextViews.get("D9").setText("D9: false");
	        }
	    });		
	}	
	public void setText(final String pin, final float v){
	    runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            // This code will always run on the UI thread, therefore is safe to modify UI elements.
	        	String s = "AN"+pin+": "+v;
	        	TextViews.get(pin).setText(s.substring(0, 6));
	        }
	    });		
	}	
	/*public void setLed(){
	    runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	try{	        		
	        		if(ToggleButtons.get("led").isChecked()){ //this one reverses
	        			OutputPins.get("led").writeBit(true);
	        		}  else {
	        			OutputPins.get("led").writeBit(false);
	        		}
	        	} catch(Exception e){
	        		toast("setLed: ");
	        		e.printStackTrace();
	        	}
	        }
	    });		
	}*/
	public class _IOIOLooper extends BaseIOIOLooper {
		String tag = getClass().getSimpleName();		
		/** The on-board LED. */			
	class InputPin {
		public DigitalInput ioiopind; //all pins
		private int pin_num;
		boolean bit = false;
		DigitalInput.Spec.Mode pull_down = DigitalInput.Spec.Mode.PULL_DOWN;
		DigitalInput.Spec.Mode pull_up = DigitalInput.Spec.Mode.PULL_UP;
		DigitalInput.Spec.Mode floating = DigitalInput.Spec.Mode.FLOATING;
		DigitalInput.Spec.Mode pinType = floating; //change to what you like
		
		InputPin(int PinNum){
			pin_num = PinNum;
			try {					
					ioiopind = ioio_.openDigitalInput(PinNum, pinType);   //all pins
				
			} catch (ConnectionLostException e) {
				
			}		
		}
		InputPin(int PinNum, int mode){
			pin_num = PinNum;
			pinType = floating;
			try {	
				switch(mode){
					case 0:
						pinType = pull_up;					
						break;
					case 1:
						pinType = pull_down;					
						break;
					case 2:
						pinType = floating;
						break;					
				}
				ioiopind = ioio_.openDigitalInput(PinNum, pinType);   //all pins			
			} catch (ConnectionLostException e) {
				
			}		
		}	
		public boolean readBit(){
			try {
				bit = ioiopind.read();
			} catch (InterruptedException e) {
				interrupted();
			} catch (ConnectionLostException e) {
			}	
			return bit;
		}	
	}
	class OutputPin {
		public DigitalOutput ioiopindo; //all except for 9
		private int pin_num;
		boolean bit = false;	
		OutputPin(int PinNum){
			pin_num = PinNum;
			try {					
				if(PinNum != 9){ //9 is input only
					ioiopindo = ioio_.openDigitalOutput(PinNum);
				}
			} catch (ConnectionLostException e) {
				
			}		
		}
		OutputPin(int PinNum, int _type, boolean state){
			pin_num = PinNum;
			try {					
				if(PinNum != 9){ //9 is input only					
					if(_type == 3)
						ioiopindo = ioio_.openDigitalOutput(PinNum, state);					
					if(_type != 3)
						ioiopindo = ioio_.openDigitalOutput(PinNum, DigitalOutput.Spec.Mode.OPEN_DRAIN, state);
				}
			} catch (ConnectionLostException e) {
				
			}		
		}
		public boolean readBit(){
			return bit; //ioiopindo.read(); no read function for output
		}
		public void writeBit(boolean _bit){
			try {
				bit = _bit;
				ioiopindo.write(bit);
			} catch (ConnectionLostException e) {
				
			}
		}
	}

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
				int _PinNum = pin_num-31;
				if( TextViews.containsKey("AN"+_PinNum) ){		//textViews ANO-AN15
					//TextView tv = TextViews.get( "AN"+_PinNum );
					//tv.setText("AN"+_PinNum);
					setText("AN"+_PinNum, v);
				}
				//val = ioiopina.readBuffered();
				//toast("Voltage: "+v+", Value: "+val);				
			//}
			return v;
		}
		public float readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
			float v = 0;
			//float val = 0;			
			printDroppedSamples();
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				v = ioiopina.getVoltage();
				//val = ioiopina.read();
				//toast("Voltage: "+v+", Value: "+val);
			}
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
	public class Twi_proxy {		
		private TwiMaster twi;	
		private TwiMaster.Rate twiRate = TwiMaster.Rate.RATE_100KHz;
		private int twiNum = 1;
		private byte[] twiResult;
		TwiMaster.Result result;
		boolean TenBitAddr = false;	
		Twi_proxy(int twi_num){
			try {
				twiNum = twi_num;
				openTWI();
			} catch (ConnectionLostException e) {
				
			}
		}
		public void openTWI() throws ConnectionLostException{
			Log.d(tag, "Opening TwiNum: [1]: " + twiNum + " and TwiRate: [100k] : "+twiRate);		
			twi = ioio_.openTwiMaster(twiNum, twiRate, false); //pass true as third argument for SMBus levels			
		}
		public void closeTWI(){
			twi.close();
			twi = null;
		}
		public void setTwiRate(TwiMaster.Rate r){
			Log.d(tag, "Setting TwiRate: "+r);
			twiRate = r;
			closeTWI();
			try {
				openTWI();
			} catch (ConnectionLostException e) {
				
			}
		}
		public byte[] twi_RW(int address, byte[] request, int responseLength, boolean async) throws InterruptedException, ConnectionLostException{
			//twi = ioio_.openTwiMaster(twiNum, twiRate, false);
			twiResult = new byte[responseLength];
			if( address > 256 ) 
				TenBitAddr = true;
			else
				TenBitAddr = false;
			
			if(async){
				TwiMaster.Result result = twi.writeReadAsync(address , TenBitAddr, request, request.length, twiResult, twiResult.length);
				result.waitReady();  // blocks until response is available
			} else {
				twi.writeRead(address, TenBitAddr, request, request.length, twiResult, twiResult.length); //smbus Level
			}
			//twi.close();
			return twiResult;
		}
	}
	public class Spi_proxy {
		private SpiMaster spi;
		private SpiMaster.Rate spiRate = SpiMaster.Rate.RATE_125K;
		int misoPin = 1;
		int mosiPin = 2;
		int clkPin = 3;
		int ssPins = 4;
		byte[] spi_result;
		
		Spi_proxy(){
			try {
				openSPI();
				spi_result = null;
			} catch (ConnectionLostException e) {
				
			}
		}
		public void openSPI() throws ConnectionLostException{
			Log.d(tag, "Opening SPI iso: [27]: "+ misoPin + " osi: [28]: "+mosiPin+" clk: [29]: "+clkPin+" rate: [125k] :"+spiRate);
			//spi = ioio_.openSpiMaster(misoPin, mosiPin, clkPin, ssPins, spiRate );
			spi = ioio_.openSpiMaster(misoPin, mosiPin, clkPin, ssPins, spiRate );			
		}
		public void closeSPI(){
			spi.close();
		}
		public void setSpiPins(int _misoPin, int _mosiPin, int _clkPin, int _ssPins){
			misoPin = _misoPin;  //int misoPin = 27;1
			mosiPin = _mosiPin;  //int mosiPin = 28;2
			clkPin = _clkPin;    //int clkPin = 29;3
			ssPins = _ssPins;    //int ssPins = 26;4		
		}
		public byte[] spi_RW(int address, byte[] request, int responselength, boolean async) throws InterruptedException, ConnectionLostException{
			//spi = ioio_.openSpiMaster(misoPin, mosiPin, clkPin, ssPins, spiRate );
			//what's up with 7???
			spi_result = new byte[responselength];
			if(async){
				SpiMaster.Result result = spi.writeReadAsync(address, request, request.length, 7, spi_result, spi_result.length);
				result.waitReady();  // blocks until response is available						
			} else {
				spi.writeRead(address, request, request.length, 7, spi_result, spi_result.length);			
			}
			//spi.close();
			return spi_result;
		}		
	}	
	
	
	@Override
	protected void setup() {
		try{
			enableUi(true);
			showVersions(ioio_, "IOIO connected!");	
			OutputPins = new HashMap<String, OutputPin>(); 
			AnalogPins = new HashMap<String, AnalogPin>();
			Twi_proxies = new HashMap<String, Twi_proxy>();
			
			OutputPins.put("led", new OutputPin(0,3,false));  //mode 3 = open_collector
			Thread.sleep(250);
			OutputPins.get("led").writeBit(true);  //mode 3 = open_collector
			//Thread.sleep(250);
			//OutputPins.get("led").writeBit(false);
			//OutputPins.put("D1", new OutputPin(1,3,false)); //DA1 resource already claimed error
			//OutputPins.put("D2", new OutputPin(2,3,false)); //CL1 resource already claimed error
			//OutputPins.put("D3", new OutputPin(3,3,false)); //weirdd setup error length=3  setled error
			//OutputPins.put("D4", new OutputPin(4,3,false)); //DA0    resource already claimed error
			//OutputPins.put("D5",  new OutputPin(5,3,false)); //CL0
			//OutputPins.put("D6",  new OutputPin(6,3,false)); //ssPin
			//OutputPins.put("D7",  new OutputPin(7,3,false)); //misoPin
			//OutputPins.put("D8",  new OutputPin(8,3,false)); //mosiPin
			//OutputPins.put("D9",  new OutputPin(10,3,false)); //clkPin
			
			//D10-24= 14 pins
			OutputPins.put("D0",  new OutputPin(11,3,false));
			OutputPins.put("D1",  new OutputPin(12,3,false));
			OutputPins.put("D2",  new OutputPin(13,3,false));			
			OutputPins.put("D3",  new OutputPin(14,3,false));
			OutputPins.put("D4",  new OutputPin(15,3,false));
			OutputPins.put("D5",  new OutputPin(16,3,false));
			OutputPins.put("D6",  new OutputPin(17,3,false));			
			
			OutputPins.put("D7",  new OutputPin(18,3,false));
			OutputPins.put("D8",  new OutputPin(19,3,false));
			OutputPins.put("D9",  new OutputPin(20,3,false));
			OutputPins.put("D10",  new OutputPin(21,3,false));			
			OutputPins.put("D11",  new OutputPin(22,3,false));
			OutputPins.put("D12",  new OutputPin(23,3,false));
			OutputPins.put("D13",  new OutputPin(24,3,false));
			OutputPins.put("D14",  new OutputPin(27,3,false));
			
			//OutputPins.put("D25",  new OutputPin(25,3,false)); //CL2
			//OutputPins.put("D26",  new OutputPin(26,3,false)); //DA2
			OutputPins.put("D15",  new OutputPin(28,3,false)); //15th bit
			//OutputPins.put("D16",  new OutputPin(29,3,false));
			//OutputPins.put("D17",  new OutputPin(30,3,false));
			
			
			for(int i=0;i<16;i++){
				AnalogPins.put("AN"+i, new AnalogPin(i+31));				
			}
			
			//Spi_proxy Spi = new Spi_proxy();
			//_spi.setSpiPins(_misoPin, _mosiPin, _clkPin, _ssPins);
			//Twi_proxies.put("TWI0", new Twi_proxy(0));  //3-4
			//Twi_proxies.put("TWI1", new Twi_proxy(1));	//1-2
			//Twi_proxies.put("TWI2", new Twi_proxy(2));	//25-26
			InputPin9 = new InputPin(9, 0); //0:pullup, 1:pulldn, 2:float
			
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
		OutputPins = null;
		Twi_proxies = null;
		Spi = null; 		
	}
	
	public void interrupted() {
		//enableUi(false);        //interrupted
		if(show_toast_connection_info) toast("IOIO interrupted"); 		
	}	
	@Override
	public void loop() {		
		try {
			//see if any buttons changed and update output			
			for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){								
				if(entry.getKey().contentEquals("led")){ 
					if(entry.getValue().isChecked()){
						OutputPins.get("led").writeBit(false);
					}
					else {
						OutputPins.get("led").writeBit(true);							
					}
				} else {
					//CharSequence cs = entry.getKey().subSequence(1, entry.getKey().length()); //D0-D15 = Pins 6-22					
					//int pn = Integer.parseInt(cs.toString()) ; //togglebuttons start at 0-15
					//Log.i("key: ", entry.getKey()+" , value:"+entry.getValue().getClass().getName() + " pn: "+pn);					
					if ( OutputPins.containsKey(entry.getKey()) ){					
						if(entry.getValue().isChecked()){ 
							OutputPins.get(entry.getKey()).writeBit(true);
						}
						else {
							OutputPins.get(entry.getKey()).writeBit(false);
						}
					} else {
						toast("no key for "+entry.getKey());					
					}					
				}
			}
			setTextD9(); //read inputpin 9 and set label			
			//get analog inputs and display in text views			
			//Thread.sleep(350); 			 //milliseconds, = 50 samples in buffer for analog
			Thread.sleep(250); 			 //milliseconds, = 50 samples in buffer for analog
			
			//Process Analog Inputs
			int i=0;
			for(i=0;i<16;i++){
				float v = 0;
				if(AnalogPins.containsKey("AN"+i)) 
					v = AnalogPins.get("AN"+i).readAnalogInBuffered();
				if(TextViews.containsKey("AN"+i))
					setText("AN"+i, v);
			}		
									
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
		setContentView(R.layout.main);
		//ks = (UIKnobSwitch) findViewById(R.id.uIKnobSwitch1); 
		//gv = (UIGraphView) findViewById(R.id.uIGraphView1);	
		ToggleButtons = new HashMap<String, ToggleButton>();
		ToggleButtons.put("D0", (ToggleButton) findViewById(R.id.toggleButtonD0));		//10
		ToggleButtons.put("D1", (ToggleButton) findViewById(R.id.toggleButtonD1));		//11
		ToggleButtons.put("D2", (ToggleButton) findViewById(R.id.toggleButtonD2));		//12
		ToggleButtons.put("D3", (ToggleButton) findViewById(R.id.toggleButtonD3));		//13 
	
		ToggleButtons.put("D4", (ToggleButton) findViewById(R.id.toggleButtonD4));		//14
		ToggleButtons.put("D5", (ToggleButton) findViewById(R.id.toggleButtonD5));		//15
		ToggleButtons.put("D6", (ToggleButton) findViewById(R.id.toggleButtonD6));		//16
		ToggleButtons.put("D7", (ToggleButton) findViewById(R.id.toggleButtonD7));		//17 

		ToggleButtons.put("D8", (ToggleButton) findViewById(R.id.toggleButtonD8));		//18
		ToggleButtons.put("D9", (ToggleButton) findViewById(R.id.toggleButtonD9));		//19
		ToggleButtons.put("D10", (ToggleButton) findViewById(R.id.toggleButtonD10));	//20
		ToggleButtons.put("D11", (ToggleButton) findViewById(R.id.toggleButtonD11));	//21
	
		ToggleButtons.put("D12", (ToggleButton) findViewById(R.id.toggleButtonD12));	//22
		ToggleButtons.put("D13", (ToggleButton) findViewById(R.id.toggleButtonD13));	//23
		ToggleButtons.put("D14", (ToggleButton) findViewById(R.id.toggleButtonD14));	//24
		ToggleButtons.put("D15", (ToggleButton) findViewById(R.id.toggleButtonD15));	//25
		
		ToggleButtons.put("led", (ToggleButton) findViewById(R.id.toggleButtonLed));
		
		TextViews = new HashMap<String, TextView>();
		TextViews.put("AN0", (TextView) findViewById(R.id.AN0Text));
		TextViews.put("AN1", (TextView) findViewById(R.id.AN1Text));
		TextViews.put("AN2", (TextView) findViewById(R.id.AN2Text));
		TextViews.put("AN3", (TextView) findViewById(R.id.AN3Text));
		TextViews.put("AN4", (TextView) findViewById(R.id.AN4Text));
		TextViews.put("AN5", (TextView) findViewById(R.id.AN5Text));
		TextViews.put("AN6", (TextView) findViewById(R.id.AN6Text));
		TextViews.put("AN7", (TextView) findViewById(R.id.AN7Text));
		
		TextViews.put("AN8", (TextView) findViewById(R.id.AN8Text));
		TextViews.put("AN9", (TextView) findViewById(R.id.AN9Text));		
		TextViews.put("AN10", (TextView) findViewById(R.id.AN10Text));
		TextViews.put("AN11", (TextView) findViewById(R.id.AN11Text));
		TextViews.put("AN12", (TextView) findViewById(R.id.AN12Text));
		TextViews.put("AN13", (TextView) findViewById(R.id.AN13Text));
		TextViews.put("AN14", (TextView) findViewById(R.id.AN14Text));
		TextViews.put("AN15", (TextView) findViewById(R.id.AN15Text));
		TextViews.put("D9", (TextView) findViewById(R.id.D9Text));

		//Disable the buttons
		for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
			Log.i("key: ", entry.getKey().trim()+" , value:"+entry.getValue().getClass().getName());
			entry.getValue().setEnabled(false);
		}				
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
		// TODO Auto-generated method stub
		
	}	
}
