package com.sensorlytics.algo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import com.sensorlytics.algo.LookupFilterClog;

public class testFilterAlgo {
    public static HashMap<Long, StateRMS> formCombinedHM (String iFileName) throws Exception{
    	//Form combined hm
    	HashMap<Long, StateRMS> hm = new HashMap<Long, StateRMS>();
        BufferedReader br = new BufferedReader(new FileReader(iFileName));
        
        /*** In order to ignore the header row ****/
        String line = br.readLine();	
        /*** In order to ignore the header row ****/
        
        line = br.readLine();
        StateRMS.resetCounter();
         
        while(line!=null) {
			 String[] b = line.split(",");
			 //System.out.println("The string is: "+b[0]);
			 if (b.length > 2) {
			 String rms   = b[1];
			 String state = b[2];
			 String ts = b[0];
			 StateRMS srms = new StateRMS(ts, (String) rms, (String) state);
			 hm.put(new Long(srms.sno), srms);     	   
     	   }
     	   line = br.readLine();
        }
        br.close();
        System.out.println("size of the hm is: "+hm.size());

    	return hm;
    }
	
    public static HashMap<String,String> formHM (String iFileName) throws Exception{
    	//Form combined hm
    	HashMap<String, String> hm = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new FileReader(iFileName));
        
        /*** In order to ignore the header row ****/
        String line = br.readLine();	
        /*** In order to ignore the header row ****/
        
        line = br.readLine();
        StateRMS.resetCounter();
         
        while(line!=null) {
			 String[] b = line.split(",");
			 //System.out.println("The string is: "+b[0]);
			 if (b.length > 1) {
				 String ts = b[0];
				 String value   = b[1];
				 hm.put(b[0], b[1]);     	   
	     	 }
	     	 line = br.readLine();
        }
        br.close();
        //System.out.println("size of the hm is: "+hm.size());

    	return hm;
    }
    
    public static void main(String[] args) throws Exception {                    	
    	
    	//run_jun19_part3();//GETTING SHORT VALUE FOR 80%DECAY. NOT SURE IF DATA IS CORRECT.
    	//String[] argR1 = {"../Utilities/19_06/part3/rms/",   "rmsCurve-Fri Jun 19 13_50_31 UTC 2015.csv", "rmsCurve-Fri Jun 19 13_40_36 UTC 2015.csv", "rmsCurve-Fri Jun 19 13_30_35 UTC 2015.csv", "rmsCurve-Fri Jun 19 13_20_34 UTC 2015.csv", "rmsCurve-Fri Jun 19 13_10_35 UTC 2015.csv", "rmsCurve-Fri Jun 19 13_00_36 UTC 2015.csv"};
    	//String[] argS1 = {"../Utilities/19_06/part3/state/", "sqrCurve-Fri Jun 19 13_50_33 UTC 2015.csv", "sqrCurve-Fri Jun 19 13_40_38 UTC 2015.csv", "sqrCurve-Fri Jun 19 13_30_37 UTC 2015.csv", "sqrCurve-Fri Jun 19 13_20_36 UTC 2015.csv", "sqrCurve-Fri Jun 19 13_10_37 UTC 2015.csv", "sqrCurve-Fri Jun 19 13_00_38 UTC 2015.csv"};
    	//runLookupFltrAlgoWith_hmRandhmS(argR1, argS1);
    	
    	//jun22_pbByNN
    	//String[] argR2 = {"../Utilities/22_06/pbByNN/", "cleanRMS", "fltr1RMS"};
    	//String[] argS2 = {"../Utilities/22_06/pbByNN/", "cleanSTATE", "fltr1STATE"};
    	//runLookupFltrAlgoWith_hmRandhmS(argR2, argS2);
    	
//    	//jul01ByNN
//    	String[] argR3 ={"../Utilities/01_07/", "fltr0RMS", "fltr1RMS", "fltr2RMS", "fltr3RMS"};
//    	String[] argS3 ={"../Utilities/01_07/", "fltr0STATE", "fltr1STATE", "fltr2STATE", "fltr3STATE"};
//    	runLookupFltrAlgoWith_hmRandhmS(argR3, argS3);
    	
//    	//jul13
//    	String[] argR4 ={"../Utilities/13_7/rms/",   "D10_0834_1034_fltr1_20V30B", "D09_1436_1636_fltr0_20V30B", "D10_0834_1034_fltr1_20V30B_II", "D10_0834_1034_fltr1_20V30B_III", "D10_0834_1034_fltr1_20V30B_IV", "D10_0834_1034_fltr1_20V30B_V", "D10_1041_1241_fltr2_20V30B_I", "D10_1041_1241_fltr2_20V30B_II", "D10_1041_1241_fltr2_20V30B_III", "D10_1041_1241_fltr2_20V30B_IV", "D10_1517_1617_fltr3_20V30B", "D14_1004_1034_fltr3_V5B50", "D14_1120_1150_fltr3_10V50B", "D14_1156_1226_fltr3_30V30B", "D14_1230_1300_fltr3_50V20B", "D14_1304_1334_fltr3_50V10B", "D14_1343_1543_fltr3_40V20B", "D10_1041_1241_fltr2_20V30B_V", "D13_0859_1059_fltr3_20V30B"};
//    	String[] argS4 ={"../Utilities/13_7/state/", "D10_0834_1034_fltr1_20V30B", "D09_1436_1636_fltr0_20V30B", "D10_0834_1034_fltr1_20V30B_II", "D10_0834_1034_fltr1_20V30B_III", "D10_0834_1034_fltr1_20V30B_IV", "D10_0834_1034_fltr1_20V30B_V", "D10_1041_1241_fltr2_20V30B_I", "D10_1041_1241_fltr2_20V30B_II", "D10_1041_1241_fltr2_20V30B_III", "D10_1041_1241_fltr2_20V30B_IV", "D10_1517_1617_fltr3_20V30B", "D14_1004_1034_fltr3_V5B50", "D14_1120_1150_fltr3_10V50B", "D14_1156_1226_fltr3_30V30B", "D14_1230_1300_fltr3_50V20B", "D14_1304_1334_fltr3_50V10B", "D14_1343_1543_fltr3_40V20B", "D10_1041_1241_fltr2_20V30B_V", "D13_0859_1059_fltr3_20V30B"};
//    	runLookupFltrAlgoWith_hmRandhmS(argR4, argS4);    	

//    	//jul16
//    	String[] argR5 ={"../Utilities/16_7/rms/",   "D16_1203_1403_fltr0_80V20B", "D16_1410_1610_fltr1_80V20B"};
//    	String[] argS5 ={"../Utilities/16_7/state/", "D16_1203_1403_fltr0_80V20B", "D16_1410_1610_fltr1_80V20B"};
//    	runLookupFltrAlgoWith_hmRandhmS(argR5, argS5);    	    

       	//jul14
    	String[] argR6 ={"../Utilities/14_7A/rms/",   "J14_1004_1034_fltr3_05V50B.csv", "J14_1120_1150_fltr3_10V50B.csv", "J14_1156_1226_fltr3_30V30B.csv", "J14_1230_1300_fltr3_50V20B.csv", "J14_1304_1334_fltr3_50V10B.csv", "J14_1343_1543_fltr3_40V20B.csv", "J15_0908_1108_fltr2_40V20B.csv", "J15_1119_1319_fltr1_40V20B.csv", "J15_1329_1529_fltr0_40V20B.csv", "J16_1203_1403_fltr0_80V20B.csv", "J16_1410_1610_fltr1_80V20B.csv", "J17_0838_1038_fltr2_80V20B.csv", "J17_1047_1247_fltr3_80V20B.csv", "J17_1252_1452_fltr3_40V40B.csv", "J20_0809_1009_fltr2_40V40B.csv", "J20_1021_1221_fltr1_40V40B.csv", "J20_1231_1431_fltr0_40V40B.csv", "J21_0838_1038_fltr0_20V60B.csv", "J21_1047_1247_fltr1_20V60B.csv", "J21_1257_1457_fltr2_20V60B.csv"};
    	String[] argS6 ={"../Utilities/14_7A/state/", "J14_1004_1034_fltr3_05V50B.csv", "J14_1120_1150_fltr3_10V50B.csv", "J14_1156_1226_fltr3_30V30B.csv", "J14_1230_1300_fltr3_50V20B.csv", "J14_1304_1334_fltr3_50V10B.csv", "J14_1343_1543_fltr3_40V20B.csv", "J15_0908_1108_fltr2_40V20B.csv", "J15_1119_1319_fltr1_40V20B.csv", "J15_1329_1529_fltr0_40V20B.csv", "J16_1203_1403_fltr0_80V20B.csv", "J16_1410_1610_fltr1_80V20B.csv", "J17_0838_1038_fltr2_80V20B.csv", "J17_1047_1247_fltr3_80V20B.csv", "J17_1252_1452_fltr3_40V40B.csv", "J20_0809_1009_fltr2_40V40B.csv", "J20_1021_1221_fltr1_40V40B.csv", "J20_1231_1431_fltr0_40V40B.csv", "J21_0838_1038_fltr0_20V60B.csv", "J21_1047_1247_fltr1_20V60B.csv", "J21_1257_1457_fltr2_20V60B.csv"};    	    	
    	runLookupFltrAlgoWith_hmRandhmS(argR6, argS6);    	    
    }

	public static void runLookupFltrAlgoWith_hmRandhmS(String[] argR, String[] argS) throws Exception {
    	//Input: Seperate  CSV
    	HashMap<String, String> hmR=null;
    	HashMap<String, String> hmS=null;
    	String[] gaugeValue=null;		

    	for (int i = 1; i < argR.length; i++) {
			hmR = formHM(argR[0] + argR[i]);
			hmS = formHM(argS[0] + argS[i]);
			System.out.println("############################ Logdata for Filter " + argR[i] + " #################################");
			//gaugeValue = LookupFilterClog.getClogLevel(hmR, hmS);
			gaugeValue = FilterClogByRegression.getClogLevel(hmR, hmS);
			System.out.println("Gauge Value for the file is " + gaugeValue[0]);
			System.out.println("Warning Msg for the file is\n" + gaugeValue[1]);
			//System.out.println("RMS for CLEAN is "+gaugeValue[2]+"\n");
			//System.out.println("STATE for CLEAN is "+gaugeValue[3]+"\n");
			System.out.println("$4059$$$$$$$$$$$$$$$$$$$$$  Logdata for Filter "+ argR[i] + " $$$$$ DC=" + String.format("%.2E & FG=%.2f", new Double(gaugeValue[4]).doubleValue(), new Double(gaugeValue[0]).doubleValue())+ 
					" $$$$$$$$$$$$$$$$$$$$\n");
		}
	}
    
}