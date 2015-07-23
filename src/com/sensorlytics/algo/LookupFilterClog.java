package com.sensorlytics.algo; //Change as per your configurations

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import learning.age.utility.Stat;

public class LookupFilterClog {
	static final float decayPercent = 0.8f;
	static final int popuSize4Median = 19;
	static final int minDecayCount = 10;
	static final String simpleDateFormat = "yyyy-mm-dd'T'HH:mm:ss.SSSZ";
	
//	static final int milli_20  = 1450;//1450
//	static final int milli_GY  = 5300;//4300
//	static final int milli_YR  = 8500;
//	static final int milli_100 = 12000;
//	
//	static final float FG_YR = 0.80f;
//	static final float FG_GY = 0.60f;
	
	public static String[] getClogLevel (HashMap<String,String> hmR, HashMap<String,String> hmS) throws Exception{
		String warning = "";
		String stateWithTS="TimeStamp,State\n";
		String rmsWithTS="TimeStamp,RMS\n";
		
		StateRMS.resetCounter();
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		warning = warning + "Size of hmR&S are "+hmR.size()+" "+hmS.size() + "\n";
		HashMap<Long, StateRMS> hm = LookupFilterClog.formCombinedHashMap(hmR, hmS);
		warning = warning + "Size of hm is "+hm.size()+"\n";
       
		Iterator itr=null;
		
		Set keyR = hmR.keySet();
		List<String> keyListR = new ArrayList<String>(keyR);
		Collections.sort(keyListR);
		itr = keyListR.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			rmsWithTS = rmsWithTS + key  + "," + hmR.get(key) + "\n";
		}
		
		Set keyS = hmS.keySet();
		List<String> keyListS = new ArrayList<String>(keyS);
		Collections.sort(keyListS);
		itr = keyListS.iterator();		
		while (itr.hasNext()) {
			String key = (String) itr.next();
			stateWithTS = stateWithTS + key + "," + hmS.get(key)+ "\n";
		}

		List<Long> decayTime;
		Object[] deCayDurationResults = new Object[2];
		Object[] args = new Object[3];
		args[0]=hm;
		args[1]=warning;
		
		String[] results = new String[10];
		try {
			deCayDurationResults = LookupFilterClog.calculateRMS_DecayDurations(args);
			decayTime = (List<Long>) deCayDurationResults[0];
			warning = (String) deCayDurationResults[1];
			String stateAndRMS = (String) deCayDurationResults[2];
			for (int i=0;i<decayTime.size();i++) {
				warning = warning + "The %decayTime are: " + decayTime.get(i)+"\n";
			}
			
			//args=null;
			args[0]=decayTime;
			args[1]=warning;
			results=LookupFilterClog.getFuelGaugeFromLookup(args);
			
			results[2]=rmsWithTS;
			results[3]=stateWithTS;
			results[4]=stateAndRMS;
			
			results[1] = results[1] + " length of RMS & state logs are " + rmsWithTS.length() + " AND "+ stateWithTS.length() ;						
			
			//System.out.println("Gauge Value is "+ results[0]);
			//System.out.println("\n\nWarning Value is\n "+results[1]);
			//System.out.println("\n\nState Values is\n "+results[2]);
			//System.out.println("\n\n"+gaugeValue[2].substring(1, 500));
			//System.out.println("\n\n"+gaugeValue[3].substring(1, 500));
			
			File file = new File("filterAlgo.log");
		    file.createNewFile();
		    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    	   writer.write(results[4]);
           writer.newLine();
	       writer.flush();
	       writer.close();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		StateRMS.resetCounter();
		return results;
	}

	static String[] getFuelGaugeFromLookup(Object[] args) {
		// TODO Auto-generated method stub
		String[] str = new String[5];
		double FG=0.50;
		List<Long> decayTime = (List<Long>) args[0];
		String warning = (String) args[1];
		
		float proxyDecayTime = new Double(LookupFilterClog.getProxyDecayTime(decayTime)).floatValue();
		warning = warning + "Size of the list is: "+decayTime.size() + " and proxyDecayTime is "+proxyDecayTime+"\n";
		
		double[] dLookUp = new double[6];
		dLookUp[0] = 0;
		dLookUp[1] = 700;
		dLookUp[2] = 1000;
		dLookUp[3] = 2000;
		dLookUp[4] = 9000;
		dLookUp[5] = 20000;		
		HashMap<Double, Double> hm = new HashMap<Double, Double>();
		hm.put(new Double(dLookUp[0]), new Double(0.80));
		hm.put(new Double(dLookUp[1]), new Double(1.00));
		hm.put(new Double(dLookUp[2]), new Double(0.00));
		hm.put(new Double(dLookUp[3]), new Double(0.50));
		hm.put(new Double(dLookUp[4]), new Double(0.80));
		hm.put(new Double(dLookUp[5]), new Double(1.00));
		
		FG = Stat.lookup(dLookUp, proxyDecayTime, hm);
		
		str[0] = (new Float(FG)).toString();
		warning = warning + "but FG value for this call is "+str[0] + " and the proxyDecayTime is "+proxyDecayTime+"\n";
		str[1]= warning;
		return str;
	}	
	
	public static double getProxyDecayTime (List<Long> decayTime){
		double proxyDecayTime = 3000f;
		Long sum = 0L;
		if (decayTime == null) return proxyDecayTime;
		
		List<Double> proxyDouble = new ArrayList<Double>();
		for (int i=0;i < decayTime.size();i++) {proxyDouble.add(decayTime.get(i).doubleValue());} 
		//proxyDecayTime = Stat.mode(proxyDouble, 0.05);
		proxyDecayTime = Stat.median(proxyDouble);
			
			
		if (decayTime.size() < minDecayCount) {return 0.0f;}
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
	

	public static Object[] calculateRMS_DecayDurations(Object[] args) throws Exception{
		HashMap hm = (HashMap) args[0];
		String warning = (String) args[1];
		List<Long> decayDurations = new ArrayList<Long> ();
		
		String stateAndRMS="TimeStamp,Counter,RMS,State\n";

		warning = warning + "Start Time is: "+(StateRMS) hm.get(new Long(1))+"\n";
		warning = warning + "End Time is: "+(StateRMS) hm.get(new Long(hm.size()))+"\n";
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		Date startTS = format.parse(((StateRMS) hm.get(new Long(1))).ts);
		Date stopTS   = format.parse(((StateRMS) hm.get(new Long(hm.size()))).ts);
		double  argumentDuration;
		argumentDuration = (stopTS.getTime() - startTS.getTime())/1000/60;
		warning = warning + "WARNING: The duration of algo argument is: "+argumentDuration +" min\n";
		
		for (int i=1;i<=hm.size();i++){	
			StateRMS srmsTemp = (StateRMS) hm.get(new Long(i));
			if (srmsTemp != null) {
				stateAndRMS = stateAndRMS+ srmsTemp.toString()+"\n";

				if ((i/1000)*1000 == i) System.out.println("CHECK-->"+srmsTemp.toString());
			}
			else {System.out.println("srmsTemp is null");}
			
			Double tempState = new Double(srmsTemp.getState());
			if( tempState.equals(new Double("0.0"))) {
				warning = warning + "BRK FND @" + i +"\n";
				
				//Calculate Vac median & decay milestone
				List<Float> median = new ArrayList<Float> ();
				while (i<=popuSize4Median) i++;
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
						warning = warning + "RMS never decayed till "+i+"\n";
						break;
					}
					i++;
				}
				if (flagFreshLookout) continue;
				
				//Calculate delta in time for decayDuration
				String decayDuration = currTS +" - "+ decayBeginTS;
				
				format = new SimpleDateFormat(simpleDateFormat);
				Date strToDateBegin = null;
				Date strToDateMilestone = null; 
				try {
					//System.out.println("TSs to subtract are "+currTS+ " "+ decayBeginTS);
					strToDateMilestone = format.parse(currTS);
					strToDateBegin   = format.parse(decayBeginTS);
					long decayDurationInMilli = strToDateMilestone.getTime() - strToDateBegin.getTime();
					warning = warning + "decay duration is: "+decayDurationInMilli+"\n";
					if (decayDurationInMilli < 20000 &&  decayDurationInMilli > 10) 
						decayDurations.add(decayDurationInMilli);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					warning = warning + "\n####Data Issue##### - Time Stamp in wrong format: "+ decayDuration+"\n\n";
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
		Object[] results = new Object[3];results[0]=decayDurations;results[1]=warning;results[2]=stateAndRMS;
		return results;
	}
}