package com.bmt.custom_classes;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class InputPin {
	public DigitalInput ioiopind; //all pins
	private int pin_num;
	boolean bit = false;
	DigitalInput.Spec.Mode pull_down = DigitalInput.Spec.Mode.PULL_DOWN;
	DigitalInput.Spec.Mode pull_up = DigitalInput.Spec.Mode.PULL_UP;
	DigitalInput.Spec.Mode floating = DigitalInput.Spec.Mode.FLOATING;
	DigitalInput.Spec.Mode pinType = floating; //change to what you like
	IOIO ioio_ = null;
	
	public InputPin(IOIO ioio, int PinNum){
		ioio_ = ioio;
		pin_num = PinNum;
		try {					
			ioiopind = ioio_.openDigitalInput(PinNum, pinType);   //all pins
		} catch (ConnectionLostException e) {
			
		}		
	}
	public InputPin(IOIO ioio, int PinNum, int mode){
		ioio_ = ioio;
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
		} catch (ConnectionLostException e) {
		}	
		return bit;
	}	
}
