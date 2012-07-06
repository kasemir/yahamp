/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** One coordinate in degrees.
 * 
 *  Consists of degrees, minutes, seconds,
 *  but can also use fractional degrees.
 *  
 *  The pieces (degs, mins, secs) are all kept
 *  positive, and a separate isNegative()/isPositive()
 *  indicator tells the about the sign.
 */
public class Coordinate
{
	final private double degrees;

	/** Initialize Coordinate with pieces.
	 * 
	 *  @param degrees (positive or negative)
	 *  @param minutes
	 *  @param seconds
	 */
	public Coordinate(double degrees, double minutes, double seconds)
	{
        minutes = Math.abs(minutes);
        seconds = Math.abs(seconds);
        if (degrees >= 0.0)
            this.degrees = degrees + (minutes + seconds/60.0)/60.0;
        else
            this.degrees = degrees - (minutes + seconds/60.0)/60.0;
	}
	
	/** Initialize Coordinate with fractional degrees. */
	public Coordinate(double degrees)
	{
        this.degrees = degrees;
	}
    
    /** @return Full fractional degrees, positive or negative. */
    public double getDegrees()
    {
        return degrees;
    }

    /** @return Minutes, 0..59, always positive. */
	public int getMinutes()
    {
        final int deg = (int) degrees;
        double rest = Math.abs(degrees - deg);
        return (int) (rest * 60);
    }

    /** @return Seconds, 0..59.999, always positive. */
    public double getSeconds()
    {
        final int deg = (int) degrees;
        double rest = Math.abs(degrees - deg);
        final int minutes = (int) (rest * 60);
        rest = rest*60.0 - minutes;
        return 60.0*rest;
    }
    
    /** @return Radians, i.e. -PI .. +PI. */
    public double getRadians()
    {
        return Math.toRadians(getDegrees());
    }
    
    /** Convert to string in fractional format. */
    @Override
    @SuppressWarnings("nls")
    public String toString()
    {
        return (degrees < 0.0 ? "-" : "") + getAbsoluteString();
    }

    /** Convert to string in fractional format, omitting the sign. */
    public String getAbsoluteString()
    {
        final int deg = (int) Math.abs(degrees);
        return String.format("%d %d'%.1f''", deg, getMinutes(), getSeconds()); //$NON-NLS-1$
    }

	public static Coordinate fromString(String string)
    {
		// 36 1'25.6''
		final Pattern pattern = Pattern.compile("([+-]?[0-9.]+)\\s*([0-9]+')?\\s*([0-9.]+'')?");
		final Matcher matcher = pattern.matcher(string);
		if (matcher.matches())
		{
			final double degrees = Double.parseDouble(matcher.group(1));
			
			final double minutes;
			String sub = matcher.group(2);
			if (sub != null  &&  sub.length() > 1)
				minutes = Double.parseDouble(sub.substring(0, sub.length()-1));
			else
				minutes = 0;
			
			final double seconds;
			sub = matcher.group(3);
			if (sub != null  &&  sub.length() > 2)
				seconds = Double.parseDouble(sub.substring(0, sub.length()-2));
			else
				seconds = 0;
			return new Coordinate(degrees, minutes, seconds);
		}
	    return null;
    }
}
