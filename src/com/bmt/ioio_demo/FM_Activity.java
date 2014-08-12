package com.bmt.ioio_demo;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FM_Activity extends IOIOActivity implements OnClickListener{
	FM_paralxx_27984 fm = null;
	String tag = getClass().getSimpleName();
	boolean show_toast_connection_info = false;
	Button auto_scan = null;
	Button channel_1 = null;
	Button channel_2 = null;
	Button channel_3 = null;
	Button channel_4 = null;
	Button channel_5 = null;
	Button channel_6 = null;
	TextView frequency = null;
	Button power = null;
	Button tune_up = null;
	Button tune_down = null;
	Button volume_up = null;		
	Button volume_down = null;
	boolean _LED = false;
	private boolean hasBytesToSend = false;
	byte[] request = null;
	byte[] response = null;	
	
	private int numConnected_ = 0;
	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						//ToggleButtons.get("led").setEnabled(true);
						//for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							//entry.getValue().setEnabled(true);
						//}						
					}
				} else {
					if (--numConnected_ == 0) {
						//ToggleButtons.get("led").setEnabled(false);
						//for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							//entry.getValue().setEnabled(false);
						//}						
					}
				}
			}
		});
	}	

	public class _IOIOLooper extends BaseIOIOLooper {
		String tag = getClass().getSimpleName();	
		private boolean fmInitalized = false;
		TwiMaster twi = null;
		DigitalOutput Led = null;
		
		@Override
		protected void setup() {
			try{
				enableUi(true);
				showVersions(ioio_, "IOIO connected!");
				Led = ioio_.openDigitalOutput(0, false); _LED = false;
				
				//if(!fmInitalized){
					fm = new FM_paralxx_27984();			//TWI num 1
					//byte[] p27984_config = fm.getRegisters();
					// ... send bytes over i2c
					request = fm.getRegisters();
					
					twi = ioio_.openTwiMaster(1, TwiMaster.Rate.RATE_100KHz, false);
					byte[] response = null;
					twi.writeRead(fm.I2CAddr_Standard, false, request, request.length, response, 0);
					//twi.writeRead(address, tenBitAddr, writeData, writeSize, readData, readSize)
					//fmInitalized = true;
				//}
			} catch (Exception e) {
				toast("setup_Error: "+e.getMessage()+" "+e.getLocalizedMessage());
			}
		}
		@Override
		public void incompatible() {
			showVersions(ioio_, "Incompatible firmware version!");
		}
		@Override
		public void disconnected() {
			enableUi(false);		
			if(show_toast_connection_info) toast("IOIO disconnected");		
		}
		
		public void interrupted() {
			//enableUi(false);        //interrupted
			if(show_toast_connection_info) toast("IOIO interrupted"); 		
		}	
		@Override
		public void loop() {		
			try {
				//if(fm != null){
					//fm.twi_RW(address, request, responseLength, async);
					if (_LED)
						Led.write(false);
					else
						Led.write(true);
				//}
			} catch (Exception e) {
				toast("loop Error: "+e.getMessage()+" , "+e.getLocalizedMessage() );
			}
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fm_activity);
		auto_scan = (Button) findViewById(R.id.auto_scan);
		auto_scan.setOnClickListener(this);
		channel_1 = (Button) findViewById(R.id.channel_1);
		channel_1.setOnClickListener(this);
		channel_2 = (Button) findViewById(R.id.channel_2);
		channel_2.setOnClickListener(this);
		channel_3 = (Button) findViewById(R.id.channel_3);
		channel_3.setOnClickListener(this);
		channel_4 = (Button) findViewById(R.id.channel_4);
		channel_4.setOnClickListener(this);
		channel_5 = (Button) findViewById(R.id.channel_5);
		channel_5.setOnClickListener(this);
		channel_6 = (Button) findViewById(R.id.channel_6);
		channel_6.setOnClickListener(this);
		frequency = (TextView) findViewById(R.id.display_freq);
		power = (Button) findViewById(R.id.power);
		power.setOnClickListener(this);
		tune_up = (Button) findViewById(R.id.tune_up);
		tune_up.setOnClickListener(this);
		tune_down = (Button) findViewById(R.id.tune_down);
		tune_down.setOnClickListener(this);
		volume_up = (Button) findViewById(R.id.volume_up_button);
		volume_up.setOnClickListener(this);
		volume_down = (Button) findViewById(R.id.volume_down_button);
		volume_down.setOnClickListener(this);
		hasBytesToSend = false;
	}
	@Override
	protected void onResume() {
		super.onResume();
	}	
	private void showVersions(IOIO ioio, String title) {
		if(show_toast_connection_info){
			try {
				toast(String.format("%s\n" +
						"IOIOLib: %s\n" +
						"Application firmware: %s\n" +
						"Bootloader firmware: %s\n" +
						"Hardware: %s",
						title,
						ioio.getImplVersion(VersionType.IOIOLIB_VER),
						ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
						ioio.getImplVersion(VersionType.BOOTLOADER_VER),
						ioio.getImplVersion(VersionType.HARDWARE_VER)));
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void toast(final String message) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new _IOIOLooper();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.volume_up_button:
				break;
			case R.id.volume_down_button:
				break;
			case R.id.tune_up:
				break;
			case R.id.tune_down:
				break;
			case R.id.auto_scan:
				break;
			case R.id.channel_1:
				break;
			case R.id.channel_2:
				break;				
			case R.id.channel_3:
				break;				
			case R.id.channel_4:
				break;				
			case R.id.channel_5:
				break;				
			case R.id.channel_6:				
				break;
		}
	}
}
