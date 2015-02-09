package com.bmt.custom_classes;

public class MathChart {
	public static float min(float[] values){
		float min = 100000000;
		for(int i=0;i<values.length;i++){
			if(values[i] < min){
				min = values[i];
			}
		}
		return min;
	}
	public static float max(float[] values){
		float max = -100000000;
		for(int i=0;i<values.length;i++){
			if(values[i] > max){
				max = values[i];
			}
		}
		return max;
	}
	
	public static float[] sinData(int length, double max, double d){
		float[] f = new float[length];
		for(int i=0;i<length;i++){
			f[i] = (float) ((max/2 * Math.sin(i*0.05) + max/2) + d); //.017 rad = 1 deg
		}
		return f;
	}
	public static float[] sqData(int length, double d, double e){
		float[] f = new float[length];
		for(int i=0;i<length;i++){
			f[i] = (float) ((Math.signum( Math.sin(2*Math.PI*i*0.005))+d/2) + e);
		}
		return f;
	}
}
