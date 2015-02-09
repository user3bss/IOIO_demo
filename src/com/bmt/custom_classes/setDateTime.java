package com.bmt.custom_classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.DatePicker;
import android.widget.TimePicker;

public class setDateTime {
	String tag = getClass().getSimpleName();
	public final String[] month_names = {"Jan","Feb","Mar","Apr","May","Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public final String[] shortDayNames = {"SUN", "MON", "TUE", "WEN", "THR", "FRI", "SAT"};
	public final String[] shortMonthNames = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};	
	
	public String getCurrentDate(){
		Calendar c = Calendar.getInstance();
		if(c != null){
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	        return df.format(c.getTime());
		}
		return null;
	}
	public String getCurrentTime(){
		Calendar c = Calendar.getInstance();
		if(c != null){
	        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	        return df.format(c.getTime());
		}
		return null;
	}
	public String getCurrentDateTime(){
		String date = getCurrentDate();
		String time = getCurrentTime();
		if(date != null & time != null){
			return date + ' ' + time;
		}
		return null;
	}
	
	public setDateTime(){
		
	}
	public String getDate(DatePicker d){
		int dom = d.getDayOfMonth();
		int year = d.getYear();
		int month = d.getMonth();
		
		//String date = year + " - " + month_names[month] + " - " + dom;
		String date = year + "-";
		month += 1;
		if(month < 10)
			date += "0" + month;
		else
			date += month;
		date += "-";
		if(dom < 10)
			date += "0"+dom;
		else
			date += dom;
		//Log.i(tag, "Date: " + date);
		return date;		
	}
	public String getTime(TimePicker t){
		int min = t.getCurrentMinute();
		int hour = t.getCurrentHour();	
		/*if(hour > 12){
			hour -= 12;
			isPM = true;
		} else { 
			isPM = false;
		}*/
		
		String time = "";
		if(hour < 10)
			time += "0" + hour + ":";
		else
			time += hour + ":";
		if(min < 10)
			time += "0"+min;
		else
			time += min;
		time += ":00";	

		//Log.i(tag, "Time: " + time);		
		return time;
	}
	public void setTime(TimePicker t, int h, int m){
		t.setCurrentHour(h);
		t.setCurrentMinute(m);
	}
	
	public void setDate(DatePicker d, int year, int month, int dayOfMonth){
		d.updateDate(year, month, dayOfMonth);
	}
	
	public void setDateFromJSON(JSONObject startTimeObj, DatePicker d){
		if(startTimeObj.has("month") && startTimeObj.has("day") && startTimeObj.has("year")){			
			try {
				String m = startTimeObj.getString("month");
				//Log.i(tag, "month : " + m + ", int: "+month);
				if(!m.contentEquals("") && !startTimeObj.getString("year").contentEquals("") && !startTimeObj.getString("day").contentEquals("")){
					int month = 0;
					for(; month < month_names.length;month++){
						if(m.contentEquals(month_names[month].toUpperCase())){
							break;
						}
					}					
					int year = startTimeObj.getInt("year");
					int day = startTimeObj.getInt("day");
					setDate(d, year, month, day);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
	}
	public void setTimeFromJson(JSONObject startTimeObj, TimePicker tp){
		try{
			if(startTimeObj.has("hour") && startTimeObj.has("minute") && startTimeObj.has("timemarker")){
				if(!startTimeObj.getString("hour").contentEquals("") && !startTimeObj.getString("minute").contentEquals("")){
					int h = startTimeObj.getInt("hour");					
					int m = startTimeObj.getInt("minute");					
					String tm = startTimeObj.getString("timemarker");
					if(tm.contentEquals("PM"))
						h += 12;
					setTime(tp, h, m);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}			
	}
	public void setTimeFromString(String timeString, TimePicker tp){
		if(!timeString.contentEquals("") && !timeString.toLowerCase().contentEquals("null")){
			try {
				//SimpleDateFormat sf = new SimpleDateFormat("HH:mm");				 
			    String[] splitStrings = timeString.split(":");

			    int h = Integer.parseInt(splitStrings[0]);
			    int m = Integer.parseInt(splitStrings[1]);
			    //int s = Integer.parseInt(splitStrings[2]);				
			    setTime(tp, h, m);			    
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
}
