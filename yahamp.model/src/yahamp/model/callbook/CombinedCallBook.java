/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.callbook;

import java.util.logging.Level;
import java.util.logging.Logger;

import yahamp.model.CallBook;
import yahamp.model.CallInfo;
import yahamp.model.WriteableCallBook;
import yahamp.model.countries.PrefixDatabase;
import yahamp.model.countries.PrefixInfo;
import yahamp.model.earth.Maidenhead;
import yahamp.qrz.QRZCallBook;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBCallbook;

/** Call book based on several sources: RDB, QRZ
 *  @author Kay Kasemir
 */
public class CombinedCallBook implements WriteableCallBook
{
    private static PrefixDatabase prefixes = null;

	final private Logger log = Logger.getLogger(getClass().getName());
	final private WriteableCallBook rdb_callbook;
	private CallBook qrz_callbook;

	public CombinedCallBook(final RDB rdb) throws Exception
	{
	    synchronized (CombinedCallBook.class)
        {
            if (prefixes == null)
                prefixes = new PrefixDatabase();
        }
		rdb_callbook = new RDBCallbook(rdb);
		try
		{
		    qrz_callbook = new QRZCallBook();
		}
		catch (final Exception ex)
		{
		    log.log(Level.FINE, "QRZ Logbook not available", ex);
		    qrz_callbook = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public CallInfo lookup(final String callsign) throws Exception
	{
	    final CallInfo result = new CallInfo(callsign);
	    // Merge Prefix info
	    if (prefixes != null)
	    {
	        final PrefixInfo prefix = prefixes.find(callsign);
	        if (prefix != null)
	        {
	            log.log(Level.FINE, "Prefix info: {0}", prefix);
	            result.setCounty(prefix.getCountry());
	            result.setDXCC(prefix.getDXCC());
	            result.setGrid(new Maidenhead(prefix.getPosition()).toGrid());
	        }
	    }
	    // Then merge QRZ
	    if (qrz_callbook != null)
	    {
	        final CallInfo qrz_info = qrz_callbook.lookup(callsign);
	        if (qrz_info != null)
	        {
	            log.log(Level.FINE, "QRZ call info: {0}", qrz_info);
	            updateCallInfo(result, qrz_info);
	        }
	    }
		// Finally merge RDB, so that overwrites anything else
	    final CallInfo rdb_info = rdb_callbook.lookup(callsign);
	    if (rdb_info != null)
	    {
	        log.log(Level.FINE, "RDB call info: {0}", rdb_info);
	        updateCallInfo(result, rdb_info);
	    }
		return result;
	}

	/** Update call info.
	 *  @param info Call info
	 *  @param update Call info with updated values
	 */
	private void updateCallInfo(final CallInfo info, final CallInfo update)
    {
	    if (! update.getName().isEmpty())
	        info.setName(update.getName());
        if (! update.getAddress().isEmpty())
            info.setAddress(update.getAddress());
        if (! update.getCity().isEmpty())
            info.setCity(update.getCity());
        if (! update.getState().isEmpty())
            info.setState(update.getState());
        if (! update.getZip().isEmpty())
            info.setZip(update.getZip());
        if (! update.getGrid().isEmpty())
            info.setGrid(update.getGrid());
        if (! update.getCounty().isEmpty())
            info.setCounty(update.getCounty());
        if (! update.getCountry().isEmpty())
            info.setCountry(update.getCountry());
        if (update.getFists_nr() > 0)
            info.setFists_nr(update.getFists_nr());
        if (update.getFists_cc() > 0)
            info.setFists_cc(update.getFists_cc());
        if (update.getFp_nr() > 0)
            info.setFp_nr(update.getFp_nr());
    }

	/** {@inheritDoc} */
	@Override
	public void save(final CallInfo call) throws Exception
	{
		rdb_callbook.save(call);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(final CallInfo call) throws Exception
	{
		rdb_callbook.delete(call);
	}
}
