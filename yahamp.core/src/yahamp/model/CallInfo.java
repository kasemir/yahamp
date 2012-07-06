/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

/** Detail for a call sign: Name, address, ...
 *  @author Kay Kasemir
 */
public class CallInfo extends Callsign
{
	private String name = "";
	private String address = "";
	private String city = "";
	private String country = "";
	private String state = "";
	private String zip = "";
	private String grid = "";
	private String county = "";
	private String dxcc = "";
	private int fists_nr = 0;
	private int fists_cc = 0;
	private int fp_nr = 0;

	/** Initialize with call sign, rest empty
	 *  @param callsign
	 */
	public CallInfo(final String callsign)
	{
		super(callsign);
	}

    /** Derived classes can use no-arg constructor */
    protected CallInfo()
    {
    }

	/** Initialize with values
	 *  @param callsign
	 *  @param name
	 *  @param address
	 *  @param city
	 *  @param state
	 *  @param zip
	 *  @param grid
	 *  @param county
	 *  @param country
	 *  @param fists_nr
	 *  @param fists_cc
	 *  @param fp_nr
	 */
	public CallInfo(final String callsign, final String name, final String address,
			final String city, final String state, final String zip, final String grid,
			final String county, final String country, final int fists_nr, final int fists_cc,
			final int fp_nr)
	{
		super(callsign);
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.grid = grid;
		this.county = county;
		this.country = country;
		this.fists_nr = fists_nr;
		this.fists_cc = fists_cc;
		this.fp_nr = fp_nr;
	}

    public String getName()
    {
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(final String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(final String city)
	{
		this.city = city;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(final String country)
	{
		this.country = country;
	}

	public String getState()
	{
		return state;
	}

	public void setState(final String state)
	{
		this.state = state;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(final String zip)
	{
		this.zip = zip;
	}

	public String getGrid()
	{
		return grid;
	}

	public void setGrid(final String grid)
	{
		this.grid = grid;
	}

	public String getCounty()
	{
		return county;
	}

	public void setCounty(final String county)
	{
		this.county = county;
	}

	public String getDxcc()
    {
        return dxcc;
    }

	public void setDXCC(final String dxcc)
	{
	    this.dxcc = dxcc;
	}

    public int getFists_nr()
	{
		return fists_nr;
	}

	public void setFists_nr(final int fistsNr)
	{
		fists_nr = fistsNr;
	}

	public int getFists_cc()
	{
		return fists_cc;
	}

	public void setFists_cc(final int fistsCc)
	{
		fists_cc = fistsCc;
	}

	public int getFp_nr()
	{
		return fp_nr;
	}

	public void setFp_nr(final int fpNr)
	{
		fp_nr = fpNr;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(call);
		builder.append(": Name=");
		builder.append(getName());
		builder.append(", address=");
		builder.append(address);
		builder.append(", city=");
		builder.append(city);
		builder.append(", zip=");
		builder.append(zip);
		builder.append(", state=");
		builder.append(state);
		builder.append(", county=");
		builder.append(county);
		builder.append(", country=");
		builder.append(country);
        builder.append(", grid=");
		builder.append(grid);
		builder.append(", fists_cc=");
		builder.append(fists_cc);
		builder.append(", fists_nr=");
		builder.append(fists_nr);
		builder.append(", fp_nr=");
		builder.append(fp_nr);
		return builder.toString();
	}
}
