/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import yahamp.model.CallInfo;
import yahamp.model.WriteableCallBook;

/** Call book that keeps {@link CallInfo} entries by call sign
 *  @author Kay Kasemir
 */
public class RDBCallbook implements WriteableCallBook
{
	final private RDB rdb;

	public RDBCallbook(final RDB rdb)
	{
		this.rdb = rdb;
	}

	/** Get call info from RDB
	 *  {@inheritDoc}
	 */
	@Override
	public CallInfo lookup(String callsign) throws Exception
	{
		callsign = callsign.trim().toUpperCase();
		try
		(
			final PreparedStatement statement =
				rdb.getConnection().prepareStatement(rdb.sel_callinfo);
		)
		{
			statement.setString(1, callsign);
			final ResultSet result = statement.executeQuery();
			if (result.next())
			{
				return new CallInfo(callsign,
					RDB.nonNull(result.getString(1)),
					RDB.nonNull(result.getString(2)),
					RDB.nonNull(result.getString(3)),
					RDB.nonNull(result.getString(4)),
					RDB.nonNull(result.getString(5)),
					RDB.nonNull(result.getString(6)),
					RDB.nonNull(result.getString(7)),
					RDB.nonNull(result.getString(8)),
					result.getInt(9),
					result.getInt(10),
					result.getInt(11));
			}	
		}
		return null;
	}

	/** Add/update RDB info for call
	 *  {@inheritDoc}
	 */
	@Override
	public void save(final CallInfo call) throws Exception
	{
		try
		(
		    final PreparedStatement statement =
		    	rdb.getConnection().prepareStatement(rdb.replace_callinfo);
		)
		{
            RDB.setStringOrNULL(statement, 1, call.getCall());
            RDB.setStringOrNULL(statement, 2, call.getName());
            RDB.setStringOrNULL(statement, 3, call.getAddress());
            RDB.setStringOrNULL(statement, 4, call.getCity());
            RDB.setStringOrNULL(statement, 5, call.getState());
            RDB.setStringOrNULL(statement, 6, call.getZip());
            RDB.setStringOrNULL(statement, 7, call.getGrid());
            RDB.setStringOrNULL(statement, 8, call.getCounty());
            RDB.setStringOrNULL(statement, 9, call.getCountry());
            RDB.setIntOrNULL(statement, 10, call.getFists_nr());
            RDB.setIntOrNULL(statement, 11, call.getFists_cc());
            RDB.setIntOrNULL(statement, 12, call.getFp_nr());
            final int rows = statement.executeUpdate();
            // 1 : REPLACE operated like INSERT for new data
            // 2 : REPLACE operated like DELETE & INSERT for existing data
            if (rows < 1  ||  rows > 2)
            	throw new Exception("Update affected " + rows + " rows instead of 1 or 2");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void delete(final CallInfo info) throws Exception
	{
		final Connection connection = rdb.getConnection();
		connection.setAutoCommit(false);
		try
		(
		    final PreparedStatement statement =
		    	connection.prepareStatement(rdb.delete_callinfo);
		)
		{
			statement.setString(1,  info.getCall());
			final int rows = statement.executeUpdate();
            if (rows > 1)
            	throw new Exception("Deleted " + rows + " rows instead of 1 or 0");
            connection.commit();
		}	
		finally
		{
			connection.setAutoCommit(true);
		}
	}
}
