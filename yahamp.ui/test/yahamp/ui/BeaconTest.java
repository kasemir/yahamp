/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import yahamp.model.UTC;
import yahamp.ui.map.Beacon;

@SuppressWarnings("nls")
public class BeaconTest
{
    @Test
    public void testGetNextDueTimeInMillis() throws Exception
    {
    	final Beacon beacon = new Beacon("4U1UN",  "FN30as", 0, 10, 5);
        System.out.println(beacon);
        assertFalse(beacon.doStandout());
        assertFalse(beacon.isVisible());

        UTC now = new UTC();
        // turn 'standout'
        beacon.run(now);
        System.out.println(beacon);
        assertTrue(beacon.doStandout());
        assertTrue(beacon.isVisible());

        // make visible, compute next time
        beacon.run(now);
        System.out.println(beacon);
        assertFalse(beacon.doStandout());
        assertTrue(beacon.isVisible());

        UTC next = new UTC(beacon.getNextDueTimeInMillis());
        System.out.println(now.toString() + " -> " + next.toString());
        assertTrue(next.compareTo(now) > 0);

        now = new UTC();
        // turn off
        beacon.run(now);
        System.out.println(beacon);
        assertFalse(beacon.doStandout());
        assertFalse(beacon.isVisible());

        // make invisible, compute next time
        beacon.run(now);
        next = new UTC(beacon.getNextDueTimeInMillis());
        System.out.println(now.toString() + " -> " + next.toString());
        assertTrue(next.compareTo(now) > 0);

        now = new UTC();
        // turn visible
        beacon.run(now);
        // make invisible, compute next time
        beacon.run(now);
        next = new UTC(beacon.getNextDueTimeInMillis());
        System.out.println(now.toString() + " -> " + next.toString());
        assertTrue(next.compareTo(now) > 0);
    }
}
