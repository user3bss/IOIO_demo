package com.bmt.GraphView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class line_chart{
	public Paint _BitmapPaint = null;
	public line_chart(){
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
		//makes up for xml renderer not supporting canvas.drawpoints method
		for(int i=0;i<points.length;i+=2){
			Path l = new Path();
			l.moveTo(points[i], points[i+1]);				
			l.lineTo(points[i], points[i+1]);
			//l.close();
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
	
	//takes array of values and converts to x,y points
	// x = leftOffset + i		//one point per value
	// y = values[i] * (h/3.3) or y = values[i] * (h/max(values)) 
	// happens that max is 3.3 volts for the samples so no need to find it
	public void drawValues(Canvas ctx, float[] values, int leftOffset){
		float[] points = new float[values.length*2];
		int w = ctx.getWidth() - leftOffset;
		int h = ctx.getHeight();
		double yScale = (h / 3.3);
		//double xTime_Per_Pixel = w / sample_rate; //640px-75px/1khz = .565s resolution
		//ctx.save();
		ctx.scale(1,-1,w/2,h/2);  //have to invert the y axis 
		
		//values.length <= chart_available_width
		int pointINDX = 0;
		for(int i =0;i<values.length;i++){
			points[pointINDX++] = leftOffset+i;			//each value = 1px
			points[pointINDX++] = (float) (values[i] * yScale);
		}
		drawPoints(ctx, points);
		//ctx.restore();
	}
}
