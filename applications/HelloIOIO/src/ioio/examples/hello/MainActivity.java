package ioio.examples.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 *
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity{
	String tag = getClass().getSimpleName();
	public HashMap<String, ToggleButton> ToggleButtons = null;
	public HashMap<String, TextView> TextViews = null;
	
	
	
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//pin9 = new InputPin(9);
		
		ToggleButtons = new HashMap<String, ToggleButton>();
		ToggleButtons.put("D0", (ToggleButton) findViewById(R.id.toggleButtonD0));
		ToggleButtons.put("D1", (ToggleButton) findViewById(R.id.toggleButtonD1));
		ToggleButtons.put("D2", (ToggleButton) findViewById(R.id.toggleButtonD2));
		ToggleButtons.put("D3", (ToggleButton) findViewById(R.id.toggleButtonD3));
	
		ToggleButtons.put("D4", (ToggleButton) findViewById(R.id.toggleButtonD4));
		ToggleButtons.put("D5", (ToggleButton) findViewById(R.id.toggleButtonD5));
		ToggleButtons.put("D6", (ToggleButton) findViewById(R.id.toggleButtonD6));
		ToggleButtons.put("D7", (ToggleButton) findViewById(R.id.toggleButtonD7));

		ToggleButtons.put("D8", (ToggleButton) findViewById(R.id.toggleButtonD8));
		ToggleButtons.put("D9", (ToggleButton) findViewById(R.id.toggleButtonD9));
		ToggleButtons.put("D10", (ToggleButton) findViewById(R.id.toggleButtonD10));
		ToggleButtons.put("D11", (ToggleButton) findViewById(R.id.toggleButtonD11));
	
		ToggleButtons.put("D12", (ToggleButton) findViewById(R.id.toggleButtonD12));
		ToggleButtons.put("D13", (ToggleButton) findViewById(R.id.toggleButtonD13));
		ToggleButtons.put("D14", (ToggleButton) findViewById(R.id.toggleButtonD14));
		ToggleButtons.put("D15", (ToggleButton) findViewById(R.id.toggleButtonD15));		
		
		ToggleButtons.put("led", (ToggleButton) findViewById(R.id.toggleButtonLed));
		
		TextViews = new HashMap<String, TextView>();
		TextViews.put("AN0", (TextView) findViewById(R.id.AN0Text));
		TextViews.put("AN1", (TextView) findViewById(R.id.AN1Text));
		TextViews.put("AN2", (TextView) findViewById(R.id.AN2Text));
		TextViews.put("AN3", (TextView) findViewById(R.id.AN3Text));
		TextViews.put("AN4", (TextView) findViewById(R.id.AN4Text));
		TextViews.put("AN5", (TextView) findViewById(R.id.AN5Text));
		TextViews.put("AN6", (TextView) findViewById(R.id.AN6Text));
		TextViews.put("AN7", (TextView) findViewById(R.id.AN7Text));
		
		TextViews.put("AN8", (TextView) findViewById(R.id.AN8Text));
		TextViews.put("AN9", (TextView) findViewById(R.id.AN9Text));		
		TextViews.put("AN10", (TextView) findViewById(R.id.AN10Text));
		TextViews.put("AN11", (TextView) findViewById(R.id.AN11Text));
		TextViews.put("AN12", (TextView) findViewById(R.id.AN12Text));
		TextViews.put("AN13", (TextView) findViewById(R.id.AN13Text));
		TextViews.put("AN14", (TextView) findViewById(R.id.AN14Text));
		TextViews.put("AN15", (TextView) findViewById(R.id.AN15Text));
		TextViews.put("D9", (TextView) findViewById(R.id.D9Text));

		for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
			Log.i("key: ", entry.getKey().trim()+" , value:"+entry.getValue().getClass().getName());
			entry.getValue().setEnabled(false);
		}		

	}
	
	public void setText(final String pin, final float v){
	    this.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            // This code will always run on the UI thread, therefore is safe to modify UI elements.
	        	TextViews.get(pin).setText(pin+": "+v);
	        }
	    });		
	}

	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		@Override
		protected void setup() throws ConnectionLostException {
			showVersions(ioio_, "IOIO connected!");
			led_ = ioio_.openDigitalOutput(0, true);
			enableUi(true);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			led_.write(!ToggleButtons.get("led").isChecked());
			
			//float v = ioiopina.getVoltageBuffered();
			try{				
				//setText("AN0", v);				
			} catch (Exception e) {
				  toast(e.getMessage());
			}
			Thread.sleep(350);
		}

		@Override
		public void disconnected() {
			enableUi(false);		
			toast("IOIO disconnected"); 		
		}		
		@Override
		public void incompatible() {
			showVersions(ioio_, "Incompatible firmware version!");
		}
}

	/**
	 * A method to create our IOIO thread.
	 *
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	private void showVersions(IOIO ioio, String title) {
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

	private int numConnected_ = 0;

	private void enableUi(final boolean enable) {
		// This is slightly trickier than expected to support a multi-IOIO use-case.
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (enable) {
					if (numConnected_++ == 0) {
						//ToggleButtons.get("led").setEnabled(true);
						for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							entry.getValue().setEnabled(true);
						}						
					}
				} else {
					if (--numConnected_ == 0) {
						//ToggleButtons.get("led").setEnabled(false);
						for( Entry<String, ToggleButton> entry : ToggleButtons.entrySet()){
							entry.getValue().setEnabled(false);
						}						
					}
				}
			}
		});
	}
}