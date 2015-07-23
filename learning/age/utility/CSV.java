package learning.age.utility;

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

public class CSV {

	public static void addColumn (String iFileName, String oFileName, String colVal) throws Exception {
       File file = new File(oFileName);
       file.createNewFile();
       BufferedWriter writer = new BufferedWriter(new FileWriter(file));

       BufferedReader br = new BufferedReader(new FileReader(iFileName));
       String line = br.readLine();
        
       while(line!=null){
    	   String[] b = line.split(",");
    	   //System.out.println("The string is: "+b[0]);
    	   if (b.length > 1) {
    	   Double rms = new Double(b[1]);
	    	   if (rms.doubleValue() != 0.0) {
		    	   	writer.write(line+colVal);
		            writer.newLine();
		            
	    	   }
    	   }
    	   line = br.readLine();
       }
       br.close();
       
       writer.flush();
       writer.close();		
	}
	
	public static void writeDuplicateRows (String iFileName, String oFileName, int colPos) throws Exception {
	       File file = new File(oFileName);
	       file.createNewFile();
	       BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	       BufferedReader br = new BufferedReader(new FileReader(iFileName));
	       String line = br.readLine();
	       
	       String prevKey = null;
	       while(line!=null){
	    	   String[] b = line.split(",");
	    	   //System.out.println("The string is: "+b[0]);
	    	   if (b.length > 1) {
	    	   Double rms = new Double(b[1]);
		    	   if (prevKey!= null && b[colPos].equals(prevKey)) {
			    	   	writer.write(line);
			            writer.newLine();			            
		    	   }
	    	   }
	    	   line = br.readLine();
	    	   prevKey = b[colPos];
	       }
	       br.close();
	       
	       writer.flush();
	       writer.close();		
		}	

	public static void calculateDuplicateRange (String iFileName, String oFileName, int deltaInMilliSec) throws Exception {
	       File file = new File(oFileName);
	       file.createNewFile();
	       BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	       BufferedReader br = new BufferedReader(new FileReader(iFileName));
	       String line = br.readLine();
	       
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	       sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	       String[] b = line.split(",");
	       Date beginTS = sdf.parse(b[0]);
	       Date prevTS = beginTS;
	       while(line!=null){
	    	   b = line.split(",");
	    	   //System.out.println("The string is: "+b[0]);
	    	   Date curTS = null;
	    	   if (b.length > 1) {
		    	   curTS = sdf.parse(b[0]);
	    		   if (curTS.getTime() - prevTS.getTime() > deltaInMilliSec ) {
	    			   
			    	  writer.write(	sdf.format(beginTS)+"\t"+
			    			  		sdf.format(curTS)+"\t"+ 
			    			  		(curTS.getTime() - beginTS.getTime()));
			          writer.newLine();
			          beginTS = curTS;
	    		   }
	    	   }
	    	   prevTS = curTS;
	    	   line = br.readLine();
	       }

	       br.close();
	       
	       writer.flush();
	       writer.close();		
		}	
	
	public static void calculateInnerJoin (String iFileName1, int col1, String iFileName2, int col2, String oFileName) throws Exception {
	       File file = new File(oFileName);
	       file.createNewFile();
	       BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	       
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	       
	       BufferedReader br1 = new BufferedReader(new FileReader(iFileName1));
	       String line1 = br1.readLine();
	       BufferedReader br2 = new BufferedReader(new FileReader(iFileName2));
	       String line2 = br2.readLine();
	       
	       String[] b1 = line1.split(",");String[] b2 = line2.split(",");
	       int countB1gB2=0, countB1lB2=0, countB1eB2=0;

	       while(line1!=null && line2!=null){
	    	   if (b1.length <2) {
	    		   line1 = br1.readLine();
	    		   continue;
	    	   }
	    	   if (b2.length <2) {
	    		   line2 = br2.readLine();
	    		   continue;
	    	   }	    	   
	    	   if (b1[0].equals(b2[0])) {
	    		   writer.write(line1+","+line2);
			       writer.newLine();
			       System.out.println(line1+","+line2);
			       countB1eB2++;
			       continue;
	    	   }
	    	   else
	    	   if (sdf.parse(b1[0]).getTime() > sdf.parse(b2[0]).getTime()) {
	    		   System.out.println("b1 > b2");
	    		   countB1gB2++;
	    		   line2 = br2.readLine();
	    		   b2 = line2.split(",");
	    		   continue;
	    	   }
	    	   else 
	    	   if (sdf.parse(b1[0]).getTime() < sdf.parse(b2[0]).getTime()) {
	    		   System.out.println("b1 < b2");
	    		   countB1lB2++;
	    		   line1 = br1.readLine();
	    		   b1 = line1.split(",");
	    		   continue;
	    	   }    	   
	    	}
	       br1.close();br2.close();
	       
	       writer.flush();
	       writer.close();
	       System.out.println("Files closed");
	       System.out.println("countB1gB2="+countB1gB2+" countB1lB2="+countB1lB2+" countB1eB2="+countB1eB2);
		}

	public static void whereBetween (String iFileName, String oFileName, int colPos, String beginDateStr, String endDateStr) throws Exception {
	       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	       Date beginDate = sdf.parse(beginDateStr);
	       Date endDate = sdf.parse(endDateStr);
			File file = new File(oFileName);
	       file.createNewFile();
	       BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	       BufferedReader br = new BufferedReader(new FileReader(iFileName));
	       String line = br.readLine();
	        
	       while(line!=null){
	    	   String[] b = line.split(",");
	    	   //System.out.println("The string is: "+b[0]);
	    	   if (b.length > 1) {
	    	   Date curDate = null;
	    	   try {
				curDate = sdf.parse(b[colPos]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    	   if (curDate != null) {
			    	   if (curDate.after(beginDate) && curDate.before(endDate)) {
				    	   	writer.write(line);
				            writer.newLine();
				            
			    	   }
		    	   }
	    	   }
	    	   line = br.readLine();
	       }
	       br.close();
	       
	       writer.flush();
	       writer.close();		
		}

	public static void objectToCSV (String iFileName, String oFileName, int varCount) throws Exception {
		File file = new File(oFileName);
	    file.createNewFile();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	    BufferedReader br = new BufferedReader(new FileReader(iFileName));
	    String line = br.readLine();
	        
	    while(line!=null) {
    	   String[] b = line.split(" ");
    	   //System.out.println("The string is: "+b[0]);
    	   if (b.length >= varCount) {
    		   String outLine = "";
    		   for (int i=0;i<b.length;i++) {
    			   String tmp = (b[i].split("=")[1]);
    			   if (outLine.equals("")) outLine+=tmp;
    			   else outLine+=","+tmp;
    		   }
	    	   writer.write(outLine);
	           writer.newLine();
    	   }
    	   line = br.readLine();
	   }
       br.close();
       
       writer.flush();
       writer.close();		
	}

	public static void splitCSV (String iFileName, String oFileName, int colPosOfKey, int colPosOfValue, int varCount) throws Exception {
		File file = new File(oFileName);
	    file.createNewFile();
	    BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	    BufferedReader br = new BufferedReader(new FileReader(iFileName));
	    String line = br.readLine();
	        
	    while(line!=null) {
    	   String[] b = line.split(",");
    	   //System.out.println("The string is: "+b[0]);
    	   if (b.length >= varCount) {
    		   String key = b[colPosOfKey];
    		   String value = b[colPosOfValue];
    		   String outLine = key+","+value;
	    	   writer.write(outLine);
	           writer.newLine();
    	   }
    	   line = br.readLine();
	   }
       br.close();
       
       writer.flush();
       writer.close();		
	}	
	/*
	 * Important: For those developers that use an IDE to implement their Java applications, 
	 * the relative path for every file must be specified starting from 
	 * the level where the src directory of the project resides.
	 */
	//@SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {                
    	{
    	//call addColumn
    	//addColumn("./data/rmsV.csv", "./data/rmsVO.csv", ",1");
        //System.out.println("CSV:-->addColumn");

    
    	//Write duplicate rows
    	//writeDuplicateRows("./data/outSorted.csv", "./data/dup.csv", 0);
    	//System.out.println("CSV:-->writeDuplicateRows");
    	
    	//System.out.println("CSV --> Calculate Duplicate Range");
    	//calculateDuplicateRange("dup.csv", "distil.csv", 1000);

    	//System.out.println("CSV --> Calculate inner Join");
    	//calculateInnerJoin("./data/rmsV.csv", 0, "./data/rmsB.csv", 0, "./data/inner.csv");    	
    	}
       	//System.out.println("CSV --> whereBetween");
    	//whereBetween ("./18_06/filter_18_06_clean.csv", "./18_06/filter_18_06_stgI_clean_partIII.csv", 0, "2015-06-18T08:27:00.000-0400", "2015-06-18T08:37:00.000-0400");
    	
       	//System.out.println("CSV --> objectToCSV");
    	objectToCSV ("./19_06/part2/1611_1616/periodE", "./19_06/part2/1611_1616/periodE.csv", 3);
    	
      	//System.out.println("CSV --> objectToCSV");
    	splitCSV ("./19_06/part2/1611_1616/periodE.csv", "./19_06/part2/1611_1616/periodE_rms.csv", 0, 2, 4);
    	splitCSV ("./19_06/part2/1611_1616/periodE.csv", "./19_06/part2/1611_1616/periodE_state.csv", 0, 3, 4);
    }
}