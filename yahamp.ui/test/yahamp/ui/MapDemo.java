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

import yahamp.model.CallInfo;
import yahamp.ui.map.MapDisplayPart;

/** JUnit demo of the {@link MapDisplayPart}
 *  @author Kay Kasemir
 */
public class MapDemo
{
    @Test
    public void demoMap() throws Exception
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        final MapDisplayPart part = new MapDisplayPart("W1AW", "FN31pr");
        part.createPartControl(shell);

        shell.setText("Wait 5 seconds for location to be set..");
        // Simulate callsign that would operationally get injected by IEventBroker
        display.timerExec(5000, new Runnable()
        {
            @Override
            public void run()
            {
                shell.setText("Location?");
                CallInfo info = new CallInfo("OTHER");
                info.setGrid("JO40gd");
                part.markCall(info);
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
