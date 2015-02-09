/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.custom_classes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class UIPaintView extends View {
	private Path tiPath = null;
	private Bitmap tiBitmap = null;
	//private Bitmap tiBackground = null;
	private Canvas tiCanvas = null;
	private Paint tiBitmapPaint = null;
	private ArrayList<Object> drawingTouch = null;
	private ArrayList<Object> drawing = null;
	private Object initDrawing = null;
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
		drawing = new ArrayList<Object>();
		initDrawing = null;			
	}	
	public UIPaintView(Context c) {
		super(c);
		setPaintOptions();
	}
	public UIPaintView(Context c, Object id) {
		super(c);
		setPaintOptions();
		initDrawing = id;
	}
	public UIPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaintOptions();
	}

	public UIPaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaintOptions();
	}	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d(tag, "onSizeChanged: ");
		super.onSizeChanged(w, h, oldw, oldh);
		tiBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		tiCanvas = new Canvas(tiBitmap);
		int width = tiCanvas.getWidth();
		int height = tiCanvas.getHeight();
		/*tiPath.moveTo(0, 0);				//draw border
		tiPath.lineTo(width, 0);
		tiPath.lineTo(width, height);
		tiPath.lineTo(0, height);
		tiPath.lineTo(0, 0);
		tiCanvas.drawPath(tiPath, tiBitmapPaint);
		tiPath.reset();*/
		tiPath = null;
		invalidate();
		
		if(initDrawing != null){	//load drawing now that canvas is set
			loadDrawing(initDrawing);
			initDrawing = null;
		}
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
	
	private void touch_start(int id, float x, float y) {
		tiPath = new Path();
		tiPath.moveTo(x, y);
		_touchDown(x, y);	
	}
	
	private void _touchUp(){
		Object[] p = drawingTouch.toArray();
		HashMap<String, Object> hm;
		hm = new HashMap<String, Object>();						
		hm.put("a", 0);
		hm.put("c", "rgb(0,0,0)");
		hm.put("w", 2);
		hm.put("p", p);
		drawing.add(hm);			
	}
	private void _touchDown(float x, float y){
		drawingTouch = new ArrayList<Object>();
		HashMap<String, Object> p = new HashMap<String, Object>();			
		p.put("x", (int)Math.round(x));
		p.put("y", (int)Math.round(y));
		drawingTouch.add(p);			
	}
	private void _touchMove(float x, float y){
		HashMap<String, Object> p = new HashMap<String, Object>();			
		p.put("x", (int)Math.round(x));
		p.put("y", (int)Math.round(y));
		drawingTouch.add(p);			
	}
	private void touch_move(int id, float x, float y) {
		_touchMove(x, y);
		tiPath.lineTo(x, y);		
	}

	@Override
	public boolean onTouchEvent(MotionEvent mainEvent) {
		for (int i = 0; i < mainEvent.getPointerCount(); i++) {
			int id = mainEvent.getPointerId(i);
			float x = mainEvent.getX(i);
			float y = mainEvent.getY(i);
			int action = mainEvent.getAction();
			if (action > 6) {
				action = (action % 256) - 5;
			}
			switch (action) {
				case MotionEvent.ACTION_DOWN:
					finalizePath(id);
					touch_start(id, x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touch_move(id, x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					_touchUp();
					finalizePath(id);
					invalidate();
					break;
			}
		}

		return true;
	}

	public void finalizePath(int id) {
		if (tiPath != null) {
			tiCanvas.drawPath(tiPath, tiBitmapPaint);
			tiPath.reset();
			tiPath = null;
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
		drawing = new ArrayList<Object>();
		finalizePaths();
		tiBitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
		invalidate();
	}
			
	public void loadDrawing(Object args){			
		//Clear the Drawing
		//clear();			
		//printObjType(args);			
		if (!(args.getClass().isArray())) {
			throw new IllegalArgumentException("Argument must be an array");
		}
		Object[] argArray = (Object[])args;
		for (int index=0; index < argArray.length; index++) {
			//drawing.add(argArray[index]);				
			Object p = ((HashMap<String, Object>) argArray[index]).get("p");
			Integer w = ((HashMap<String, Integer>) argArray[index]).get("w"); //Line Width
			Integer a = ((HashMap<String, Integer>) argArray[index]).get("a");	//Drawing Mode
			String c = ((HashMap<String, String>) argArray[index]).get("c");	//Color rgb
			Log.d(tag, "DrawMode: "+a+", LineWidth: "+w+", Color: "+c); //printObjType(p);
			if(tiCanvas != null)
				Log.d(tag, "tiCanvas: " + tiCanvas.getClass().getName());
			else
				Log.d(tag, "tiCanvas: null");
			if(a == 0){
				if ((p.getClass().isArray())) {
					Object[] argArray1 = (Object[])p;
					if(argArray1.length > 1){
						Path pth = new Path();						
						for(int pndex=0; pndex<argArray1.length;pndex++){
							Float x = (float) ((HashMap<String, Integer>) argArray1[pndex]).get("x");
							Float y = (float) ((HashMap<String, Integer>) argArray1[pndex]).get("y");
							Log.d(tag, "x: "+x+", y: "+y);
							if(pndex == 0){
								pth.moveTo(x, y);									
							} else {
								pth.lineTo(x, y);
							}
						}
						if(tiCanvas != null){
							tiCanvas.drawPath(pth, tiBitmapPaint);
							invalidate();
						}
					}
				}
			}
		}				
	}
	
	public Object[] saveDrawing(){
		return drawing.toArray();
	}
	
	public Bitmap loadBitmapFromView() {
	    //Bitmap b = Bitmap.createBitmap( this.getLayoutParams().width, this.getLayoutParams().height, Bitmap.Config.ARGB_8888);                
	    //Canvas c = new Canvas(b);
	    this.layout(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
	    this.draw(tiCanvas);
	    return tiBitmap;
	}	
}