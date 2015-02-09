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
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;

import com.bmt.ioio_demo.R;

public class UIKnob extends View implements OnGestureListener {
	private Canvas ctx = null;
	private Paint tiBitmapPaint = null;
	//private GestureDetector 	gestureDetector;
	private float 				mAngleDown , mAngleUp, mAngle;
	private Bitmap background = null;
	private Bitmap rotorOn = null;
	private Bitmap rotorOff = null;
	private Bitmap stator = null;
	private boolean mState = false;	
	//private Path tiPath = null;	
	private int stateToSave;
	private boolean enabled = true;
	String tag = getClass().getSimpleName();	
	Context c = null;
	Matrix scale_matrix = null;
	Matrix matrix = null;
	DisplayMetrics metrics = null;
	String msg = null;
	
	private void Init(){
		if(!isInEditMode()){
			//gestureDetector = new GestureDetector(c, this);
	        final GestureDetector gdt = new GestureDetector(this);
	        this.setOnTouchListener(new OnTouchListener() {
	            @Override
	            public boolean onTouch(final View view, final MotionEvent event) {
	                gdt.onTouchEvent(event);
	                return true;
	            }
	        });
		}		
	}
	public interface UIKnobListener {
		public void onStateChange(boolean newstate);
		public void onRotate(int percentage);
	}
	private UIKnobListener m_listener = null;
	
	public void SetListener(UIKnobListener uiKnobListener) {
		m_listener = uiKnobListener;
	}	
	public UIKnob(Context context) {
		super(context);
		c = context;
		setPaintOptions();
		Init();
	}
	public UIKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
		Init();
	}

	public  UIKnob(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
		Init();
	} 	
	public void setEnabled(boolean s){
		enabled = s;
	}	
	public void SetState(boolean state) {
		mState = state;
		invalidate();
		//ivRotor.setImageBitmap(state?bmpRotorOn:bmpRotorOff);
	}
	  @Override
	  public Parcelable onSaveInstanceState() {

	    Bundle bundle = new Bundle();
	    bundle.putParcelable("instanceState", super.onSaveInstanceState());
	    bundle.putInt("stateToSave", this.stateToSave);
	    bundle.putFloat("mAngle", mAngle);
	    bundle.putBoolean("mState", mState);
	    // ... save everything
	    return bundle;
	  }

	  @Override
	  public void onRestoreInstanceState(Parcelable state) {

	    if (state instanceof Bundle) {
	      Bundle bundle = (Bundle) state;
	      this.stateToSave = bundle.getInt("stateToSave");
	      this.mAngle = bundle.getFloat("mAngle");
	      this.mState = bundle.getBoolean("mState");
	      // ... load everything
	      state = bundle.getParcelable("instanceState");
	    }
	    super.onRestoreInstanceState(state);
	  }	
	private void setPaintOptions() {
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
		stator = BitmapFactory.decodeResource(c.getResources(), R.drawable.stator);
		rotorOff = BitmapFactory.decodeResource(c.getResources(), R.drawable.rotoroff);
		rotorOn = BitmapFactory.decodeResource(c.getResources(), R.drawable.rotoron);						
	}
	private void setPaintOptions(AttributeSet attrs) {		
		Log.i(tag, "using attrs");		
		TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.UIKnob, 0, 0);
		   try {			   	
				int r = a.getResourceId(R.styleable.UIKnob_on_image, R.drawable.rotoron);
				rotorOn = BitmapFactory.decodeResource(c.getResources(), r);
				r = a.getResourceId(R.styleable.UIKnob_off_image, R.drawable.rotoroff);
				rotorOff = BitmapFactory.decodeResource(c.getResources(), r);
				setRotorPercentage(a.getInt(R.styleable.UIKnob_value, 0));				
				invalidate();
		   } finally {
		       a.recycle();
		   }		  
	}	 

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(tag, "onSizeChanged: ");
		super.onSizeChanged(w, h, oldw, oldh);
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
		metrics = getResources().getDisplayMetrics();		
		float scale = (this.getWidth()/metrics.scaledDensity) / 250.0f;	//graphic size is 250	
		scale_matrix = new Matrix();
		scale_matrix.setScale(scale, scale);
		//scale_matrix.setTranslate(0, 0);
		matrix = new Matrix();
		matrix.postScale(scale, scale);		
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    int left = getPaddingLeft();
	    int top = getPaddingTop();
	    int right = getWidth() - getPaddingRight();
	    int bottom = getHeight() - getPaddingBottom();
	    
		canvas.drawBitmap(background, 0, 0, tiBitmapPaint);
		if(stator != null)
			canvas.drawBitmap(stator, scale_matrix, tiBitmapPaint);		
		
		matrix.postRotate(mAngle, (this.getWidth()/metrics.scaledDensity)/2, (this.getWidth()/metrics.scaledDensity)/2);		
		if(mState && rotorOn != null)		
			canvas.drawBitmap(rotorOn, matrix, tiBitmapPaint);
		else if(rotorOff != null)
			canvas.drawBitmap(rotorOff, matrix, tiBitmapPaint);
		/*if (tiPath != null) {
			//Log.i(tag, "tiPath != null ");
			canvas.drawPath(tiPath, tiBitmapPaint);
		}*/
	}		

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int desiredWidth = 100;
	    int desiredHeight = 100;

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;

	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = desiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = desiredHeight;
	    }

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}	
	
	/**
	 * math..
	 * @param x
	 * @param y
	 * @return
	 */
	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}
	public void setRotorPosAngle(float deg) {
		if (deg >= 210 || deg <= 150) {
			if (deg > 180) deg = deg - 360;	
			mAngle = deg;					//onDraw uses this deg
			invalidate();
		}
	}
	
	public float getAngle(){
		Log.i(tag, "Angle: "+mAngle);
		return mAngle;
	}
	
	public void setRotorPercentage(int percentage) {
		int posDegree = percentage * 3 - 150;
		if (posDegree < 0) posDegree = 360 + posDegree;
		setRotorPosAngle(posDegree);
	}
	public float getRotorPercentage() {
		float posDegree = (float) ((mAngle / 3.0) + 150);
		if (posDegree > 360) posDegree = posDegree - 360;
		return posDegree;
	}
	
	@Override public boolean onTouchEvent(MotionEvent event) {
		//if (gestureDetector.onTouchEvent(event)) return true;
		//else return super.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onDown(MotionEvent event) {
		if(enabled){
			float x = event.getX() / ((float) getWidth());
			float y = event.getY() / ((float) getHeight());
			mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
		}
		return true;
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(enabled){
			float x = e.getX() / ((float) getWidth());
			float y = e.getY() / ((float) getHeight());
			mAngleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
			
			// if we click up the same place where we clicked down, it's just a button press
			//if (! Float.isNaN(mAngleDown) && ! Float.isNaN(mAngleUp) && Math.abs(mAngleUp-mAngleDown) < 10) {
				SetState(!mState);
				if (m_listener != null) m_listener.onStateChange(mState);
			//}
		}
		return true;
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
					if (m_listener != null) m_listener.onRotate(percent);
					return true; //consumed			
				}
			}
		}
		return false; // not consumed
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}
}
