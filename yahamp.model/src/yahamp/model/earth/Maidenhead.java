/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

/** Maidenhead grid square support.
 *  @author Kay Kasemir
 */
public class Maidenhead extends Position
{
    public Maidenhead(final Coordinate latitude, final Coordinate longitude)
    {
        super(latitude, longitude);
    }

    public Maidenhead(final Position position)
    {
    	super(position.getLatitude(), position.getLongitude());
	}

	/** Parse a grid specification.
     *  @param grid For example, "EM76va"
     *  @return The position.
     */
    public static Maidenhead fromGrid(String grid)
    {
        int right_field = grid.charAt(0) - 'A';
        int up_field = grid.charAt(1) - 'A';

        int right_square = grid.charAt(2) - '0';
        int up_square = grid.charAt(3) - '0';
        
        int right_sub;
        int up_sub;
        if (grid.length() > 4)
        {
            right_sub = grid.charAt(4) - 'a';
            up_sub = grid.charAt(5) - 'a';
        }
        else
        {   // locate in middle of sub-square
            right_sub = 11;
            up_sub = 11;
        }
        
        Coordinate longitude =
            new Coordinate(20.0*right_field + 2.0*right_square + 5.0*right_sub/60.0 - 180.0);
        Coordinate latitude =
            new Coordinate(10.0*up_field + 1.0*up_square + 2.5*up_sub/60.0 - 90.0);
        return new Maidenhead(latitude, longitude);
    }
    
    /** Convert to 6-element Maidenhead grid locator.
     *  @return Returns for example "EM76va".
     */
    public String toGrid()
    {
        StringBuffer result = new StringBuffer();
    
        // Starting at -180==180deg, we go 'right' (east)
        double right = 180.0 + getLongitude().getDegrees();

        // Starting at the south pole, we go 'up' (north)
        double up = 90.0 + getLatitude().getDegrees();
        
        // Field: 20 x 10 degrees
        int right_field = (int)(right/20.0);
        int up_field = (int)(up/10.0);
        result.append((char)('A' + right_field));
        result.append((char)('A' + up_field));

        // Square within Field: 2 x 1 degrees (120x60min)
        int right_square = (int)((right - right_field*20.0)/2);
        int up_square = (int)(up - up_field*10.0);
        result.append((char)('0' + right_square));
        result.append((char)('0' + up_square ));

        // Subsquare within Field: 5 x 2.5 minutes
        int minutes = (int)  ((right - right_field*20.0 - right_square*2)*60.0);
        int right_sub = minutes / 5;
        int up_sub = (int) ( ( ( (up-(int)up)*60.0) ) / 2.5);
        result.append((char)('a' + right_sub));
        result.append((char)('a' + up_sub));
    
        return result.toString();
    }
}
