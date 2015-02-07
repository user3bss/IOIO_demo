package com.bmt.custom_classes;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class InputPin {
	String tag = getClass().getSimpleName();
	public DigitalInput ioiopind = null; //all pins
	private int pin_num;
	boolean bit = false;
	DigitalInput.Spec.Mode pinType = DigitalInput.Spec.Mode.FLOATING; //change to what you like

	public int getPinNum(){
		return pin_num;
	}
	public InputPin(IOIO ioio, int PinNum){
		pin_num = PinNum;
		try {					
			ioiopind = ioio.openDigitalInput(PinNum, pinType);   //all pins
		} catch (ConnectionLostException e) {
			ioiopind = null;
		}		
	}
	public InputPin(IOIO ioio, int PinNum, int mode){
		pin_num = PinNum;
		try {	
			switch(mode){
				case 0:
					pinType = DigitalInput.Spec.Mode.PULL_DOWN;					
					break;
				case 1:
					pinType = DigitalInput.Spec.Mode.PULL_UP;					
					break;
				case 2:
					pinType = DigitalInput.Spec.Mode.FLOATING;
					break;					
			}
			ioiopind = ioio.openDigitalInput(PinNum, pinType);   //all pins			
		} catch (ConnectionLostException e) {
			ioiopind = null;
		}		
	}	
	public boolean readBit(){
		try {
			if(ioiopind != null)
				bit = ioiopind.read();
		} catch (InterruptedException e) {
			//ioiopind = null;
		} catch (ConnectionLostException e) {
			ioiopind = null;
		}	
		return bit;
	}	
}
