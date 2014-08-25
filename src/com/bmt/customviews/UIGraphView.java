/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import com.bmt.customviews.UIKnobSwitch.UIKnobSwitchListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;


public class UIGraphView extends View implements OnGestureListener{
	private Path border_path = null;
	private Path graph_lines_path = null;
	
	private Bitmap background_bitmap = null;
	//private Bitmap tiBackground = null;
	private Canvas tiCanvas = null;
	String tag = getClass().getSimpleName();
	private Paint border_paint = null;
	private Paint graph_lines_paint = null;
	private Paint graph_text_paint = null;
	
	public interface UIGraphViewListener {
		public void onScrollUpdate(int position);
	}
	private UIGraphViewListener m_listener = null;
	public void SetListener(UIGraphViewListener uiGraphViewListener) {
		m_listener = uiGraphViewListener;
	}
	
	private void setPaintDefaults(Paint p, String c){
		p.setAntiAlias(true);
		p.setDither(true);
		p.setColor(Color.parseColor(c));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(2);
		p.setAlpha(255);		
	}
	private void setupPaint(){
		border_paint = new Paint();
		setPaintDefaults(border_paint, "#000000");
		border_paint.setStrokeWidth(1);
		
		graph_lines_paint = new Paint();
		setPaintDefaults(graph_lines_paint, "#3c3c3c");
		graph_text_paint = new Paint();
		setPaintDefaults(graph_text_paint, "#000000");		
	}
	private class line_chart{
		public Paint _BitmapPaint = null;
		line_chart(){
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
		public void drawPoints(Canvas ctx, float[] points){
			//ctx.drawPoints(points, 0, points.length, _BitmapPaint);			
			for(int i=0;i<points.length;i+=2){
				Path l = new Path();
				l.moveTo(points[i], points[i+1]);				
				l.lineTo(points[i], points[i+1]);
				ctx.drawPath(l, _BitmapPaint);
			}			
		}
		public void drawLines(Canvas ctx, float[] points){
			//ctx.drawPoints(points, 0, points.length, _BitmapPaint);
			Path l = new Path();
			l.moveTo(points[0], points[1]);			
			for(int i=2;i<points.length;i+=2){				
				l.lineTo(points[i], points[i+1]);				
			}
			ctx.drawPath(l, _BitmapPaint);			
		}		
		private float min(float[] values){
			float min = 100000000;
			for(int i=0;i<values.length;i++){
				if(values[i] < min){
					min = values[i];
				}
			}
			return min;
		}
		private float max(float[] values){
			float max = -100000000;
			for(int i=0;i<values.length;i++){
				if(values[i] > max){
					max = values[i];
				}
			}
			return max;
		}		
		public void drawValues(Canvas ctx, float[] values, int leftOffset){
			float[] points = new float[values.length*2];
			int w = ctx.getWidth() - leftOffset;
			int h = ctx.getHeight();
			int sample_rate = 1000;
			double yScale = (h / 3.3);
			//double xTime_Per_Pixel = w / sample_rate; //640px-75px/1khz = .565s resolution
			ctx.save();
			ctx.scale(1,-1,w/2,h/2);
			
			//values.length <= chart_available_width
			int pointINDX = 0;
			for(int i =0;i<values.length;i++){
				points[pointINDX++] = leftOffset+i;			//each value = 1px
				points[pointINDX++] = (float) (values[i] * yScale);
			}
			//drawPoints(ctx, points);
			drawLines(ctx, points);
			ctx.restore();
		}
	}	
	public UIGraphView(Context c) {
		super(c);
		setupPaint();
	}
	public UIGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupPaint();		
		//setBrush(context, attrs);		
	}

	public UIGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupPaint();	
		//setBrush(context, attrs);
	}	
	public void drawlines(float[] f, int c){
		line_chart lc = new line_chart();
		lc._BitmapPaint.setColor(c);
		if(f.length <= tiCanvas.getWidth()){
			lc.drawValues(tiCanvas, f, 75);
		} else {
			int diff = f.length - tiCanvas.getWidth() - 1;
			float[] _f = new float[tiCanvas.getWidth()];
			for(int i=0;i<tiCanvas.getWidth();i++){
				_f[i] = f[i+diff];
			}
			lc.drawValues(tiCanvas, _f, 75);
		}
	}
	protected void drawGraphLines(int numlinesX, int numlinesY, int leftOffset, int bottomOffset){
		int width = tiCanvas.getWidth();
		int height = tiCanvas.getHeight();
		graph_lines_path = new Path();
		graph_lines_path.moveTo(leftOffset, 0);	//draw border
		graph_lines_path.lineTo(width, 0);
		graph_lines_path.lineTo(width, height-bottomOffset);
		graph_lines_path.lineTo(leftOffset, height-bottomOffset);
		graph_lines_path.lineTo(leftOffset, 0);
		
		float divisor = (height-bottomOffset)/numlinesY;
		for(int i=0;i<numlinesY;i++){
			graph_lines_path.moveTo(leftOffset, ((i+1)*divisor) );	//draw border
			graph_lines_path.lineTo(width, ((i+1)*divisor));			
		}
		
		float divisorV = (width-leftOffset)/numlinesX;
		
		tiCanvas.drawText("3.3", 20, 0+graph_text_paint.getTextSize(), graph_text_paint);// determine text box size?
		for(int i=0;i<numlinesY-1;i++){
			graph_lines_path.moveTo(leftOffset+((i+1)*divisorV), 0 );
			graph_lines_path.lineTo(leftOffset+((i+1)*divisorV), height );
			
			String v = ((3.3/numlinesY)*(numlinesY-i-1))+"";
			tiCanvas.drawText( v.substring(0, 4), 20, (divisor*(i+1))+(graph_text_paint.getTextSize()/2), graph_text_paint);// determine text box size?	
		}
		tiCanvas.drawText("0", 20, height, graph_text_paint);// determine text box size?
		tiCanvas.drawPath(graph_lines_path, graph_lines_paint);	
		//prepare and draw sine wave
		if(isInEditMode()){
			float[] v = new float[width-leftOffset];
			for(int i=0;i<width-leftOffset;i++){
				v[i] = (float) (1.65 * Math.sin(i*0.05) + 1.65); //.017 rad = 1 deg
			}
			drawlines(v, Color.RED);			
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//if(!isInEditMode()){		
			Log.d(tag, "onSizeChanged: ");
			super.onSizeChanged(w, h, oldw, oldh);
			background_bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			tiCanvas = new Canvas(background_bitmap);
			int width = tiCanvas.getWidth();
			int height = tiCanvas.getHeight();
			border_path = new Path();
			border_path.moveTo(0, 0);	//draw border
			border_path.lineTo(width, 0);
			border_path.lineTo(width, height);
			border_path.lineTo(0, height);
			border_path.lineTo(0, 0);
			tiCanvas.drawPath(border_path, border_paint);
			//tiBitmapPaint.setTextSize(20);
			//tiBitmapPaint.setShadowLayer(35, 0, 0, Color.DKGRAY);
			//tiCanvas.drawText("drawText:"+tiBitmapPaint.measureText("drawText:"), 10, 20, tiBitmapPaint);// determine text box size?
			drawGraphLines(4, 4, 75, 0);
			invalidate();
		//}	
	}	

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(background_bitmap, 0, 0, border_paint);
		/*if (border_path != null){
			canvas.drawPath(border_path, border_paint);
		}
		if(graph_lines_path != null){
			canvas.drawPath(graph_lines_path, graph_lines_paint);
		}*/
	}

	public void clear() {
		background_bitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
		tiCanvas.drawPath(border_path, border_paint);
		drawGraphLines(4, 4, 75, 0);
		invalidate();
	}
/*
abstract boolean 	onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
					Notified when a scroll occurs with the initial on down MotionEvent and the current move MotionEvent.
 */
	@Override
	public boolean onScroll(MotionEvent ondown, MotionEvent currentMove, float distanceX, float distanceY) {
			float x = ondown.getX() / ((float) getWidth());
			float y = ondown.getY() / ((float) getHeight());
			float xScroll = currentMove.getX()  / ((float) getWidth());
			float yScroll = currentMove.getY()  / ((float) getHeight());
			//e2.getYPrecision()			
			//float mm = e2.getTouchMajor() + e2.getTouchMinor();
			
			//TODO have to shift the data backwards and forwards invalidate then a refresh draw
			//pulling data from ? with the ability to pull using start offset and end offset
			//getting chunks the same size as the available graph area that is
			//width-leftOffset
		return false;
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,	float arg3) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {		
	}	
	@Override
	public void onShowPress(MotionEvent arg0) {		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
}