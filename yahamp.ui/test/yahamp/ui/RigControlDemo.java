/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import java.util.logging.Level;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import yahamp.logging.LogConfigurator;
import yahamp.ui.rig.RigControlPart;

/** JUnit demo of the {@link RigControlPart}
 *  @author Kay Kasemir
 */
public class RigControlDemo
{
    @Test
    public void demoMap() throws Exception
    {
        LogConfigurator.setLevel(Level.FINER);

        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final RigControlPart part = new RigControlPart();
        part.createPartControl(shell);

        shell.setSize(400, 400);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
