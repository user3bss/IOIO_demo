package com.bmt.custom_classes;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AnalogPin {
	String tag = getClass().getSimpleName();
	public AnalogInput ioiopina;  //31-46
	private int pin_num;
	private float RefVolts = (float) 3.3;
	IOIO ioio_ = null;
	
	public AnalogPin(IOIO ioio, int PinNum) {
		ioio_ = ioio;
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
			Log.i(tag, "DroppedSamples: "+droppedSamples);
		}			
	}
}
