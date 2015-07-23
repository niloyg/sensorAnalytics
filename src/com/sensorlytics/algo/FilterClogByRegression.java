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

import learning.age.utility.LinearBestFit;
import learning.age.utility.Regression;
import learning.age.utility.Stat;

public class FilterClogByRegression {
	static final String simpleDateFormat = "yyyy-mm-dd'T'HH:mm:ss.SSSZ";
	static final double[] x= { 0.20,     0.45,     0.65,     0.90};
	static final double[] y= {-2.00E-3, -3.00E-3, -4.00E-3, -6.00E-3};
	
	static final int MAXN = 99999;
	
	public static String[] getClogLevel (HashMap<String,String> hmR, HashMap<String,String> hmS) throws Exception {
											String warning = "";
											String stateWithTS="TimeStamp,State\n";
											String rmsWithTS="TimeStamp,RMS\n";
		
		StateRMS.resetCounter();
		SimpleDateFormat sdf = new SimpleDateFormat();
		
											warning = warning + "Size of hmR&S are "+hmR.size()+" "+hmS.size() + "\n";
		HashMap<Long, StateRMS> hm = FilterClogByRegression.formCombinedHashMap(hmR, hmS);
											warning = warning + "Size of hm is "+hm.size()+"\n";
											//System.out.println(warning);
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

		String[] results = estimateDecayConst (hm, warning);
		
		String[] gaugeResults = FilterClogByRegression.getFuelGaugeIndicator(results);
		gaugeResults[2] = rmsWithTS;
		gaugeResults[3] = stateWithTS;
		gaugeResults[4] = results[0];
		
		return gaugeResults;
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
	
	public static String[] estimateDecayConst (HashMap<Long,StateRMS> hm, String warning) throws Exception {
		//TODO: iterate hm
		List<Double> decayConstants = new ArrayList<Double>();
		SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
		Date decayStartsAt = null;
		List<Double> lstVacRMS = null;
		double[] rmsBRK = null;
		double[] timeBRK = null;
		int countBRK =0;

		boolean brk=false;
		
		for (int i=1;i<hm.size();i++) {
				StateRMS currSRMS = (StateRMS) hm.get(new Long(i));
				//System.out.println(i);
				String currState = currSRMS.getState();
				if (currState.equals("1.0")) {
					if (brk == true){
						//TODO: form arguments and call Linear Regression Routine
						double[] T = new double[countBRK+1];
						double[] logY = new double[countBRK+1];
						//System.out.println("countBRK="+countBRK);
						for (int j=0;j<=countBRK;j++){
							T[j] = timeBRK[j];
							logY[j] = Math.log(rmsBRK[j]);
						}
						double[] results = Regression.linearRegression(T, logY, countBRK);
//						warning = warning + String.format(" BRK leg={Initial Value=%.2f, DecayConst=%.5f}\n", 
//											Math.exp(results[0]), results[1]);
						
						//TODO: Form the list of decay const.
						decayConstants.add(results[1]);
						//System.out.println("\n");
						countBRK=0;

						
						//TODO: Initialize List to gather VAC RMS
						lstVacRMS = new ArrayList<Double>();
						brk=false;
					}
					else {
						//Nothing to DO: next sentence would keep adding VAC RMS to its List
						if (lstVacRMS == null) lstVacRMS = new ArrayList<Double>();
					}
					lstVacRMS.add((new Double(currSRMS.RMS).doubleValue()));
				}
				if (currSRMS.getState().equals("0.0")) {
					if (brk == false) {
						//New BRK cycle ---> Initialize
						rmsBRK = new double[MAXN];
						timeBRK = new double[MAXN];
						brk = true;
						rmsBRK[countBRK]=(new Double(currSRMS.RMS)).doubleValue();
						decayStartsAt = format.parse(currSRMS.ts);
						timeBRK[countBRK]= 0;
						countBRK=1;
						//System.out.println("CP1");
						//Terminate lstVacRMS
						if (lstVacRMS != null && lstVacRMS.size() !=0) {
//							String print = String.format("VAC leg={AvgRMS=%.2f, ModeRMS=%.2f },", 
//									Stat.average(lstVacRMS), Stat.mode(lstVacRMS));
//							warning = warning +print;
						}
					}
					else {
						rmsBRK[countBRK]=(new Double(currSRMS.RMS)).doubleValue();
						Date currTime = format.parse(currSRMS.ts);
						timeBRK[countBRK]= (currTime.getTime() - decayStartsAt.getTime())/1000;
						countBRK++;
					}
				}
		}
		
		String[] results = new String[2];
		
		warning = warning + "\n";
		warning = warning + "Number of times decay Constant calculated is "+decayConstants.size()+"\n";
		List<Double> posDecays = new ArrayList<Double>();
		for (int i=0;i<decayConstants.size();i++) {
			Double dc = decayConstants.get(i);
			if (dc.doubleValue() < 0) {
				posDecays.add(dc);
				warning = warning + String.format("accepted value %2d is: %.4f\n", i, dc.doubleValue());
			}
		}
		warning = warning + "Number of selected decays = "+posDecays.size()+"\n";
		//results[0] = new Double(Stat.mode(posDecays, 2.5/posDecays.size())).toString();//mode
		results[0] = new Double(Stat.median(posDecays)).toString();//Average
		String printLast = String.format(">>>>>>>>>>>>>>> Median Decay constant = %.2E /sec", new Double(results[0]).doubleValue());
		warning = warning +printLast;
		results[1]=warning;
		return results;
	}
	
	static String[] getFuelGaugeIndicator(String[] args) {
		// TODO Auto-generated method stub
		String[] str = new String[5];
		float FG=0.50f;
		double proxyDecayConst = (new Float(args[0])).doubleValue();
		String warning = (String) args[1];
		
		try {
			LinearBestFit lbf = new LinearBestFit(x, y);
			FG = new Double(lbf.getXwhileGivenY(proxyDecayConst)).floatValue();
			if (FG > 1) FG = 0.99f;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	
		str[0] = (new Float(FG)).toString();
		String print = String.format("FG value for this call is %.3f and the proxyDecayConst is %.2E /sec \n",
				new Float(str[0]).floatValue(), proxyDecayConst);
		str[1]= warning;
		return str;
	}
}