package com.bmt.custom_classes;

import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class TwiProxy {
	String tag = getClass().getSimpleName();
	private TwiMaster twi;	
	private TwiMaster.Rate twiRate = TwiMaster.Rate.RATE_100KHz;
	private int twiNum = 1;
	private byte[] twiResult;
	TwiMaster.Result result;
	boolean TenBitAddr = false;	
	IOIO ioio_ = null;
	
	TwiProxy(int twi_num, IOIO ioio){
		ioio_ = ioio;
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
