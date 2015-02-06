package com.bmt.custom_classes;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class OutputPin {
	public DigitalOutput ioiopindo; //all except for 9
	private int pin_num;
	boolean bit = false;
	IOIO ioio_ = null;
	
	public OutputPin(IOIO ioio, int PinNum){
		ioio_ = ioio;
		pin_num = PinNum;
		try {					
			if(PinNum != 9){ //9 is input only
				ioiopindo = ioio_.openDigitalOutput(PinNum);
			}
		} catch (ConnectionLostException e) {
			
		}		
	}
	public OutputPin(IOIO ioio, int PinNum, int _type, boolean state){
		ioio_ = ioio;
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