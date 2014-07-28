package ioio.examples.hello;

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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bmt.customviews.UIGraphView;
import com.bmt.customviews.UIKnobSwitch;
//import com.bmt.customviews.UIKnob.UIKnobListener;

public class MainActivity extends IOIOActivity{	
	String tag = getClass().getSimpleName();
	UIGraphView gv = null; 
	UIKnobSwitch ks = null; 

	public class _IOIOLooper extends BaseIOIOLooper {
		String tag = getClass().getSimpleName();		
		/** The on-board LED. */
		private DigitalOutput led_;	
		public AnalogPin[] analogPins = null;
		public OutputPin[] outputPins = null;
		public twi_proxy[] twi_proxies = null;
		public spi_proxy _spi = null;	
		InputPin _inputPin = null;
		boolean input_pin9 = false;	
		
	class InputPin {
		public DigitalInput ioiopind; //all pins
		private int pin_num;
		boolean bit = false;
		DigitalInput.Spec.Mode pull_down = DigitalInput.Spec.Mode.PULL_DOWN;
		DigitalInput.Spec.Mode pull_up = DigitalInput.Spec.Mode.PULL_UP;
		DigitalInput.Spec.Mode floating = DigitalInput.Spec.Mode.FLOATING;
		DigitalInput.Spec.Mode pinType = floating;
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		OutputPin(int PinNum, int _type){
			pin_num = PinNum;
			try {					
				if(PinNum != 9){ //9 is input only
					if(_type == 3)
						ioiopindo = ioio_.openDigitalOutput(PinNum);
					if(_type != 3)
						ioiopindo = ioio_.openDigitalOutput(PinNum, DigitalOutput.Spec.Mode.OPEN_DRAIN, bit);
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
		private AnalogInput ioiopina;  //31-46
		private int pin_num;
		private float RefVolts = (float) 3.3;
		
		AnalogPin(int PinNum) {
			pin_num = PinNum;
			try {					
				if(PinNum <= 46 && PinNum >= 31){	
					ioiopina = ioio_.openAnalogInput(PinNum);
					ioiopina.setBuffer(256);
					float sr = ioiopina.getSampleRate();			//in Hz units.		
					RefVolts = ioiopina.getReference();
					Log.v(tag, "Reference Voltage: " + RefVolts+", SampleRate: "+sr);				
				}
				
			} catch (ConnectionLostException e) {
				
			}		
		}
		public float readAnalogInBuffered() throws InterruptedException, ConnectionLostException{
			float v = 0;
			float val = 0;			
			//printAnalogDroppedSamples(aIn0);
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				v = ioiopina.getVoltageBuffered();
				val = ioiopina.readBuffered();
				Log.v(tag, "Voltage: "+v+", Value: "+val);				
			}
			return v;
		}
		public float readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
			float v = 0;
			float val = 0;			
			printDroppedSamples();
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				v = ioiopina.getVoltage();
				val = ioiopina.read();
				Log.v(tag, "Voltage: "+v+", Value: "+val);
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
				Log.v(tag, "DroppedSamples: "+droppedSamples);
			}			
		}	
	}
	public class twi_proxy {		
		private TwiMaster twi;	
		private TwiMaster.Rate twiRate = TwiMaster.Rate.RATE_100KHz;
		private int twiNum = 1;
		private byte[] twiResult;
		TwiMaster.Result result;
		boolean TenBitAddr = false;	
		twi_proxy(int twi_num){
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
	public class spi_proxy {
		private SpiMaster spi;
		private SpiMaster.Rate spiRate = SpiMaster.Rate.RATE_125K;
		int misoPin = 28;
		int mosiPin = 29;
		int clkPin = 30;
		int ssPins = 27;
		byte[] spi_result;
		
		spi_proxy(){
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
			misoPin = _misoPin;  //int misoPin = 27;
			mosiPin = _mosiPin;  //int mosiPin = 28;
			clkPin = _clkPin;    //int clkPin = 29;
			ssPins = _ssPins;    //int ssPins = 26;			
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
			showVersions(ioio_, "IOIO connected!");			
			outputPins = new OutputPin[30];			
			//outputPins[0] = new OutputPin(1);	 //this turns led off??
			//outputPins[1] = new OutputPin(2);  //this turns led off??
			outputPins[2] = new OutputPin(3);
			//outputPins[3] = new OutputPin(4); //DA0
			//outputPins[4] = new OutputPin(5); //CL0				
			outputPins[5] = new OutputPin(6);
			outputPins[6] = new OutputPin(7);
			outputPins[7] = new OutputPin(8);
			
			outputPins[8] = new OutputPin(10);
			outputPins[9] = new OutputPin(11);
			outputPins[10] = new OutputPin(12);
			outputPins[11] = new OutputPin(13);
			outputPins[12] = new OutputPin(14);
			outputPins[13] = new OutputPin(15);
			outputPins[14] = new OutputPin(16);
			outputPins[15] = new OutputPin(17);
			
			outputPins[16] = new OutputPin(18);
			outputPins[17] = new OutputPin(19);
			outputPins[18] = new OutputPin(20);
			outputPins[19] = new OutputPin(21);
			outputPins[20] = new OutputPin(22);
			outputPins[21] = new OutputPin(23);
			outputPins[22] = new OutputPin(24);
			
			//outputPins[23] = new OutputPin(25);	//CL2		25
			
			//outputPins[24] = new OutputPin(26);	//DA2       26	
			//outputPins[25] = new OutputPin(27); //spi pins
			//outputPins[26] = new OutputPin(28);
			//outputPins[27] = new OutputPin(29);
			//outputPins[28] = new OutputPin(30);
			//OutputPin io23 = new OutputPin(47);	//DA1       47	
			//OutputPin io24 = new OutputPin(48);	//CL1		48 
			
			analogPins = new AnalogPin[16];
			analogPins[0] = new AnalogPin(31);
			analogPins[1] = new AnalogPin(32);
			analogPins[2] = new AnalogPin(33);
			analogPins[3] = new AnalogPin(34);
			analogPins[4] = new AnalogPin(35); //ref+
			analogPins[5] = new AnalogPin(36); //ref-
			analogPins[6] = new AnalogPin(37);
			analogPins[7] = new AnalogPin(38);
			
			analogPins[8] = new AnalogPin(39);
			analogPins[9] = new AnalogPin(40);
			analogPins[10] = new AnalogPin(41);
			analogPins[11] = new AnalogPin(42);
			analogPins[12] = new AnalogPin(43);
			analogPins[13] = new AnalogPin(44);
			analogPins[14] = new AnalogPin(45);
			analogPins[15] = new AnalogPin(46);	
		
			_spi = new spi_proxy();	
			//_spi.setSpiPins(_misoPin, _mosiPin, _clkPin, _ssPins);
			twi_proxies = new twi_proxy[3];
			twi_proxies[0] = new twi_proxy(0);
			twi_proxies[1] = new twi_proxy(1);
			twi_proxies[2] = new twi_proxy(2);

			led_ = ioio_.openDigitalOutput(0, false);			
			_inputPin = new InputPin(9, 0); //0:pullup, 1:pulldn, 2:float
			
			
		} catch (ConnectionLostException e) {
			
		}			
	}

	@Override
	public void disconnected() {
		enableUi(false);		
		toast("IOIO disconnected");			
		analogPins = null;
		outputPins = null;
		twi_proxies = null;
		_spi = null;	
		_inputPin = null;
		input_pin9 = false;		
	}		
	
	@Override
	public void loop() {		
		try {			
			int i = 0;
			for(i=0; i < analogPins.length;i++){
				if(analogPins[i] != null)
					analogPins[i].readAnalogInBuffered();
			}				
			for(i=0; i < outputPins.length;i++){
				if(outputPins[i] != null)
					outputPins[i].writeBit(false);
			}				
			//for(i=0; i < twi_proxies.length;i++){
				//int address = 0;
				//twi_proxies[i].twi_RW(address, request, responseLength, true);
			//}
			//_spi.spi_RW(address, request, responselength, async);
			input_pin9 = _inputPin.readBit();			
			Thread.sleep(50); 			 //milliseconds, = 50 samples in buffer for analog
			
			//How to measure latency???
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ConnectionLostException e) {
		}
	}
}
	private int numConnected_ = 0;	
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				if (enable) {
					if (numConnected_++ == 0) {						
						gv.setEnabled(true);
						ks.setEnabled(true);
					}
				} else {
					if (--numConnected_ == 0) {
						gv.setEnabled(false);
						ks.setEnabled(false);
					}
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		ks = (UIKnobSwitch) findViewById(R.id.uIKnobSwitch1); 
		gv = (UIGraphView) findViewById(R.id.uIGraphView1);		
	}	
	private void showVersions(IOIO ioio, String title) {
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

	private void toast(final String message) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}	
}
