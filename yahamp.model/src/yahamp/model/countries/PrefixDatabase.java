/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.countries;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yahamp.model.Activator;

/** Builds a country database from the Jim Reisert, AD1C, CTY.DAT file.
 *
 *  <p>http://www.country-files.com/cty/cty.dat
 *
 *  @author Kay Kasemir
 */
public class PrefixDatabase
{
    private final static boolean debug = false;
    final private Map<String, PrefixInfo> prefixes = new HashMap<String, PrefixInfo>();

    /** Initialize */
    public PrefixDatabase() throws Exception
    {
    	final InputStream in = Activator.getStream("dat/cty.dat");
        // Note that the fields are aligned in columns and spaced out for
	    // readability only. It is the ":" at the end of each field that acts as
	    // a delimiter for that field:
	    //
	    // Column Length Description
	    // 1 26 Country Name
	    // 27 5 CQ Zone
	    // 32 5 ITU Zone
	    // 37 5 2-letter continent abbreviation
	    // 42 9 Latitude in degrees, + for North
	    // 51 10 Longitude in degrees, + for West
	    // 61 9 Local time offset from GMT
	    // 70 6 Primary DXCC Prefix (A "*" preceding this prefix indicates that
	    // the country is on the DARC WAEDC list, and counts in CQ-sponsored
	    // contests, but not ARRL-sponsored contests).
	    //
	    // Alias DXCC prefixes (including the primary one) follow on consecutive
	    // lines, separated by ",". A ";" terminates the last prefix in the list.
	    //
	    // The following special characters can be applied to an alias prefix:
	    //      (#)     Override CQ Zone
	    //      [#]     Override ITU Zone
	    //      <#/#>   Override latitude/longitude
	    //      {aa}    Override Continent
    	final LineNumberReader r = new LineNumberReader(new InputStreamReader(in));

	    String country = null;
	    int cq = 0;
	    int itu = 0;
	    String continent = null;
	    double latitude = 0.0;
	    double longitude = 0.0;
	    double gmt_offset = 0.0;
	    String dxcc = null;

	    // call(cq)[itu], or ...;
	    final Pattern call_pattern = Pattern.compile("([A-Z0-9/]+)(\\([0-9]+\\))?(\\[[0-9]+\\])?([,;])");

	    boolean need_country = true;
	    String line = r.readLine();
	    while (line != null)
	    {
	        if (need_country)
	        {
	            final StringTokenizer t = new StringTokenizer(line, ":");
	            country = t.nextToken().trim();
	            cq = Integer.parseInt(t.nextToken().trim());
	            itu = Integer.parseInt(t.nextToken().trim());
	            continent = t.nextToken().trim();
	            latitude = Double.parseDouble(t.nextToken().trim());
	            longitude = - Double.parseDouble(t.nextToken().trim());
	            gmt_offset = -Double.parseDouble(t.nextToken().trim());
	            dxcc = t.nextToken().trim();
	            need_country = false;
	        }
	        else
	        {
	            final Matcher m = call_pattern.matcher(line);
	            while (m.find())
	            {
	                final String prefix = m.group(1);
	                int c_cq, c_itu;
	                if (m.group(2) == null)
	                    c_cq = cq;
	                else
	                {
	                    String s = m.group(2);
	                    s = s.substring(1, s.length()-1);
	                    c_cq = Integer.parseInt(s);
	                }
	                if (m.group(3) == null)
	                    c_itu = itu;
	                else
	                {
	                    String s = m.group(3);
	                    s = s.substring(1, s.length()-1);
	                    c_itu = Integer.parseInt(s);
	                }

	                final String end = m.group(4);

	                final PrefixInfo p = new PrefixInfo(prefix, country, dxcc, continent, c_cq, c_itu,
	                        latitude, longitude, gmt_offset);
	                prefixes.put(p.getPrefix(), p);
	                if (debug)
	                    System.out.println(p);

	                if (end.charAt(0) == ';')
	                    need_country = true;
	            }
	        }
	        line = r.readLine();
	    }
	}

    /** Locate prefix info
     *  @param call Call sign
     *  @return Prefix info or <code>null</code>
     */
    public PrefixInfo find(String call)
    {
        call = call.trim().toUpperCase();
        // For calls "KH6/AB1CD", lookup the actual call,
        // not the current location?
        final int i = call.indexOf('/');
        if (i > 0)
            call = call.substring(i+1);
        int l = call.length();
        while (l > 0)
        {
            final PrefixInfo p = prefixes.get(call);
            if (p != null)
                return p;
            --l;
            call = call.substring(0, l);
        }
        return null;
    }
}
