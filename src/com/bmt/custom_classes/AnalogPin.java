package com.bmt.custom_classes;

//import java.util.ArrayList;
import java.nio.ByteBuffer;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class AnalogPin {
	String tag = getClass().getSimpleName();
	public AnalogInput ioiopina = null;  //31-46
	private int pin_num;
	//public float RefVolts = (float) 3.3;
	//public float SampleRate = 0;
	int sizeOfFloat = 0;
	public int getPinNum(){
		return pin_num;
	}
	public AnalogInput getIOIO_Pin(){
		return ioiopina;
	}
	public AnalogPin(IOIO ioio, int PinNum) {
		pin_num = PinNum;
		Util u = new Util();
		sizeOfFloat = u.sizeOfFloat();	  //4		
		try {					
			if(PinNum <= 46 && PinNum >= 31){	
				ioiopina = ioio.openAnalogInput(PinNum);
				ioiopina.setBuffer(64);
				//SampleRate = ioiopina.getSampleRate();			//in Hz units.		
				//RefVolts = ioiopina.getReference();
				//Log.i(tag, "Reference Voltage: " + RefVolts+", SampleRate: "+sr );				
			}
		} catch (ConnectionLostException e) {
			ioiopina = null;
		}		
	}
	public byte[] readAnalogInByteBuffer() throws InterruptedException, ConnectionLostException{
		int numSamplesToRead = ioiopina.available();
		ByteBuffer _byteBuffer = ByteBuffer.allocate(numSamplesToRead * sizeOfFloat);
		for(int i=0;i<numSamplesToRead;i++){	//reads all available samples
			_byteBuffer.putFloat(ioiopina.getVoltageBuffered());
		}
		return _byteBuffer.array();
	}	
	public float[] readAnalogInBuffered() throws InterruptedException, ConnectionLostException{
		float[] samples = null;
		if(ioiopina != null){			
			printDroppedSamples();
			int numSampleToRead = ioiopina.available();
			samples = new float[numSampleToRead];
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				//samples.add( ioiopina.getVoltageBuffered() );
				//float v = ioiopina.readBuffered();
				samples[i] = (float) ioiopina.getVoltageBuffered();
			}
		}
		return samples;
	}
	public float readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
		float v = 0;
		if(ioiopina != null){			
			printDroppedSamples();
			//samples.add( ioiopina.getVoltage() );
			//v = ioiopina.read();
			v = ioiopina.getVoltage();
		}
		return v;			
	}
	/*public void setRefVolts(float volts){
		RefVolts = (float) volts;
		if (volts > 3.3) 
			Log.d(tag,"RefVolts set to higher than 3.3");
	}*/
	public void printDroppedSamples() throws ConnectionLostException{
		if(ioiopina != null){
			int droppedSamples = ioiopina.getOverflowCount();
			if(droppedSamples > 0){
				Log.i(tag, "DroppedSamples: "+droppedSamples);
			}
		}
	}
}
