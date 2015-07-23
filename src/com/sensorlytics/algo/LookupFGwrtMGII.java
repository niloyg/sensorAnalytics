package com.sensorlytics.algo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import learning.age.utility.LinearBestFit2D;



public class LookupFGwrtMGII {

	//public static final double exclusionThreshold = 0.15;	
	public static final double percentile = 0.95;	

	public static String[] calculate95percentile(List<Double> lst) {
		
		double proxyXbar = 0.0;
		String warning = new String();
		String[] results = new String[2];
		
		if (lst.size() == 0) {
			proxyXbar = Double.MAX_VALUE;
			warning = warning + "WARNING: The list size is zero";
			results[0]=(new Double(proxyXbar)).toString();
			results[1]=warning;
			return results;
		}
		
		double avg = 0;
		for (int i=0;i<lst.size();i++) {
			double element = ((Double)lst.get(i)).doubleValue();
			avg = avg + element;
			//warning=warning+element +"\n";
		}
		avg = avg / lst.size();
		warning = warning + "Avg is "+avg +"\n";
		
		List<Double> meanLst = new ArrayList<Double> ();
		for (int i=0;i<lst.size();i++) {
			 double currVal = ((Double)lst.get(i)).doubleValue() - avg;
			 if (currVal < 0) currVal = - currVal;
			 meanLst.add(currVal);
		}
		Collections.sort(meanLst);
		warning = warning + "95%OnMGx-->The List size is "+meanLst.size() +", max & min are "+meanLst.get(0)+" "+meanLst.get(lst.size()-1)+"\n";
		
		double fIndex = percentile*meanLst.size();
		double pIndex = Math.floor(fIndex);
		int iIndex = new Double(pIndex).intValue();
		
		proxyXbar = (Double) meanLst.get(iIndex).doubleValue(); 
		warning = warning + "95%OnMGx-->The calculated 95percentile is: "+ proxyXbar+"\n";
		
		results[0]= (new Double(proxyXbar)).toString();
		results[1]= warning;
		return results;
	}

	public static String readFromFileAndGetMode(String iFile) throws IOException {
		//Read from CSV & form lst
		//call conditional mode method
    	List<Double> lst = new ArrayList<Double> ();
        BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(iFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*** In order to ignore the header row ****/
        String line = br.readLine();	
        /*** In order to ignore the header row ****/
        
        line = br.readLine();

         
        while(line!=null) {
        	Double d = new Double(line);lst.add(d);  
	     	line = br.readLine();
        }
        br.close();
    	
        HashMap hmS = CSV2JavaDS.csvToStringStringHashMap("../Utilities/22_06/pbByNN/cleanSTATE");

        String[] results = LookupFGwrtMGII.getFuelGaugeValue(lst, hmS);
    	System.out.println("Pressure is "+results[0]);
    	System.out.println("Warning is "+results[1]);
    	return results[0];
	}
	
	public static String[] getFuelGaugeValue(List<Double> lst, HashMap<String, String> hmS) {
        double[] processData = ProcessEstimator.getModeVacAnBrk(hmS);
        double processRatio = processData[0]/processData[1];
        String results[] = LookupFGwrtMGII.LookupFG(lst, processRatio);		
        return results;
	}

	public static String[] LookupFG(List lst, double process){
		String[] stat  = LookupFGwrtMGII.calculate95percentile(lst);
    	String[] results = getPressure(stat, process);
    	return results;
	}	
	
	public static String[] LookupFG(List lst, int process){
		String[] stat  = LookupFGwrtMGII.calculate95percentile(lst);
    	String[] results = getPressure(stat, process);
    	return results;
	}
	
	private static String[] getPressure(String[] state, double process) {
		double FG_pressure = 0.0;
		String[] results = new String[2];
		double percentile95 = (new Double(state[0])).doubleValue();
		String warning = state[1];
		
		LinearBestFit2D ldf2D_abusive = LinearBestFit2D.createLinearBestFit2D("ABUSIVE");
		
		FG_pressure = ldf2D_abusive.getXwhileGivenYandZ(percentile95, process);
		if (FG_pressure > 1.0 ) FG_pressure = 0.99;
        results[0] = (new Double(FG_pressure)).toString();
        warning=warning+"FG value is: "+results[0]+"\n#############\n";
        results[1] = warning;
		return results;
	}
	
	private static String[] getPressure(String[] state, int process) {
		double FG_pressure = 0.0;
		String[] results = new String[2];
		double percentile95 = (new Double(state[0])).doubleValue();
		String warning = state[1];
		
		LinearBestFit2D ldf2D_abusive = LinearBestFit2D.createLinearBestFit2D("ABUSIVE");
		
		FG_pressure = ldf2D_abusive.getXwhileGivenYandZ(percentile95, process);
        results[0] = (new Double(FG_pressure)).toString();
        warning=warning+"FG value is: "+results[0]+"\n#############\n";
        results[1] = warning;
		return results;
	}

	public static void main(String[] args) throws IOException{
		double P95tile =0.0;
		P95tile = new Double(readFromFileAndGetMode("../Utilities/MG/02_07/06_CONT_x")).doubleValue();
		System.out.println("Abusive for PR=-6\"Hg is "+P95tile);
		System.out.println("$4059############ MG/02_07/06_CONT_x ### Estimated Pressure="+String.format("%.1f", P95tile)+" ############");		
		P95tile = new Double(readFromFileAndGetMode("../Utilities/MG/02_07/10_CONT_x")).doubleValue();
		System.out.println("Abusive for PR=-10\"Hg is "+P95tile);
		System.out.println("$4059############ MG/02_07/10_CONT_x ### Estimated Pressure="+String.format("%.1f", P95tile)+" ############");		
		P95tile = new Double(readFromFileAndGetMode("../Utilities/MG/02_07/12_CONT_x")).doubleValue();
		System.out.println("Abusive for PR=-12\"Hg is "+P95tile);
		System.out.println("$4059############ MG/02_07/12_CONT_x ### Estimated Pressure="+String.format("%.1f", P95tile)+" ############");		
//		mode = readFromFileAndGetMode("../Utilities/MG/06_11/vacPres_08");//0.627
//		System.out.println("Abusive FG for PR=-8 PSI is "+mode);
//		System.out.println("###########################");
//		mode = readFromFileAndGetMode("../Utilities/MG/06_11/vacPres_10");//0.627
//		System.out.println("Abusive FG for PR=-10 PSI is "+mode);
//		System.out.println("###########################");
	}
}