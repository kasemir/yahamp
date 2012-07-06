/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz.internal;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/** QRZ Session info
 *
 *  <p>Used with JAXB
 *
 *  @author Kay Kasemir
 */
@XmlAccessorType(XmlAccessType.NONE)
public class QRZSession
{
	/** Session key */
	@XmlElement(name="Key")
	private String key = "";

	/** Session expiration date/time */
	@XmlElement(name = "SubExp", required = true)
    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    private Date expiration = new Date(0);

	/** Error message */
	@XmlElement(name="Error")
	private String error = "";

	/** Warning or info message */
	@XmlElement(name="Message")
	private String message = "";

	/** @return Expiration date/time for this session */
	public Date getExpiration()
    {
    	return expiration;
    }

	/** @param expiration Expiration date/time */
	public void setExpiration(final Date expiration)
    {
    	this.expiration = expiration;
    }

	/** @return Session key */
	public String getKey()
    {
    	return key;
    }

	/** @param key Session key */
	public void setKey(final String key)
    {
    	this.key = key;
    }

	/** @return Error message */
	public String getError()
    {
    	return error;
    }

	/** @param error Error message */
	public void setError(final String error)
    {
    	this.error = error;
    }

	/** @return Warning or info message */
    public String getMessage()
    {
    	return message;
    }

	/** @param error Warning or info message */
	public void setMessage(final String message)
    {
    	this.message = message;
    }

	/** Check if the session has a key, no error and is still valid.
	 *  @return <code>true</code> if we consider session to be valid
	 */
	public boolean isValid()
	{
		if (!error.isEmpty())
			return false;
		if (key.isEmpty())
			return false;
		// Fudge:
		// Check if the session is still valid in 1 minute
		final Date now = new Date();
		return expiration.after(new Date(now.getTime() + 60*1000L));
	}

	/** @return Debug representation */
	@Override
    public String toString()
    {
		final StringBuilder builder = new StringBuilder();
	    builder.append("Session ");
	    builder.append("key '").append(key).append("'");
	    builder.append(" expires ");
	    builder.append(expiration);
	    if (!error.isEmpty())
	    	builder.append(", Error: ").append(error);
	    if (!message.isEmpty())
	    	builder.append(", Message: ").append(message);
	    return builder.toString();
    }
}
