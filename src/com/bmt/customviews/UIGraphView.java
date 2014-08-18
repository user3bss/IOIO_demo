/**
 * Ti.Paint Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package com.bmt.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class UIGraphView extends View {
	private Path border_path = null;
	private Path graph_lines_path = null;
	
	private Bitmap background_bitmap = null;
	//private Bitmap tiBackground = null;
	private Canvas tiCanvas = null;
	String tag = getClass().getSimpleName();
	private Paint border_paint = null;
	private Paint graph_lines_paint = null;
	private Paint graph_text_paint = null;

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
		//private Path[] _Path = null;			//store path with paint
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
			ctx.drawPoints(points, 0, points.length, _BitmapPaint);
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
			ctx.scale(-1,1,w/2,h/2);
			
			//values.length <= chart_available_width
			for(int i =0;i<values.length;i++){	
				points[i] = leftOffset+i;
				points[i+1] = (float) (values[i] * yScale);
			}
			drawPoints(ctx, points);
			//ctx.scale(-1,1,w/2,h/2);
			ctx.restore();
		}
	}
	/*
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
	}*/	
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
		
		//tiPath.moveTo(leftOffset, 0 );	//top left corner
		//tiPath.lineTo(width, height); //bottom right corner
		
		//tiPath.moveTo(leftOffset, height/2 );	//center horizontal line
		//tiPath.lineTo(width, height/2);
		
		//tiPath.moveTo((width+leftOffset)/2, 0 );	//center vertical line
		//tiPath.lineTo((width+leftOffset)/2, height); 		//bottom right corner
		tiCanvas.drawPath(graph_lines_path, graph_lines_paint);		
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
		if (border_path != null){
			canvas.drawPath(border_path, border_paint);
		}
		if(graph_lines_path != null){
			canvas.drawPath(graph_lines_path, graph_lines_paint);
		}
	}

	public void clear() {
		background_bitmap.eraseColor(Color.TRANSPARENT);	//don't want to erase backgroundImage, commenting doesn't erase anything
		invalidate();
	}
}