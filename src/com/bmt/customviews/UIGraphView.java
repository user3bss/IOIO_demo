/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
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

import com.bmt.custom_classes.line_chart;
import com.bmt.ioio_demo.R;


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
	protected int leftOffset = 75;
	protected int textLeftPadding = 20;
	//private boolean isScrolling = false;
	
	public interface UIGraphViewListener {
		public void onScrollUpdate(float x, float y, float xScroll, float yScroll);
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
	private void setPaintOptions(Context context, AttributeSet attrs) {		
		Log.i(tag, "using attrs");
		setupPaint();		   
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIGraphView, 0, 0);
		   try {
			   	int borderColor = a.getColor(R.styleable.UIGraphView_BorderColor, Color.parseColor("#000000"));
			   	border_paint.setColor(borderColor);
			   	
			   	int borderStrokeWidth = a.getInt(R.styleable.UIGraphView_BorderStrokeWidth, 1);
			   	border_paint.setStrokeWidth(borderStrokeWidth);
			   	
			   	int graph_linesColor = a.getColor(R.styleable.UIGraphView_LineColor, Color.parseColor("#3c3c3c"));
			   	graph_lines_paint.setColor(graph_linesColor);
			   	
			   	int graph_linesStrokeWidth = a.getInt(R.styleable.UIGraphView_LineStrokeWidth, 2);
			   	graph_lines_paint.setStrokeWidth(graph_linesStrokeWidth);
			   	
			   	int textColor = a.getColor(R.styleable.UIGraphView_FontColor, Color.parseColor("#000000"));
			   	graph_text_paint.setColor(textColor);
		   } finally {
		       a.recycle();
		   }		  
	}	
	public UIGraphView(Context c) {
		super(c);
		setupPaint();
	}
	public UIGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaintOptions(context, attrs);		
	}

	public UIGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaintOptions(context, attrs);
	}	
	public void drawLineChart(float[] f, int c){
		line_chart lc = new line_chart();
		lc._BitmapPaint.setColor(c);
		if(f.length <= tiCanvas.getWidth()){
			lc.drawValues(tiCanvas, f, leftOffset);
		} else { //f.length > width read a section of data			
			//int diff = f.length - tiCanvas.getWidth() - 1;		//(f.length - width) could be much more than width 
			float[] _f = new float[tiCanvas.getWidth()];
			int start = f.length - tiCanvas.getWidth();			
			int j = 0;
			for(int i=start;i<f.length;i++){
				_f[j++] = f[i];
			}
			lc.drawValues(tiCanvas, _f, leftOffset);
		}
	}
	public void drawSamplesLineChart(ArrayList<Float> alf, int c){
		line_chart lc = new line_chart();
		lc._BitmapPaint.setColor(c);
		if(alf != null && alf.size() > 0){
			if(alf.size() <= tiCanvas.getWidth()){
				lc.drawValues(tiCanvas, alf, leftOffset, 0, alf.size());
			} else { //f.length > width read a section of data
				lc.drawValues(tiCanvas, alf, leftOffset, alf.size()-(tiCanvas.getWidth()-leftOffset), alf.size());
			}
		}
	}
	public void drawSamplesLineChart(ArrayList<Float> alf, int c, int start, int end){
		line_chart lc = new line_chart();
		lc._BitmapPaint.setColor(c);
		if(alf.size() <= tiCanvas.getWidth()){
			lc.drawValues(tiCanvas, alf, leftOffset, 0, alf.size());
		} else { //f.length > width read a section of data
			lc.drawValues(tiCanvas, alf, leftOffset, start, end);
		}
	}	
	protected void drawYAxisLabels(int numlinesY, int leftOffset, float divisor, float divisorV){
		tiCanvas.drawText("3.3", textLeftPadding, 0+graph_text_paint.getTextSize(), graph_text_paint);// determine text box size?
		for(int i=0;i<numlinesY-1;i++){			
			String v = ((3.3/numlinesY)*(numlinesY-i-1))+"";
			tiCanvas.drawText( v.substring(0, 4), textLeftPadding, (divisor*(i+1))+(graph_text_paint.getTextSize()/2), graph_text_paint);// determine text box size?	
		}
		tiCanvas.drawText("0", textLeftPadding, tiCanvas.getHeight(), graph_text_paint);// determine text box size?
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
		for(int i=0;i<numlinesY-1;i++){
			graph_lines_path.moveTo(leftOffset+((i+1)*divisorV), 0 );
			graph_lines_path.lineTo(leftOffset+((i+1)*divisorV), tiCanvas.getHeight() );	
		}
		tiCanvas.drawPath(graph_lines_path, graph_lines_paint);				
		drawYAxisLabels( numlinesY, leftOffset, divisor, divisorV);		
		//prepare and draw sine wave
		if(isInEditMode()){
			float[] v = new float[width-leftOffset];
			for(int i=0;i<width-leftOffset;i++){
				v[i] = (float) (1.65 * Math.sin(i*0.05) + 1.65); //.017 rad = 1 deg
			}
			drawLineChart(v, Color.RED);			
		}
	}
	protected void draw_border(){
		border_path = new Path();
		border_path.moveTo(0, 0);	//draw border
		border_path.lineTo(tiCanvas.getWidth(), 0);
		border_path.lineTo(tiCanvas.getWidth(), tiCanvas.getHeight());
		border_path.lineTo(0, tiCanvas.getHeight());
		border_path.lineTo(0, 0);
		tiCanvas.drawPath(border_path, border_paint);		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//if(!isInEditMode()){		
			Log.d(tag, "onSizeChanged: ");
			super.onSizeChanged(w, h, oldw, oldh);
			background_bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			tiCanvas = new Canvas(background_bitmap);			
			draw_border();

			//tiBitmapPaint.setTextSize(20);
			//tiBitmapPaint.setShadowLayer(35, 0, 0, Color.DKGRAY);
			//tiCanvas.drawText("drawText:"+tiBitmapPaint.measureText("drawText:"), 10, 20, tiBitmapPaint);// determine text box size?
			drawGraphLines(4, 4, leftOffset, 0);
			//tiCanvas.scale(1,-1,tiCanvas.getWidth()/2,tiCanvas.getHeight()/2);			
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
		//tiCanvas.drawPath(graph_lines_path, graph_lines_paint);
		drawGraphLines(4, 4, leftOffset, 0); //have to draw text labels too
		//tiCanvas.scale(1,-1,tiCanvas.getWidth()/2,tiCanvas.getHeight()/2);		
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
			if (m_listener != null) m_listener.onScrollUpdate(x,y,xScroll, yScroll);
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