/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import yahamp.model.UTC;

/** JUnit test of {@link UTC}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class UTCTest
{
    @Test
	public void testUTC() throws Exception
	{
        final UTC utc = new UTC();
        System.out.println("IT'S: " + utc.toString() + " Zulu");
        System.out.println("Time zone: " + UTC.getTimeZone().getDisplayName());
        assertEquals(0, UTC.getTimeZone().getRawOffset());
	}

    @Test
    public void testUTCFromDateTime() throws Exception
    {
        UTC utc = new UTC();
        utc = new UTC("1970-01-01", "00:00:10");
        assertEquals("1970-01-01 00:00:10", utc.toString());
        assertEquals("Really stored as  UTC?",
                     // 10 ms
                     10000,
                     utc.getCalendar().getTimeInMillis(), 0.1);

        assertEquals(1970, utc.getYear());
        assertEquals(1, utc.getMonth());
        assertEquals(1, utc.getDay());

        utc = new UTC("2007-01-18", "13:14");
		assertEquals("2007-01-18 13:14:00", utc.toString());
        assertEquals(13, utc.getHours());
        assertEquals(14, utc.getMinutes());
        assertEquals(0, utc.getSeconds());

		utc = new UTC("2007-01-18", "1314");
		assertEquals("2007-01-18 13:14:00", utc.toString());

		utc = new UTC("2007-01-18", "13:14:02");
		assertEquals("2007-01-18 13:14:02", utc.toString());
	}

    /** Check 'local' flag */
	@Test
	public void testLocal() throws Exception
	{
		UTC utc = new UTC("2008-11-22", "11:00l");
        assertEquals("2008-11-22 16:00:00", utc.toString());

        utc = new UTC("2008-11-22", "l11:00");
        assertEquals("2008-11-22 16:00:00", utc.toString());

        utc = new UTC("2008-11-22", "11l00");
        assertEquals("2008-11-22 16:00:00", utc.toString());
	}
}
