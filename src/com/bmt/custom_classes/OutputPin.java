package com.bmt.custom_classes;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class OutputPin {
	String tag = getClass().getSimpleName();
	public DigitalOutput ioiopindo = null; //all except for 9
	private int pin_num = -1;
	boolean bit = false;

	public int getPinNum(){
		return pin_num;
	}
	public OutputPin(IOIO ioio, int PinNum){		
		try {					
			if(PinNum != 9){ //9 is input only
				pin_num = PinNum;
				ioiopindo = ioio.openDigitalOutput(PinNum);
			}
		} catch (ConnectionLostException e) {
			ioiopindo = null;
		}		
	}
	public OutputPin(IOIO ioio, int PinNum, int _type, boolean state){
		try {					
			if(PinNum != 9){ //9 is input only
				pin_num = PinNum;
				if(_type < 3)
					ioiopindo = ioio.openDigitalOutput(PinNum, state);					
				if(_type == 3)
					ioiopindo = ioio.openDigitalOutput(PinNum, DigitalOutput.Spec.Mode.OPEN_DRAIN, state);
			}
		} catch (ConnectionLostException e) {
			ioiopindo = null;
		}		
	}
	public boolean readBit(){
		return bit; //ioiopindo.read(); no read function for output
	}
	public void writeBit(boolean _bit){
		try {
			bit = _bit;
			if(ioiopindo != null)
				ioiopindo.write(bit);
		} catch (ConnectionLostException e) {
			ioiopindo = null;
		}
	}
}