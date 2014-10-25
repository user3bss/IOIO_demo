package com.bmt.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.bmt.ioio_demo.R;

public class UIKnobSwitch extends View implements OnGestureListener {
	private Canvas ctx = null;
	private Paint tiBitmapPaint = null;
	private GestureDetector 	gestureDetector;
	private float 				mAngleDown , mAngleUp, mAngle;
	private Bitmap background = null;
	private Bitmap rotor = null;
	private Bitmap stator = null;
	int default_stator_image = R.drawable.statorswitch;
	int default_rotor_image = R.drawable.rotoron;	
	private Path tiPath = null;	
	private boolean enabled = true;
	private int position = 0;
	String tag = getClass().getSimpleName();	
	Context c = null;
	
	public interface UIKnobSwitchListener {
		public void onChange(int position);
	}
	private UIKnobSwitchListener m_listener = null;
	public void SetListener(UIKnobSwitchListener uiKnobListener) {
		m_listener = uiKnobListener;
	}
	public void set_stator_image(int r){
		stator = BitmapFactory.decodeResource(c.getResources(), r);		
	}
	public void set_stator_image(Bitmap b){
		stator = b;
	}
	public void set_rotor_image(int r){
		rotor = BitmapFactory.decodeResource(c.getResources(), r);		
	}
	public void set_rotor_image(Bitmap b){
		rotor = b;		
	}
	public void set_position(int _position){
	   	switch(_position){
	   		case 0:
	   			setRotorPosAngle(-144);
	   			break;
	   		case 1:
	   			setRotorPosAngle(-90);
	   			break;
	   		case 2:
	   			setRotorPosAngle(0);
	   			break;
	   		case 3:
	   			setRotorPosAngle(90);
	   			break;
	   		case 4:
	   			setRotorPosAngle(144);
	   			break;
	   		default:
	   			setRotorPosAngle(-144);	   			
	   			break;
	   	}
	}	
	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}	
	private void setRotorPosAngle(float deg) {
		if (deg >= 210 || deg <= 150) {
			if (deg > 180) deg = deg - 360;
			
			if (deg <= -134 && deg >= -154) {
				mAngle = -144;
	   			position = 0;
			}
			else if (deg <= -80 && deg >= -100){
				mAngle = -90;
	   			position = 1;
			}			
			else if (deg <= 10 && deg >=-10) {
				mAngle = 0;
	   			position = 2;
			}
			else if (deg >= 80 && deg <= 100){
				mAngle = 90;
	   			position = 3;
			}			
			else if (deg >= 134 && deg <= 154) {
				mAngle = 144;
	   			position = 4;
			}
   			if (m_listener != null) m_listener.onChange(position);			
			invalidate();
		}
	}	
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
	private void setPaintOptions(Context context, AttributeSet attrs) {		
		Log.i(tag, "using attrs");
		   setPaintOptions(context);		   
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIKnobSwitch, 0, 0);
			   try {
				   	int _position = a.getInt(R.styleable.UIKnobSwitch_position, 2);
				   	set_position(_position);
					set_stator_image(a.getResourceId(R.styleable.UIKnobSwitch_stator_image, R.drawable.statorswitch));
					set_rotor_image(a.getResourceId(R.styleable.UIKnobSwitch_rotor_image, R.drawable.rotoroff));
					invalidate();
			   } finally {
			       a.recycle();
			   }
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);		  
	}
	
	public void setEnabled(boolean b){
		enabled = b;
	}	
	public  UIKnobSwitch(Context context) {
		super(context);
		setPaintOptions(context);
		set_stator_image(default_stator_image);
		set_rotor_image(default_rotor_image);
		set_position(0);		
		if(!isInEditMode())
			gestureDetector = new GestureDetector(getContext(), this);
	}
	public  UIKnobSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaintOptions(context, attrs);
		if(!isInEditMode())
			gestureDetector = new GestureDetector(getContext(), this);
	}

	public  UIKnobSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaintOptions(context, attrs);
		if(!isInEditMode())
			gestureDetector = new GestureDetector(getContext(), this);
	}	  

	@Override
	public Parcelable onSaveInstanceState() {
	    Bundle bundle = new Bundle();
	    bundle.putParcelable("instanceState", super.onSaveInstanceState());
	    bundle.putFloat("mAngle", mAngle);
	    // ... save everything
	    return bundle;
	}
	
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.mAngle = bundle.getFloat("mAngle");
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
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
		canvas.drawBitmap(background, 0, 0, tiBitmapPaint);
		Matrix scale_matrix = new Matrix();		
		float scale = (this.getWidth()/metrics.scaledDensity) / 250.0f;	//graphic size is 250
		scale_matrix.setScale(scale, scale);
		canvas.drawBitmap(stator, scale_matrix, tiBitmapPaint);	
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postRotate(mAngle, (this.getWidth()/metrics.scaledDensity)/2, (this.getWidth()/metrics.scaledDensity)/2);				
		canvas.drawBitmap(rotor, matrix, tiBitmapPaint);				
		
		if (tiPath != null) {
			canvas.drawPath(tiPath, tiBitmapPaint);
		}		
	}
	
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) return true;
		else return super.onTouchEvent(event);
	}	
	public boolean onDown(MotionEvent event) {
		if(enabled){
			float x = event.getX() / ((float) getWidth());
			float y = event.getY() / ((float) getHeight());
			mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
		}
		return true;
	}
	private boolean setAngle(MotionEvent e2){
		if(enabled){
			float x = e2.getX() / ((float) getWidth());
			float y = e2.getY() / ((float) getHeight());
			float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
			
			if (! Float.isNaN(rotDegrees)) {
				// instead of getting 0-> 180, -180 0 , we go for 0 -> 360
				float posDegrees = rotDegrees;
				if (rotDegrees < 0) posDegrees = 360 + rotDegrees;
				
				// deny full rotation, start start and stop point, and get a linear scale
				if (posDegrees > 210 || posDegrees < 150) {
					// rotate our imageview
					setRotorPosAngle(posDegrees);
					return true; //consumed
				}
			}
		}
		return false;		
	}
	@Override	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return setAngle(e2);
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e2) {
		return setAngle(e2);
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
