/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/** JAXB Adapter between date string in XML and Date
 *  @author Kay Kasemir
 */
public class XmlDateAdapter extends XmlAdapter<String, Date>
{
	// Wed Jan 1 12:34:03 2010
    private final SimpleDateFormat dateFormat =
    	new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy");

    /** {@inheritDoc} */
    @Override
    public String marshal(final Date date) throws Exception
    {
        return dateFormat.format(date);
    }

    /** {@inheritDoc} */
    @Override
    public Date unmarshal(final String date) throws Exception
    {
    	// Use fake 1-day expiration for non-subscriber
    	if ("non-subscriber".equals(date))
    		return new Date(new Date().getTime() + 24*60*60*1000L);
        return dateFormat.parse(date);
    }
}
