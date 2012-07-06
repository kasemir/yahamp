/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

/** Info about beacons
 * 
 *  TODO These are only the 20m beacons on 14.110 MHzs...
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Beacons
{
    static class BeaconInfo
    {
        final String call;
        final String grid;

        public BeaconInfo(final String call, final String grid)
        {
            this.call = call;
            this.grid = grid;
        }
    }
    
    private static final BeaconInfo beacon_info[] = new BeaconInfo[]
    {
        new BeaconInfo("4U1UN",  "FN30as"),
        new BeaconInfo("VE8AT",  "EQ79ax"),
        new BeaconInfo("W6WX",   "CM97bd"),
        new BeaconInfo("KH6WO",  "BL11ap"),
        new BeaconInfo("ZL6B",   "RE78tw"),
        new BeaconInfo("VK6RBP", "OF87av"),
        new BeaconInfo("JA2IGY", "PM84jk"),
        new BeaconInfo("RR9O",   "NO14kx"),
        new BeaconInfo("VR2B",   "OL72bg"),
        new BeaconInfo("4S7B",   "NJ06cc"),
        new BeaconInfo("ZS6DN",  "KG44dc"),
        new BeaconInfo("5Z4B",   "KI88mx"),
        new BeaconInfo("4X6TU",  "KM72jb"),
        new BeaconInfo("OH2B",   "KP20"),
        new BeaconInfo("CS3B",   "IM12or"),
        new BeaconInfo("LU4AA",  "GF05tj"),
        new BeaconInfo("OA4B",   "FH17mw"),
        new BeaconInfo("YV5B",   "FK60nj"),
    };
    
    private static Beacon beacons[] = null;
    
    /** @see #getBeacons() */
    private Beacons()
    { /* prevent instantiation */ }

    /** @return Info string for beacons */
    final public static String getInfo()
    {
        return "14.100 MHz beacons";
    }
    
    /** @return Array of beacons */
    public static final Beacon[] getBeacons()
    {
        if (beacons == null)
        {
            final int N = beacon_info.length;
            beacons = new Beacon[N];
            int minutes = 0;
            int seconds = 0;
            final int three_minutes = 3*60;
            for (int i = 0; i < N; ++i)
            {
                beacons[i] = new Beacon(beacon_info[i].call,
                                        beacon_info[i].grid,
                                        minutes, seconds, three_minutes);
                seconds += 10;
                if (seconds >= 60)
                {
                    ++minutes;
                    seconds = 0;
                }
            }
        }
        return beacons;
    }
}
