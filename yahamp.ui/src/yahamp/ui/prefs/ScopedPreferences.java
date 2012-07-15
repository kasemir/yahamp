/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.prefs;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/** Access Instance preferenecs, falling back to DefaultPreference Dialog
 *
 *  <p>Similar to <code>ScopedPreferenceStore</code> from Eclipse 3.x
 *  which seems missing in Eclipse 4.2
 *  @author Kay Kasemir
 */
public class ScopedPreferences
{
    /** Read preference setting
     *  @param pref_qualifier Preference plugin name
     *  @param pref_key Preference key within the plugin
     *  @param default_value Default value to use
     *  @return value of preference or default
     */
    public static String get(final String pref_qualifier, final String pref_key,
            final String default_value)
    {
        // Try instance preferences
        IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pref_qualifier);
        if (node != null)
        {
            final String value = node.get(pref_key, null);
            if (value != null)
                return value;
        }
        // Fall back to default preferences
        node = DefaultScope.INSTANCE.getNode(pref_qualifier);
        if (node != null)
            return node.get(pref_key, default_value);
        return default_value;
    }

    /** Write preference setting
     *  @param pref_qualifier Preference plugin name
     *  @param pref_key Preference key within the plugin
     *  @param value Value to set
     *  @throws BackingStoreException on error
     */
    public static void set(final String pref_qualifier, final String pref_key,
            final String value) throws BackingStoreException
    {
        final IEclipsePreferences node = InstanceScope.INSTANCE.getNode(pref_qualifier);
        node.put(pref_key, value);
        node.flush();
    }
}
