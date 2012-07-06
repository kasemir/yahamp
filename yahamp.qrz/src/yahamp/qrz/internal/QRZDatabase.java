/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz.internal;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/** QRZ Database model
 *
 *  <p>Used with JAXB
 *
 *  @author Kay Kasemir
 */
@XmlRootElement(name="QRZDatabase", namespace="http://www.qrz.com")
@XmlAccessorType(XmlAccessType.NONE)
public class QRZDatabase
{
	/** Session */
	@XmlElement(name="Session")
	private QRZSession session = null;

	/** Call sign */
	@XmlElement(name="Callsign")
	private QRZCallInfo callsign = null;

	/** Initialize from XML stream
	 *  @param stream Stream with XML from qrz.com
	 *  @return
	 *  @throws Exception on error
	 */
	public static QRZDatabase fromStream(final InputStream stream) throws Exception
	{
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
		return (QRZDatabase) unmarshaller.unmarshal(stream);
	}

	/** @return <code>true</code> if QRZ provided session without error */
	public boolean isValid()
	{
		return session != null  &&  session.isValid();
	}

	/** @return Session */
	public QRZSession getSession()
    {
    	return session;
    }

	/** @param session Session */
	public void setSession(final QRZSession session)
    {
    	this.session = session;
    }

	/** @return Human-readable summary of session */
	public String getSessionInfo()
	{
		if (session == null)
			return "No session";
		final StringBuilder buf = new StringBuilder();
		if (! session.getError().isEmpty())
			buf.append("Session error: ").append(session.getError());
		if (! session.getMessage().isEmpty())
		{
			if (buf.length() > 0)
				buf.append(", ");
			buf.append("Session message: ").append(session.getMessage());
		}
		if (buf.length() <= 0)
			buf.append("Session key ").append(session.getKey());
		return buf.toString();
	}

	/** @return Call sign info */
	public QRZCallInfo getCallInfo()
    {
    	return callsign;
    }

	/** @param callsign Call sign info */
	public void setCallsign(final QRZCallInfo callsign)
    {
    	this.callsign = callsign;
    }

	/** @return Debug representation */
	@Override
    public String toString()
    {
	    final StringBuilder builder = new StringBuilder();
	    builder.append("QRZDatabase [session=");
	    builder.append(session);
	    builder.append("]");
	    return builder.toString();
    }
}
