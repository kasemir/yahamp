/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

/** A position, consisting of latitude (North) and longitude (West).
 *  <p>
 *  The Southern and Western directions are represented
 *  by negative coordinates internally, but when
 *  shown as a string, positive numbers with N, S, E, W are used.
 */
public class Position
{
	private Coordinate latitude;
	private Coordinate longitude;
	
	/** Initialize with given position. */
	public Position(Coordinate latitude, Coordinate longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
    /** Initialize with given position. */
    public Position(double latitude, double longitude)
    {
        this.latitude = new Coordinate(latitude);
        this.longitude = new Coordinate(longitude);
    }
    
	/** Convert to string.
	 *  @return Returns for example "86 1'25'' N, 84 12'56'' W"
	 */
	@Override
    @SuppressWarnings("nls")
    public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(latitude.getAbsoluteString());
		if (latitude.getDegrees() >= 0)
			result.append( " N, ");
		else
			result.append( " S, ");
		
		result.append(longitude.getAbsoluteString());
		if (longitude.getDegrees() >= 0)
			result.append( " E");
		else
			result.append( " W");
		return result.toString();
	}
	
	/** @return Returns the latitude, i.e. "degrees North". */
	public Coordinate getLatitude()
	{
		return latitude;
	}
	
	/** @return Returns the longitude, i.e. "degrees East". */
	public Coordinate getLongitude()
	{
		return longitude;
	}
	
	/** @param latitude The latitude to set. */
	public void setLatitude(Coordinate latitude)
	{
		this.latitude = latitude;
	}

	/** @param longitude The longitude to set. */
	public void setLongitude(Coordinate longitude)
	{
		this.longitude = longitude;
	}
}
