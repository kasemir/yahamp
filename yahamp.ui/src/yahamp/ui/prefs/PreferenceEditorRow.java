/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.prefs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

/** A 'row' for editing values in the preference dialog
 *
 *  <p>Label, text field, associated with preferences
 *  @author Kay Kasemir
 */
public class PreferenceEditorRow
{
    final private String pref_qualifier, pref_key;
    final private Text text;

    /** Initialize
     *  @param parent Parent widget
     *  @param label Label
     *  @param pref_qualifier Preference plugin name
     *  @param pref_key Preference key within the plugin
     */
    public PreferenceEditorRow(final Composite parent, final String label,
            final String pref_qualifier, final String pref_key)
    {
        this.pref_qualifier = pref_qualifier;
        this.pref_key = pref_key;
        final Label l = new Label(parent, 0);
        l.setText(label);
        l.setLayoutData(new GridData());

        text = new Text(parent, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        text.setText(ScopedPreferences.get(pref_qualifier, pref_key, ""));
    }

    /** Write currently entered value to preferences
     *  @throws BackingStoreException on error
     */
    public void save() throws BackingStoreException
    {
        ScopedPreferences.set(pref_qualifier, pref_key, text.getText().trim());
    }
}
