package com.bmt.custom_classes;

import ioio.lib.api.IOIO;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import android.util.Log;

public class SpiProxy {
	String tag = getClass().getSimpleName();
	private SpiMaster spi;
	private SpiMaster.Rate spiRate = SpiMaster.Rate.RATE_125K;
	int misoPin = 27;
	int mosiPin = 28;
	int clkPin = 29;
	int ssPins = 26;
	byte[] spi_result = null;
	
	SpiProxy(IOIO ioio){
		openSPI(ioio);
	}
	public void openSPI(IOIO ioio){
		try {
			Log.d(tag, "Opening SPI iso: [27]: "+ misoPin + " osi: [28]: "+mosiPin+" clk: [29]: "+clkPin+" rate: [125k] :"+spiRate);
			spi = ioio.openSpiMaster(misoPin, mosiPin, clkPin, ssPins, spiRate );
		} catch (ConnectionLostException e) {
			spi = null;
		}
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
	public byte[] spi_RW(int address, byte[] request, int responselength, boolean async){
		//what's up with 7???
		spi_result = new byte[responselength];
		try {
			if(async && spi != null){
				SpiMaster.Result result = spi.writeReadAsync(address, request, request.length, 7, spi_result, spi_result.length);
				result.waitReady();
			} else if(spi != null){  // blocks until response is available
				spi.writeRead(address, request, request.length, 7, spi_result, spi_result.length);
			}
		} catch (ConnectionLostException e) {
			spi = null;
		} catch (InterruptedException e) {
			spi = null;
		}
		return spi_result;
	}
}
