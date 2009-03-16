
package com.pingfit.util;

import java.util.*;

public class DateDiff{

	/*
	* DateDiff Implementation.
	* Units: "year", "month", "week", "day", "hour", "minute", "second", "milli".
	* Returns zero if the dates are the same at the unit's resolution.
	*/	
	public static int dateDiff(String unit, Calendar d1, Calendar d2){
		  int result=0;
		  if (d1!=null && d2!=null){
              if (unit.equals("week") || unit.equals("day") || unit.equals("hour") || unit.equals("minute") || unit.equals("second") || unit.equals("milli") ) {
                    //We'll use the mathDateDiff method
                    result = (int) mathDateDiff(unit, d1, d2);
              } else if (unit.equals("month")) {
                    //Month calculation
                    result = ((d1.get(Calendar.YEAR)*12)+d1.get(Calendar.MONTH)) - ((d2.get(Calendar.YEAR)*12)+d2.get(Calendar.MONTH));
              } else if (unit.equals("year")) {
                    //Year calculation
                    result = d1.get(Calendar.YEAR)-d2.get(Calendar.YEAR);
              }
          }
		  return result;
	}
	
	
	
	/**
	* DateDiff Implementation based on division by multiples.
	* Used by DateDiff.
	* Works for week, day, hour, minute, second, milli.
	*/
	public static int mathDateDiff(String unit, Calendar d1, Calendar d2) {
		long returnval=0;

		if (d1!=null && d2!=null){
            //Find the difference in milliseconds
            long milli1 = d1.getTimeInMillis();
            long milli2 = d2.getTimeInMillis();
            long diff = milli1 - milli2;


            //Calculate the result
            if (unit.equals("week")) {
                returnval=diff/(1000*60*60*24*7);
            } else if (unit.equals("day")) {
                returnval=diff/(1000*60*60*24);
            } else if (unit.equals("hour")) {
                returnval=diff/(1000*60*60);
            } else if (unit.equals("minute")) {
                returnval=diff/(1000*60);
            } else if (unit.equals("second")) {
                returnval=diff/1000;
            } else if (unit.equals("millisecond")) {
                returnval=diff;
            } else {
                returnval=diff;
            }
        }

		//Return the difference
		return (int) returnval;
	}
	
	

	/**
	* A pure implementation of DateDiff that strips the date info
	* that is lower than the unit resolution I want and counts barrier
	* crossings.  Relies heavily on Java's Calendar object
	* Note: Very, very, very slow for minute, second and milli.
	*/
	public static int pureDateDiff(String unit, Calendar g1, Calendar g2) {
		  //Some vars
		  int elapsed = 0;
		  int sign=1;
	      Calendar gc1, gc2;
	
		  //Swap 'em if we need to
		  //Also record the swap so that I 
		  //can keep track of sign.
	      if (g2.after(g1)) {
	         gc2 = (Calendar) g2.clone();
	         gc1 = (Calendar) g1.clone();
			 sign=-1;
	      }
	      else   {
	         gc2 = (Calendar) g1.clone();
	         gc1 = (Calendar) g2.clone();
			 sign=1;
	      }
		  
		  //This is ugly but I need a numerical heirarchy of units
		  int unitStrength=0;
		  if (unit.equals("year")) {
		  		unitStrength=8;
		  } else if (unit.equals("month")) {
		  		unitStrength=7;
		  } else if (unit.equals("week")) {
		  		unitStrength=6;
		  } else if (unit.equals("day")) {
		  		unitStrength=5;
		  } else if (unit.equals("hour")) {
		  		unitStrength=4;
		  } else if (unit.equals("minute")) {
		  		unitStrength=3;
		  } else if (unit.equals("second")) {
		  		unitStrength=2;
		  } else if (unit.equals("milli")) {
		  		unitStrength=1;
		  } else {
		  		unitStrength=1;
		  }
		 
		  //Now store all of the vars in ints.
		  //This is done because I want to make sure the calendar is 100% cleared
		  //in the next step.
		  int gc1YEAR = gc1.get(Calendar.YEAR);
		  int gc2YEAR = gc2.get(Calendar.YEAR);
		  
		  int gc1MONTH = gc1.get(Calendar.MONTH);
		  int gc2MONTH = gc2.get(Calendar.MONTH);
		  
		  int gc1WEEK_OF_YEAR = gc1.get(Calendar.WEEK_OF_YEAR);
		  int gc2WEEK_OF_YEAR = gc2.get(Calendar.WEEK_OF_YEAR);
		  
		  int gc1DATE = gc1.get(Calendar.DATE);
		  int gc2DATE = gc2.get(Calendar.DATE);
	
		  int gc1HOUR_OF_DAY = gc1.get(Calendar.HOUR_OF_DAY);
		  int gc2HOUR_OF_DAY = gc2.get(Calendar.HOUR_OF_DAY);
	
		  int gc1MINUTE = gc1.get(Calendar.MINUTE);
		  int gc2MINUTE = gc2.get(Calendar.MINUTE);
		  
		  int gc1SECOND = gc1.get(Calendar.SECOND);
		  int gc2SECOND = gc2.get(Calendar.SECOND);
		  
		  int gc1MILLISECOND = gc1.get(Calendar.MILLISECOND);
		  int gc2MILLISECOND = gc2.get(Calendar.MILLISECOND);
		  
		  //Completely clear the calendars of all date information
		  gc1.clear();
		  gc2.clear();
		  
	      //Now rebuild the calendars.
		  //Only use fields that are as big or bigger than the unit I'm measuring.
		  //This is why I need the unitstrength
		  if (unitStrength<=8) {
			  gc1.set(Calendar.YEAR, gc1YEAR);
			  gc2.set(Calendar.YEAR, gc2YEAR);
		  }
		  if (unitStrength<=7) {
			  gc1.set(Calendar.MONTH, gc1MONTH);
			  gc2.set(Calendar.MONTH, gc2MONTH);
		  }
		  if (unitStrength<=6) {
			  gc1.set(Calendar.WEEK_OF_YEAR, gc1WEEK_OF_YEAR);
			  gc2.set(Calendar.WEEK_OF_YEAR, gc2WEEK_OF_YEAR);
		  }
		  if (unitStrength<=5) {
			  gc1.set(Calendar.DATE, gc1DATE);
			  gc2.set(Calendar.DATE, gc2DATE);
		  }
		  if (unitStrength<=4) {
			  gc1.set(Calendar.HOUR_OF_DAY, gc1HOUR_OF_DAY);
			  gc2.set(Calendar.HOUR_OF_DAY, gc2HOUR_OF_DAY);
		  }
	      if (unitStrength<=3) {
			  gc1.set(Calendar.MINUTE, gc1MINUTE);
			  gc2.set(Calendar.MINUTE, gc2MINUTE);
		  }
		  if (unitStrength<=2) {
			  gc1.set(Calendar.SECOND, gc1SECOND);
			  gc2.set(Calendar.SECOND, gc2SECOND);
		  }
		  if (unitStrength<=1) {
			  gc1.set(Calendar.MILLISECOND, gc1MILLISECOND);
			  gc2.set(Calendar.MILLISECOND, gc2MILLISECOND);
	      }
		  
		  //Make sure I increment the right unit
		  int whatToIncrement=0;
		  if (unitStrength==8) {
		  	  whatToIncrement=Calendar.YEAR;
		  } else if (unitStrength==7) {
			  whatToIncrement=Calendar.MONTH;
		  } else if (unitStrength==6) {
			  whatToIncrement=Calendar.WEEK_OF_YEAR;
		  } else if (unitStrength==5) {
			  whatToIncrement=Calendar.DATE;
		  } else if (unitStrength==4) {
			  whatToIncrement=Calendar.HOUR_OF_DAY;
		  } else if (unitStrength==3) {
			  whatToIncrement=Calendar.MINUTE;
		  } else if (unitStrength==2) {
			  whatToIncrement=Calendar.SECOND;
		  } else if (unitStrength==1) {
			  whatToIncrement=Calendar.MILLISECOND;
	      } else {
		  	  //Avoid infinite loops
		  	  whatToIncrement=Calendar.DATE;
		  }
	
		  //Count the number of unit boundaries crossed.
		  //Finally doing some work here.
		  while ( gc1.before(gc2) ) {
			  gc1.add(whatToIncrement, 1);
		
		 	  //Increment the counter
		      elapsed++;
		  }
		  
		  return sign*elapsed;
	}
	  
}


