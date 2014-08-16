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
	private Path tiPath = null;	
	private boolean enabled = true;
	String tag = getClass().getSimpleName();	
	Context c = null;
	
	public interface UIKnobSwitchListener {
		public void onChange(int percent);
	}
	private UIKnobSwitchListener m_listener;
	public void SetListener(UIKnobSwitchListener uiKnobListener) {
		m_listener = uiKnobListener;
	}
	
	public void setEnabled(boolean s){
		enabled = s;
	}	
	public  UIKnobSwitch(Context context) {
		super(context);
		setPaintOptions(context);
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
		Log.d(tag, "onSizeChanged: ");
		super.onSizeChanged(w, h, oldw, oldh);
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    //if(tiBitmapPaint == null) tiBitmapPaint = new Paint();
	    //setPaintOptions(c);
	    
		canvas.drawBitmap(background, 0, 0, tiBitmapPaint);
		Matrix scale_matrix = new Matrix();		
		float scale = (this.getWidth()/metrics.scaledDensity) / 250.0f;	//graphic size is 250	
		//scale_matrix.setTranslate(0, 0);
		scale_matrix.setScale(scale, scale);
		canvas.drawBitmap(stator, scale_matrix, tiBitmapPaint);	
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postRotate(mAngle, this.getWidth()/2, this.getWidth()/2);				
		canvas.drawBitmap(rotor, matrix, tiBitmapPaint);				
		
		if (tiPath != null) {
			canvas.drawPath(tiPath, tiBitmapPaint);
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
		stator = BitmapFactory.decodeResource(c.getResources(), R.drawable.statorswitch);
		rotor = BitmapFactory.decodeResource(c.getResources(), R.drawable.rotoron);		
	}	
	private void setPaintOptions(Context context, AttributeSet attrs) {		
		Log.i(tag, "using attrs");
		   setPaintOptions(context);
		   
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIKnob, 0, 0);
		   try {	       
				tiBitmapPaint = new Paint();

		   } finally {
		       a.recycle();
		   }
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);		  
	}	

	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
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

	public void setRotorPosAngle(float deg) {

		if (deg >= 210 || deg <= 150) {
			if (deg > 180) deg = deg - 360;
			
			if (deg <= -134 && deg >= -154) {
				mAngle = -144;
			}
			else if (deg <= -80 && deg >= -100){
				mAngle = -90;
			}			
			else if (deg <= 10 && deg >=-10) {
				mAngle = 0;				
			}
			else if (deg >= 80 && deg <= 100){
				mAngle = 90;
			}			
			else if (deg >= 134 && deg <= 154) {
				mAngle = 144;				
			}						
			invalidate();
		}
	}
	
	public float getValue(){
		return mAngle;
	}
	
	public void setRotorPercentage(int percentage) {
		int posDegree = percentage * 3 - 150;
		if (posDegree < 0) posDegree = 360 + posDegree;
		setRotorPosAngle(posDegree);
	}
	
	@Override	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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
					// get a linear scale
					float scaleDegrees = rotDegrees + 150; // given the current parameters, we go from 0 to 300
					// get position percent
					int percent = (int) (scaleDegrees / 3);
					if (m_listener != null) m_listener.onChange(percent);
					return true; //consumed
				}
			}
		} return false;
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
	@Override
	public boolean onSingleTapUp(MotionEvent e2) {
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
					// get a linear scale
					float scaleDegrees = rotDegrees + 150; // given the current parameters, we go from 0 to 300
					// get position percent
					int percent = (int) (scaleDegrees / 3);
					if (m_listener != null) m_listener.onChange(percent);
					return true; //consumed
				}
			}
		}
		return false;	
	}	

}
