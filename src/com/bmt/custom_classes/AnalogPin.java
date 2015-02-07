package com.bmt.custom_classes;

import java.util.ArrayList;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class AnalogPin {
	String tag = getClass().getSimpleName();
	public AnalogInput ioiopina = null;  //31-46
	private int pin_num;
	private float RefVolts = (float) 3.3;
	public ArrayList<Float> samples = null;
	
	public int getPinNum(){
		return pin_num;
	}
	public float[] getSamples(){
		float[] f = new float[samples.size()];
		for(int i=0;i<samples.size();i++){
			f[i] = samples.get(i);
		}
		return f;
	}
	public AnalogPin(IOIO ioio, int PinNum) {
		pin_num = PinNum;
		try {					
			if(PinNum <= 46 && PinNum >= 31){	
				ioiopina = ioio.openAnalogInput(PinNum);
				ioiopina.setBuffer(256);
				//float sr = ioiopina.getSampleRate();			//in Hz units.		
				RefVolts = ioiopina.getReference();
				//Log.i(tag, "Reference Voltage: " + RefVolts+", SampleRate: "+sr );				
			}
			samples = new ArrayList<Float>();
			
		} catch (ConnectionLostException e) {
			ioiopina = null;
		}		
	}
	public float readAnalogInBuffered() throws InterruptedException, ConnectionLostException{
		float v = 0;
		if(ioiopina != null){			
			printDroppedSamples();
			int numSampleToRead = ioiopina.available();
			for(int i=0;i<numSampleToRead;i++){	//reads all available samples
				samples.add( ioiopina.getVoltageBuffered() );
				//v = ioiopina.readBuffered();
			}
		}
		return v;
	}
	public float readAnalogInUnBuffered() throws InterruptedException, ConnectionLostException{
		float v = 0;
		if(ioiopina != null){			
			printDroppedSamples();
			samples.add( ioiopina.getVoltage() );
			//v = ioiopina.read();
		}
		return v;			
	}
	public void setRefVolts(float volts){
		RefVolts = (float) volts;
		if (volts > 3.3) 
			Log.d(tag,"RefVolts set to higher than 3.3");
	}
	public void printDroppedSamples() throws ConnectionLostException{
		if(ioiopina != null){
			int droppedSamples = ioiopina.getOverflowCount();
			if(droppedSamples > 0){
				Log.i(tag, "DroppedSamples: "+droppedSamples);
			}
		}
	}
}
