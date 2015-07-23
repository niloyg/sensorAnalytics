package learning.age.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class LookupFGwrtMG {

	public static final double exclusionThreshold = 0.15;
	public static final double mode00 = 0.15;
	public static final double modeGY = 0.18;
	public static final double modeYR = 0.22;
	public static final double mode99 = 0.75;

	public static String[] calculateCoditionalMode(List<Double> lst) {
		
		double mode = 0.0;
		String warning = new String();
		String[] results = new String[2];
		
		if (lst.size() == 0) {
			mode = Double.MAX_VALUE;
			warning = warning + "WARNING: The list size is zero";
			results[0]=(new Double(mode)).toString();
			results[1]=warning;
			return results;
		}
		
		double avg = 0;
		for (int i=0;i<lst.size();i++) {
			double element = ((Double)lst.get(i)).doubleValue();
			avg = avg + element;
			warning=warning+element +"\n";
		}
		avg = avg / lst.size();
		warning = warning + "Avg is "+avg +"\n";
		
		List<Double> meanLst = new ArrayList<Double> ();
		for (int i=0;i<lst.size();i++) {
			 double currVal = ((Double)lst.get(i)).doubleValue();
			 double _currVal = currVal - avg;
			 double _modOfCurrVal = Math.sqrt(_currVal*_currVal);
			 if (_modOfCurrVal > exclusionThreshold) meanLst.add(new Double(_modOfCurrVal));
		}
		Collections.sort(meanLst);
		warning = warning + "modeOnMG-->The List size is "+meanLst.size() +", max & min are "+meanLst.get(0)+" "+meanLst.get(meanLst.size()-1)+"\n";
		
		mode = Stat.mode(meanLst, 0.001);
		
		if (mode <0) mode = -mode;
		warning = warning + "modeOnMG-->The calculated mode is: "+ mode+"\n";
		
		results[0]= (new Double(mode)).toString();
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
        //System.out.println("size of the hm is: "+hm.size());

//    	double mode = LookupFGwrtMG.calculateCoditionalMode(lst);
//    	String[] results = getFgIndicator(mode);
    	
    	String results[] = LookupFGwrtMG.LookupFG(lst);
    	System.out.println("mode is "+results[0]);
    	System.out.println("Warning is "+results[1]);
    	return results[0];
	}
	
	public static String[] LookupFG(List lst){
		String[] state = LookupFGwrtMG.calculateCoditionalMode(lst);
    	String[] results = getFgIndicator(state);
    	return results;
	}
	
	private static String[] getFgIndicator(String[] state) {
		double FG = 0.0;
		String[] results = new String[2];
		double mode = (new Double(state[0])).doubleValue();
		String warning = state[1];
		
        if (mode >= mode99) {FG = 1.0;}
        else 
        if (mode >= modeYR) {FG = 0.8 + (mode - modeYR)/(mode99 - modeYR)*(1.0  - 0.8);}
        else 
        if (mode >= modeGY) {FG = 0.6 + (mode - modeGY)/(modeYR  - modeGY)*(0.8 - 0.6);}
        else 
        if (mode >= mode00) {FG = 0.0 + (mode - mode00)/(modeGY  - mode00)*(0.6 - 0.0);}
        else FG = 0.0;
        results[0] = (new Double(FG)).toString();
        warning=warning+"FG value is: "+results[0]+"\n#############\n";
        results[1] = warning;
		return results;
	}

	public static void main(String[] args) throws IOException{
		String mode ="0";
		mode = readFromFileAndGetMode("../Utilities/MG/test11");//0.104
		System.out.println("Abusive for PR=-6\"Hg is "+mode);
		System.out.println("###########################");		
//		mode = readFromFileAndGetMode("../Utilities/MG/06_11/vacPres_08");//0.627
//		System.out.println("Abusive FG for PR=-8 PSI is "+mode);
//		System.out.println("###########################");
//		mode = readFromFileAndGetMode("../Utilities/MG/06_11/vacPres_10");//0.627
//		System.out.println("Abusive FG for PR=-10 PSI is "+mode);
//		System.out.println("###########################");
	}
}