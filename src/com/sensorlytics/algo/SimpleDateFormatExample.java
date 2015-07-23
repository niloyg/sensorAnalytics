package com.sensorlytics.algo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

 

public class SimpleDateFormatExample {

    public static void main(String[] args) {
 
        Date curDate = new Date();

        SimpleDateFormat format = new SimpleDateFormat();
        String DateToStr = format.format(curDate);
        System.out.println("Default pattern: " + DateToStr);
 
        format = new SimpleDateFormat("yyyy/MM/dd");
        DateToStr = format.format(curDate);
        System.out.println(DateToStr);
 
        format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        DateToStr = format.format(curDate);
        System.out.println(DateToStr);

        format = new SimpleDateFormat("dd MMMM yyyy zzzz", Locale.ENGLISH);
        DateToStr = format.format(curDate);
        System.out.println(DateToStr);

        format = new SimpleDateFormat("MMMM dd HH:mm:ss zzzz yyyy", Locale.ITALIAN);
        DateToStr = format.format(curDate);
        System.out.println(DateToStr);
             
        format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        DateToStr = format.format(curDate);
        System.out.println(DateToStr);
    
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateToStr = format.format(curDate);
        System.out.println("ProphecyFormat: " + DateToStr);   

        try {
            Date strToDate = format.parse(DateToStr);
            Date strToDate2 = format.parse("2015-05-29T09:13:25.924-0400");
            System.out.println(strToDate2);
            long diff =strToDate.getTime() - strToDate2.getTime();
            System.out.println("Diff in milli-second is "+diff);
            //yyyy-MM-dd'T'HH:mm:ss.SSSZ
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}