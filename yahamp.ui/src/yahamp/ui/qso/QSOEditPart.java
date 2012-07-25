/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.qso;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import yahamp.model.Callsign;
import yahamp.model.Category;
import yahamp.model.QSO;
import yahamp.model.QSOParser;
import yahamp.model.UTC;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBLogbook;
import yahamp.rig.RigCtrlThread;
import yahamp.rig.RigInfo;
import yahamp.rig.RigListener;
import yahamp.ui.Activator;

/** Editor for a QSO
 *  @author Kay Kasemir
 */
public class QSOEditPart implements RigListener
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** Currently entered {@link Callsign} is posted to this event broker
     *  Will be <code>null</code> in unit tests.
     */
    @Inject
    private IEventBroker event_broker;

    /** Track rig frequency and mode */
    private RigCtrlThread rig_thread;

    /** GUI Elements */
    private Text entry, callsign, date, time, rst_sent, rst_rcvd, freq, mode, qsl, info;
    /** GUI Elements */
    private Combo category;

    /** Flag to avoid recursion when code sets the entry,
     *  then entry notices update and sends events because of that, ...
     */
    private boolean setting_entry = false;

    /** Create controls
     *  @param parent Parent widget
     */
    @PostConstruct
    public void createControls(final Composite parent)
    {
        createGUI(parent);

        populateCategories();

        rig_thread = RigCtrlThread.getInstance();
        rig_thread.addListener(this);

        hookActions();
    }

    /** Set focus to 'quick' entry field */
    @Focus
    public void onFocus()
    {
        entry.setFocus();
        selectEntryFieldy();
    }

    /** Remove resources */
    @PreDestroy
    public void dispose()
    {
        if (rig_thread != null)
        {
            rig_thread.removeListener(this);
            rig_thread.release();
        }
        // NOP
    }

    /** Create GUI elements
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new FillLayout());
        // Create 'scrolled' box within the parent
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        final Composite scrolled = new Composite(sc, SWT.NONE);
        sc.setContent(scrolled);

        final GridLayout layout = new GridLayout(7, false);
        scrolled.setLayout(layout);

        // Entry:    ___entry________________________________
        // Call:     ___callsign__________  RST:      _sent_  _rcvd_
        // UTC:      _date_ _time___ Freq:  __freq___ Mode:   _mode_
        // Category: _______________________________  Qsl:    _qsl__
        // --------- info -----------
        // --------------------------
        Label l = new Label(scrolled, 0);
        l.setText("Entry:");
        l.setLayoutData(new GridData());
        entry = new Text(scrolled, SWT.BORDER);
        entry.setToolTipText("Quick entry: Call rst-sent rst-received info");
        entry.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-1, 1));

        l = new Label(scrolled, 0);
        l.setText("Call:");
        l.setLayoutData(new GridData());
        callsign = new Text(scrolled, SWT.BORDER);
        callsign.setToolTipText("Call sign");
        callsign.setLayoutData(new GridData(SWT.FILL, 0, true, false, 3, 1));

        l = new Label(scrolled, 0);
        l.setText("RST:");
        l.setLayoutData(new GridData(SWT.RIGHT, 0, false, false));
        rst_sent = new Text(scrolled, SWT.BORDER);
        rst_sent.setToolTipText("RST sent");
        rst_sent.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        rst_rcvd = new Text(scrolled, SWT.BORDER);
        rst_rcvd.setToolTipText("RST received");
        rst_rcvd.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(scrolled, 0);
        l.setText("UTC:");
        l.setLayoutData(new GridData());
        date = new Text(scrolled, SWT.BORDER);
        date.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        time = new Text(scrolled, SWT.BORDER);
        time.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        showCurrentTime();

        l = new Label(scrolled, 0);
        l.setText("Freq:");
        l.setLayoutData(new GridData());
        freq = new Text(scrolled, SWT.BORDER);
        freq.setToolTipText("Frequency");
        freq.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(scrolled, 0);
        l.setText("Mode:");
        l.setLayoutData(new GridData());
        mode = new Text(scrolled, SWT.BORDER);
        mode.setToolTipText("Mode: CW, SSB, PSK, ...");
        mode.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        l = new Label(scrolled, 0);
        l.setText("Category:");
        l.setLayoutData(new GridData());
        category = new Combo(scrolled, SWT.DROP_DOWN);
        category.setToolTipText("Category");
        category.setLayoutData(new GridData(SWT.FILL, 0, true, false, 4, 1));

        l = new Label(scrolled, 0);
        l.setText("QSL:");
        l.setLayoutData(new GridData());
        qsl = new Text(scrolled, SWT.BORDER);
        qsl.setToolTipText("QSO sent, received, both?");
        qsl.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        info = new Text(scrolled, SWT.BORDER | SWT.WRAP | SWT.MULTI);
        info.setToolTipText("QSO details");
        info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        // Individual Text fields lack estimate for widths because they're empty,
        // so set an overall min. widths, derive min. height from widgets
        sc.setMinSize(scrolled.computeSize(250, SWT.DEFAULT));
    }

    /** Pupulate available categories from */
    private void populateCategories()
    {
        try
        (
            RDB rdb = new RDB();
        )
        {
            final RDBLogbook logbook = new RDBLogbook(rdb);
            for (final Category cat : logbook.getCategories())
                category.add(cat.getName());
        }
        catch (final Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot fetch categories", ex);
        }
    }

    private void hookActions()
    {
        // Handle 'quick entry'
        entry.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(final ModifyEvent e)
            {
                // Ignore changes that are done by code
                if (setting_entry)
                    return;
                QSO template;
                try
                {
                    template = getQso();
                }
                catch (final Exception ex)
                {
                    // Time didn't parse, use current time
                    showCurrentTime();
                    try
                    {
                        template = getQso();
                    }
                    catch (final Exception ex2)
                    {
                        logger.log(Level.FINE, "QSO info error", ex2);
                        return;
                    }
                }

                final String prev_call = template.getCall();
                final QSO qso =
                        QSOParser.analyze(entry.getText(), template);
                if (qso != null)
                {
                    showQso(qso);

                    final Callsign newcall = qso.getCallsign();
                    // Post new call sign via event broker
                    if (event_broker != null  &&  !newcall.getCall().equals(prev_call))
                    {
                        logger.log(Level.FINE, "Posting {0}", newcall);
                        event_broker.post("yahamp", newcall);
                    }
                }
            }
        });

        entry.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                if (e.character != '\r')
                    return;
                save();
                selectEntryFieldy();
            }
        });
        final KeyListener save_on_return = new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                if (e.character != '\r')
                    return;
                save();
            }
        };
        callsign.addKeyListener(save_on_return);
        date.addKeyListener(save_on_return);
        time.addKeyListener(save_on_return);
        rst_sent.addKeyListener(save_on_return);
        rst_rcvd.addKeyListener(save_on_return);
        freq.addKeyListener(save_on_return);
        mode.addKeyListener(save_on_return);
        qsl.addKeyListener(save_on_return);
        info.addKeyListener(save_on_return);
        category.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                save();
            }
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                save();
            }
        });
    }

    /** Select complete 'quick' entry so typing will replace what's in there */
    private void selectEntryFieldy()
    {
        entry.setSelection(0, entry.getText().length());
    }

    /** Clear all GUI elements */
    public void clear()
    {
        entry.setText("");
        callsign.setText("");
        date.setText("");
        time.setText("");
        rst_sent.setText("");
        rst_rcvd.setText("");
        freq.setText("");
        mode.setText("");
        qsl.setText("");
        category.setText("");
        info.setText("");
    }

    /** @return QSO for values in GUI */
    private QSO getQso() throws Exception
    {
        final UTC utc = new UTC(date.getText(), time.getText());
        final QSO qso = new QSO(callsign.getText());
        qso.setUTC(utc);
        qso.setRstSent(rst_sent.getText());
        qso.setRstRcvd(rst_rcvd.getText());
        qso.setFreq(freq.getText());
        qso.setMode(mode.getText());
        qso.setCategory(category.getText());
        qso.setQsl(qsl.getText());
        qso.setInfo(info.getText());
        return qso;
    }

    /** @param qso QSO to display in the View */
    private void showQso(final QSO qso)
    {
        callsign.setText(qso.getCall());
        showTime(qso.getUTC());
        rst_sent.setText(qso.getRstSent());
        rst_rcvd.setText(qso.getRstRcvd());

        freq.setText(qso.getFreq());
        freq.setToolTipText("Frequency " + qso.getFreq());

        mode.setText(qso.getMode());
        mode.setToolTipText("Mode " + qso.getMode());

        qsl.setText(qso.getQsl());
        category.setText(qso.getCategory());
        info.setText(qso.getInfo());
    }

    /** Display current date and time */
    protected void showCurrentTime()
    {
        final UTC utc = new UTC();
        showTime(utc);
    }

    /** Display date and time
     *  @param utc {@link UTC}
     */
    private void showTime(final UTC utc)
    {
        final String date_txt = utc.toDateString();
        final String time_txt = utc.toTimeString();
        date.setText(date_txt);
        date.setToolTipText("Date " + date_txt);
        time.setText(time_txt);
        time.setToolTipText("Time " + time_txt);
    }

    /** Update the QSO info displayed in the part
     *  @param qso {@link QSO}
     */
    @Inject
    public void displayQSO(@Optional @UIEventTopic("yahamp") final QSO qso)
    {
        if (qso == null  ||  entry == null  ||  entry.isDisposed())
            return;
        logger.log(Level.FINE, "Received {0}", qso);
        showQso(qso);

        final StringBuilder quick = new StringBuilder();
        quick.append(qso.getCall()).append(' ');
        quick.append(qso.getRstSent()).append(' ');
        quick.append(qso.getRstRcvd()).append(' ');
        quick.append(qso.getInfo());
        setting_entry = true;
        try
        {
            entry.setText(quick.toString());
            selectEntryFieldy();
        }
        finally
        {
            setting_entry = false;
        }
    }

    /** Save data in display to RDB */
    private void save()
    {
        try
        (
            RDB rdb = new RDB();
        )
        {
            // Save QSO
            final QSO qso = getQso();
            final RDBLogbook logbook = new RDBLogbook(rdb);
            logbook.save(qso);

            // Update rest of app about new/updated QSO
            if (event_broker != null)
                event_broker.post(Activator.TOPIC, qso);
        }
        catch (final Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot save QSO", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void newRigInfo(final RigInfo info)
    {
        if (freq.isDisposed())
            return;
        freq.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                freq.setText(info.getFreq());
                mode.setText(info.getMode());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void rigError(final Exception ex)
    {
        // Ignore
    }
}