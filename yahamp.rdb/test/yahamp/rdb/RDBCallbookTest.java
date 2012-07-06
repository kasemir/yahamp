/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import yahamp.model.CallBook;
import yahamp.model.CallInfo;
import yahamp.model.WriteableCallBook;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBCallbook;

/** JUnit test of {@link RDBLogbook}
 *  @author Kay Kasemir
 */
public class RDBCallbookTest
{
	private RDB rdb;

	@Before
	public void connect() throws Exception
	{
    	rdb = new RDB();
	}

	@After
	public void disconnect()
	{
    	rdb.close();
	}
	
	@Test
	public void testLookup() throws Exception
	{
		CallBook callbook = new RDBCallbook(rdb);
		CallInfo call = callbook.lookup("W1AW");
		System.out.println(call);
	}

	@Test
	public void testSave() throws Exception
	{
		final String callsign = "ABC1DEFTEST";
		final WriteableCallBook callbook = new RDBCallbook(rdb);
		CallInfo call = new CallInfo(callsign);
		callbook.save(call);
		
		call = callbook.lookup(callsign);
		assertEquals(callsign, call.getCall());
		
		// Remove dummy call again
		try
		(
			final PreparedStatement statement =
				rdb.getConnection().prepareStatement("DELETE FROM calls WHERE callsign=?")
		)
		{
			statement.setString(1, callsign);
			final int rows = statement.executeUpdate();
			assertEquals(1, rows);
		}
		
		call = callbook.lookup(callsign);
		assertNull(call);
	}
}
