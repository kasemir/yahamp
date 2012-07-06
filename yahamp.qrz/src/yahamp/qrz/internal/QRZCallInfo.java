/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import yahamp.model.CallInfo;

/** QRZ Call Sign info
*
*  <p>Wraps the {@link CallInfo} with JAXB annotations.
*
*  @author Kay Kasemir
*/
@XmlAccessorType(XmlAccessType.NONE)
public class QRZCallInfo extends CallInfo
{
	/** First name */
	@XmlElement(name="fname")
	private String first_name = "";

	/** Last name */
	@XmlElement(name="name")
	private String last_name = "";

	/** @return Call sign */
	@XmlElement(name="call")
    @Override
	public String getCall()
    {
    	return super.getCall();
    }

	/** @param callsign Call sign */
	public void setCall(final String callsign)
    {
    	this.call = callsign;
    }

	@Override
    public String getName()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append(first_name);
		if (!last_name.isEmpty())
		{
			if (buf.length() > 0)
				buf.append(" ");
			buf.append(last_name);
		}
		return buf.toString();
	}

	/** Setting the name is not supported.
	 *  JAXB will set first_name, last_name
	 *  which we present as one name
	 */
	@Override
    public void setName(final String name)
    {
	    throw new UnsupportedOperationException();
    }

	@XmlElement(name="addr1")
	@Override
    public String getAddress()
    {
	    return super.getAddress();
    }

	// Unclear why every set* must be present in this class,
	// but the inherited set* calls from the base class
	// will not be used by JAXB
	@Override
    public void setAddress(final String address)
    {
        super.setAddress(address);
    }

	@XmlElement(name="addr2")
	@Override
    public String getCity()
    {
    	return super.getCity();
    }

    @Override
	public void setCity(final String city)
    {
        super.setCity(city);
    }

	@XmlElement(name="state")
	@Override
    public String getState()
    {
    	return super.getState();
    }

	@Override
    public void setState(final String state)
    {
        super.setState(state);
    }

	@XmlElement(name="zip")
	@Override
    public String getZip()
    {
    	return super.getZip();
    }

	@Override
    public void setZip(final String zip)
	{
	    super.setZip(zip);
	}

	@XmlElement(name="county")
	@Override
    public String getCounty()
    {
    	return super.getCounty();
    }

	@Override
    public void setCounty(final String county)
	{
	    super.setCounty(county);
	}

	@XmlElement(name="land")
	@Override
    public String getCountry()
    {
    	return super.getCountry();
    }

	@Override
    public void setCountry(final String country)
	{
	    super.setCountry(country);
	}

	@XmlElement(name="grid")
	@Override
    public String getGrid()
    {
    	return super.getGrid();
    }

	@Override
    public void setGrid(final String grid)
    {
	    super.setGrid(grid);
    }
}
