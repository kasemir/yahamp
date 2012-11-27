/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import yahamp.model.CallBook;
import yahamp.model.CallInfo;
import yahamp.qrz.internal.QRZCallInfo;
import yahamp.qrz.internal.QRZDatabase;
import yahamp.qrz.internal.QRZSession;

/** Perform call info lookup on qrz.com
 *
 *  <p>Requires a QRZ account (user, password).
 *  With a free account, only the name associated with a call sign is available.
 *  With a paid QRZ subscription, address, license info and more can be retrieved.
 *
 *  @author Kay Kasemir
 */
public class QRZCallBook implements CallBook
{
	/** Logger */
	final private Logger logger = Logger.getLogger(getClass().getName());

	/** Session, kept as long as 'valid'
	 *  <p>SYNC on QRZCallBook.class
	 */
	private static QRZSession session = null;

	/** Cache of lookups to reduce network load */
    private static Map<String, CallInfo> cache = new HashMap<>();

	/** QRZ call book with user/pass from preferences
	 *  @throws Exception on error
	 */
	public QRZCallBook() throws Exception
	{
		this(Preferences.getUser(), Preferences.getPassword());
	}

	/** @param user QRZ user name
	 *  @param password .. password
	 *  @throws Exception on error
	 */
	public QRZCallBook(final String user, final String password) throws Exception
	{
	    synchronized (QRZCallBook.class)
        {
	        if (session == null  ||  !session.isValid())
	            session = login(user, password);
        }
	}

	/** Log into QRZ.com, obtainig session key
	 *  @param user QRZ user name
	 *  @param password .. password
	 *  @return {@link QRZSession}
	 *  @throws Exception on error
	 */
	private QRZSession login(final String call, final String password) throws Exception
    {
		logger.log(Level.FINE, "Logging into QRZ as {0}", call);
		final URL url =
			new URL("http://xmldata.qrz.com/xml/current/?username=" + call
					+ ";password=" + password + ";agent=yahamp");

		final BufferedInputStream stream = new BufferedInputStream(url.openStream());
		final QRZDatabase qrz_database = QRZDatabase.fromStream(stream);
		logger.log(Level.FINE, "Logged in: {0}", qrz_database);
		if (! qrz_database.isValid())
			throw new Exception("Login failed, " + qrz_database.getSessionInfo());
		return qrz_database.getSession();
    }

	/** Perform call sign lookup
	 *  @param call Call sign to look up
	 *  @return {@link QRZCallInfo}
	 *  @throws Exception on error
	 */
	@Override
	public CallInfo lookup(String call) throws Exception
	{
	    call = call.trim().toUpperCase();

		CallInfo result;
		// Check cache
		synchronized (cache)
        {
            result = cache.get(call);
        }
		if (result != null)
		{
		    logger.log(Level.FINER, "Cached {0}...", call);
		    return result;
		}

		// Perform network lookup
		logger.log(Level.FINE, "Lookup {0}...", call);
		final String url =
	        "http://xmldata.qrz.com/xml/current/?s=" + session.getKey() + ";callsign=" + call;
		final BufferedInputStream stream =
	        new BufferedInputStream(new URL(url).openStream());
		final QRZDatabase qrz_database = QRZDatabase.fromStream(stream);
		logger.log(Level.FINE, "Info: {0}", qrz_database);

		// If callsign is unknown: Not an error; return null.
		if (! qrz_database.isValid())
		{
		    if (qrz_database.getCallInfo() == null  &&
		        qrz_database.getSession().getError().toLowerCase().contains("not found"))
		        return null;
		    // Else: Other type of error
			throw new Exception("Lookup failed, " + qrz_database.getSessionInfo());
		}

		// Received valid call info
		final QRZCallInfo qrz_info = qrz_database.getCallInfo();
		if (qrz_info == null)
			return null;
		result = qrz_info;
        synchronized (cache)
        {   // Remember in cache
            cache.put(call, result);
        }
		return result;
	}
}
