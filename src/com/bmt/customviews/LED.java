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
import android.view.GestureDetector;
import android.view.View;

import com.bmt.ioio_demo.R;

public class LED extends View{
	private Canvas ctx = null;
	private Paint tiBitmapPaint = null;
	private Bitmap LEDon = null;
	private Bitmap LEDoff = null;
	private Bitmap background = null;
	int default_LEDon_image = R.drawable.ledon;
	int default_LEDoff_image = R.drawable.ledoff;
	boolean ison = false;
	private boolean enabled = true;
	String tag = getClass().getSimpleName();	
	Context c = null;
	/*public interface UIKnobSwitchListener {
		public void onChange(int position);
	}
	private UIKnobSwitchListener m_listener = null;
	public void SetListener(UIKnobSwitchListener uiKnobListener) {
		m_listener = uiKnobListener;
	}*/
	private void setPaintOptions(Context context) {
		tiBitmapPaint = new Paint();
		tiBitmapPaint.setAntiAlias(true);
		tiBitmapPaint.setDither(true);
		tiBitmapPaint.setColor(Color.BLACK);
		tiBitmapPaint.setStyle(Paint.Style.STROKE);
		tiBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		tiBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		tiBitmapPaint.setStrokeWidth(2);
		tiBitmapPaint.setAlpha(128);
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);	
		c = context;		
	}
	public void set_LEDon_image(int r){
		LEDon = BitmapFactory.decodeResource(c.getResources(), r);		
	}
	public void set_LEDoff_image(int r){
		LEDoff = BitmapFactory.decodeResource(c.getResources(), r);		
	}	
	private void setPaintOptions(Context context, AttributeSet attrs) {		
		Log.i(tag, "using attrs");
		setPaintOptions(context);		   
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LED, 0, 0);
		   try {
			   int on = a.getResourceId(R.styleable.LED_ledon_image, R.drawable.ledon);
			   LEDon = BitmapFactory.decodeResource(context.getResources(), on);
			   int off = a.getResourceId(R.styleable.LED_ledoff_image, R.drawable.ledoff);
			   LEDoff = BitmapFactory.decodeResource(context.getResources(), off);
			   ison = a.getBoolean(R.styleable.LED_ison, false);
			   invalidate();
		   } finally {
		       a.recycle();
		   }
		tiBitmapPaint = new Paint(Paint.DITHER_FLAG);		  
	}
	public void setEnabled(boolean b){
		enabled = b;
	}
	public LED(Context context) {
		super(context);
		setPaintOptions(context);
		set_LEDon_image(default_LEDon_image);
		set_LEDoff_image(default_LEDoff_image);
	}
	public LED(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaintOptions(context, attrs);
	}

	public LED(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaintOptions(context, attrs);
	}	
	@Override
	public Parcelable onSaveInstanceState() {
	    Bundle bundle = new Bundle();
	    bundle.putParcelable("instanceState", super.onSaveInstanceState());
	    //bundle.putFloat("mAngle", mAngle);
	    // ... save everything
	    return bundle;
	}
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			//this.mAngle = bundle.getFloat("mAngle");
			// ... load everything
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
		Matrix scale_matrix = new Matrix();
		if(ison && LEDon != null)
			ctx.drawBitmap(LEDon, scale_matrix, tiBitmapPaint);
		else if(LEDoff != null)
			ctx.drawBitmap(LEDoff, scale_matrix, tiBitmapPaint);		
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    //DisplayMetrics metrics = getResources().getDisplayMetrics();
	    canvas.drawBitmap(background, 0, 0, tiBitmapPaint);
		//Matrix scale_matrix = new Matrix();		
		//float scale = (this.getWidth()/metrics.scaledDensity) / 30.0f;	//graphic size is 250
		//scale_matrix.setScale(scale, scale);
		/*if(ison)
			canvas.drawBitmap(LEDon, scale_matrix, tiBitmapPaint);
		else
			canvas.drawBitmap(LEDoff, scale_matrix, tiBitmapPaint);*/
	}
}
