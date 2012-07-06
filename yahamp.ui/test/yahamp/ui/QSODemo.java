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

import yahamp.model.QSO;
import yahamp.model.UTC;
import yahamp.ui.qso.QSOEditPart;

/** JUnit demo of the {@link QSOEditPart}
 *  @author Kay Kasemir
 */
public class QSODemo
{
    @Test
    public void demoQSO()
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final QSOEditPart part = new QSOEditPart();
        part.createControls(shell);

        shell.setText("Wait 5 seconds for QSO to be set..");
        // Simulate QSO that would operationally get injected by IEventBroker
        display.timerExec(5000, new Runnable()
        {
            @Override
            public void run()
            {
                shell.setText("QSO Display");
                final QSO qso = new QSO("TE1ST");
                qso.setCategory("Default");
                qso.setFreq("14.025");
                qso.setMode("CW");
                qso.setRstSent("5nn");
                qso.setRstRcvd("55n");
                qso.setUTC(new UTC());
                qso.setInfo("Some info...");
                part.displayQSO(qso );
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
