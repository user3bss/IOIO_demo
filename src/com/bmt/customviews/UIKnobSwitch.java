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

public class UIKnobSwitch extends View implements OnGestureListener {
	private Canvas ctx = null;
	private Paint tiBitmapPaint = null;
	//private GestureDetector 	gestureDetector;
	private float 				mAngleDown , mAngleUp, mAngle;
	private Bitmap background = null;
	private Bitmap rotor = null;
	private Bitmap stator = null;
	int default_stator_image = R.drawable.statorswitch;
	int default_rotor_image = R.drawable.rotoron;	
	//private Path tiPath = null;	
	private boolean enabled = true;
	private int position = 0;
	String tag = getClass().getSimpleName();	
	Context c = null;
	Matrix scale_matrix = null;	
	Matrix matrix = null;
	DisplayMetrics metrics = null;
	
	public interface UIKnobSwitchListener {
		public void onChange(int position);
	}
	private UIKnobSwitchListener m_listener = null;
	public void SetListener(UIKnobSwitchListener uiKnobListener) {
		m_listener = uiKnobListener;
	}
	
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
	public  UIKnobSwitch(Context context) {
		super(context);
		c = context;
		setPaintOptions();
		set_stator_image(default_stator_image);
		set_rotor_image(default_rotor_image);
		set_position(0);		
		Init();
	}
	public  UIKnobSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
		Init();
	}

	public  UIKnobSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
		Init();
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
	public void setEnabled(boolean b){
		enabled = b;
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
	}
	private void setPaintOptions(AttributeSet attrs) {		
		Log.i(tag, "using attrs");		   		   
		TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.UIKnobSwitch, 0, 0);
		   try {
			   	int _position = a.getInt(R.styleable.UIKnobSwitch_position, 2);
			   	set_position(_position);
				set_stator_image(a.getResourceId(R.styleable.UIKnobSwitch_stator_image, default_stator_image));
				set_rotor_image(a.getResourceId(R.styleable.UIKnobSwitch_rotor_image, default_rotor_image));
				invalidate();
		   } finally {
		       a.recycle();
		   }		  
	}

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//if(w==0 || h == 0) return;
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
	    metrics = getResources().getDisplayMetrics();
		scale_matrix = new Matrix();		
		float scale = (this.getMeasuredWidth()/metrics.scaledDensity) / 250.0f;	//graphic size is 250
		scale_matrix.setScale(scale, scale);
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
		if(stator != null)	canvas.drawBitmap(stator, scale_matrix, tiBitmapPaint);	
		//matrix.postRotate(mAngle, (this.getMeasuredWidth()/metrics.scaledDensity)/2, (this.getMeasuredWidth()/metrics.scaledDensity)/2);				
		if(rotor != null)	canvas.drawBitmap(rotor, matrix, tiBitmapPaint);
		canvas.drawText("test", 0, 0, tiBitmapPaint);
		//canvas.drawCircle((this.getMeasuredWidth()/2)/metrics.scaledDensity, (this.getMeasuredHeight()/2)/metrics.scaledDensity, (this.getMeasuredWidth()/2-1)/metrics.scaledDensity, tiBitmapPaint);
		/*if (tiPath != null) {
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
	
	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}	
	private void setRotorPosAngle(float deg) {
		Log.i(tag, "setRotorPosAngle: "+deg);
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
	
	private boolean setAngle(MotionEvent e2){
		Log.i(tag, "setAngle: "+e2);
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
	public boolean onTouchEvent(MotionEvent event) {
		//if (gestureDetector.onTouchEvent(event)) return true;
		//else return super.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onDown(MotionEvent event) {
		Log.i(tag, "onDown "+enabled);
		if(enabled){
			float x = event.getX() / ((float) getWidth());
			float y = event.getY() / ((float) getHeight());
			mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
		}
		return true;
	}	
	@Override	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Log.i(tag, "onScroll "+enabled);
		return setAngle(e2);
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e2) {
		return setAngle(e2);
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
