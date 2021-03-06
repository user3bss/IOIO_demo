/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bmt.custom_classes.FileIO;
import com.bmt.custom_classes.MathChart;
import com.bmt.custom_classes.Util;
import com.bmt.custom_classes.line_chart;
import com.bmt.ioio_demo.R;


public class UIGraphView extends View implements GestureDetector.OnGestureListener{
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
	Util u = null;
	FileIO[] files = null;
	private int[] colors = null;
	int sizeOfFloat = 0;
	
	Context c = null;
	
	private void init(){
		if(!isInEditMode()){
	        final GestureDetector gdt = new GestureDetector(this);
	        this.setOnTouchListener(new OnTouchListener() {
	            @Override
	            public boolean onTouch(final View view, final MotionEvent event) {
	                gdt.onTouchEvent(event);
	                return true;
	            }
	        });
		}
		u = new Util();
		sizeOfFloat = u.sizeOfFloat();
	}
	public UIGraphView(Context _c) {
		super(_c);
		c = _c;
		setupPaint();		
		init();
	}
	public UIGraphView(Context _c, AttributeSet attrs) {
		super(_c, attrs);
		c = _c;
		setPaintOptions(_c, attrs);
		init();
	}

	public UIGraphView(Context _c, AttributeSet attrs, int defStyle) {
		super(_c, attrs, defStyle);
		c = _c;
		setPaintOptions(_c, attrs);
		init();
	}
	public void setFilesAndColors(Application app, String[] _fileNames, int[] _colors){
		//Log.i(tag, "setFilesAndColors");
		if(_fileNames.length == _colors.length){
			colors = _colors;
			//Log.i(tag, "Creating FileInPutStreams");
			files = new FileIO[_fileNames.length];
			for(int i=0;i<_fileNames.length;i++){
				files[i] = new FileIO(app, FileIO.file_location.APPTEMP, FileIO.file_mode.READ, _fileNames[i]);
			}
		}
	}
	public void closeFiles(){
		for(int i=0;i<files.length;i++){
			files[i].closeFile();
		}		
	}
	
	public void filesUpdated(boolean[] enabled_channels){
		//if scroll is at end of data display the new data coming in.
		//Log.i(tag, "numGraphPointsXaxis: "+numGraphPointsXaxis);
		//graph0.drawSamplesLineChart(p.samples, color); //pass the data directly
		//drawSamplesLineChart(p.samples, color, int start, int end);
		
		//TODO need to filter samples according to scroll here??		
		//graph0.drawLineChart(p.getSamples(), color); //convert arraylist<float> to float[] 
		//TODO use paging for render frames???

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int numGraphPointsXaxis = metrics.widthPixels - leftOffset; //949+75 = 1024		
		Log.i(tag, "numGraphPointsXaxis: "+numGraphPointsXaxis+" density: "+metrics.densityDpi);
		try{
			if(files != null && numGraphPointsXaxis > 0 && colors.length == files.length && files.length == enabled_channels.length){
				for(int i =0;i<files.length;i++){
					if(enabled_channels[i]){	//use enabled_channels to graph selected channels
						Log.i(tag, "Plotting channel: "+i);
						long fl = files[i].fileLength();
						byte[] buffer = new byte[numGraphPointsXaxis * sizeOfFloat];
						int byteOffset = (int) fl - (numGraphPointsXaxis * sizeOfFloat);	//4 bytes for each float
						/*if(byteOffset < (numGraphPointsXaxis * sizeOfFloat)){
							break;
						}*/
						Log.i(tag, "numGraphPointsXaxis: "+numGraphPointsXaxis);
						Log.i(tag, "File Length: "+fl + ", buffer length:" + buffer.length+ ", offset: "+byteOffset);
						long position = files[i].seek(byteOffset);
						int numBytesRead = files[i].readByteBuffer(buffer);
						Log.i(tag, "position: "+position+", numbytesread: "+numBytesRead);
						
						//convert byte[] to float[]
						FloatBuffer fb = ByteBuffer.wrap(buffer).asFloatBuffer();
						float[] f = new float[fb.capacity()];
						fb.get(f); 					// Copy the contents of the FloatBuffer into dst						
						Log.i(tag, "f.length: "+f.length);
						
						//if(f.length > 0){
							//drawGraphLines(4, 4, leftOffset, 0); //have to draw text labels too
							//drawLineChart(f, colors[i]);
						//}
						/*background_bitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
						tiCanvas.drawPath(border_path, border_paint);
						drawGraphLines(4, 4, leftOffset, 0); //have to draw text labels too
						float[] v = new float[metrics.widthPixels-leftOffset];
						float[] sq = new float[metrics.widthPixels-leftOffset];
						float[] t = new float[metrics.widthPixels-leftOffset];
						float[] s = new float[metrics.widthPixels-leftOffset];
						for(int i1=0;i1<metrics.widthPixels-leftOffset;i1++){
							v[i1] = (float) (1.65 * Math.sin(i1*0.05) + 1.65); //.017 rad = 1 deg
							sq[i1] = (float) (Math.signum( Math.sin(2*Math.PI*i1*0.005))+1.65);
							t[i1] = Math.abs(v[i1]);
						}
						drawLineChart(v, Color.RED);
						drawLineChart(t, Color.GREEN);
						drawLineChart(sq, Color.CYAN);						
						invalidate();*/
					}
				}
			}
		} catch (Exception e) {
			Toast.makeText(c, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}		
	}
	/*public interface UIGraphViewListener {
		public void onScrollUpdate(float x, float y, float xScroll, float yScroll);
	}
	private UIGraphViewListener m_listener = null;
	public void SetListener(UIGraphViewListener uiGraphViewListener) {
		m_listener = uiGraphViewListener;
	}*/
	/*
	abstract boolean 	onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
						Notified when a scroll occurs with the initial on down MotionEvent and the current move MotionEvent.
	 */	
	private void setPaintDefaults(Paint p, String c){
		p.setAntiAlias(true);
		p.setDither(true);
		p.setColor(Color.parseColor(c));
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(2);
		p.setAlpha(255);
		if(!isInEditMode())
			p.setShadowLayer(4, 2, 2, 0x80000000);
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
			float[] v = MathChart.sinData(width-leftOffset, 1.65, 1.0);
			drawLineChart(v, Color.RED);
			
			float[] t = MathChart.sqData(width-leftOffset, 1.65, .8);
			drawLineChart(t, Color.GREEN);
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
			Log.d(tag, "onSizeChanged w:"+w+", h:");
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
		if (border_path != null){
			canvas.drawPath(border_path, border_paint);
		}
		if(graph_lines_path != null){
			canvas.drawPath(graph_lines_path, graph_lines_paint);
		}
	}

	public void clear() {
		background_bitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
		tiCanvas.drawPath(border_path, border_paint);
		//tiCanvas.drawPath(graph_lines_path, graph_lines_paint);
		drawGraphLines(4, 4, leftOffset, 0); //have to draw text labels too
		//tiCanvas.scale(1,-1,tiCanvas.getWidth()/2,tiCanvas.getHeight()/2);		
		invalidate();
	}
	@Override
	public boolean onScroll(MotionEvent ondown, MotionEvent currentMove, float distanceX, float distanceY) {
		Log.i(tag, "onScroll fired");
		float x = ondown.getX() / ((float) getWidth());
		float y = ondown.getY() / ((float) getHeight());
		float xScroll = currentMove.getX()  / ((float) getWidth());
		float yScroll = currentMove.getY()  / ((float) getHeight());
		
		if(currentMove.getActionMasked() == MotionEvent.ACTION_MOVE){
			Log.i(tag, "Action Move");
			Log.i(tag, "X: "+x+", Y:"+y);
			Log.i(tag, "xScroll: "+xScroll+", yScroll: "+yScroll);
			Log.i(tag, "xDistance: "+distanceX+", yDistance"+distanceY);
		}
		//e2.getYPrecision()			
		//float mm = e2.getTouchMajor() + e2.getTouchMinor();
		
		//TODO have to shift the data backwards and forwards invalidate then a refresh draw
		//pulling data from ? with the ability to pull using start offset and end offset
		//getting chunks the same size as the available graph area that is
		//width-leftOffset
		//
		
		//graph0.drawSamplesLineChart needs to graph the selected samples block,
		//or most recent samples block if no scroll has been made
		//if (m_listener != null) m_listener.onScrollUpdate(x,y,xScroll, yScroll);
		return true;
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		Log.i(tag, "onDown");
		return true;
	}
	
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	       float velocityY) {
		Log.i(tag, "onFling");
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            return false; // Right to left
        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            return false; // Left to right
        }

        if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            return false; // Bottom to top
        }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
            return false; // Top to bottom
        }
        return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {	
		Log.i(tag, "onLongPress");
	}	
	@Override
	public void onShowPress(MotionEvent arg0) {	
		Log.i(tag, "onShowPress");
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		Log.i(tag, "onSingleTapUp");
		return true;
	}
}