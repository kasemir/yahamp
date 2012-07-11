/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.log;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;

import yahamp.model.Category;
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
    private static final String ALL = "- All -";

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

	/** QSO category. Index 0 for 'all' */
    private Combo category;

	/** Create GUI elements
	 *  @param parent Parent widget
	 */
	@PostConstruct
	public void createPartControl(final Composite parent)
	{
		createLogTable(parent);

		createMenu();

		category.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                loadQSOs();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }
        });

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
        parent.setLayout(new GridLayout(2, false));

        Label l = new Label(parent, 0);
        l.setText("Category");
        l.setLayoutData(new GridData());

        category = new Combo(parent, 0);
        category.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        category.setText(ALL);

        // TableColumnLayout requires table to be only widget
        // in its container, so create box for that
        final Composite box = new Composite(parent, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        final TableColumnLayout layout = new TableColumnLayout();
		box.setLayout(layout);

		viewer = new TableViewer(box,
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

    /** Create context menu */
	private void createMenu()
    {
	    final Control control = viewer.getControl();
	    final MenuManager manager = new MenuManager();
        manager.add(new Action("Remove")
        {
            @Override
            public void run()
            {
                final IStructuredSelection selection =
                        (IStructuredSelection) viewer.getSelection();
                if (selection.isEmpty())
                    return;
                final QSO qso = (QSO) selection.getFirstElement();
                if (!  MessageDialog.openConfirm(control.getShell(),
                        "Delete",
                        NLS.bind("Delete QSO with {0}?", qso.getCall())))
                    return;
                delete(qso);
            }
        });
	    final Menu menu = manager.createContextMenu(control);
	    control.setMenu(menu);
    }

    /** Load QSOs from logbook */
    private void loadQSOs()
    {
        final String selected_category = category.getText().trim();

        final Job job = new Job("Fetch Log")
        {
    		@Override
    		protected IStatus run(final IProgressMonitor monitor)
    		{
                final Category[] categories;
    			final List<QSO> qsos;
    			try
    		    (
    	            RDB rdb = new RDB();
    		    )
    		    {
    		        final RDBLogbook logbook = new RDBLogbook(rdb);
    		        categories = logbook.getCategories();

    		        if (ALL.equals(selected_category))
    		            logbook.setCategory(null);
    		        else
    		            logbook.setCategory(selected_category);
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
                        // Need to update category list?
                        final String[] cats = new String[1 + categories.length];
                        cats[0] = ALL;
                        for (int i=0; i<categories.length; ++i)
                            cats[i+1] = categories[i].getName();
                        final String[] items = category.getItems();
                        if (!Arrays.equals(items, cats))
                        {   // This flickers, so only done when necessary
                            category.setItems(cats);
                            category.setText(selected_category == null ? ALL : selected_category);
                        }
                        // Updat QSO table
                        LogDisplayPart.this.qsos = qsos;
                        viewer.setInput(qsos);
                    }
    			});

    			return Status.OK_STATUS;
    		}
        };
        job.schedule();
    }

    /** Delete a QSO
     *  @param qso QSO to delete
     */
    protected void delete(final QSO qso)
    {
        try
        (
            RDB rdb = new RDB();
        )
        {
            final RDBLogbook logbook = new RDBLogbook(rdb);
            logbook.delete(qso);
        }
        catch (final Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot save QSO", ex);
            return;
        }
        viewer.remove(qso);
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
            qso.setNumber(index + 1);
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
