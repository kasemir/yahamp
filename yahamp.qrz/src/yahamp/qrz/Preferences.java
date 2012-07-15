/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
 *  For explanation of settings see preferences.ini
 *  @author Kay Kasemir
 */
public class Preferences
{
	public static final String USER = "user";
	public static final String PASSWORD = "password";

	public static String getUser()
	{
	    return getText(USER);
	}

	public static String getPassword()
	{
        return getText(PASSWORD);
	}

	/** @param preference Preference tag name
     *  @return Text from preference, falling back to property "qrz_..."
     */
    private static String getText(final String preference)
    {
        String text = System.getProperty("qrz_" + preference);
        // QRZ classes are not part of the E4 application model.
        // Can therefore not use @Inject @Preference but
        // have to access the preferences directly.
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
            text = prefs.getString(Activator.ID, preference, text, null);
        return text;
    }

}
