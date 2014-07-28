/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import ioio.examples.hello.R;
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

public class UIGraphView extends View {
	private Path tiPath = null;
	private Bitmap tiBitmap = null;
	//private Bitmap tiBackground = null;
	private Canvas tiCanvas = null;
	private Paint tiBitmapPaint = null;
	String tag = getClass().getSimpleName();
	
	private void setPaintOptions() {
		tiBitmapPaint = new Paint();
		tiBitmapPaint.setAntiAlias(true);
		tiBitmapPaint.setDither(true);
		tiBitmapPaint.setColor(Color.BLACK);
		tiBitmapPaint.setStyle(Paint.Style.STROKE);
		tiBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		tiBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		tiBitmapPaint.setStrokeWidth(2);
		tiBitmapPaint.setAlpha(255);
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
		tiPath = new Path();		
	}

	public void setUIGraphView_AntiAlias(boolean AntiAlias){
		tiBitmapPaint.setAntiAlias(AntiAlias);		
	}
	public void setUIGraphView_Dither(boolean Dither){
		tiBitmapPaint.setDither(Dither);
	}
	public void setUIGraphView_StrokeJoin(int StrokeJoin){
		if(StrokeJoin == 0)
			tiBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
		if(StrokeJoin == 1)
			tiBitmapPaint.setStrokeJoin(Paint.Join.BEVEL);
		if(StrokeJoin == 2)
			tiBitmapPaint.setStrokeJoin(Paint.Join.MITER);
	}
	public void setUIGraphView_StrokeCap(int StrokeCap){
		if(StrokeCap == 0)
			tiBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
		if(StrokeCap == 1)
			tiBitmapPaint.setStrokeCap(Paint.Cap.BUTT);
		if(StrokeCap == 2)
			tiBitmapPaint.setStrokeCap(Paint.Cap.SQUARE);		
	}
	public void setUIGraphView_StrokeWidth(int StrokeWidth){
		tiBitmapPaint.setStrokeWidth(StrokeWidth);
	}
	public void setUIGraphView_Paint_Style(int Paint_Style){
		if(Paint_Style == 0)
			tiBitmapPaint.setStyle(Paint.Style.STROKE);
		if(Paint_Style == 1)
			tiBitmapPaint.setStyle(Paint.Style.FILL);
		if(Paint_Style == 2)
			tiBitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);		
	}
	public void setUIGraphView_Color(int _Color){
		tiBitmapPaint.setColor(_Color);	
	}
	public void setUIGraphView_Alpha(int _Alpha){		
		tiBitmapPaint.setAlpha(_Alpha);
	}	
	private void setPaintOptions(Context context, AttributeSet attrs) {		
		Log.i(tag, "using attrs");
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIGraphView, 0, 0);
		   try {	       
			   	setPaintOptions();
				setUIGraphView_AntiAlias(a.getBoolean(R.styleable.UIGraphView_AntiAlias, false));
				setUIGraphView_Alpha(a.getInteger(R.styleable.UIGraphView_Alpha, 255));
				//setUIGraphView_Color(a.getInteger(R.styleable.UIGraphView_Color, Color.BLACK));
				//setUIGraphView_Color( Color.parseColor( a.getString(R.styleable.UIGraphView_Color) ));
				setUIGraphView_Dither(a.getBoolean(R.styleable.UIGraphView_Dither, true));
				setUIGraphView_StrokeCap(a.getInteger(R.styleable.UIGraphView_StrokeCap, 0));
				setUIGraphView_StrokeWidth(a.getInteger(R.styleable.UIGraphView_StrokeWidth, 2));
				setUIGraphView_Paint_Style(a.getInteger(R.styleable.UIGraphView_Paint_Style, 0));
				setUIGraphView_StrokeJoin(a.getInteger(R.styleable.UIGraphView_StrokeJoin, 0));
				
		   } finally {
		       a.recycle();
		   }
		//tiBitmapPaint = new Paint(Paint.DITHER_FLAG);
		tiPath = new Path();		
	}	
	public UIGraphView(Context c) {
		super(c);
		setPaintOptions();
		
	}
	public UIGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaintOptions(context, attrs);
	}

	public UIGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaintOptions(context, attrs);
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
		tiCanvas.drawText("1000", 20, divisor, tiBitmapPaint);// determine text box size?		
		tiCanvas.drawText("1000", 20, divisor*2, tiBitmapPaint);// determine text box size?
		tiCanvas.drawText("1000", 20, divisor*3, tiBitmapPaint);// determine text box size?
		tiCanvas.drawText("1000", 20, divisor*4, tiBitmapPaint);// determine text box size?
		
		//tiBitmapPaint.setColor(Color.RED);
		//
		//
		//
		//
		/*Paint redBrush = new Paint();
		redBrush.setAntiAlias(true);
		redBrush.setDither(true);
		redBrush.setColor(Color.RED);
		redBrush.setStyle(Paint.Style.STROKE);
		redBrush.setStrokeJoin(Paint.Join.ROUND);
		redBrush.setStrokeCap(Paint.Cap.ROUND);
		redBrush.setStrokeWidth(2);
		redBrush.setAlpha(255);  
		
		//tiPath.moveTo(leftOffset, 0 );	//top left corner
		//tiPath.lineTo(width, height); //bottom right corner
		
		//tiPath.moveTo(leftOffset, height/2 );	//center horizontal line
		//tiPath.lineTo(width, height/2);
		
		//tiPath.moveTo((width+leftOffset)/2, 0 );	//center vertical line
		//tiPath.lineTo((width+leftOffset)/2, height); 		//bottom right corner
		tiCanvas.drawPath(tiPath, redBrush);*/
		
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
		canvas.drawBitmap(tiBitmap, 0, 0, tiBitmapPaint);
		if (tiPath != null) {
			//Log.i(tag, "tiPath != null ");
			canvas.drawPath(tiPath, tiBitmapPaint);
		}
	}
	
	public void finalizePaths() {
		if (tiPath != null) {
			tiCanvas.drawPath(tiPath, tiBitmapPaint);
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