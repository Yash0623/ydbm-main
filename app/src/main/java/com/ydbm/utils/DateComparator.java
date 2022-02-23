package com.ydbm.utils;

import com.ydbm.models.BookingDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<BookingDetails>
{
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public int compare(BookingDetails lhs, BookingDetails rhs)
    {
        try {
            return dateFormat.parse(lhs.getCheckinDt()).compareTo(dateFormat.parse(rhs.getCheckinDt()));
        } catch (ParseException e) {

            e.printStackTrace();
            return 0;
        }
    }
}