/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.countries;

import yahamp.model.earth.Position;

/** Info about a callsign prefix
 *  <p>
 *  Some contries have just one callsign prefix,
 *  CQ and ITU zone etc., but some have more.
 *  So there might be several entries that
 *  match except for the prefix.
 *
 *  @author Kay Kasemir
 */
public class PrefixInfo
{
    /** A callsign prefix ("VE" or "VA2BY" or ...). */
    private final String prefix;

    /** Name ("Canada"). */
    private final String country;

    /** Primary DXCC prefix ("VE"). */
    private final String dxcc;

    /** Continent ("NA"). */
    private final String continent;

    /** CQ Zone (5) */
    private final int cq;

    /** ITU Zone (8) */
    private final int itu;

    /** Approximate position. */
    private final Position position;

    /** Time zone (offset from GMT). */
    double gmt_offset;

    /** Create from parameters. */
    public PrefixInfo(final String prefix, final String country, final String dxcc, final String continent, final int cq_zone, final int itu_zone,
            final double latitude, final double longitude, final double gmt_offset)
    {
        this.prefix = prefix;
        this.country = country;
        this.dxcc = dxcc;
        this.continent = continent;
        this.cq = cq_zone;
        this.itu = itu_zone;
        this.position = new Position(latitude, longitude);
        this.gmt_offset = gmt_offset;
    }

    /** @return Returns the continent. */
    public String getContinent()
    {
        return continent;
    }

    /** @return Returns the country. */
    public String getCountry()
    {
        return country;
    }

    /** @return Returns the CQ zone. */
    public int getCQ()
    {
        return cq;
    }

    /** @return Returns the DX CC. */
    public String getDXCC()
    {
        return dxcc;
    }

    /** @return Returns the ITU Zone. */
    public int getITU()
    {
        return itu;
    }

    /** @return Returns the position. */
    public Position getPosition()
    {
        return position;
    }

    /** @return Returns the prefix. */
    public String getPrefix()
    {
        return prefix;
    }

    /** @return Returns the time_zone. */
    public double getGMToffset()
    {
        return gmt_offset;
    }

    @Override
    @SuppressWarnings("nls")
    public String toString()
    {
        return "Prefix '" + prefix + "': " + country + ", DXCC '" + dxcc +
            ", Continent " + continent + ", CQ " + cq + ", ITU " + itu +
            ", Position  " + position + ", GMT " + gmt_offset;
    }
}
