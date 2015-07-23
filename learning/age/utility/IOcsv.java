package learning.age.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class IOcsv {

    //@SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {                
    	String splitBy = "\t";
/*
 * Important: For those developers that use an IDE to implement their Java applications, 
 * the relative path for every file must be specified starting from 
 * the level where the src directory of the project resides.
 */
        File file = new File("result.csv");
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i=0;i<200000;i++) {
	        writer.write(new Integer(i) + "\t" + Math.random());
	        writer.newLine();
        }
        writer.flush();
        writer.close();
    	
    	
    	double time1, time2;
    	time1 = System.nanoTime();
    	BufferedReader br = new BufferedReader(new FileReader("result.csv"));
    	List<Double> lst = new ArrayList<Double> ();
        String line = br.readLine();
        while(line!=null){
             String[] b = line.split(splitBy);
             //System.out.println(b[0]+" " + b[1]);
             lst.add(new Double(b[1]));
             line = br.readLine();
        }
        br.close();
        double sum =0,average;
        for (int i = 0;i<lst.size();i++){
        	sum = sum + lst.get(i);
        }
        average = sum / lst.size();
        time2 = System.nanoTime();
        System.out.println("Time Taken is: " + (time2 - time1)/1000000 + " mSec. & avg value is: "+average);


  }
}