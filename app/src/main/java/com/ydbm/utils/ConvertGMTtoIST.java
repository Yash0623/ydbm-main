package com.ydbm.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by Maitri on 03-04-2018.
 */

public class ConvertGMTtoIST {


    public static String getDateChatTimeFromGMT(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        TimeZone timeZone1 = TimeZone.getTimeZone("GMT");// IMP !!!
        dateFormat.setTimeZone(timeZone1);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        try {
            String formattedDate = df.format(dateFormat.parse(date).getTime());
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String getDateFromGMT(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        TimeZone timeZone1 = TimeZone.getTimeZone("GMT");// IMP !!!
        dateFormat.setTimeZone(timeZone1);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try {
            String formattedDate = df.format(dateFormat.parse(date).getTime());
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static long getLongTime(String date1) {
        long time = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        //   SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

        Date date = null;
        try {
            date = format.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        time = date.getTime();

        return time;
    }

    public static String getDateFromTt(String date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        TimeZone timeZone1 = TimeZone.getTimeZone("GMT");// IMP !!!
        dateFormat.setTimeZone(timeZone1);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String formattedDate = df.format(dateFormat.parse(date).getTime());
            Log.d("RETURNEDDATE888", formattedDate.toString() + "<---");
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String convertISTtoGMTFormat() {
        String dt = null;

        SimpleDateFormat GMTTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        {
            GMTTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        // dt = GMTTimeFormatter.format(Calendar.getInstance().getTime(), Locale.getDefault());
        dt = GMTTimeFormatter.format(new Date());
        Log.d("cHECKDATEINTZ", dt + "<---");
        return dt;
    }

    public static String getCurrentDate() {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String getCurrentDateBookingList() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        return date;
    }
    public static String getCurrentDateAndTime() {
        String date = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(new Date());
        return date;
    }

    public static Date getCurrentDateActive() {
        Date date1 = null;
        //   String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
    public static Date getDateActiveCheckout(String dt) {
        Date date1 = null;
        //   String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static String getTomorrowDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);

        return dateFormat.format(cal.getTime());
    }

    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getUtcCurrentTimeStamp() {
        Date date = new Date();
        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC Time is: " + dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static String getCurrentDateFromUTC(String date1) {
        String dtStart = date1;
        String dt = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            date = format.parse(dtStart);

            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dayOfTheWeek = sdf.format(date);
        return dayOfTheWeek;
    }

    public static String Find_Duration(String start_date, String end_date) {
        String Etime = null;
        try {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //String to_date = "2018-10-03 13:08:00";
            Date startDate = DF.parse(start_date);
            //String from_date = "2018-10-04 08:45:19";
            Date endDate = DF.parse(end_date);
            long duration = endDate.getTime() - startDate.getTime();

           /* long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);*/
            // long diffInSeconds = duration / 1000 % 60;
            long diffInMinutes = duration / (60 * 1000) % 60;
            int diffInHours = (int) (duration / (60 * 60 * 1000) % 24);
            //long diffInDays = duration / (24 * 60 * 60 * 1000);
            Log.d("Duration", "DURINTIME:" + duration);
            Etime = diffInHours + "," + diffInMinutes;
            Log.d("Duration", "Duration:" + diffInHours + "hours," + "----" + Etime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Etime;
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDateandTime = databaseDateTimeFormate.format(new Date());
        return currentDateandTime;
    }

    public static String getCurrentDateTimeLastSeen() {
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String currentDateandTime = databaseDateTimeFormate.format(new Date());
        return currentDateandTime;
    }

    public static String getCurrentDateTime24Hours() {
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = databaseDateTimeFormate.format(new Date());
        return currentDateandTime;
    }

    public static String getMOMDAtetime(String startTime) {

        StringTokenizer tk = new StringTokenizer(startTime);
        String date = tk.nextToken();
        String time = tk.nextToken();
        String s = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a dd:MMM:yyyy ");
        Date dt;
        try {
            dt = sdf.parse(startTime);
            s = sdfs.format(dt);
            System.out.println("Time Display: " + s); // <-- I got result here
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static long Find_DurationInMinus(String start_date, String end_date) {
        long Etime = 0;
        try {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //String to_date = "2018-10-03 13:08:00";
            Date startDate = DF.parse(start_date);
            //String from_date = "2018-10-04 08:45:19";
            Date endDate = DF.parse(end_date);
            long duration = endDate.getTime() - startDate.getTime();

           /* long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);*/
            // long diffInSeconds = duration / 1000 % 60;
            long diffInMinutes = duration / (60 * 1000) % 60;
            //int diffInHours = (int) (duration / (60 * 60 * 1000) % 24);
            //long diffInDays = duration / (24 * 60 * 60 * 1000);
            Log.d("Duration", "DURINTIME:" + duration);
            Etime = diffInMinutes;
            Log.d("Duration", "Duration:" + "hours,");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Etime;
    }

    public static void printdiff() {
        //DateTimeUtils obj = new DateTimeUtils();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        try {
            Date date1 = simpleDateFormat.parse("10/10/2013 11:30:10");
            Date date2 = simpleDateFormat.parse("13/10/2013 20:35:55");

            printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

//1 minute = 60 seconds
//1 hour = 60 x 60 = 3600
//1 day = 3600 x 24 = 86400

    }

    public static int Find_DurationInDays(String start_date, String end_date) {
        int Etime = 0;
        try {
            SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
            //String to_date = "2018-10-03 13:08:00";
            Date startDate = DF.parse(start_date);
            //String from_date = "2018-10-04 08:45:19";
            Date endDate = DF.parse(end_date);
            long different = endDate.getTime() - startDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;


            Etime = (int) elapsedDays;
            Log.d("Duration", "Duration:" + "days,");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Etime;
    }

    public static String getCurrentDATE(String date) {
        String mytime = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyyha");

        Date myDate = null;
        try {
            myDate = dateFormat.parse(mytime);

        } catch (ParseException e) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                    "dd-MM-yyyy");


            try {
                myDate = dateFormat.parse(mytime);

            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = null;

        currentDateandTime = databaseDateTimeFormate.format(myDate);

        return currentDateandTime;
    }

    public static String diffrenceDates(String start, String end) {
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String diff12 = null;
        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(start);
            d2 = format.parse(end);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            if (diffDays > 0) {
                diff12 = diffDays + " days";
            } else if (diffHours > 0) {
                diff12 = diffHours + " hours";
            } else if (diffMinutes > 0) {
                diff12 = diffMinutes + " min.";
            } else if (diffSeconds > 0) {
                diff12 = diffSeconds + " seconds";
            } else {
                diff12 = "0 seconds";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff12;


    }

    public static int printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return (int) elapsedDays;
    }
}
