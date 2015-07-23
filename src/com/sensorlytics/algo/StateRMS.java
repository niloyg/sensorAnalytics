package com.sensorlytics.algo; //change as per ur requirement

import java.util.HashMap;

public class StateRMS {
	private static long counter =0;public static void resetCounter(){StateRMS.counter=0;}
	public String state;public String getState(){return this.state;}
	public String RMS;
	public long sno;
	public String ts;
	
	public StateRMS(String ts, String RMS, String state){
		this.sno = ++counter;
		this.state = state;
		this.ts = ts;
		this.RMS = RMS;
		//System.out.println(this.toString());
	}
	
	public String toString(){
		return ("ts="+this.ts+" Sno="+this.sno + " RMS="+this.RMS+" state="+this.state);
	}
	
	/*public static void populateStateAndRMS_Data(HashMap hmR, HashMap hmS){
		hmR.put("2015-05-29T12:00:00.001-0400", "0.693");
		hmR.put("2015-05-29T12:00:00.003-0400", "0.793");
		
		hmS.put("2015-05-29T12:00:00.001-0400", "1.0");
		hmS.put("2015-05-29T12:00:00.002-0400", "1.0");
	}*/
}