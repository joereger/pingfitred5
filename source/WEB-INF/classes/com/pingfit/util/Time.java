package com.pingfit.util;

import org.apache.log4j.Logger;

import java.util.*;
import java.text.*;

public class Time {


//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------

	/**
	* Convert a date to a calendar with the GMT Timezone
	* The offset var = the user's timezone offset
	*/
	public static Calendar usertogmttime(Calendar date, String timezoneid){
	

	    //System.out.println("-----------");
	    //System.out.println("-----------");
	    //System.out.println("timezoneid: "+timezoneid+"<br>dateformatfordb(date): " +reger.core.TimeUtils.dateformatfordb(date) + "<br>date.getTimeZone().getID():"+date.getTimeZone().getID());
	    Calendar outCal = (Calendar) date.clone();
	    //System.out.println("timezoneid: "+timezoneid+"<br>dateformatfordb(outCal): " +reger.core.TimeUtils.dateformatfordb(outCal) + "<br>outCal.getTimeZone().getID():"+outCal.getTimeZone().getID());
	    outCal.setTimeZone(TimeZoneCache.get("GMT"));
	    //System.out.println("timezoneid: "+timezoneid+"<br>dateformatfordb(outCal): " +reger.core.TimeUtils.dateformatfordb(outCal) + "<br>outCal.getTimeZone().getID():"+outCal.getTimeZone().getID());
		int offset = TimeZoneCache.get(timezoneid).getOffset(outCal.getTimeInMillis());
		//System.out.println("timezoneid: "+timezoneid+"<br>offset: " +offset);
		outCal.add(Calendar.MILLISECOND, (-1)*offset);
		//System.out.println("timezoneid: "+timezoneid+"<br>dateformatfordb(outCal): " +reger.core.TimeUtils.dateformatfordb(outCal) + "<br>outCal.getTimeZone().getID():"+outCal.getTimeZone().getID());
		return outCal;
	}


//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------
	/**
	* Convert a gmt date to a user's local time
	* The offset var = the user's timezone offset
	*/
	public static Calendar gmttousertime(Calendar date, String timezoneid){
		if (date!=null){
            Calendar outCal = (Calendar) date.clone();
            if (timezoneid != null && !timezoneid.equals("")) {
                outCal.setTimeZone(TimeZoneCache.get(timezoneid));
            } else {
                outCal.setTimeZone(TimeZoneCache.get("GMT"));
            }
            int offset = TimeZoneCache.get(timezoneid).getOffset(outCal.getTimeInMillis());
            outCal.add(Calendar.MILLISECOND, (1)*offset);
            return outCal;
        } else {
            return null;
        }
	}
//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------
//---------------------------------------------------------------

    /**
	* Convert from one timezone to another
	*/
	public static Calendar convertFromOneTimeZoneToAnother(Calendar date, String oldtimezoneid, String newtimezoneid){
	    Calendar outCal = (Calendar) date.clone();
	    //Convert to GMT
	    outCal = usertogmttime(outCal, oldtimezoneid);
	    //Then convert to the new timezone
	    outCal = gmttousertime(outCal, newtimezoneid);
		return outCal;
	}

	/**
     * Gets the current time in the user's timezone
     */
    public static Calendar nowInUserTimezone(String timezoneid){
        //Get time on the physical server (probably in Atlanta)
		Calendar usertime = Calendar.getInstance();
		//Convert from server (not GMT... server in Atlanta) time to user's timezone.
		usertime = Time.convertFromOneTimeZoneToAnother(usertime, usertime.getTimeZone().getID(), timezoneid);
        return usertime;
    }
	
	/**
    * Format a calendar date object in the correct string format so that it can be saved in mySql
    */
	public static String dateformatfordb(Calendar date) {
	    DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Format a calendar date object in UTC format.
    */
	public static String dateformatUtc(Calendar date) {
	    DateFormat myDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
	    if (date==null){
            date = nowInGmtCalendar();
        }
        return myDateFormat.format(date.getTime()) + date.getTimeZone().getID();
	}

	/**
    * Format a calendar date object to a filestamp
    */
	public static String dateformatfilestamp(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("(yyyy-MM-dd)HH-mm-ss");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Format a calendar date object to a filestamp
    */
	public static String dateformatdate(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Format a calendar date object to a filestamp
    */
	public static String dateformatdateSlashes(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Format a calendar date object to MMddyyyy
    */
	public static String dateformatdateMmddyyyy(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("MMddyyyy");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Format a calendar date object to a filestamp
    */
	public static String dateformattime(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("HH-mm-ss");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	public static Calendar getCalFromDate(Date date){
        Calendar out = Calendar.getInstance();
        out.setTime(date);
        return out;
    }
    
	
	/**
	* Convert a Db string date to a calendar
	*/
	public static Calendar dbstringtocalendar(String instr){
	    Logger logger = Logger.getLogger("com.pingfit.util.Time");
		Calendar cal = Calendar.getInstance();
		try{
			DateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cal.setTime(myDateFormat.parse(instr));
		} catch(Exception e) {
			logger.error("", e);
			//If the parse fails, return current date/time
			cal=Calendar.getInstance();
		}
		return cal;
	}

	public static int getDatePart(Calendar cal, String datePartToGet){
	    Logger logger = Logger.getLogger("com.pingfit.util.Time");
	    try{
            String date = Time.dateformatfordb(cal);
            if (datePartToGet.equals("month")){
                logger.debug("Start getDatePart()<br>date=" + date + "<br>datePartToGet=" + datePartToGet + "<br>returning:" + (Integer.parseInt(date.substring(5,7))));
                return Integer.parseInt(date.substring(5,7));
            } else if (datePartToGet.equals("day")){
                logger.debug("Start getDatePart()<br>date=" + date + "<br>datePartToGet=" + datePartToGet + "<br>returning:" + (date.substring(8,10)));
                return Integer.parseInt(date.substring(8,10));
            } else if (datePartToGet.equals("year")){
                logger.debug("Start getDatePart()<br>date=" + date + "<br>datePartToGet=" + datePartToGet + "<br>returning:" + (date.substring(0,4)));
                return Integer.parseInt(date.substring(0,4));
            } else if (datePartToGet.equals("hour")){
                int h =  Integer.parseInt(date.substring(11,13));
                if (h>=13){
                    h=h-12;
                } else if (h==12){
                    h=12;
                } else if (h==0){
                    h=12;
                }
                logger.debug("Start getDatePart()<br>date=" + date + "<br>datePartToGet=" + datePartToGet + "<br>returning:" + h);
                return h;
            } else if (datePartToGet.equals("minute")){
                logger.debug("Start getDatePart()<br>date=" + date + "<br>datePartToGet=" + datePartToGet + "<br>returning:" + (date.substring(14,16)));
                return Integer.parseInt(date.substring(14,16));
            }
        } catch (Exception e){
            logger.error("getDatePart() failed - incoming cal=" + cal.toString(), e);
        }
        logger.debug("Start getDatePart()<br>cal=" + cal.toString() + "<br>datePartToGet=" + datePartToGet + "<br>returning 0 as default");
        return 0;
    }

    public static String getAmPm(Calendar cal){
        String date = Time.dateformatfordb(cal);
        int h =  Integer.parseInt(date.substring(11,13));
        String ampm = "";
        if (h>=13){
            ampm = "PM";
        } else if (h==12){
            ampm = "PM";
        } else if (h==0){
            ampm = "AM";
        } else {
            ampm = "AM";
        }
        return ampm;
    }
	
	
	/**
    * Date format
    *
    */
	public static String dateformatcompact(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

	/**
    * Date format
    *
    */
	public static String dateformatcompactwithtime(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}

    public static String dateformatmonthdayyearmiltime(Calendar date) {
		DateFormat myDateFormat = new SimpleDateFormat("MMM d, yyyy k:mm:ss");
		if (date==null){
            date = nowInGmtCalendar();
        }
		return myDateFormat.format(date.getTime());
	}


	/**
	* Overload so that dbstring can be used
	*/
	public static Calendar gmttousertime(String date, String timezoneid){
		return gmttousertime(dbstringtocalendar(date), timezoneid);
	}
	
	
	/**
	* Converts from incoming form data to a calendar with the correct ampm.
	*/
	public static Calendar formtocalendar(int yyyy, int mm, int dd, int h, int m, int s, String ampm) {
		if (ampm.equalsIgnoreCase("PM")){
			h=h+12;
			if (h==24){
				h=12;
			}
		} else {
			if (h==12){
				h=0;
			}
		}

		//Put leading zeroes
		String hStr=String.valueOf(h);
		String mStr=String.valueOf(m);
		String mmStr=String.valueOf(mm);
		String ddStr=String.valueOf(dd);
		String sStr=String.valueOf(s);
		if (h<10){
            hStr = "0" + h;
        }
        if (m<10){
            mStr = "0" + m;
        }
        if (mm<10){
            mmStr = "0" + mm;
        }
        if (dd<10){
            ddStr = "0" + dd;
        }
        if (s<10){
            sStr = "0" + s;
        }

		//Calendar date = Calendar.getInstance();
		//date.set(yyyy, mm - 1, dd, h, m, s);

		Calendar date = Time.dbstringtocalendar(yyyy + "- " + mmStr + "-" + ddStr + " " + hStr + ":" + mStr + ":" + sStr);

		return date;
	}
	
	/**
	* Overload to allow strings to be passed and auto-converted
	*/
	public static Calendar formtocalendar(String yyyy, String mm, String dd, String h, String m, String s, String ampm) {
		
		//Convert the strings to ints
		int iyyyy = Integer.parseInt(yyyy);
		int imm = Integer.parseInt(mm);
		int idd = Integer.parseInt(dd);
		int ih = Integer.parseInt(h);
		int im = Integer.parseInt(m);
		int is = Integer.parseInt(s);
		
		//Pass the ints to the main function and get a calendar to return
		Calendar date = formtocalendar(iyyyy, imm, idd, ih, im, is, ampm);
		return date;
	}
	
	


	
	/**
	* Get the pretty "2 Days Ago" text as compared to current time
	* Must pass a GMT date
	*/
	public static String agoText(Calendar indate){
	    Logger logger = Logger.getLogger("com.pingfit.util.Time");
	    Calendar indateClone;
	    try{
	        indateClone = (Calendar) indate.clone();
        } catch (Exception e){
            logger.debug("input to agoText(indate) was not a date.");
            indateClone = Calendar.getInstance();
        }

        //Get time on the physical server (probably in Atlanta)
        Calendar now = Calendar.getInstance();
        //Convert from server (not GMT... server in Atlanta) time to gmt timezone.
        now = Time.convertFromOneTimeZoneToAnother(now, now.getTimeZone().getID(), "GMT");

		
		//Calculate datediff at various units
		int yearsago= DateDiff.dateDiff("year", now, indateClone);
		int monthsago= DateDiff.dateDiff("month", now, indateClone);
		int weeksago= DateDiff.dateDiff("week", now, indateClone);
		int daysago= DateDiff.dateDiff("day", now, indateClone);
		int hoursago= DateDiff.dateDiff("hour", now, indateClone);
		int minutesago= DateDiff.dateDiff("minute", now, indateClone);
		int secondsago= DateDiff.dateDiff("second", now, indateClone);
		int millisago= DateDiff.dateDiff("milli", now, indateClone);

		//Initialize output string
		String result="";
		
		//Used for debugging
		//String tmp="";
		//tmp = tmp + "<br>yearsago:" + yearsago;
		//tmp = tmp + "<br>monthsago:" + monthsago;
		//tmp = tmp + "<br>daysago:" + daysago;
		//tmp = tmp + "<br>hoursago:" + hoursago;
		//tmp = tmp + "<br>minutesago:" + minutesago;
		//tmp = tmp + "<br>secondsago:" + secondsago;
		//tmp = tmp + "<br>millisago:" + millisago;
		//tmp = tmp + "<br>";
		
		//Reminder:Need to deal with "Yesterday" case
		
		//Determine the best way to say it
		if (Num.absoluteValue(secondsago) < 60) {
			//Seconds
			result = agoTextEnd(secondsago, "Second");
		} else {
			if (Num.absoluteValue(minutesago) < 60) {
				//Minutes
				result = agoTextEnd(minutesago, "Minute");
			} else {
				if (Num.absoluteValue(hoursago) < 24) {
					//Hours
					result = agoTextEnd(hoursago, "Hour");
				} else {
					if (Num.absoluteValue(daysago) < 31) {
						//Days
						result = agoTextEnd(daysago, "Day");
						//Special for yesterday
                        if (daysago==1){
                            result = "Yesterday";
                        }
                        //Special for today
                        if (daysago==0){
                            result = "Today";
                        }
                        //Special for tomorrow
                        if (daysago==-1){
                            result = "Tomorrow";    
                        }
					} else {
						if (Num.absoluteValue(weeksago) < 4) {
							//Weeks
							result = agoTextEnd(weeksago, "Week");
						} else {
							if (Num.absoluteValue(monthsago) < 12) {
								//Months
								result = agoTextEnd(monthsago, "Month");
							} else {
								//Years
								result = agoTextEnd(yearsago, "Year");
							}
						}
					}
				}
			}
		}
		
		//Return the result to the user
		return result;
	}
	
	/**
	* Figures out the "Units Ago" part of it
	*/
	public static String agoTextEnd(int diff, String unit){
		String end="";
		String suffix = agoTextSuffix(diff);
		String ess = agoTextEss(diff);
		end = Num.absoluteValue(diff) + " " + unit + ess + " " + suffix;
		return end;
	}
	
	/**
	* Determines Ago or Future based on sign of diff
	*/
	public static String agoTextSuffix(int diff){
		String suffix="";		
		if (diff >= 0 ) {
			suffix="Ago";
		} else {
			suffix="In The Future";
		}
		return suffix;
	}
	
	/**
	* Determines whether we need to add an "s" to the end
	*/
	public static String agoTextEss(int diff){
		String s="";		
		if (diff > 1 || diff < -1 ) {
			s="s";
		} else {
			s="";
		}
		return s;
	}

	
	
	
	/**
	* Get the name of the graphic that shows "3 days ago"
	* Must pass a GMT date
	*/
	public static String agoGraphicText(Calendar indate){
	    Calendar indateClone = (Calendar) indate.clone();
	
		//Get current time
		Calendar now = Calendar.getInstance();
		Date currtime = new Date();
		now.setTime(currtime);
		
		//Calculate datediff at various units
		int yearsago= DateDiff.dateDiff("year", now, indateClone);
		int monthsago= DateDiff.dateDiff("month", now, indateClone);
		int weeksago= DateDiff.dateDiff("week", now, indateClone);
		int daysago= DateDiff.dateDiff("day", now, indateClone);
		int hoursago= DateDiff.dateDiff("hour", now, indateClone);
		int minutesago= DateDiff.dateDiff("minute", now, indateClone);
		int secondsago= DateDiff.dateDiff("second", now, indateClone);
		int millisago= DateDiff.dateDiff("milli", now, indateClone);

		//Initialize output string
		String result="";
		
		//Reminder:Need to deal with "Yesterday" case
		
		//Determine the best way to say it
        if (Num.absoluteValue(daysago) < 31) {
            //Days
            result = Num.absoluteValue(daysago) + "daysago.gif";
        } else {
            if (Num.absoluteValue(weeksago) < 4) {
                //Weeks
                result = Num.absoluteValue(weeksago) + "weeksago.gif";
            } else {
                if (Num.absoluteValue(monthsago) < 12) {
                    //Months
                    result = Num.absoluteValue(monthsago) + "monthsago.gif";
                } else {
                    //Years
                    result = Num.absoluteValue(yearsago) + "yearsago.gif";
                }
            }
        }

		
		result = "<img src='/images/ago/"+ result +"' border=0>";
		
		//Return the result to the user
		return result;
	}


    /**
     * Returns a calendar set to the earliest time x minutes ago.  Send minutesago=0 for this minute
     */
	public static Calendar xSecondsAgo(Calendar indate, int secondsago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the seconds
        outCal.add(Calendar.SECOND, (-1*secondsago));

        return outCal;
    }


	/**
     * Returns a calendar set to the earliest time x minutes ago.  Send minutesago=0 for this minute
     */
	public static Calendar xMinutesAgoStart(Calendar indate, int minutesago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.MINUTE, (-1*minutesago));

        //Set the time to 12:00:00AM
        outCal=minTimeMinute(outCal);

        return outCal;
    }

    public static Calendar xMinutesAgo(Calendar indate, int minutesago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.MINUTE, (-1*minutesago));

        return outCal;
    }

    /**
     * Returns a calendar set to the latest time x minutes ago.  Send minutesago=0 for this minute
     */
	public static Calendar xMinutesAgoEnd(Calendar indate, int minutesago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.MINUTE, (-1*minutesago));

        //Set the time to 12:00:00AM
        outCal=maxTimeMinute(outCal);

        return outCal;
    }


	/**
     * Returns a calendar set to the earliest time x hours ago.  Send daysago=0 for this hour
     */
	public static Calendar xHoursAgoStart(Calendar indate, int hoursago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.HOUR, (-1*hoursago));

        //Set the time to 12:00:00AM
        outCal=minTimeHour(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the latest time x hours ago.  Send hoursago=0 for this hour
     */
	public static Calendar xHoursAgoEnd(Calendar indate, int hoursago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.HOUR, (-1*hoursago));

        //Set the time to 12:00:00AM
        outCal=maxTimeHour(outCal);

        return outCal;
    }





	/**
     * Returns a calendar set to the earliest time x days ago.  Send daysago=0 for today
     */
	public static Calendar xDaysAgoStart(Calendar indate, int daysago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.DATE, (-1*daysago));

        //Set the time to 12:00:00AM
        outCal=minTime(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the latest time x days ago.  Send daysago=0 for today
     */
	public static Calendar xDaysAgoEnd(Calendar indate, int daysago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.DATE, (-1*daysago));

        //Set the time to 12:00:00AM
        outCal=maxTime(outCal);

        return outCal;
    }

	/**
     * Returns a calendar set to the first day of the week x weeks ago.  Send weeksago=0 for this week
     */
	public static Calendar xWeeksAgoStart(Calendar indate, int weeksago){
        Calendar outCal = (Calendar) indate.clone();

        //Calculate the number of days into the week that this is
        int dayofweek = outCal.get(Calendar.DAY_OF_WEEK);

        //Calculate number of days that need to be subtracted from the current calendar to find the desired week start
        int daystosubtractforstart = ((7*weeksago) + dayofweek) -1;

        //Set the time to 12:00:00AM
        outCal=minTime(outCal);

        //Do the subtraction
        outCal.add(Calendar.DATE, (-1*daystosubtractforstart));

        return outCal;
    }

    /**
     * Returns a calendar set to the last day of the week x weeks ago.  Send weeksago=0 for this week
     */
	public static Calendar xWeeksAgoEnd(Calendar indate, int weeksago){
	    Calendar outCal = (Calendar) indate.clone();

        //Calculate the number of days into the week that this is
        int dayofweek = outCal.get(Calendar.DAY_OF_WEEK);

        //Calculate number of days that need to be subtracted from the current calendar to find the desired week start
        int daystosubtractforstart = ((7*weeksago) + dayofweek) - 1;
        int daystosubtractforend = daystosubtractforstart - 6;

        //Do the subtraction
        outCal.add(Calendar.DATE, (-1*daystosubtractforend));

        //Set the time to 11:59:59PM
        outCal=maxTime(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the first day of the month x months ago.  Send monthsago=0 for this month
     */
	public static Calendar xMonthsAgoStart(Calendar indate, int monthsago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.MONTH, (-1*monthsago));

        //Set the day to the first day
        outCal.set(Calendar.DATE, 1);

        //Set the time to 12:00:00AM
        outCal=minTime(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the last day of the month x months ago.  Send monthsago=0 for this month
     */
	public static Calendar xMonthsAgoEnd(Calendar indate, int monthsago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the months
        outCal.add(Calendar.MONTH, (-1*monthsago));

        //Set the day to the last day
        outCal.set(Calendar.DATE, outCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        //Set the time to 11:59:59PM
        outCal=maxTime(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the first day of the year x years ago.  Send yearsago=0 for this year.
     */
	public static Calendar xYearsAgoStart(Calendar indate, int yearsago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the year
        outCal.add(Calendar.YEAR, (-1*yearsago));

        //Set the month to the first day
        outCal.set(Calendar.MONTH, 0);

        //Set the day to the first day
        outCal.set(Calendar.DATE, 1);

        //Set the time to 12:00:00AM
        outCal=minTime(outCal);

        return outCal;
    }

    /**
     * Returns a calendar set to the last day of the year x years ago.  Send yearsago=0 for this year.
     */
	public static Calendar xYearsAgoEnd(Calendar indate, int yearsago){
        Calendar outCal = (Calendar) indate.clone();

        //Subtract the year
        outCal.add(Calendar.YEAR, (-1*yearsago));

        //Set the month to the last month
        outCal.set(Calendar.MONTH, outCal.getActualMaximum(Calendar.MONTH));

        //Set the day to the last day
        outCal.set(Calendar.DATE, outCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        //Set the time to 11:59:59PM
        outCal=maxTime(outCal);

        return outCal;
    }

    /**
     * Set the time of this calendar to the earliest time in the day
     */
     public static Calendar minTime(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        //Set the time to 12:00:00AM
        outCal.set(Calendar.HOUR_OF_DAY, outCal.getActualMinimum(Calendar.HOUR_OF_DAY));
        outCal.set(Calendar.MINUTE, outCal.getActualMinimum(Calendar.MINUTE));
        outCal.set(Calendar.SECOND, outCal.getActualMinimum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMinimum(Calendar.MILLISECOND));

        return outCal;
     }

     /**
     * Set the time of this calendar to the latest time in the day
     */
     public static Calendar maxTime(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        //Set the time to 11:59:59PM
        outCal.set(Calendar.HOUR_OF_DAY, outCal.getActualMaximum(Calendar.HOUR_OF_DAY));
        outCal.set(Calendar.MINUTE, outCal.getActualMaximum(Calendar.MINUTE));
        outCal.set(Calendar.SECOND, outCal.getActualMaximum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMaximum(Calendar.MILLISECOND));

        return outCal;
     }

     /**
     * Set the time of this calendar to the earliest time in the day
     */
     public static Calendar minTimeHour(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        outCal.set(Calendar.MINUTE, outCal.getActualMinimum(Calendar.MINUTE));
        outCal.set(Calendar.SECOND, outCal.getActualMinimum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMinimum(Calendar.MILLISECOND));

        return outCal;
     }

     /**
     * Set the time of this calendar to the latest time in the day
     */
     public static Calendar maxTimeHour(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        outCal.set(Calendar.MINUTE, outCal.getActualMaximum(Calendar.MINUTE));
        outCal.set(Calendar.SECOND, outCal.getActualMaximum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMaximum(Calendar.MILLISECOND));

        return outCal;
     }

     /**
     * Set the time of this calendar to the earliest time in the day
     */
     public static Calendar minTimeMinute(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        outCal.set(Calendar.SECOND, outCal.getActualMinimum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMinimum(Calendar.MILLISECOND));

        return outCal;
     }

     /**
     * Set the time of this calendar to the latest time in the day
     */
     public static Calendar maxTimeMinute(Calendar incal){
        Calendar outCal = (Calendar) incal.clone();

        outCal.set(Calendar.SECOND, outCal.getActualMaximum(Calendar.SECOND));
        outCal.set(Calendar.MILLISECOND, outCal.getActualMaximum(Calendar.MILLISECOND));

        return outCal;
     }

//     /**
//     * Given a millisecond count will return the number of weeks ago that this occurs in as compared to today.
//     */
//     public static int milliWeeksAgo(int millis){
//
//        //Create a reference calendar for now
//        Calendar now = Calendar.getInstance();
//
//        //Convert millis to a calendar.
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.MILLISECOND, millis);
//
//        int weeksago=reger.core.DateDiff.DateDiff("week", now, cal);
//
//        return weeksago;
//     }

     /**
     * Determines whether a given calendar is within a week segment x weeksago
     * Period should be "week" or "month"
     */
     public static boolean isInTimeAgoPeriod(int periodsago, Calendar cal, String period){

        //Find the starting date bound for weeksago weeks ago today
        Calendar start=Calendar.getInstance();
        Calendar end=Calendar.getInstance();;
        if (period.equals("week")){
            start = xWeeksAgoStart(Calendar.getInstance(), periodsago);
            end = xWeeksAgoEnd(Calendar.getInstance(), periodsago);
        } else if (period.equals("month")) {
            start = xMonthsAgoStart(Calendar.getInstance(), periodsago);
            end = xMonthsAgoEnd(Calendar.getInstance(), periodsago);
        } else if (period.equals("day")) {
            start = xDaysAgoStart(Calendar.getInstance(), periodsago);
            end = xDaysAgoEnd(Calendar.getInstance(), periodsago);
        }

        //Do the comparison
        if (cal.after(start) && cal.before(end)){
            return true;
        }

        return false;
     }

     /**
     * Returns the text name of a month that the calendar is set to
     */
     public static String monthname(Calendar incal){

         if (incal.get(Calendar.MONTH)== Calendar.JANUARY){
             return "January";
         } else if (incal.get(Calendar.MONTH)== Calendar.FEBRUARY) {
            return "February";
         } else if (incal.get(Calendar.MONTH)== Calendar.MARCH) {
            return "March";
         } else if (incal.get(Calendar.MONTH)== Calendar.APRIL) {
            return "April";
         } else if (incal.get(Calendar.MONTH)== Calendar.MAY) {
            return "May";
         } else if (incal.get(Calendar.MONTH)== Calendar.JUNE) {
            return "June";
         } else if (incal.get(Calendar.MONTH)== Calendar.JULY) {
            return "July";
         } else if (incal.get(Calendar.MONTH)== Calendar.AUGUST) {
            return "August";
         } else if (incal.get(Calendar.MONTH)== Calendar.SEPTEMBER) {
            return "September";
         } else if (incal.get(Calendar.MONTH)== Calendar.OCTOBER) {
            return "October";
         } else if (incal.get(Calendar.MONTH)== Calendar.NOVEMBER) {
            return "November";
         } else if (incal.get(Calendar.MONTH)== Calendar.DECEMBER) {
            return "December";
         }

         return "";
     }

     /**
     * Returns the text name of a month that the calendar is set to
     */
     public static String dayname(Calendar incal){

         if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.SUNDAY){
             return "Sunday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.MONDAY) {
            return "Monday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.TUESDAY) {
            return "Tuesday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.WEDNESDAY) {
            return "Wednesday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.THURSDAY) {
            return "Thursday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.FRIDAY) {
            return "Friday";
         } else if (incal.get(Calendar.DAY_OF_WEEK)== Calendar.SATURDAY) {
            return "Saturday";
         }
         
         return "";
     }


    public static int GetDaysInMonth(int iMonth, int iYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(iYear, iMonth, 1, 1, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int GetWeekdayMonthStartsOn(int iMonth, int iYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(iYear, iMonth, 1, 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Calendar SubtractOneMonth(Calendar cal){
        Calendar outCal = (Calendar) cal.clone();
        outCal.add(Calendar.MONTH, -1);
        return outCal;
    }

    public static Calendar AddOneMonth(Calendar cal){
        Calendar outCal = (Calendar) cal.clone();
        outCal.add(Calendar.MONTH, 1);
        return outCal;
    }

    public static Calendar addOneYear(Calendar cal){
        Calendar outCal = (Calendar) cal.clone();
        outCal.add(Calendar.YEAR, 1);
        return outCal;
    }

    public static Calendar subtractOneYear(Calendar cal){
        Calendar outCal = (Calendar) cal.clone();
        outCal.add(Calendar.YEAR, -1);
        return outCal;
    }

    public static Calendar subtractYear(Calendar cal, int howmanytosubtract){
        Calendar outCal = (Calendar) cal.clone();
        outCal.add(Calendar.YEAR, -1*howmanytosubtract);
        return outCal;
    }

    public static String nowInGmtString(){
        return dateformatfordb(nowInUserTimezone("GMT"));
    }

    public static Calendar nowInGmtCalendar(){
        return nowInUserTimezone("GMT");
    }

    public static Calendar getRandomDateInPast(int maxAgeInDays){
        int maxAgeInMinutes = maxAgeInDays * 60 * 24;
        Calendar tmpDate = Time.nowInGmtCalendar();
        return Time.xMinutesAgoStart(tmpDate, Num.randomInt(maxAgeInMinutes));
    }

}
