/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import static org.junit.Assert.*;

import org.junit.Test;

import yahamp.rig.internal.BCD;

/** JUnit test fpr BCD
 *  @author Kay Kasemir
 */
public class BCDTest
{
    @Test
    public void testBCD()
    {
        assertEquals(0x00, BCD.encode(0));
        assertEquals(0x12, BCD.encode(12));
        assertEquals(0x99, BCD.encode(99));
        
        assertEquals(0, BCD.decode(0x0));
        assertEquals(10, BCD.decode(0x10));
        assertEquals(99, BCD.decode(0x99));
    }
}
