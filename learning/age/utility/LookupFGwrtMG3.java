package learning.age.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



public class LookupFGwrtMG3 {

	public static final double exclusionThreshold = 0.1;
	public static final double mode00 = 0.10;
	public static final double modeGY = 0.68;
	public static final double modeYR = 0.70;
	public static final double mode99 = 0.75;

	public static String[] calculateCoditionalMode(List<Double[]> lst) {
		
		double mode = 0.0;
		String warning = "";
		String[] results = new String[2];
		if (lst.size() == 0) {
			mode = Double.MAX_VALUE;
			warning = warning + "WARNING: The list size is zero";
			results[0] =new String( new Double(mode).toString());
			results[1] = warning;
			return results;
		}
		
		double avgX=0.0,avgY=0.0,avgZ=0.0;
		for (int i=0;i<lst.size();i++) {
			double elementX = ((Double[])lst.get(i))[0].doubleValue();avgX=avgX+elementX;
			double elementY = ((Double[])lst.get(i))[1].doubleValue();avgX=avgY+elementY;
			double elementZ = ((Double[])lst.get(i))[2].doubleValue();avgX=avgZ+elementZ;
		}
		avgX=avgX/lst.size();
		avgY=avgY/lst.size();
		avgZ=avgZ/lst.size();

		List<Double> lstRMS = new ArrayList<Double>();
		for (int i=0;i<lst.size();i++) {
			double _x = ((Double[])lst.get(i))[0].doubleValue() - avgX;
			double _y = ((Double[])lst.get(i))[1].doubleValue() - avgY;
			double _z = ((Double[])lst.get(i))[2].doubleValue() - avgZ;
			double RMS = Math.sqrt(_x*_x + _y*_y + _z*_z);
			lstRMS.add(RMS);
		}
		
		List<Double> selectedRMS = new ArrayList<Double> ();
		for (int i=0;i<lstRMS.size();i++) {
			 double currRMS = ((Double)lstRMS.get(i)).doubleValue();
			 double modeOfCurrRMS = Math.sqrt(currRMS*currRMS);
			 if (modeOfCurrRMS > exclusionThreshold) selectedRMS.add(new Double(currRMS));
		}
		Collections.sort(selectedRMS);

		mode = Stat.mode(selectedRMS, 0.001);
		
		if (mode <0) mode = -mode;
		warning = warning + "modeOnMG-->The List size of input data and data after exclusion are "+selectedRMS.size() +" and " +lstRMS.size()+". Min & Max are "+
				String.format("%.2f", selectedRMS.get(0))+" "+String.format("%.2f",selectedRMS.get(selectedRMS.size()-1))+
				". Mode value is: "+String.format("%.2f", mode)+"\n";
		results[0]= new String((new Double(mode).toString()));
		results[1]= warning;
		return results;
	}

	public static String readFromFileAndGetMode(String iFile) throws IOException {
		//Read from CSV & form lst
		//call conditional mode method
    	List<Double[]> lst = new ArrayList<Double[]> ();
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
        	String[] values = line.split(",");
        	Double[] d = new Double[3];
        	if (values.length == 3 ) {
	        	for (int i=0;i<d.length;i++) d[i] = new Double(values[i]);
//	        	for (int i=0;i<d.length;i++) System.out.print(new Double(values[i]) + ", ");
//	        	System.out.println("\n");
	        	lst.add(d);
        	}
	     	line = br.readLine();
        }
        br.close();
        //System.out.println("size of the hm is: "+hm.size());

    	String results[] = LookupFGwrtMG3.LookupFG(lst);
    	//System.out.println("modeOnMG-->Alpha FG is "+results[0]);
    	System.out.println("Warning is "+results[1]);
    	return results[0];
	}
	
	public static String[] LookupFG(List<Double[]> lst){
		String[] results = LookupFGwrtMG3.calculateCoditionalMode(lst);
		//System.out.println("modeOnMG-->Mode is "+results[0]);
    	String[] results2 = getFgIndicator(results);
    	return results2;
	}
	
	private static String[] getFgIndicator(String[] args) {
		double FG = 0.0;
		String[] results = new String[2];
//       if (mode >= mode99) {FG = 1.0;}
//        else 
//        if (mode >= modeYR) {FG = 0.8 + (mode - modeYR)/(mode99 - modeYR)*(1.0  - 0.8);}
//        else 
//        if (mode >= modeGY) {FG = 0.6 + (mode - modeGY)/(modeYR  - modeGY)*(0.8 - 0.6);}
//        else 
//        if (mode >= mode00) {FG = 0.0 + (mode - mode00)/(modeGY  - mode00)*(0.6 - 0.0);}
//        else FG = 0.0;
		
/*		if (mode > 99) FG = 0.95 + mode/2000;
		if (mode <= 99 && mode > 9) FG = 0.8 + mode/500;
		if (mode <= 9  && mode > 0.9) FG = 0.7 + mode/30;
*/        
		results[0] = args[0];
        results[1] = args[1];
		return results;
	}

	public static void main(String[] args) throws IOException{
		String mode ="0";
		if (args.length > 0) {
			mode = readFromFileAndGetMode(args[0]);
		}
		else {
			String files[] = new String[27];
			String dir = "../Utilities/MG/06_26/";
			files[00] = "0830_0840_6";
			files[01] = "0840_0850_6";
			files[02] = "0900_0910_6_cont_vac";
			files[03] = "0830_0840_6";
			files[04] = "0840_0850_6";
			files[05] = "0900_0910_6_cont_vac";
			files[06] = "0920_0930_7";
			files[07] = "1100_1110_7_cont_vac";
			files[8] =  "1110_1120_7_cont_vac";
			files[9] =  "1120_1130_8";
			files[10] = "1130_1140_8";
			files[11] = "1150_1200_8_cont_vac";
			files[12] = "1250_1300_9";
			files[13] = "1300_1310_9";
			files[14] = "1310_1320_9";
			files[15] = "1320_1330_9_cont_vac";
			files[16] = "1330_1340_9_cont_vac";
			files[17] = "1350_1400_10";
			files[18] = "1410_1420_10_cont_vac";
			files[19] = "1430_1440_11";
			files[20] = "1440_1450_11";
			files[21] = "1450_1500_11_cont_vac";
			files[22] = "1500_1510_11_cont_vac";
			files[23] = "1520_1530_12";
			files[24] = "1530_1540_12";
			files[25] = "1540_1550_12_cont_vac";
			files[26] = "1550_1600_12_cont_vac";		
			
			
			
			
			
			System.out.println("####### STARTED ############# No of Files is ####### "+ files.length);
			for (int i=0;i<files.length;i++) {
				System.out.println("####### " + files[i] +" ####################");
				mode = readFromFileAndGetMode(dir+files[i]);
			}
			System.out.println("####### ENDED ####################");
		}
	}
}