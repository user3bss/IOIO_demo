package com.bmt.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.bmt.ioio_demo.R;

public class LED extends View{
	private Canvas ctx = null;
	private Paint tiBitmapPaintOn = null;
	private Paint tiBitmapPaintOff = null;
	private Bitmap LEDon = null;
	private Bitmap LEDoff = null;
	private Bitmap background = null;
	int default_LEDon_image = R.drawable.ledon;
	int default_LEDoff_image = R.drawable.ledoff;
	boolean ison = false;
	String tag = getClass().getSimpleName();	
	Context c = null;
	Matrix scale_matrix = null;
	
	/*public interface LEDListener {
		public void onChange(boolean b);
	}
	private LEDListener m_listener = null;
	public void SetListener(LEDListener uiLEDListener) {
		m_listener = uiLEDListener;
	}*/
	private void setPaintOptions() {
		tiBitmapPaintOn = new Paint();
		tiBitmapPaintOn.setAntiAlias(true);
		tiBitmapPaintOn.setDither(true);
		tiBitmapPaintOn.setColor(Color.parseColor("#FF0000"));
		tiBitmapPaintOn.setStyle(Paint.Style.FILL_AND_STROKE);
		tiBitmapPaintOn.setStrokeJoin(Paint.Join.ROUND);
		tiBitmapPaintOn.setStrokeCap(Paint.Cap.ROUND);
		tiBitmapPaintOn.setStrokeWidth(8);
		tiBitmapPaintOn.setAlpha(128);
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
		
		tiBitmapPaintOff = new Paint();
		tiBitmapPaintOff.setAntiAlias(true);
		tiBitmapPaintOff.setDither(true);
		tiBitmapPaintOff.setColor(Color.parseColor("#990000"));
		tiBitmapPaintOff.setStyle(Paint.Style.FILL_AND_STROKE);
		tiBitmapPaintOff.setStrokeJoin(Paint.Join.ROUND);
		tiBitmapPaintOff.setStrokeCap(Paint.Cap.ROUND);
		tiBitmapPaintOff.setStrokeWidth(8);
		tiBitmapPaintOff.setAlpha(128);		
	}
	public void set_LEDon_image(int r){
		LEDon = BitmapFactory.decodeResource(c.getResources(), r);		
	}
	public void set_LEDoff_image(int r){
		LEDoff = BitmapFactory.decodeResource(c.getResources(), r);		
	}	
	private void setPaintOptions(AttributeSet attrs) {			   
		TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.LED, 0, 0);
	   try {
		   int on = a.getResourceId(R.styleable.LED_ledon_image, default_LEDon_image);
		   set_LEDon_image(on);			   
		   int off = a.getResourceId(R.styleable.LED_ledoff_image, default_LEDoff_image);
		   set_LEDoff_image(off);
		   ison = a.getBoolean(R.styleable.LED_ison, false);
		   invalidate();
	   } finally {
	       a.recycle();
	   }		  
	}
	public LED(Context context) {
		super(context);
		c = context;
		setPaintOptions();
		set_LEDon_image(default_LEDon_image);
		set_LEDoff_image(default_LEDoff_image);
	}
	public LED(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		setPaintOptions();
		Log.i(tag, "using attrs0");
		setPaintOptions(attrs);
	}

	public LED(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
		setPaintOptions();
		Log.i(tag, "using attrs1");
		setPaintOptions(attrs);
	}	
	@Override
	public Parcelable onSaveInstanceState() {
	    Bundle bundle = new Bundle();
	    bundle.putParcelable("instanceState", super.onSaveInstanceState());
	    bundle.putBoolean("ison", ison);
	    return bundle;
	}
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			ison = bundle.getBoolean("ison");
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
		scale_matrix = new Matrix();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float scale = (this.getWidth()/metrics.scaledDensity) / 30.0f;	//graphic size is 250
		scale_matrix.setScale(scale, scale);		
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    canvas.drawBitmap(background, 0, 0, tiBitmapPaintOff);	    
		if(ison && LEDon != null)
			canvas.drawBitmap(LEDon, scale_matrix, tiBitmapPaintOff);
		else if(LEDoff != null)
			canvas.drawBitmap(LEDoff, scale_matrix, tiBitmapPaintOff);
		else if(ison)
			canvas.drawCircle(20, 20, 15, tiBitmapPaintOn);
		else
			canvas.drawCircle(20, 20, 15, tiBitmapPaintOff);
	}
	
	//TODO create touchevent Listener for toggle LED
	//if (m_listener != null) m_listener.onChange(mState);
}
