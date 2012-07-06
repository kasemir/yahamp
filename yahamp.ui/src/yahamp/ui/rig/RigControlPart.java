/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.rig;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import yahamp.rig.RigCtrlThread;
import yahamp.rig.RigInfo;
import yahamp.rig.RigListener;

/** Rig control/display
 *  @author Kay Kasemir
 */
public class RigControlPart implements RigListener
{
    /** GUI Elements */
	private Text freq, mode, status;

	/** Rig control thread */
	private RigCtrlThread rig_thread;

	/** Create GUI elements
	 *  @param parent Parent widget
	 */
	@PostConstruct
	public void createPartControl(final Composite parent)
	{
	    final GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);

        // New row
        Label l = new Label(parent, 0);
        l.setText("Frequency:");
        l.setLayoutData(new GridData());

        freq = new Text(parent, SWT.BORDER);
        freq.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        freq.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                if (e.character != '\r')
                    return;
                // Update freq on rig
                try
                {
                    final double f = Double.parseDouble(freq.getText().trim());
                    if (rig_thread != null)
                        rig_thread.setFreq(f);
                }
                catch (final Throwable ex)
                {
                    Logger.getLogger(getClass().getName()).
                        log(Level.WARNING, "Cannot set rig frequency", ex);
                }
            }
        });

        l = new Label(parent, 0);
        l.setText("Mode:");
        l.setLayoutData(new GridData());

        mode = new Text(parent, SWT.BORDER);
        mode.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        // New row
        status = new Text(parent, SWT.READ_ONLY | SWT.BORDER);
        status.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));

        status.setText("<not connected to rig>");

        rig_thread = RigCtrlThread.getInstance();
        rig_thread.addListener(this);
	}

	/** Set focus */
	@Focus
	public void onFocus()
	{
	    freq.setFocus();
	}

	/** Cleanup */
	@PreDestroy
	public void preDestroy()
	{
	    if (rig_thread != null)
	    {
    	    rig_thread.removeListener(this);
    	    rig_thread.release();
	    }
	}

	/** {@inheritDoc} */
    @Override
    public void newRigInfo(final RigInfo info)
    {
        onGuiThread(new Runnable()
        {
            @Override
            public void run()
            {
                freq.setText(info.getFreq());
                mode.setText(info.getMode());
                status.setText(info.getModel().toString());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void rigError(final Exception ex)
    {
        onGuiThread(new Runnable()
        {
            @Override
            public void run()
            {
                freq.setText("");
                mode.setText("");
                status.setText(ex.getMessage());
            }
        });
    }

    /** Execute code on GUI thread with valid, non-disposed GUI elements
     *  @param code {@link Runnable} to run
     */
    private void onGuiThread(final Runnable code)
    {
        if (status == null  ||  status.isDisposed())
            return;
        status.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (status == null  ||  status.isDisposed())
                    return;
                code.run();
            }
        });
    }
}