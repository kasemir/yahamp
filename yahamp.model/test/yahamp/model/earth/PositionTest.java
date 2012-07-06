/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of Position.
 *  @author Kay Kasemir
 */
public class PositionTest
{
    @SuppressWarnings("nls")
    @Test
    public void testPosition()
    {
        Position p = new Position(new Coordinate(36.02377), new Coordinate(-84.2158));
        System.out.println(p);
        assertEquals("36 1'25.6'' N, 84 12'56.9'' W", p.toString());
    }
}
