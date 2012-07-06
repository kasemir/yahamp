/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import yahamp.model.UTC;

/** Julian Day computation according to Jean Meeus.
 *  <p>
 *  Julien day number is defined as the number of days since
 *  noon Universal Time (UT) on Monday, January 1, 4713 BC.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JulianDay
{
    private final double julian_day;

    /** Julian day number for J2000. */
    public static final double J2000 = 2451545.0;

    /** Days in a Julian Century. */
    public static final double Century = 36525.0;

    /** Construct JD from UTC time. */
    public JulianDay(final UTC utc)
    {
        this(utc.getYear(), utc.getMonth(), (double) utc.getDay(),
             utc.getHours() + utc.getMinutes()/60.0);
    }

    /** Construct JD from date and time pieces.
     *  @param year The year, 19xx
    /** @param month The month, 1..12
    /** @param day The day, 1..31, might include 0.5 for 'noon' etc.
    /** @param hours Hours within the day, 0..24
     */
    public JulianDay(int year, int month, double day, final double hours)
    {
        day += hours/24.0;
        if (month <= 2)
        {
            // January and Feb. are considered the 13th and 14th month
            // of the previous year...
            --year;
            month += 12;
        }
        // Leap year handling in Gregorian Calendar
        final int a = year/100;
        final int b = 2 - a + a/4;
        // Result...
        julian_day = Math.floor(365.25*(year+4716))
                    + Math.floor(30.6001*(month+1))
                    + day + b - 1524.5;
    }

    @Override
    public String toString()
    {
        return "JD " + julian_day;
    }

    /** @return the Julian Day number. */
    public double get()
    {
        return julian_day;
    }

    /** @return Julian centuries relative to J2000 epoch. */
    public double getJ2000Centuries()
    {
        return (julian_day - J2000)/Century;
    }
}
