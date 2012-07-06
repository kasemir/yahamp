/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import yahamp.ui.log.LogDisplayPart;

/** JUnit demo of the {@link LogDisplayPart}
 *  @author Kay Kasemir
 */
public class LogDisplayDemo
{
    @Test
    public void demoLogDisplay()
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final LogDisplayPart part = new LogDisplayPart();
        part.createPartControl(shell);

        shell.setText("Logbook");

        shell.setSize(600, 800);
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
