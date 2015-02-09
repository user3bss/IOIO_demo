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
	private Paint BitmapPaintOn = null;
	private Paint BitmapPaintOff = null;
	private Paint BitmapPaintStroke = null;
	private Bitmap LEDon = null;
	private Bitmap LEDoff = null;
	String default_LEDOnColor = "#FF0000";
	String default_LEDOffColor = "#990000";
	private Bitmap background = null;
	boolean ison = false;
	String tag = getClass().getSimpleName();	
	Context c = null;
	Matrix scale_matrix = null;
	DisplayMetrics metrics = null;
	
	public LED(Context context) {
		super(context);
		c = context;
		setPaintOptions();
		//set_LEDon_image(default_LEDon_image);
		//set_LEDoff_image(default_LEDoff_image);
	}
	public LED(Context context, AttributeSet attrs) {
		super(context, attrs);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
	}

	public LED(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		c = context;
		setPaintOptions();
		setPaintOptions(attrs);
	}	
	public void setIsOn(boolean _ison){
		ison = _ison; 
		invalidate();
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
	
	private void setPaintOptions() {
		BitmapPaintStroke = new Paint();
		BitmapPaintStroke.setDither(true);		
		BitmapPaintStroke.setStyle(Paint.Style.STROKE);
		BitmapPaintStroke.setStrokeJoin(Paint.Join.ROUND);
		BitmapPaintStroke.setStrokeCap(Paint.Cap.ROUND);
		BitmapPaintStroke.setStrokeWidth(2);
		BitmapPaintStroke.setAlpha(128);		
		BitmapPaintStroke.setColor(Color.parseColor("#333333"));
		BitmapPaintStroke.setAntiAlias(true);
		
		BitmapPaintOn = new Paint();
		BitmapPaintOn.setDither(true);		
		BitmapPaintOn.setStyle(Paint.Style.FILL);
		BitmapPaintOn.setStrokeJoin(Paint.Join.ROUND);
		BitmapPaintOn.setStrokeCap(Paint.Cap.ROUND);
		BitmapPaintOn.setAlpha(128);
		//BitmapPaint = new Paint(Paint.DITHER_FLAG);
		
		BitmapPaintOff = new Paint();
		BitmapPaintOff.setDither(true);

		BitmapPaintOff.setStyle(Paint.Style.FILL);
		BitmapPaintOff.setStrokeJoin(Paint.Join.ROUND);
		BitmapPaintOff.setStrokeCap(Paint.Cap.ROUND);
		BitmapPaintOff.setStrokeWidth(8);
		BitmapPaintOff.setAlpha(128);
		
		BitmapPaintOn.setColor(Color.parseColor(default_LEDOnColor));
		BitmapPaintOff.setColor(Color.parseColor(default_LEDOffColor));
	}
	public void set_LEDon_image(int r){
		LEDon = BitmapFactory.decodeResource(c.getResources(), r);		
	}
	public void set_LEDoff_image(int r){
		LEDoff = BitmapFactory.decodeResource(c.getResources(), r);		
	}	
	private void setPaintOptions(AttributeSet attrs) {
		TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.LED, 0, 0);
		try{
			ison = a.getBoolean(R.styleable.LED_ison, false);
			if(!isInEditMode()){			   
			   int on = a.getResourceId(R.styleable.LED_ledon_image, -1);
			   if(on != -1)
				   set_LEDon_image(on);			   
			   int off = a.getResourceId(R.styleable.LED_ledoff_image, -1);
			   if(on != -1)
			   		set_LEDoff_image(off);
			   			   
			   /*String _LEDOnColor = a.getString(R.styleable.LED_ledon_color);
			   String _LEDOffColor = a.getString(R.styleable.LED_ledoff_color);		       
			   if(_LEDOnColor.length() == 7){
				   try{
					   Color.parseColor(_LEDOnColor);
					   default_LEDOnColor = _LEDOnColor;					
				   } catch (Exception e){				   
				   }
			   }			   
			   if(_LEDOnColor.length() == 7){			   
				   try{
					   Color.parseColor(_LEDOffColor);
					   default_LEDOffColor = _LEDOffColor;
				   } catch (Exception e){				   
				   }
			   }*/
		   }
		   BitmapPaintOn.setColor(Color.parseColor(default_LEDOnColor));
		   BitmapPaintOff.setColor(Color.parseColor(default_LEDOffColor));
		   invalidate();
	   } finally {
		   a.recycle();
	   }
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		background = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		ctx = new Canvas(background);
		metrics = getResources().getDisplayMetrics();
		if(LEDon != null && LEDoff != null){
			scale_matrix = new Matrix();
			float scale = (this.getWidth()/metrics.scaledDensity) / 30.0f;	//graphic size is 250
			scale_matrix.setScale(scale, scale);
		}
		invalidate();
	}	

	@Override
	protected void onDraw(Canvas canvas) {
	    int left = getPaddingLeft();
	    int top = getPaddingTop();
	    int right = getWidth() - getPaddingRight();
	    int bottom = getHeight() - getPaddingBottom();
	    
	    canvas.drawBitmap(background, 0, 0, BitmapPaintOff);	    
		if(ison && LEDon != null)
			canvas.drawBitmap(LEDon, scale_matrix, BitmapPaintOff);
		else if(LEDoff != null)
			canvas.drawBitmap(LEDoff, scale_matrix, BitmapPaintOff);
		else if(ison)
			canvas.drawCircle(this.getMeasuredWidth()/2, this.getMeasuredHeight()/2, this.getMeasuredWidth()/2-(1*metrics.scaledDensity), BitmapPaintOn);
		else
			canvas.drawCircle(this.getMeasuredWidth()/2, this.getMeasuredHeight()/2, this.getMeasuredWidth()/2-(1*metrics.scaledDensity), BitmapPaintOff);
		canvas.drawCircle(this.getMeasuredWidth()/2, this.getMeasuredHeight()/2, this.getMeasuredWidth()/2-(1*metrics.scaledDensity), BitmapPaintStroke);
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
}
