/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import yahamp.model.Callsign;
import yahamp.model.QSO;
import yahamp.model.UTC;

/** JUnit test of {@link QSO}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class QSOTest
{
    @Test
	public void testComparison() throws Exception
	{
        final UTC now = new UTC();
        final QSO a = new QSO(new Callsign("AB1CD"));
        a.setUTC(now );
        final QSO b = new QSO(new Callsign("XY2ZA"));
        b.setUTC(now);
        assertNotSame(a, b);
        System.out.println(a);
        System.out.println(b);
        // equal because at same time
        assertTrue(! a.getCall().equals(b.getCall()));
        assertEquals(a, b);
	}
}
