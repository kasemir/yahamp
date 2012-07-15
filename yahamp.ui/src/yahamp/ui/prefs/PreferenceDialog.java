/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.prefs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

/** Preference Dialog
 *
 *  <p>... since the Eclipse 3.x preference extension point support
 *  seems missing in Eclipse 4.2
 *  @author Kay Kasemir
 */
public class PreferenceDialog extends Dialog
{
    /** Editors for preferences */
    final private List<PreferenceEditorRow> editors = new ArrayList<>();

    /** Initialize
     *  @param shell Parent shell
     */
    public PreferenceDialog(final Shell shell)
    {
        super(shell);
    }

    /** Set title */
    @Override
    protected void configureShell(final Shell shell)
    {
       super.configureShell(shell);
       shell.setText("Preference Settings");
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** Create preference editors */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite top = (Composite) super.createDialogArea(parent);
        final GridLayout layout = (GridLayout) top.getLayout();
        layout.numColumns = 2;

        editors.add(new PreferenceEditorRow(top, "My Callsign:", "yahamp.ui", "call"));
        editors.add(new PreferenceEditorRow(top, "My Grid:", "yahamp.ui", "grid"));
        separator(top);
        editors.add(new PreferenceEditorRow(top, "RDB:", "yahamp.rdb", "url"));
        separator(top);
        editors.add(new PreferenceEditorRow(top, "QRZ.com User:", "yahamp.qrz", "user"));
        editors.add(new PreferenceEditorRow(top, "QRZ.com Password:", "yahamp.qrz", "password"));
        separator(top);
        editors.add(new PreferenceEditorRow(top, "Rig Control Port:", "yahamp.rig", "port"));
        editors.add(new PreferenceEditorRow(top, "Rig Control Rate:", "yahamp.rate", "rate"));
        separator(top);
        editors.add(new PreferenceEditorRow(top, "Log level:", "yahamp.app", "log_level"));

        return top;
    }

    /** Add separator line
     *  @param top Parent widget
     */
    private void separator(final Composite top)
    {
        Label sep = new Label(top, SWT.SEPARATOR | SWT.HORIZONTAL);
        sep.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));
    }

    /** Save preferences */
    @Override
    protected void okPressed()
    {
        try
        {
            for (PreferenceEditorRow editor : editors)
                editor.save();
        }
        catch (BackingStoreException ex)
        {
            MessageDialog.openError(getShell(), "Error",
                    NLS.bind("Cannot save settings:\n{0}", ex.getMessage()));
            return;
        }
        super.okPressed();
    }
}
