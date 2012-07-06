/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.Test;

import yahamp.logging.LogConfigurator;
import yahamp.model.callbook.CombinedCallBook;
import yahamp.rdb.RDB;

/** JUnit test of the combined call lookup
 *  @author Kay Kasemir
 */
public class CombinedCallBookTest
{
	@Test
	public void testLookup() throws Exception
	{
		LogConfigurator.setLevel(Level.FINE);
		try
		(
			final RDB rdb = new RDB()
		)
		{
			final WriteableCallBook book = new CombinedCallBook(rdb);
			final CallInfo info = book.lookup("W1AW");
			System.out.println(info);
			assertNotNull(info);
			assertTrue(info.getName().contains("ARRL"));
			// Unsubscribed QRZ lookup would not include city...
			assertTrue(info.getCity().equalsIgnoreCase("Newington"));
		}
	}

	@Test
	public void testWrite() throws Exception
	{
		LogConfigurator.setLevel(Level.FINE);
		try
		(
			final RDB rdb = new RDB()
		)
		{
			final WriteableCallBook book = new CombinedCallBook(rdb);
			final CallInfo info = book.lookup("n3kn");
			System.out.println(info);
			assertNotNull(info);
			// ARRL President, didn't have QSO yet to not in callbook
			assertEquals("N3KN", info.getCall());
			assertTrue(info.getName().contains("Kay"));
			// Unsubscribed QRZ lookup would not include city...
			assertTrue(info.getCity().isEmpty());
			// Set and write
			info.setCity("Blacksburg");
			book.save(info);
			
			final CallInfo info2 = book.lookup("n3kn");
			System.out.println(info2);
			assertNotNull(info2);
			assertNotSame(info, info2);
			assertEquals("Blacksburg", info.getCity());
			book.delete(info);
		}
	}
}
