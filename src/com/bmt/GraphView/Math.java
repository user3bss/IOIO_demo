package com.bmt.GraphView;

public class Math {
	public float min(float[] values){
		float min = 100000000;
		for(int i=0;i<values.length;i++){
			if(values[i] < min){
				min = values[i];
			}
		}
		return min;
	}
	public float max(float[] values){
		float max = -100000000;
		for(int i=0;i<values.length;i++){
			if(values[i] > max){
				max = values[i];
			}
		}
		return max;
	}
	
}
