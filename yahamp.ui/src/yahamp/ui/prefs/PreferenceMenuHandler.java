/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.prefs;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

/** Display Preference Dialog
 *
 *  <p>Invoked via <code>DirectMenuItem</code>
 *  @author Kay Kasemir
 */
public class PreferenceMenuHandler
{
    @Execute
    public void show(final Shell shell)
    {
        new PreferenceDialog(shell).open();
    }
}
