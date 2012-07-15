/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

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
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            port = prefs.getString(Activator.ID, PORT, port, null);
        return port;
    }

	public static int getRate()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        	rate = prefs.getInt(Activator.ID, RATE, rate, null);
        return rate;
    }
}
