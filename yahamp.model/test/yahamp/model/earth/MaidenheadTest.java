/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of  Maidenhead grid computations.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MaidenheadTest
{
	@Test
	public void testGridConversions()
	{
        Maidenhead grid;
		
		grid = new Maidenhead(new Coordinate(36.02377), new Coordinate(-84.2158));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("EM76va", grid.toGrid());

		grid = new Maidenhead(new Coordinate(36.02377), new Coordinate(-86.0));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("EM76", grid.toGrid().substring(0, 4));

		grid = new Maidenhead(new Coordinate(36.02377), new Coordinate(-84.01));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("EM76", grid.toGrid().substring(0, 4));

		grid = new Maidenhead(new Coordinate(36.02377), new Coordinate(-83.99));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("EM86", grid.toGrid().substring(0, 4));

		grid = new Maidenhead(new Coordinate(46.5), new Coordinate(-111.0));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("DN46", grid.toGrid().substring(0, 4));

		grid = new Maidenhead(new Coordinate(39, 6, 0), new Coordinate(-76, 58, 0));
		System.out.println(grid + "  ->  " + grid.toGrid());
		assertEquals("FM19mc", grid.toGrid());
        
        grid = Maidenhead.fromGrid("EM76va");
        System.out.println(grid + " <-> " + grid.toGrid());
        assertEquals("EM76va", grid.toGrid());
        
        Coordinate longitude = grid.getLongitude();
        longitude = new Coordinate((int) longitude.getDegrees(),
                                   longitude.getMinutes() - 6,
                                   (int) longitude.getSeconds());
        grid = new Maidenhead(grid.getLatitude(), longitude);
        System.out.println(grid + " <-> " + grid.toGrid());
        assertEquals("EM76wa", grid.toGrid());

        Coordinate latitude = grid.getLatitude();
        latitude = new Coordinate((int) latitude.getDegrees(),
                                  latitude.getMinutes() + 3,
                                  (int) latitude.getSeconds());
        grid = new Maidenhead(latitude, grid.getLongitude());
        System.out.println(grid + " <-> " + grid.toGrid());
        assertEquals("EM76wb", grid.toGrid());
        
        
        grid = Maidenhead.fromGrid("CN94ji");
        System.out.println(grid.toGrid() + ":");
        System.out.println("Lat: " + grid.getLatitude().getDegrees());
        System.out.println("Lon: " + grid.getLongitude().getDegrees());
        
        grid = new Maidenhead(new Coordinate(44.355854),
                              new Coordinate(-121.186400));
        System.out.println(grid.toGrid() + ":");
        System.out.println("Lat: " + grid.getLatitude().getDegrees());
        System.out.println("Lon: " + grid.getLongitude().getDegrees());
    }
}
