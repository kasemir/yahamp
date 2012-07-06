/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.earth;

import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

/** Earth-related computations.
 *  @author Kay Kasemir
 */
public class Earth
{
    /** Equatorial radius of earth in kilometers. */
    public final static double Radius = 6378.14;
    
    /** Earth' flattening, ratio of radius at equator to radius at poles,
     *  in kilometers.
     */
    public final static double Flattening = 1/298.257;
    
    /** Perihelion: Point of earth's orbit closest to the sun in degrees. */
    public final static double Perihelion = 102.9372;
    
    /** Obliquity: Angle between equator and ecliptic in degrees. */
    public final static double Obliquity = 23.45;
    
    /** Prevent instantiation. */
    private Earth()
    { /* NOP */ }
    
    /** Determine distance between two coordinates on surface of earth
     *  in kilometers.
     *  @param p1 One position
     *  @param p2 Another position
     *  @return Distance in kilometers
     */
    public static double getDistance(final Position p1, final Position p2)
    {
        // Formula from Meeus p.85
        double F = (p1.getLatitude().getRadians() + p2.getLatitude().getRadians())/2;
        double G = (p1.getLatitude().getRadians() - p2.getLatitude().getRadians())/2;
        double l = (p1.getLongitude().getRadians() - p2.getLongitude().getRadians())/2;
        
        double sin2G = sin(G) * sin(G);
        double cos2G = cos(G) * cos(G);
        double sin2F = sin(F) * sin(F);
        double cos2F = cos(F) * cos(F);
        double sin2l = sin(l) * sin(l);
        double cos2l = cos(l) * cos(l);
        double S = sin2G * cos2l + cos2F*sin2l;
        double C = cos2G * cos2l + sin2F*sin2l;
        if (S == 0.0)
            return 0.0;
        double w = atan(sqrt(S/C));
        double R = sqrt(S*C) / w;
        double D = 2*w*Radius;
        double H1 = (3*R-1) / (2*C);
        double H2 = (3*R+1) / (2*S);
        
        return D*(1+Flattening*H1*sin2F*cos2G - Flattening*H2*cos2F*sin2G);
    }
    
    /** Mean anomaly of Earth for given Julian Day.
     *  <p>
     *  The earth moves around the sun on an ellipsis, i.e.
     *  at variing speeds.
     *  Mean anomaly describes the angle 0..360deg at which the earth
     *  would appread from the sun if it moved at constant speed on a circle.
     *  Mean anomaly therefore varies uniformly from 0 to 360 degrees within
     *  a year.
     *  @return Mean anomaly in degrees.
     */
    public static double meanAnomaly(JulianDay JD)
    {
        //  Meuus eq. 25.3
        final double T = JD.getJ2000Centuries();
        return 357.52911 + 35999.05029 * T - 0.0001537 * T * T;
    }
    
    /** Compute equation of center.
     *  <p>
     *  true anomaly nu = meanAnomaly + center
     *  <p>
     *  @param JD Julian day
     *  @param M meanAnomaly
     *  @return value of center in degrees
     */
    public static double equationOfCenter(JulianDay JD, double M)
    {
        // Meeus P. 164 (chapter 25)
        final double T = JD.getJ2000Centuries();
        double Mrad = toRadians(M);
        return (1.914602 - 0.004817*T - 0.000014*T*T) * sin(Mrad)
                + (0.019993 - 0.000101*T) * sin(2*Mrad)
                + 0.000289  * sin(3*Mrad);
    }
}
