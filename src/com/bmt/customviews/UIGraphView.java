/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bmt.ioio_demo.R;


public class UIGraphView extends View {
	private Path tiPath = null;
	private Bitmap tiBitmap = null;
	//private Bitmap tiBackground = null;
	private Canvas tiCanvas = null;
	String tag = getClass().getSimpleName();
	private brush defaultBrush = null;
	
	private class brush{
		private Path[] _Path = null;			//store path with paint
		private Paint _BitmapPaint = null;		
		brush(){
			_BitmapPaint = new Paint();
			_BitmapPaint.setAntiAlias(true);
			_BitmapPaint.setDither(true);
			_BitmapPaint.setColor(Color.BLACK);
			_BitmapPaint.setStyle(Paint.Style.STROKE);
			_BitmapPaint.setStrokeJoin(Paint.Join.ROUND);
			_BitmapPaint.setStrokeCap(Paint.Cap.ROUND);
			_BitmapPaint.setStrokeWidth(2);
			_BitmapPaint.setAlpha(255);			
		}
		public Path[] getPaths(){
			return _Path;	//return the array
		}
		public Paint getPaint(){
			return _BitmapPaint;
		}
		public void setAntiAlias(boolean AntiAlias){
			_BitmapPaint.setAntiAlias(AntiAlias);		
		}
		public void setDither(boolean Dither){
			_BitmapPaint.setDither(Dither);
		}
		public void setStrokeJoin(int StrokeJoin){
			if(StrokeJoin == 0)
				_BitmapPaint.setStrokeJoin(Paint.Join.ROUND);
			if(StrokeJoin == 1)
				_BitmapPaint.setStrokeJoin(Paint.Join.BEVEL);
			if(StrokeJoin == 2)
				_BitmapPaint.setStrokeJoin(Paint.Join.MITER);
		}
		public void setStrokeCap(int StrokeCap){
			if(StrokeCap == 0)
				_BitmapPaint.setStrokeCap(Paint.Cap.ROUND);
			if(StrokeCap == 1)
				_BitmapPaint.setStrokeCap(Paint.Cap.BUTT);
			if(StrokeCap == 2)
				_BitmapPaint.setStrokeCap(Paint.Cap.SQUARE);		
		}
		public void setStrokeWidth(int StrokeWidth){
			_BitmapPaint.setStrokeWidth(StrokeWidth);
		}
		public void setPaintStyle(int Paint_Style){
			if(Paint_Style == 0)
				_BitmapPaint.setStyle(Paint.Style.STROKE);
			if(Paint_Style == 1)
				_BitmapPaint.setStyle(Paint.Style.FILL);
			if(Paint_Style == 2)
				_BitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);		
		}
		public void setColor(int _Color){
			_BitmapPaint.setColor(_Color);	
		}
		public void setAlpha(int _Alpha){		
			_BitmapPaint.setAlpha(_Alpha);
		}		
	}
	
	public void setBrush(Context context, AttributeSet attrs) {		
		Log.i(tag, "graph view using attrs");
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIGraphView, 0, 0);
		   try {
			   	defaultBrush.setAntiAlias(a.getBoolean(R.styleable.UIGraphView_AntiAlias, false));
			   	defaultBrush.setAlpha(a.getInteger(R.styleable.UIGraphView_Alpha, 255));
				//setUIGraphView_Color(a.getInteger(R.styleable.UIGraphView_Color, Color.BLACK));
				//setUIGraphView_Color( Color.parseColor( a.getString(R.styleable.UIGraphView_Color) ));
			   	defaultBrush.setDither(a.getBoolean(R.styleable.UIGraphView_Dither, true));
			   	defaultBrush.setStrokeCap(a.getInteger(R.styleable.UIGraphView_StrokeCap, 0));
			   	defaultBrush.setStrokeWidth(a.getInteger(R.styleable.UIGraphView_StrokeWidth, 2));
			   	defaultBrush.setPaintStyle(a.getInteger(R.styleable.UIGraphView_Paint_Style, 0));
			   	defaultBrush.setStrokeJoin(a.getInteger(R.styleable.UIGraphView_StrokeJoin, 0));
				
		   } finally {
		       a.recycle();
		   }		
	}	
	public UIGraphView(Context c) {
		super(c);
		defaultBrush = new brush();		
		//setPaintOptions();
	}
	public UIGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultBrush = new brush();		
		setBrush(context, attrs);		
	}

	public UIGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		defaultBrush = new brush();		
		setBrush(context, attrs);
	}	
	
	protected void drawGraphLines(int numlinesX, int numlinesY, int leftOffset, int bottomOffset){
		int width = tiCanvas.getWidth();
		int height = tiCanvas.getHeight();
		
		int spacingX = (width - leftOffset)/numlinesX;
		int spacingY = (height - bottomOffset)/numlinesY;
		tiPath.moveTo(leftOffset, 0);	//draw border
		tiPath.lineTo(width, 0);
		tiPath.lineTo(width, height-bottomOffset);
		tiPath.lineTo(leftOffset, height-bottomOffset);
		tiPath.lineTo(leftOffset, 0);
		
		float divisor = (height-bottomOffset)/5;
		tiPath.moveTo(leftOffset, divisor );	//draw border
		tiPath.lineTo(width, divisor);
				
		tiPath.moveTo(leftOffset,  divisor*2);	//draw border
		tiPath.lineTo(width, divisor*2);
		
		tiPath.moveTo(leftOffset, divisor*3 );	//draw border
		tiPath.lineTo(width, divisor*3);

		tiPath.moveTo(leftOffset, divisor*4 );	//draw border
		tiPath.lineTo(width, divisor*4);
		
		float divisorV = (width-leftOffset)/5;
		tiPath.moveTo(leftOffset+divisorV, 0 );	//draw border
		tiPath.lineTo(leftOffset+divisorV, height );		
		
		tiPath.moveTo(leftOffset+divisorV*2, 0 );	//draw border
		tiPath.lineTo(leftOffset+divisorV*2, height );
		
		tiPath.moveTo(leftOffset+divisorV*3, 0 );	//draw border
		tiPath.lineTo(leftOffset+divisorV*3, height );
		
		tiPath.moveTo(leftOffset+divisorV*4, 0 );	//draw border
		tiPath.lineTo(leftOffset+divisorV*4, height );		
		
		//tiBitmapPaint.measureText("drawText:")
		tiCanvas.drawText("1000", 20, divisor, defaultBrush.getPaint());// determine text box size?		
		tiCanvas.drawText("1000", 20, divisor*2, defaultBrush.getPaint());// determine text box size?
		tiCanvas.drawText("1000", 20, divisor*3, defaultBrush.getPaint());// determine text box size?
		tiCanvas.drawText("1000", 20, divisor*4, defaultBrush.getPaint());// determine text box size?
		
		//tiPath.moveTo(leftOffset, 0 );	//top left corner
		//tiPath.lineTo(width, height); //bottom right corner
		
		//tiPath.moveTo(leftOffset, height/2 );	//center horizontal line
		//tiPath.lineTo(width, height/2);
		
		//tiPath.moveTo((width+leftOffset)/2, 0 );	//center vertical line
		//tiPath.lineTo((width+leftOffset)/2, height); 		//bottom right corner
		tiCanvas.drawPath(tiPath, defaultBrush.getPaint());
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(tag, "onSizeChanged: ");
		super.onSizeChanged(w, h, oldw, oldh);
		tiBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		tiCanvas = new Canvas(tiBitmap);
		int width = tiCanvas.getWidth();
		int height = tiCanvas.getHeight();
		tiPath.moveTo(0, 0);	//draw border
		tiPath.lineTo(width, 0);
		tiPath.lineTo(width, height);
		tiPath.lineTo(0, height);
		tiPath.lineTo(0, 0);
		//tiBitmapPaint.setTextSize(20);
		//tiBitmapPaint.setShadowLayer(35, 0, 0, Color.DKGRAY);
		//tiCanvas.drawText("drawText:"+tiBitmapPaint.measureText("drawText:"), 10, 20, tiBitmapPaint);// determine text box size?
		drawGraphLines(3, 3, 100, 0);
		invalidate();		
	}	

	@Override
	protected void onDraw(Canvas canvas) {
		/*if(!hasBackgroundImage){ 
			boolean containsBG = props.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_COLOR);
			canvas.drawColor(containsBG ? TiConvert.toColor(props, TiC.PROPERTY_BACKGROUND_COLOR) : TiConvert.toColor("transparent"));
		}*/		
		//Log.i(tag, "onDraw fired: " + hasBackgroundImage)
		canvas.drawBitmap(tiBitmap, 0, 0, defaultBrush.getPaint());
		if (tiPath != null) {
			//Log.i(tag, "tiPath != null ");
			canvas.drawPath(tiPath, defaultBrush.getPaint());
		}
	}
	
	public void finalizePaths() {
		if (tiPath != null) {
			tiCanvas.drawPath(tiPath, defaultBrush.getPaint());
			tiPath.reset();
			tiPath = null;
		}		
	}

	public void clear() {
		finalizePaths();
		tiBitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
		invalidate();
	}
}