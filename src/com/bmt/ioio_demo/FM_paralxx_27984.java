package com.bmt.ioio_demo;

import java.math.BigInteger;

public class FM_paralxx_27984{
	private class register {
		int reg = 0;
		register(int value){
			reg = value;
		}
		public boolean bitIsSet(int bit_num){
			return BigInteger.valueOf(reg).testBit(bit_num);
		}
		public void setBit(int bit_num){
			//7  6  5  4   3 2 1 0
		   //128 64 32 16  8 4 2 1              
			int bit_value = 2^bit_num;
			if( bitIsSet(bit_num) ){
				//do nothing
			} else {
				reg += bit_value;
			}
		}
		public void clearBit(int bit_num){
			int bit_value = 2^bit_num;
			if( bitIsSet(bit_num) ){
				reg -= bit_value;
			} else {
				//do nothing
			}			
		}
		public int getValue(){
			return reg;
		}
		public void setValue(int v){
			reg = v;
		}
		public void addValue(int v){
			reg += v;
		}
		public void subValue(int v){
	 		reg -= v;
		}		
	}

	private register r2 = new register(0);
	private register r3 = new register(0);
	private register r4 = new register(0);
	private register r5 = new register(34992);
	private register r10 = new register(1024);
	private register r11 = new register(0);
    public final byte I2CAddr_Continue = 32;  //%00100000_0 Continue write/read data to/from device's registers without sending the Registers Name
    public final byte I2CAddr_Standard = 33;  //%00100001_0 Standard write/read data to/from device's registers
    
	FM_paralxx_27984() {
		reset();		
	}
	
	public byte[] getRegisters(){//register 2
		byte[] b = {2, (byte) r2.getValue(), (byte) r3.getValue(),
				    (byte) r4.getValue(), (byte) r5.getValue(),
				    0,0,0,0,
				    (byte) r10.getValue(), (byte) r11.getValue()};
		return b;		
	}
	public void reset(){
		r2 = new register(0);
		r3 = new register(0);
		r4 = new register(0);
		r5 = new register(34992);
		r10 = new register(1024);
		r11 = new register(0);
		setSeekTuneComplete(true);	//enable seek interrupt
		setStereoIndicator();		//enable stereo GP3 and external int GP2
		setVolume(12);			//15 = max
		enableAudio();
		//setChannel(int)
		//setTune(bool);
		//setI2S_ENABLED(bool);
		//bool seekIsComplete();		//
		//int readChan();
		//fmReady();
		//isFM()		
	}
	
	//all default values for chip
	/*0x02
	15 DHIZ Audio Output 0 = High Impedance; 1 = Normal Operation 0
	14 DMUTE Mute Disable 0 = Mute; 1 = Normal Operation 0
	13 MONO Mono Select 0 = Stereo; 1 = Force Mono 0
	12 BASS Bass Boost 0 = Disabled; 1 = Bass boost enabled 0
	11 RESERVED - 
	10 CLK32_INPUT_ENB 0 = Enable; 1 = Disable (select 1 if crystal is shared) 0
	8:7 RESERVED - -
	6:4 CLK_MODE[2:0] 000 = 32.768 kHz (used for this module) 000 1 SOFT_RESET Soft reset; set to 1 to reset. 0
	0 ENABLE Power Up Enable 0 = Disabled; 1 = Enabled 0*/	
	public void enableAudio(){
		r2.setBit(0);							//0 ENABLE Power Up Enable 0 = Disabled; 1 = Enabled 0
		r2.setBit(15);							//15 DHIZ Audio Output0 = High Impedance; 1 = Normal Operation 0
		r2.setBit(14);							//14 DMUTE Mute Disable 0 = Mute; 1 = Normal Operation 0
		r2.setBit(12);							//12 BASS Bass Boost 0 = Disabled; 1 = Bass boost enabled 0
	}
	public void disableAudio(){
		r2.clearBit(0);							//0 ENABLE Power Up Enable 0 = Disabled; 1 = Enabled 0
		r2.clearBit(15);							//15 DHIZ Audio Output0 = High Impedance; 1 = Normal Operation 0
		r2.clearBit(14);							//14 DMUTE Mute Disable 0 = Mute; 1 = Normal Operation 0
	}
	
	
/*	0x03
	15:6 CHAN[9:0]Channel Select
		BAND = 0 Frequency = Channel Spacing (kHz) x CHAN + 87 MHz
		BAND = 1 Frequency = Channel Spacing (kHz) x CHAN + 76 MHz Updated after a seek operation.
	5 RESERVED - -
	4 TUNE Tune 0 = Disable; 1 = Enable 0
	3:2 BAND[1:0] Band Select
	    00 - default
		00 = 87 – 108 MHz (US/Europe)
		01 = 76 – 91 MHz (Japan)
		10 = 76 – 108 MHz (Japan wide)
	1:0 SPACE[1:0] Channel Spacing
		00 = 100 kHz
		01 = 200 KHz
		10 = 50 kHz
		11 = 12.5 kHz
	*/	
	public void setChannel(int v){
		//15:6 CHAN[9:0]Channel Select  
		//BAND = 0 Frequency = Channel Spacing ( 100 kHz ) x CHAN + 87 MHz
		//BAND = 1 Frequency = Channel Spacing ( 100 kHz ) x CHAN + 76 MHz Updated after a seek operation.		
		int _channel = r3.getValue() & ((2^6)-1); //clear channel bits == (2^n+1)-1)
		_channel = v * (2^6); //
		r3.setValue(_channel);		
	}
	/*public void getChannel(int channel){
		//15:6 CHAN[9:0]Channel Select
		//BAND = 0 Frequency = Channel Spacing ( 100 kHz ) x CHAN + 87 MHz
		//BAND = 1 Frequency = Channel Spacing ( 100 kHz ) x CHAN + 76 MHz Updated after a seek operation.
		//r3.setValue(0); read channel from twi.read operation
	}*/	
		
	public void setTune(boolean t){
		if(t){							//4 TUNE Tune 0 = Disable; 1 = Enable 0
			r3.setBit(14);
		} else {
			r3.clearBit(14);
		}
	}
	

	/*0x04
	14 STCEIN Seek/Tune Complete Interrupt Enable
		0 = 1 Disable Interrupt; 1 = Enable Interrupt
		Setting to 1 will generate a low pulse on GPIO2 when the interrupt occurs.
		
	12 RESERVED - -
	11 DE De-emphasis 0 = 75 µs; 1 = 50 µs 0
	9:7 RESERVED - -
	6 I2S_ENABLED I2S bus enable 0 = disabled; 1 = enabled 0
	5:4 GPIO3[1:0] General Purpose I/O 3
		00 = High Impedance
		01 = Mono/Stereo indicator (ST)
		10 = Low
		11 = High
	3:2 GPIO2[1:0] General Purpose I/O 2
		00 = High Impedance
		01 = Interrupt (INT)
		10 = Low
		11 = High
	1:0 GPIO1[1:0]	General Purpose I/O 1
		00 = High Impedance
		01 = Reserved
		10 = Low
		11 = High*/
	//Seek/Tune Complete Interrupt Enable 0 = 1 Disable Interrupt; 1 = Enable Interrupt
	//Setting to 1 will generate a low pulse on GPIO2 when the interrupt occurs.
	public void setSeekTuneComplete(boolean b){
		if(b){
			r4.setBit(14);
		} else {
			r4.clearBit(14);
		}
			
	}
	public void setI2S_ENABLED(boolean b){		//6 I2S_ENABLED I2S bus enable 0 = disabled; 1 = enabled 0
		if(b){
			r4.setBit(6);
		} else {
			r4.clearBit(6);
		}		
	}
	
	//5:4 GPIO3[1:0] General Purpose I/O  value:01 = Mono/Stereo indicator (ST)
	public void setStereoIndicator(){
		r4.clearBit(5);
		r4.setBit(4);
		
		r4.clearBit(3); //3:2 GPIO2[1:0] General Purpose I/O 2
		r4.setBit(2);	//val:01 = Interrupt (INT)
						
	}

	/*0x05	
	15 INT_MODE
		If 0, generate 5ms interrupt;
		If 1, interrupt lasts until read register 0x0C
		occurs 1
	14:8 SEEKTH[6:0] Seek Threshold RSSI scale is logarithmic, 0000000 = min RSSI 0001000
	7:6 LNA_PORT_SEL[1:0]
		LNA input port selection bit
		00 = no input
		01 = LNAN
		10 = LNAP
		11 = dual port input
	5:4 LNA_ICSEL_BIT[1:0]	LNA working current bit:
		00 = 1.8 mA
		01 = 2.1 mA
		10 = 2.5 mA
		11 = 3.0 mA
	3:0 VOLUME[3:0] DAC Gain Control Bits (Volume) 0000 = min; 1111 = max (Scale is logarithmic) 1000 */	
	
	//15 INT_MODE	If 0, generate 5ms interrupt;	If 1, interrupt lasts until read register 0x0C	
	public void setIntMode(boolean b){
		if(b){
			r5.setBit(15);
		} else {
			r5.clearBit(15);
		}
	}
	//3:0 VOLUME[3:0] DAC Gain Control Bits (Volume) 0000 = min; 1111 = max (Scale is logarithmic) 1000 = 50%*/
	public void setVolume(int v){
		int mask_v = r5.getValue() & 240;
		r5.setValue(mask_v + v);
	}
	
	/*0x0A
	15 RESERVED - -
	14 STC Seek/Tune Complete 0 = Not complete; 1 = Complete
		Flag is set when the seek or tune operation completes.

	13 SF Seek Fail 0 = Seek successful; 1 = Seek failure
		Flag is set when the seek operation fails to fails
		to find a channel with an RSSI level greater than SEEKTH[5:0]
	12:11 RESERVED - -
	10 ST Stereo Indicator 0 = Mono; 1 = Stereo
		Stereo indication available on GPIO3.
	9:0 READCHAN[9:0] Read Channel
		BAND = 0
		Frequency = Channel Spacing (kHz) x READCHAN[7:0] +	87 MHz
		BAND = 1
		Frequency = Channel Spacing (kHz) x READCHAN[7:0] +	76.0 MHz
		READCHAN[7:0] is updated after a tune or seek operation.	*/
	public boolean seekIsComplete(){
		//readReg 0A
		return false;
	}
	public int readChan(){
		//read Reg
		int r = 0;
		return r;
	}
	
	
	/* 	0x0B
		15:9 RSSI[6:0] RSSI (scale is logarithmic) 000000 = min; 111111 = max 000000
		8 FM_TRUE 0 = Current channel is not a station 1 = Current channel is a station 
		7 FM_READY 0 = not ready; 1 = read Used for soft seek 0 
	 */
	public int getRSSI(){
		return 0;
	}
	public boolean fmReady(){
		return false;
	}
	public boolean isFM(){
		return false;
	}
}
