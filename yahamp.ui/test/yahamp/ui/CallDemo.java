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
import yahamp.ui.call.CallEditPart;

/** JUnit demo of the {@link CallEditPart}
 *  @author Kay Kasemir
 */
public class CallDemo
{
    @Test
    public void demoCall()
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final CallEditPart part = new CallEditPart("EM74");
        part.createPartControl(shell);

        shell.setText("Wait 5 seconds for call to be set..");
        // Simulate QSO that would operationally get injected by IEventBroker
        display.timerExec(5000, new Runnable()
        {
            @Override
            public void run()
            {
                shell.setText("Call info Display");
                part.setCallsign(new Callsign("W1AW"));
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
