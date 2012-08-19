/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static yahamp.UTCDateMatcher.isDate;

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
        assertThat(UTC.getTimeZone().getRawOffset(), equalTo(0));
	}

    @Test
    public void testUTCFromDateTime() throws Exception
    {
        UTC utc = new UTC();
        utc = new UTC("1970-01-01", "00:00:10");
        assertThat(utc, isDate("1970-01-01 00:00:10"));
        assertThat("Really stored as  UTC?",
                utc.getCalendar().getTimeInMillis(),
                equalTo(10000L)// 10 ms
                );

        assertThat(utc.getYear(), equalTo(1970));
        assertThat(utc.getMonth(), equalTo(1));
        assertThat(utc.getDay(), equalTo(1));

        utc = new UTC("2007-01-18", "13:14");
        assertThat(utc, isDate("2007-01-18 13:14:00"));
        assertThat(utc.getHours(), equalTo(13));
        assertThat(utc.getMinutes(), equalTo(14));
        assertThat(utc.getSeconds(), equalTo(0));

		utc = new UTC("2007-01-18", "1314");
		assertThat(utc, isDate("2007-01-18 13:14:00"));

		utc = new UTC("2007-01-18", "13:14:02");
		assertThat(utc, isDate("2007-01-18 13:14:02"));
	}

    /** Check 'local' flag */
	@Test
	public void testLocal() throws Exception
	{
		UTC utc = new UTC("2008-11-22", "11:00l");
		assertThat(utc, isDate("2008-11-22 16:00:00"));

        utc = new UTC("2008-11-22", "l11:00");
        assertThat(utc, isDate("2008-11-22 16:00:00"));

        utc = new UTC("2008-11-22", "11l00");
        assertThat(utc, isDate("2008-11-22 16:00:00"));
	}
}
