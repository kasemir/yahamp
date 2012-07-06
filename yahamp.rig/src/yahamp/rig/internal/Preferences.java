/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import yahamp.rig.Activator;

/** Read preference settings.
 *  For explanation of settings see preferences.ini
 *  @author Kay Kasemir
 */
public class Preferences
{
	public static final String PORT = "port";
	public static final String RATE = "rate";

    private static String port = "COM3";

    private static int rate = 9600;

	public static String getPort()
    {
	    // Rig* classes are not part of the E4 application model.
	    // Can therefore not use @Inject @Preference but
	    // have to access the preferences directly.
        final IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(Activator.ID);
        if (prefs != null)
            port = prefs.get(PORT, port);
        return port;
    }

	public static int getRate()
    {
        final IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(Activator.ID);
        if (prefs != null)
        	rate = prefs.getInt(RATE, rate);
        return rate;
    }
}
