package com.sensorlytics.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;

public class LookupAbusive {
    static final float percentile = 0.9996f;
    
    static final float rms20 =  1.66f;
    static final float rmsGY =  1.86f;
    static final float rmsYR =  2.04f;
    static final float rms100 = 2.22f;
    
    static final float FG_YR =  0.80f;
    static final float FG_GY =  0.60f;
    
    public static void main(String[] args) {
        System.out.println("Arg length is: "+args.length);
        
        // TODO Json to float arraylist conversion

        List<Float> population = new ArrayList<Float>();
        
        for (int i=0;i<8765;i++) {
            population.add(new Float(1.0+0.6*Math.random()));
        }
        
        long beginTime = System.nanoTime();
        float fgIndicator = getFgIndicator(population);
        long endTime = System.nanoTime();
        System.out.println("Execution time for "+population.size() +" elements is: " + (endTime - beginTime)/1000000 + " milli sec." );
        System.out.println("FG needle value is "+fgIndicator);
    }
    
    public static float getFgGy() {
        return FG_GY;
    }

    public static float getFgIndicator(List<Float> population){
        //Sort an arraylist which is passed as argument
        Collections.sort(population);
        Iterator itr = population.iterator();

        //calculate proxyRMS using percentile parm on the argument arraylist
        int size = population.size();
        int floor = (new Float(Math.floor(size*percentile))).intValue() -1;
        int ceil  = (new Float(Math.ceil(size*percentile))).intValue() - 1;
        if (ceil >= size ) {ceil = size -1 ;}

        float proxyRMS = (population.get(floor) + population.get(ceil))/2;
        System.out.println("The proxyRMS is "+proxyRMS);
        
        //Lookup to the gauge value
        float FG=0.50f;
        if (proxyRMS >= rms100) {FG = 1.0f;}
        else 
        if (proxyRMS >= rmsYR) {FG = FG_YR + (proxyRMS - rmsYR)/(rms100 - rmsYR)*(1.0f  - FG_YR);}
        else 
        if (proxyRMS >= rmsGY) {FG = FG_GY + (proxyRMS - rmsGY)/(rmsYR  - rmsGY)*(FG_YR - FG_GY);}
        else 
        if (proxyRMS >= rms20) {FG =   0.2f + (proxyRMS - rms20)/(rmsGY  - rms20)*(FG_GY - 0.2f);}
        else FG = 0.2f;
    
        return FG;
    }
}