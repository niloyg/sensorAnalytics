package com.sensorlytics.algo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

import learning.age.utility.Stat;

public class ProcessEstimator {
	static final String simpleDateFormat = "yyyy-mm-dd'T'HH:mm:ss.SSSZ";
    
    public static void populateArg(HashMap<String,String> arg){
        
        arg.put("2015-07-01T14:00:00.000-0400","1");
        arg.put("2015-07-01T14:00:10.000-0400","1");
        arg.put("2015-07-01T14:00:20.000-0400","1");
        
        arg.put("2015-07-01T14:00:30.000-0400","0");
        arg.put("2015-07-01T14:00:40.000-0400","0");
        arg.put("2015-07-01T14:00:50.000-0400","0");
        
        arg.put("2015-07-01T14:01:00.000-0400","1");
        arg.put("2015-07-01T14:01:10.000-0400","1");
        arg.put("2015-07-01T14:01:20.000-0400","1");
        
        arg.put("2015-07-01T14:01:30.000-0400","0");
        arg.put("2015-07-01T14:01:40.000-0400","0");
        
        arg.put("2015-07-01T14:01:50.000-0400","1");
        arg.put("2015-07-01T14:02:00.000-0400","1");
        arg.put("2015-07-01T14:02:10.000-0400","1");
        
        arg.put("2015-07-01T14:02:20.000-0400","0");
        arg.put("2015-07-01T14:02:30.000-0400","0");
        arg.put("2015-07-01T14:02:40.000-0400","0");
        
        arg.put("2015-07-01T14:02:50.000-0400","1");
        arg.put("2015-07-01T14:03:00.000-0400","1");
        
        arg.put("2015-07-01T14:03:10.000-0400","0");
        arg.put("2015-07-01T14:03:20.000-0400","0");
        arg.put("2015-07-01T14:03:30.000-0400","0");
        
        arg.put("2015-07-01T14:03:40.000-0400","1");
        arg.put("2015-07-01T14:03:50.000-0400","1");
        arg.put("2015-07-01T14:04:00.000-0400","1");
    }
    public static double calculateStateDuration(String referenceTime, String flipTime, String simpleDateFormat){
    	double durationMilli=0.0;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(simpleDateFormat);
			Date beginTime = dateFormat.parse(referenceTime);
			Date endTime = dateFormat.parse(flipTime);
			durationMilli = (endTime.getTime() -  beginTime.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("referenceTime="+referenceTime+"\nflipTime=     "+flipTime+"\ndurationMilli="+durationMilli);
    	return durationMilli/1000;
    }
    public static List[] organiseStateData (HashMap<String,String> arg){
        List[] results = new List[2];
        System.out.println("arg length is: "+arg.size());
        Set ks = arg.keySet();
        List keyList = new ArrayList(ks);

        boolean estimationStarted=false;
        boolean prevState=false;
        String prevKey="";
        List lstVAC = new ArrayList<Double>();
        List lstBRK = new ArrayList<Double>();
        String referenceTime = null;
        int currCounter=0;

        Collections.sort(keyList);
        Iterator itr = keyList.iterator();        
        while (itr.hasNext()){
            String currKey = (String) itr.next();

            String tempState  = (String) arg.get(currKey);
            //System.out.println("currKey="+currKey+" currState="+tempState);
            boolean currState=false;
            if (tempState.equals("1.0")) currState=true;
            if (tempState.equals("0.0")) currState=false;
            //System.out.println("Boolean state is: "+currState);

            if (!estimationStarted) {
                estimationStarted = true;
                referenceTime = currKey;
                prevState = currState;
                prevKey=currKey;
                currCounter=1;
            }
            if(currState == prevState){
            	prevKey = currKey;
            	prevState=currState;
            	currCounter++;
            }
            else {
                //Move currCounter to currStateList
                String flipTime = prevKey;
                double stateDuration = calculateStateDuration(referenceTime, flipTime, simpleDateFormat);
                if (currState == true)  lstVAC.add(new Double(stateDuration));
                if (currState == false) lstBRK.add(new Double(stateDuration));
                prevState = currState;
                referenceTime=currKey;
                currCounter=1;
            }
        }
        results[0]=lstBRK;results[1]=lstVAC;
        return results;
    }

    public static double[] calculateProcessData(List[] processData) {
        double[] results = new double[3];
        System.out.println("Length of process data is "+processData.length);
        System.out.println("Size of processData[1] is "+processData[1].size());
        System.out.println("Size of processData[0] is "+processData[0].size());
        double[] modeVAC = Stat.modeWithFrequency(processData[1], 0.1);
        double[] modeBRK = Stat.modeWithFrequency(processData[0], 0.1);
        double confidence = (
                modeVAC[1]/processData[1].size() + 
                modeBRK[1]/processData[0].size()
            )/2;
        results[0] = modeVAC[0];
        results[1] = modeBRK[0];
        results[2] = confidence;
        return results;
    }    
    
    public static String[] calculateProcessDataToDisplay(List[] processData) {
        String[] results = new String[2];
        double[] data = calculateProcessData(processData);
        results[0] = String.format("%.0fV_%.0fB",data[0], data[1]);
        results[1] = data[2]+"";
        return results;
    }
    
    public static double[] getModeVacAnBrk(HashMap<String, String> arg) {
        List[] processData = organiseStateData(arg);
        double[] results = calculateProcessData(processData);
        return results;
    }
    
    public static void main(String[] args) {
    	   /*
    	../Utilities/13_7/tmp/ D10_0834_1034_fltr1_20V30B D09_1436_1636_fltr0_20V30B D10_0834_1034_fltr1_20V30B_II D10_0834_1034_fltr1_20V30B_III D10_0834_1034_fltr1_20V30B_IV D10_0834_1034_fltr1_20V30B_V D10_1041_1241_fltr2_20V30B_I D10_1041_1241_fltr2_20V30B_II D10_1041_1241_fltr2_20V30B_III D10_1041_1241_fltr2_20V30B_IV D10_1517_1617_fltr3_20V30B D14_1004_1034_fltr3_V5B50 D14_1120_1150_fltr3_10V50B D14_1156_1226_fltr3_30V30B D14_1230_1300_fltr3_50V20B D14_1304_1334_fltr3_50V10B D14_1343_1543_fltr3_40V20B D10_1041_1241_fltr2_20V30B_V D13_0859_1059_fltr3_20V30B
    	     */
    	for (int i = 1; i < args.length; i++) {
    		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    		System.out.println("File is: " + args[i]);
			HashMap<String, String> arg = CSV2JavaDS
					.csvToStringStringHashMap(args[0]+args[i]);
			double[] results = getModeVacAnBrk(arg);
			System.out.println("Vac Duration is: " + results[0]);
			System.out.println("Brk Duration is: " + results[1]);
			System.out.println("Process confidence is: "
					+ String.format("%.1f",
							new Double(results[2]).doubleValue() * 100) + "%");
			System.out.println("############################");
		}
    }
}