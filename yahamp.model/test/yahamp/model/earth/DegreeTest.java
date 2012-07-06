/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test for Degree class
 *  @author Kay Kasemir
 */
public class DegreeTest
{
    @Test
    public void testNorm()
    {
        final double eps = 0.001; 
        assertEquals(0.0, Degree.norm(0.0), eps);
        assertEquals(90.0, Degree.norm(90.0), eps);
        assertEquals(270.0, Degree.norm(-90.0), eps);
        assertEquals(0.0, Degree.norm(360), eps);
        assertEquals(40.0, Degree.norm(400), eps);
        assertEquals(360.0-40.0, Degree.norm(-400), eps);
    }
}
