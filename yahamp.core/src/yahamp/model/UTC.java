/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Handles the current "UTC" time.
 *  <p>
 *  Basically wraps <code>Calendar</code>,
 *  calls to which I didn't want to duplicate all over the code.
 *
 *  For what it's worth, could re-thing the implementation
 *  based on a Calendar in the UTC time zone
 *  Calendar utc_cal = Calendar.getInstance(new SimpleTimeZone(0, "UTC"));
 */
@SuppressWarnings("nls")
public class UTC implements Comparable<UTC>
{
    /** String in time text to indicate 'local' time */
	private static final String LOCAL_FLAG = "l";

	/** HHMM or HH:MM pattern */
	private static Pattern time_pattern =
		Pattern.compile("([0-9][0-9]):?([0-9][0-9])");

    private static SimpleDateFormat utc_parser =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private static SimpleDateFormat local_parser =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat excel_parser =
        new SimpleDateFormat("dd-MMM-yy HH:mm Z");

    final private static TimeZone utc_zone = TimeZone.getTimeZone("+0000");

    final private Calendar utc = Calendar.getInstance(utc_zone);

    /** @return Returns the UTC/GMT time zone. */
    public static TimeZone getTimeZone()
    {
        return utc_zone;
    }

    /** Create UTC set to 'now'. */
    public UTC()
    {
        // utc already set in member initializer
    }

    /** Create UTC from milliseconds. */
    public UTC(final long millis)
    {
        utc.setTimeInMillis(millis);
    }

    /** Create UTC from Date. */
    public UTC(final Date date)
    {
        this(date.getTime());
    }

    /** Create UTC for date and time.
     *  @param date "yyyy-mm-dd"
     *  @param time "HHMM" or "HH:MM" or "HH:MM:SS". May include the LOCAL_FLAG
     *              to indicate local time instead of UTC
     *  @exception Throws exception when date or time don't parse.
     */
    public UTC(final String date, String time) throws Exception
    {
        final int local_idx = time.toLowerCase().indexOf(LOCAL_FLAG);
        final boolean is_local = local_idx >= 0;
        if (is_local)
        {
            time = time.substring(0, local_idx)
                + time.substring(local_idx + 1);
        }
        try
        {
        	// Does time match "HHMM" or "HH:MM"?
        	final Matcher m = time_pattern.matcher(time);
        	if (m.matches()) // -> append ":00"
        		time = m.group(1) + ":" + m.group(2) + ":00";
            Date d;
            if (is_local)
                d = local_parser.parse(date + " " + time);
            else
                d = utc_parser.parse(date + " " + time + " +0000");
            utc.setTimeInMillis(d.getTime());
        }
        catch (final Exception e)
        {
        		throw new Exception("Date has to match '2006-01-18',"
        				+ " and time has to match '15:42'");
        }
    }

    /** Create UTC for date and time given in hours.
     *  @param date "yyyy-mm-dd"
     *  @param hours 0..24
     *  @exception Throws exception when date or time don't parse.
     */
    public UTC(final String date, final double hours) throws Exception
    {
        try
        {
            final Date d = utc_parser.parse(date+ " 00:00:00 +0000");
            final long millis = d.getTime() + ((long)(hours*60.0*60.0))*1000;
            utc.setTimeInMillis(millis);
        }
        catch (final Exception e)
        {
                throw new Exception("Date has to match '2006-01-18'");
        }
    }

    public Calendar getCalendar()
    {
        return (Calendar) utc.clone();
    }

    /** @return Time as milliseconds since epoch */
    public long getTimeInMillis()
    {
        return utc.getTimeInMillis();
    }

    /** Parse date and time "11-Feb-06 19:55". */
    public void parseExcel(final String date, final String time) throws Exception
    {
        final Date d = excel_parser.parse(date + " " + time + " +0000");
        utc.setTimeInMillis(d.getTime());
    }

    /** @return Returns the date "yyyy-mm-dd". */
    public String toDateString()
    {
        return String.format("%4d-%02d-%02d",
                utc.get(Calendar.YEAR),
                utc.get(Calendar.MONTH) + 1,
                utc.get(Calendar.DAY_OF_MONTH)
                );
    }

    /** @return Returns the time as "HH:MM:SS". */
    public String toTimeString()
    {
        return String.format("%02d:%02d:%02d",
                getHours(), getMinutes(), getSeconds());
    }

    /** @return Returns the time as "HH:MM". */
    public String toShortTimeString()
    {
        return String.format("%02d:%02d",
                getHours(), getMinutes());
    }

    /** @return Returns the date and time as "yyyy-mm-dd HH:MM". */
    public String toShortString()
    {
        return String.format("%s %s",
                toDateString(), toShortTimeString());
    }

    public int getYear()
    {
        return utc.get(Calendar.YEAR);
    }

    public int getMonth()
    {
        return utc.get(Calendar.MONTH) + 1;
    }

    public int getDay()
    {
        return utc.get(Calendar.DATE);
    }

    /** @return Hours 0..23 */
    public int getHours()
    {
        return utc.get(Calendar.HOUR_OF_DAY);
    }

    /** @return Minutes 0..59 */
    public int getMinutes()
    {
        return utc.get(Calendar.MINUTE);
    }

    /** @return Seconds 0..59 */
    public int getSeconds()
    {
        return utc.get(Calendar.SECOND);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return utc.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof UTC))
            return false;
        final UTC other = (UTC) obj;
        return utc.equals(other.utc);
    }

    /** Compares two UTC times.
     *  @return Returns a value less than, equal or greater than zero.
     */
    @Override
	public int compareTo(final UTC other)
    {
        return utc.compareTo(other.utc);
    }

    /** @return Returns the date and time as "yyyy-mm-dd HH:MM:SS". */
    @Override
    public String toString()
    {
        return String.format("%s %s", toDateString(), toTimeString());
    }
}
