package com.sensorlytics.algo; //Change as per your configurations

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class LookupFilterClog_1stJun {
	static final float decayPercent = 0.8f;
	static final int popuSize4Median = 9;
	static final String simpleDateFormat = "yyyy-mm-dd'T'HH:mm:ss.SSSZ";
	
	static final int milli_20  = 1450;
	static final int milli_GY  = 4300;
	static final int milli_YR  = 5800;
	static final int milli_100 = 7000;
	
	static final float FG_YR = 0.80f;
	static final float FG_GY = 0.60f;
	
/*	public static void main(String[] args) {
		//TODO: JSON to HASHMAP conversion
		//TODO: Handling bulk data
		//TODO: MAp gauge
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		HashMap hmR = new HashMap();
		HashMap hmS = new HashMap();
		
		long beginTime = System.nanoTime();
		StateRMS.populateStateAndRMS_Data(hmR, hmS);
		float FG = LookupFilterClog_1stJun.getClogLevel(hmR, hmS);
		long endTime = System.nanoTime();
		System.out.println("Execution time is: " + (endTime - beginTime)/1000000 + " milli sec");
		System.out.println("FG: "+FG+"\n\n\n");		

		StateRMS.populateStateAndRMS_Data(hmR, hmS);
		HashMap<Long, StateRMS> hm = LookupFilterClog_1stJun.formCombinedHashMap(hmR, hmS);
		System.out.println("Size of hm is "+hm.size());
		List<Long> decayTime = LookupFilterClog_1stJun.calculateRMS_DecayDurations(hm);
		for (int i=0;i<decayTime.size();i++) {
			System.out.println("The values are: " + decayTime.get(i));
		}
		float gaugeValue = LookupFilterClog_1stJun.getFuelGaugeIndicator(decayTime);
		System.out.println("Gauge Value is "+gaugeValue);

	}*/
	
	public static float getClogLevel(HashMap hmR, HashMap hmS) {
		//TODO: JSON to HASHMAP conversion
		//TODO: Handling bulk data
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		System.out.println("Size of hmR&S are "+hmR.size()+" "+hmS.size());
		HashMap<Long, StateRMS> hm = LookupFilterClog_1stJun.formCombinedHashMap(hmR, hmS);
		System.out.println("Size of hm is "+hm.size());
		List<Long> decayTime = LookupFilterClog_1stJun.calculateRMS_DecayDurations(hm);
		System.out.println("CP1");
		for (int i=0;i<decayTime.size();i++) {
			System.out.println("The values are: " + decayTime.get(i));
		}
		
		float gaugeValue = LookupFilterClog_1stJun.getFuelGaugeIndicator(decayTime);
		System.out.println("Gauge Value is "+gaugeValue);
		return gaugeValue;
	}	
	
	static float getFuelGaugeIndicator(List<Long> decayTime) {
		// TODO Auto-generated method stub
		float FG=0.59f;
		float proxyDecayTime =LookupFilterClog_1stJun.getProxyDecayTime(decayTime);
		if (proxyDecayTime > milli_100) {FG = 1.0f;}
		else 
		if (proxyDecayTime > milli_YR) {FG = FG_YR +(proxyDecayTime - milli_YR)/(milli_100 - milli_YR)*(1.0f - FG_YR);}
		else
		if (proxyDecayTime > milli_GY) {FG = FG_GY +(proxyDecayTime - milli_GY)/(milli_YR - milli_GY)*(FG_YR - FG_GY);}
		else
		if (proxyDecayTime > milli_20) {FG = 0.2f  +(proxyDecayTime - milli_20)/(milli_GY - milli_20)*(FG_GY - 0.2f);}
		else 
		if (proxyDecayTime > 0.0f) {FG = 0.2f;}
		else FG = 0.0f;
		return FG;
	}
	
	public static float getProxyDecayTime (List<Long> decayTime){
		float proxyDecayTime = 3000f;
		Long sum = 0L;
		if (decayTime == null) return proxyDecayTime;
		Iterator itr = decayTime.iterator();
		while(itr.hasNext()) {
				sum = sum + (Long) itr.next();
			}
			proxyDecayTime = sum.floatValue() / decayTime.size();
		if (decayTime.size() < 15) {return 0.0f;}
		else {return proxyDecayTime;}
	}

	public static HashMap<Long, StateRMS> formCombinedHashMap (HashMap<String, String> hmR, HashMap<String, String> hmS) {
		HashMap<Long, StateRMS> hm = new HashMap<Long, StateRMS>();
		
		Set setR = hmR.keySet();
		List<String> keyListR = new ArrayList<String>(setR);
		Collections.sort(keyListR);
		Iterator itr = keyListR.iterator();
		
		while(itr.hasNext()){
			Object key =  itr.next();
			if (hmS.containsKey(key)) {
				StateRMS srms = new StateRMS((String) key, (String) hmR.get(key), (String) hmS.get(key));
				hm.put(new Long(srms.sno), srms);
			}
		}
		return hm;
	}
	

	public static List<Long> calculateRMS_DecayDurations(HashMap hm) {
		List<Long> decayDurations = new ArrayList<Long> ();
		for (int i=1;i<=hm.size();i++){
			StateRMS srmsTemp = (StateRMS) hm.get(new Long(i));
			if (srmsTemp != null) {//System.out.println(srmsTemp.toString());}
			}
			else {System.out.println("srmsTemp is null");}
			String tempState = srmsTemp.getState();
			if( tempState.equals("0.0")) {
				//System.out.println("BRK FND @" + i );
				
				//Calculate Vac median & decay milestone
				List<Float> median = new ArrayList<Float> ();
				for (int j=i-popuSize4Median;j<=i-1;j++) {
					Float temp = new Float(((StateRMS) hm.get(new Long(j))).RMS);
					median.add (temp);
				}
				Collections.sort(median);
				int medianIndex = (popuSize4Median+1)/2;
				Float medianValue = median.get(medianIndex);
				Float decayMilestone = medianValue*decayPercent;
				
				//Note TS for BRK_ST Begin
				String decayBeginTS = ((StateRMS) hm.get(new Long(i-1))).ts;
				
				//Now, iterate BRK_STATE until mile stone reached
				Float currRMS = new Float(((StateRMS) hm.get(new Long(i))).RMS);
				Float currState = new Float(((StateRMS) hm.get(new Long(i))).state); 
				String currTS = ((StateRMS) hm.get(new Long(i))).ts;
				boolean flagFreshLookout = false;
				while (  currRMS > decayMilestone   ) {
					currRMS = new Float(((StateRMS) hm.get(new Long(i))).RMS);
					currState = new Float(((StateRMS) hm.get(new Long(i))).state);
					currTS = ((StateRMS) hm.get(new Long(i))).ts;
					if (currState.equals(new Float(1.0))) {
						flagFreshLookout = true;
						System.out.println("RMS never decayed til "+i);
						break;
					}
					i++;
				}
				if (flagFreshLookout) continue;
				
				//Calculate delta in time for decayDuration
				String decayDuration = currTS +" - "+ decayBeginTS;
				
				SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
				Date strToDateBegin = null;
				Date strToDateMilestone = null; 
				try {
					//System.out.println("TSs to subtract are "+currTS+ " "+ decayBeginTS);
					strToDateMilestone = format.parse(currTS);
					strToDateBegin   = format.parse(decayBeginTS);
					long decayDurationInMilli = strToDateMilestone.getTime() - strToDateBegin.getTime();
					if (decayDurationInMilli < 20000) decayDurations.add(decayDurationInMilli);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					System.out.println("\n####Data Issue##### - Time Stamp in wrong format: "+ decayDuration+"\n");
				}

				
				//Iterate till next VAC state and let the control go to the begin of the for loop
				currState = new Float(((StateRMS) hm.get(new Long(i))).state);
				Float floatZero = new Float(0.0f);
				while ( 
						currState.equals(floatZero)
					) {
					i++;
					if (i < hm.size()) {
						StateRMS srms = (StateRMS) hm.get(new Long(i));
						String temp = srms.getState();
						currState = new Float(temp);
					}
					else break;
				}
			}			
		}
		hm = null;
		StateRMS.resetCounter();
		return decayDurations;
	}
}