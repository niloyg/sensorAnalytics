package learning.age.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Stat {
	public static final double DEFAULT_INTERVAL = 0.01;

	public static double getHashMapKeyGivenValue(HashMap hm, Object value) {
		double result = 0.0;
		Set keys = hm.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			Double key = (Double) itr.next();
			if (hm.get(key).equals((Double) value)) return key.doubleValue();
		}
		return Double.MAX_VALUE;
	}
	public static int gravitedTowards(double data, double[] array) {
		int result=0;
		HashMap<Double, Double> hmDelta = new HashMap<Double, Double>();
		for (int i=0;i<array.length;i++){
			hmDelta.put(new Double(i), Math.abs(array[i] - data));
		}
		List lstDelta = new ArrayList(hmDelta.values());
		Collections.sort(lstDelta);
		double leastDelta = ((Double) lstDelta.get(0)).doubleValue();
		Double key = getHashMapKeyGivenValue(hmDelta, leastDelta);
		int intIndex = key.intValue();
		return intIndex;
		//return array(intIndex);
	}
	
	public static HashMap<Double, Integer> frquencyDistribution(List<Double> data, double interval) {
		HashMap<Double, Integer> hmFreq = new HashMap<Double, Integer>();
		Collections.sort(data);
		double min = data.get(0);
		double max = data.get(data.size()-1);
		double width = (max - min)*interval;
		
		//If all values are identical, mode = max = min = any value and Freq = Number of elements in the List
		if (width == 0 ) {
			hmFreq.put(new Double(max), new Integer(data.size()));
		}
		
		double width_by_2 = width/2;
		//System.out.println(data.toString());
		//System.out.println("Stat-->freqDistri: min, max, width:"+min+" "+max+" "+width);
		double anchor=min + width_by_2;
		int intervalCounter = 0;
		
		for (int i = 0; i <= (1/interval); i++) {
			intervalCounter = 0;
			Iterator itr = data.iterator();
			while (itr.hasNext()) {
				double element = ((Double) itr.next()).doubleValue();
				if (element >= (anchor - width_by_2)
						&& element < (anchor + width_by_2))
					intervalCounter += 1;
			}
			
			Double ANCHOR = new Double(anchor);
			Integer COUNTER = new Integer(intervalCounter);
			//System.out.println("Stats.frequencyDistribution--> "+ANCHOR + " "+COUNTER);
			hmFreq.put(ANCHOR, COUNTER);
			anchor += width;
		}
		return hmFreq;
	}
	
	public static double mode(HashMap<Double, Integer> hmFreq){
		Collection<Integer> values = hmFreq.values();
		List lst = new ArrayList(values);
		Collections.sort(lst);
		Integer modeFreq = (Integer) lst.get(lst.size()-1);
		
		Set keys = hmFreq.keySet();
		Iterator itr = keys.iterator();
		
		while (itr.hasNext()) {
			Double anchor = (Double) itr.next();
			if (hmFreq.get(anchor).equals(modeFreq)) return anchor.doubleValue();
		}
		return Integer.MAX_VALUE;
	}

	public static double mode(List<Double> lst) {
		HashMap<Double, Integer> hmFreq = Stat.frquencyDistribution(lst, DEFAULT_INTERVAL);
		return Stat.mode(hmFreq);
	}

	public static double mode(List<Double> lst, double interval) {
		HashMap<Double, Integer> hmFreq = Stat.frquencyDistribution(lst, interval);
		return Stat.mode(hmFreq);
	}

	public static double[] modeWithFrequency(List<Double> lst, double interval) {
		double[] results = new double[2];
		HashMap<Double, Integer> hmFreq = Stat.frquencyDistribution(lst, interval);
		results[0] = Stat.mode(hmFreq);
		results[1] = hmFreq.get(new Double(results[0])).intValue();
		return results;
	}

	public static double average(List<Double> lst) {
		double avg = 0;
		if (lst.size() == 0) return Double.MAX_VALUE;
		for (int i=0;i<lst.size();i++) {
			double element = ((Double)lst.get(i)).doubleValue();
			avg = avg + element;
		}
		avg = avg / lst.size();
		return avg;
	}
	
	public static double median(List<Double> lst) {
		double median = Double.MAX_VALUE;
		Collections.sort(lst);
		int size = lst.size();
		int medianIndex=0;
		
		boolean odd = false;
		if ((size/2)*2 != size) odd = true;
		
		if (odd) {
			median = lst.get(size/2);
		}
		else {
			medianIndex = size/2;
			median = (lst.get(medianIndex) + lst.get(medianIndex-1))/2;			
		}
		return median;
	}
	
	public static double lookup(double[] mileStones, double arg, HashMap hm) {
		double result = 0;
		
		for (int i=1;i<mileStones.length;i++){
			if (arg >= mileStones[i-1] && arg <=mileStones[i]) {
				double ll = ((Double) hm.get(new Double(mileStones[i-1]))).doubleValue();
				double ul = ((Double) hm.get(new Double(mileStones[i+0]))).doubleValue();
				result = ll + (ul - ll)*(arg - mileStones[i-1])/(mileStones[i]-mileStones[i-1]);
			}
		}
		
		return result;
	}
	
	public static double lookup2D(Lookup2D lookup2D, double arg, int dimension1Index) {
		double result=0.0;
		result = Stat.lookup(lookup2D.twoDlookup[dimension1Index], arg, lookup2D.hms[dimension1Index]);
		return result;
	}

	public static void main (String[] args) {
		//testMode();
		//testGravity();
		//testMedian();
		//testLookup();
		testLookup2D();
	}
	
	
	public static void testGravity() {
		double d = 6.99999;
		double[] dArr = new double[5];
		dArr[0] = 2.0;dArr[1] = 4.0;dArr[2]=6.0;dArr[3]=8;dArr[4]=10.0;
		System.out.println("The gravity value of "+d+" is "+ dArr[gravitedTowards(d, dArr)]);
		
	}
	
	
	public static void testMode () { 
		List<Double> lst = new ArrayList<Double>();
		for (int i = 0; i < 1000; i++) {
			lst.add(Math.random()*100);
		}		
		double mode = Stat.mode(lst, 0.05);
		System.out.println("Stat:-->Mode is: "+mode);
	}
	
	
	public static void testMedian () { 
		List<Double> lst = new ArrayList<Double>();
		for (int i = 0; i < 11; i++) {
			lst.add(i*1.0+i*1.0/100);
		}		
		System.out.println("Stat:-->samples are: "+lst.toString());
		double median = Stat.median(lst);
		System.out.println("Stat:-->Median is: "+median);
	}

	public static void testLookup() {
		double[] dLookUp = new double[6];
		dLookUp[0] = 0;dLookUp[1] = 700;dLookUp[2] = 1000;dLookUp[3] = 2000;dLookUp[4] = 9000;dLookUp[5] = 20000;		
		HashMap hm = new HashMap<Double, Double>();
		hm.put(new Double(dLookUp[0]), new Double(0.80));
		hm.put(new Double(dLookUp[1]), new Double(1.00));
		hm.put(new Double(dLookUp[2]), new Double(0.00));
		hm.put(new Double(dLookUp[3]), new Double(0.50));
		hm.put(new Double(dLookUp[4]), new Double(0.80));
		hm.put(new Double(dLookUp[5]), new Double(1.00));
		
		for (int i = 0; i <= 200; i++) {
			System.out.println("Stat-->Lookup value for "+100*i +"is "+
		String.format("%.2f", lookup(dLookUp, 100*i, hm)));
		}
	}
	
	public static void testLookup2D(){
		
		Lookup2D lookup2D = new Lookup2D(2, 6);
		
		double[] dLookUp = new double[6];
		dLookUp[0] = 0;dLookUp[1] = 700;dLookUp[2] = 1000;dLookUp[3] = 2000;dLookUp[4] = 9000;dLookUp[5] = 20000;		
		HashMap hm = new HashMap<Double, Double>();
		hm.put(new Double(dLookUp[0]), new Double(0.80));
		hm.put(new Double(dLookUp[1]), new Double(1.00));
		hm.put(new Double(dLookUp[2]), new Double(0.00));
		hm.put(new Double(dLookUp[3]), new Double(0.50));
		hm.put(new Double(dLookUp[4]), new Double(0.80));
		hm.put(new Double(dLookUp[5]), new Double(1.00));
		lookup2D.twoDlookup[0]=dLookUp;
		lookup2D.hms[0]=hm;
		
		double[] dLookUp2 = new double[6];
		dLookUp2[0] = 0.0;dLookUp2[1] = 70.0;dLookUp2[2] = 100.0;dLookUp2[3] = 200.0;dLookUp2[4] = 900.0;dLookUp2[5] = 10000.0;		
		HashMap hm2 = new HashMap<Double, Double>();
		hm2.put(new Double(dLookUp2[0]), new Double(080.0));
		hm2.put(new Double(dLookUp2[1]), new Double(100.0));
		hm2.put(new Double(dLookUp2[2]), new Double(000.0));
		hm2.put(new Double(dLookUp2[3]), new Double(050.0));
		hm2.put(new Double(dLookUp2[4]), new Double(080.0));
		hm2.put(new Double(dLookUp2[5]), new Double(100.0));
		lookup2D.twoDlookup[1]=dLookUp2;
		lookup2D.hms[1]=hm2;

		for (int i = 0; i <= 200; i++) {
			System.out.println("Stat-->Lookup value for "+100*i +"is "+
		String.format("%.2f", lookup2D(lookup2D, 100*i, 0)));
		}		

		System.out.println("2nd Part Now >>>>>>>>>");
		for (int i = 0; i <= 200; i++) {
			System.out.println("Stat-->Lookup value for "+100*i +"is "+
		String.format("%.2f", lookup2D(lookup2D, 100*i, 1)));
		}				
	}
}