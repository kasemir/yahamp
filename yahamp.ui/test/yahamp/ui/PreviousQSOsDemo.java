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

import yahamp.model.Callsign;
import yahamp.ui.previous_qsos.PreviousQSOsPart;

/** JUnit demo of the {@link PreviousQSOsPart}
 *  @author Kay Kasemir
 */
public class PreviousQSOsDemo
{
    private static final String LOOKUP_CALL = "EA6UN";

    @Test
    public void demoPreviousQSOs()
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final PreviousQSOsPart part = new PreviousQSOsPart();
        part.createPartControl(shell);

        shell.setText("Wait 5 seconds for callsign to be set..");
        // Simulate callsign that would operationally get injected by IEventBroker
        display.timerExec(5000, new Runnable()
        {
            @Override
            public void run()
            {
                shell.setText(LOOKUP_CALL + "?");
                part.lookupQSOs(new Callsign(LOOKUP_CALL));
            }
        });

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
