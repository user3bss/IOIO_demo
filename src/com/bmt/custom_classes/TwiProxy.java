package 	com.bmt.custom_classes;

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
	
	TwiProxy(int twi_num, IOIO ioio){
		twiNum = twi_num;
		openTWI(ioio);
	}
	public void openTWI(IOIO ioio){					
		try {
			twi = ioio.openTwiMaster(twiNum, twiRate, false);
		} catch (ConnectionLostException e) {
			twi = null;
		} //pass true as third argument for SMBus levels
	}
	public void closeTWI(){
		twi.close();
		twi = null;
	}
	public void setTwiRate(TwiMaster.Rate r){
		Log.d(tag, "Setting TwiRate: "+r);
		twiRate = r;
	}
	public byte[] twi_RW(int address, byte[] request, int responseLength, boolean async){
		twiResult = new byte[responseLength];
		if( address > 256 ) 
			TenBitAddr = true;
		else
			TenBitAddr = false;
		try{
			if(async && twi != null){
				TwiMaster.Result result = twi.writeReadAsync(address , TenBitAddr, request, request.length, twiResult, twiResult.length);
				result.waitReady();
			} else if(twi != null){
				twi.writeRead(address, TenBitAddr, request, request.length, twiResult, twiResult.length); //smbus Level
			}
		} catch (ConnectionLostException e) {
			twi = null;
		} catch (InterruptedException e) {
			twi = null;
		}  // blocks until response is available
		return twiResult;
	}
}
