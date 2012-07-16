/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.previous_qsos;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import yahamp.model.Callsign;
import yahamp.model.QSO;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBLogbook;
import yahamp.ui.Activator;
import yahamp.ui.internal.LogColumn;

public class PreviousQSOsPart
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** Call sign */
    private Text call;

    /** Previous QSOs with that call */
    private TableViewer viewer;

    /** Currently handled call, cached to avoid recursion */
    private Callsign current_call = null;

    @PostConstruct
    public void createPartControl(final Composite parent)
    {
        final GridLayout layout = new GridLayout(2, false);
        parent.setLayout(layout);

        // Call:     ___callsign_____
        // --------- list -----------
        // --------------------------
        final Label l = new Label(parent, 0);
        l.setText("Call:");
        l.setLayoutData(new GridData());

        call = new Text(parent, SWT.BORDER);
        call.setToolTipText("Call sign");
        call.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        // Lookup previous QSOs when call is entered
        call.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                lookupQSOs(new Callsign(call.getText().trim().toUpperCase()));
                onFocus();
            }
        });

        // TableColumnLayout requires table to be only child of a container
        final Composite table_box = new Composite(parent, 0);
        table_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        final TableColumnLayout table_layout = new TableColumnLayout();
        table_box.setLayout(table_layout);

        viewer = new TableViewer(table_box,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER |
                SWT.FULL_SELECTION | SWT.VIRTUAL);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.setContentProvider(new LogTableContentProvider());

        new LogColumn(table_layout, viewer, "UTC", 25, 80)
        {
            @Override
            protected void update(final ViewerCell cell, final QSO qso)
            {
                cell.setText(qso.getUTC().toString());
            }
        };
        new LogColumn(table_layout, viewer, "Freq", 20, 35)
        {
            @Override
            protected void update(final ViewerCell cell, final QSO qso)
            {
                cell.setText(qso.getFreq());
            }
        };
        new LogColumn(table_layout, viewer, "Mode", 1, 45)
        {
            @Override
            protected void update(final ViewerCell cell, final QSO qso)
            {
                cell.setText(qso.getMode());
            }
        };
        new LogColumn(table_layout, viewer, "Cat", 30, 50)
        {
            @Override
            protected void update(final ViewerCell cell, final QSO qso)
            {
                cell.setText(qso.getCategory());
            }
        };
        new LogColumn(table_layout, viewer, "Info", 100, 0)
        {
            @Override
            protected void update(final ViewerCell cell, final QSO qso)
            {
                cell.setText(qso.getInfo());
            }
        };
    }

    /** Select call widget and all text in there,
     *  so pressing any key will start replacing its text
     */
    @Focus
    public void onFocus()
    {
        call.setFocus();
        call.setSelection(0, call.getText().length());
    }

    /** Select specific QSO in table
     *
     *  <p>Called via {@link IEventBroker} mechanism and injection
     *  @param call_or_qso {@link Callsign} for which to locate QSOs
     */
    @Inject
    @Optional
    public void lookupQSOs(@UIEventTopic(Activator.TOPIC) final Callsign callsign)
    {
        // Avoid recursion or redundant lookups
        if (callsign == null  ||  callsign.equals(current_call))
            return;
        current_call = callsign;
        logger.log(Level.FINE, "Received {0}", current_call);
        if (callsign == null  ||  call == null  ||  call.isDisposed())
            return;
        call.setText(callsign.getCall());

        final Job job = new Job("Previous QSOS")
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                // Lookup of previous QSOs
                final QSO[] qsos;
                try
                (
                    final RDB rdb = new RDB();
                )
                {
                    qsos = new RDBLogbook(rdb).findQSOs(callsign.getCall());
                }
                catch (final Exception ex)
                {
                    logger.log(Level.WARNING, "Previous QSO lookup error", ex);
                    return Status.CANCEL_STATUS;
                }

                if (! call.isDisposed())
                {
                    call.getDisplay().asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            viewer.setInput(Arrays.asList(qsos));
                        }
                    });

                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
