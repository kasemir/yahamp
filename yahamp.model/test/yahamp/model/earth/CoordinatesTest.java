/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import junit.framework.TestCase;

import org.junit.Test;

/** JUnit test of the Coordinate converter
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CoordinatesTest extends TestCase
{
	private static final double tolerance = 0.001;
	
    @Test
	public void testDegGrad2Fractional()
	{
		Coordinate c = new Coordinate(0.0);
		
		assertEquals(0.0, c.getDegrees(), tolerance);
		assertEquals(0, c.getDegrees(), tolerance);
		assertEquals(0, c.getMinutes());
		
        c = new Coordinate(45, 30, 0);
		assertEquals(45.5, c.getDegrees(), tolerance);

        c = new Coordinate(-45, 30, 0);
		assertEquals(-45.5, c.getDegrees(), tolerance);
	}

    @Test
	public void testFractional2DegGrad()
	{
		Coordinate c = new Coordinate(0.0);
		
		assertEquals(c.getDegrees(), 0, tolerance);
		assertEquals(c.getMinutes(), 0);
		
		c = new Coordinate(45.5);
		assertEquals(c.getDegrees(), 45.5, tolerance);
		assertEquals(c.getMinutes(), 30);	

		c = new Coordinate(-45.5);
		assertEquals(c.getDegrees(), -45.5, tolerance);
		assertEquals(c.getMinutes(), 30);	
	}
	
    @Test
	public void testString()
	{
		// OR: 36.. N
		Coordinate c = new Coordinate(36.02377);
		System.out.println(c);
        assertEquals("36 1'25.6''", c.toString());
        
		// OR: 84 W
        c = new Coordinate(84.2158);
		System.out.println(c);
        assertEquals("84 12'56.9''", c.toString());

        // OR: 84 E
        c = new Coordinate(-84.2158);
		System.out.println(c);
        assertEquals("-84 12'56.9''", c.toString());
	}
	
    @Test
	public void testParser()
	{
    	Coordinate c = Coordinate.fromString("36 1'25.6''");
    	assertEquals(36.02377, c.getDegrees(), 0.0001);

    	c = Coordinate.fromString("-84 12'56.9''");
    	assertEquals(-84.2158, c.getDegrees(), 0.0001);

    	c = Coordinate.fromString("-84 30'");
    	assertEquals(-84.5, c.getDegrees(), 0.0001);

    	c = Coordinate.fromString("-84");
    	assertEquals(-84.0, c.getDegrees(), 0.0001);

    	c = Coordinate.fromString("-84deg");
    	assertNull(c);
	}
}
