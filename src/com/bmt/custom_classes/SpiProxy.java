package com.bmt.custom_classes;

import ioio.lib.api.IOIO;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class SpiProxy {
	String tag = getClass().getSimpleName();
	private SpiMaster spi;
	private SpiMaster.Rate spiRate = SpiMaster.Rate.RATE_125K;
	int misoPin = 1;
	int mosiPin = 2;
	int clkPin = 3;
	int ssPins = 4;
	byte[] spi_result;
	IOIO ioio_ = null;
	
	SpiProxy(IOIO ioio){
		ioio_ = ioio;
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
		misoPin = _misoPin;  //int misoPin = 27;1
		mosiPin = _mosiPin;  //int mosiPin = 28;2
		clkPin = _clkPin;    //int clkPin = 29;3
		ssPins = _ssPins;    //int ssPins = 26;4		
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
