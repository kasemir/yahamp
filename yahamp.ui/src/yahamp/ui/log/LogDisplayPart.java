/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.log;

import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import yahamp.model.QSO;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBLogbook;
import yahamp.ui.Activator;
import yahamp.ui.internal.LogColumn;
import yahamp.ui.previous_qsos.LogTableContentProvider;

/** Tabular display of the log
 *  @author Kay Kasemir
 */
public class LogDisplayPart
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** Currently selected {@link QSO} is posted to this event broker
     *  Will be <code>null</code> in unit tests.
     */
    @Inject
    private IEventBroker event_broker;

    /** Table of {@link QSO}s */
	private TableViewer viewer;

	/** QSOs to display. Input for the viewer */
	private List<QSO> qsos = new ArrayList<>();

	/** Currently selected QSO, posted to broker.
	 *  Cached to avoid recursion
	 */
	private QSO selected_qso = null;

	/** Create GUI elements
	 *  @param parent Parent widget
	 */
	@PostConstruct
	public void createPartControl(final Composite parent)
	{
		createLogTable(parent);

		// Publish currently selected QSO
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(final SelectionChangedEvent event)
            {
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                final Object item = selection.getFirstElement();
                if (! (item instanceof QSO))
                    return;

                selected_qso = (QSO) item;
                if (event_broker != null)
                {
                    logger.log(Level.FINE, "Posting {0}", selected_qso);
                    event_broker.post("yahamp", selected_qso);
                }
            }
        });

        loadQSOs();
	}

	@Focus
    public void setFocus()
    {
    	viewer.getTable().setFocus();
    }

    /** Create table that displays the log
	 *  @param parent Parent widget
	 */
    private void createLogTable(final Composite parent)
    {
        final TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER |
				SWT.FULL_SELECTION | SWT.VIRTUAL);
		viewer.setUseHashlookup(true);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new LogTableContentProvider());

		new LogColumn(layout, viewer, "#", 1, 35)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(Integer.toString(qso.getNumber()));
			}
		};
		new LogColumn(layout, viewer, "UTC", 25, 85)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getUTC().toString());
			}
		};
		new LogColumn(layout, viewer, "Call", 10, 65)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getCall());
			}
		};
		new LogColumn(layout, viewer, "Sent", 10, 40)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getRstSent());
			}
		};
		new LogColumn(layout, viewer, "Rcvd", 10, 40)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getRstRcvd());
			}
		};
		new LogColumn(layout, viewer, "Freq", 10, 35)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getFreq());
			}
		};
		new LogColumn(layout, viewer, "Mode", 10, 40)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getMode());
			}
		};
		new LogColumn(layout, viewer, "Cat", 10, 50)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getCategory());
			}
		};
		new LogColumn(layout, viewer, "Info", 100, 10)
		{
			@Override
			protected void update(final ViewerCell cell, final QSO qso)
			{
				cell.setText(qso.getInfo());
			}
		};

		// Enable tool tips
        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
    }

	/** Load QSOs from logbook */
    private void loadQSOs()
    {
        final Job job = new Job("Fetch Log")
        {
    		@Override
    		protected IStatus run(final IProgressMonitor monitor)
    		{
    			final List<QSO> qsos;
    			try
    		    (
    	            RDB rdb = new RDB();
    		    )
    		    {
    		        final RDBLogbook logbook = new RDBLogbook(rdb);
    		        logbook.setCategory(null);
    		        final int count = logbook.getQsoCount();
                    qsos = new ArrayList<>(count);
    		        for (int i=0; i<count; ++i)
    		            qsos.add(logbook.getQso(i));
    		    }
    		    catch (final Exception ex)
    		    {
    		        ex.printStackTrace();
    		        return Status.CANCEL_STATUS;
    		    }

    			viewer.getControl().getDisplay().asyncExec(new Runnable()
    			{
                    @Override
                    public void run()
                    {
                        LogDisplayPart.this.qsos = qsos;
                        viewer.setInput(qsos);
                    }
    			});

    			return Status.OK_STATUS;
    		}
        };
        job.schedule();
    }

	/** Select specific QSO in table
	 *
	 *  <p>Called via {@link IEventBroker} mechanism and injection
	 *  @param qso QSO to select
	 */
    @Inject
    @Optional
	public void selectQSO(@UIEventTopic(Activator.TOPIC) final QSO qso)
    {
        if (qso == selected_qso)
        {   // Avoid recursion
            logger.log(Level.FINE, "Ignoring QSO");
            return;
        }

        // Is that a known QSO?
        final int index = qsos.indexOf(qso);
        if (index >= 0)
        {   // Update
            logger.log(Level.FINE, "Updating {0}", qso);
            qsos.set(index, qso);
            viewer.replace(qso, index);
        }
        else
        {   // New QSO
            logger.log(Level.FINE, "Adding {0}", qso);
            qso.setNumber(qsos.size() + 1);
            qsos.add(qso);
            viewer.add(qso);
        }
        // In any case, select/highlight the QSO
        viewer.setSelection(new StructuredSelection(qso), true);
    }
}
