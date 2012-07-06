/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Read preference settings.
 *  For explanation of settings see preferences.ini
 *  @author Kay Kasemir
 */
public class Preferences
{
	public static final String URL = "url";

	public static String getURL()
    {
        // RDB classes are not part of the E4 application model.
        // Can therefore not use @Inject @Preference but
        // have to access the preferences directly.
		String url = "jdbc:mysql://localhost/yahamp?user=yahamp&password=$yahamp";
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs != null)
        	url = prefs.getString(Activator.ID, URL, url, null);
        return url;
    }
}
