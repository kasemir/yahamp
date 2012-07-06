/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

/** Helper for dealing with degrees.
 *  @author Kay Kasemir
 */
public class Degree
{
    /** Prevent instantiation. */
    private Degree()
    {
        // NOP
    }
    
    /** Norm degrees by removing multiples of 360.
     *  @param degrees Any degree value.
     *  @return Degrees normed to 0..360
     */
    public static double norm(double degrees)
    {
        return degrees - Math.floor(degrees / 360.0) * 360.0;
    }
}
