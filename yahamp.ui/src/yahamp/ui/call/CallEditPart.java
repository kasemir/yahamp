/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.call;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import yahamp.flags.FlagImageFile;
import yahamp.model.CallBook;
import yahamp.model.CallInfo;
import yahamp.model.Callsign;
import yahamp.model.QSO;
import yahamp.model.callbook.CombinedCallBook;
import yahamp.model.earth.Earth;
import yahamp.model.earth.Maidenhead;
import yahamp.rdb.RDB;
import yahamp.rdb.RDBCallbook;
import yahamp.ui.Activator;
import yahamp.ui.internal.Preferences;

/** Editor for a Call
 *  @author Kay Kasemir
 */
public class CallEditPart
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** 'My' location, used to compute QSO distances */
    final private Maidenhead my_location;

    // GUI constants
    private static final int CHAR_WIDTH = 10;
    private static final int FLAG_HEIGHT = FlagImageFile.WIDTH/4;
    private static final int FLAG_WIDTH = FlagImageFile.HEIGHT/4;

    // GUI Elements
    private ImageRegistry flag_images;
    private Text call, name, addr, city, state, zip;
    private Button qrz_loookup;
    private Text grid, county, country;
    private Text dx_cc, fists_nr, fists_cc, flying_pigs;
    private Label flag;
    private Text distance;

    /** Event broker where result of call sign lookup is posted */
    @Inject
    private IEventBroker event_broker;

    /** Call info posted by this part to avoid recursion */
    private volatile CallInfo posted_call = null;

    @Inject
    private EPartService part_service;

    /** @parammy_grid 'My' location, used to compute QSO distances */
    @Inject
    public CallEditPart(
            @Preference(nodePath=Activator.ID, value=Preferences.GRID)
            final String my_grid)
    {
        my_location = Maidenhead.fromGrid(my_grid);
    }

    /** Create GUI components
     *  @param parent Parent widget
     */
    @PostConstruct
    public void createPartControl(final Composite parent)
    {
        createGUI(parent);

        call.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                startCallLookup(call.getText());
            }
        });

        qrz_loookup.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                // Not available in unit tests
                if (part_service == null)
                    return;
                // Locate part for QRZ web page display
                final MPart part = part_service.findPart("yahamp.ui.call.qrz");
                part_service.showPart(part, PartState.VISIBLE);

                // Update call sign in QRZ dispay
                final QRZCallDisplayPart qrz = (QRZCallDisplayPart) part.getObject();
                qrz.setCallsign(new Callsign(call.getText()));
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
        name.addKeyListener(save_on_return);
        addr.addKeyListener(save_on_return);
        city.addKeyListener(save_on_return);
        state.addKeyListener(save_on_return);
        zip.addKeyListener(save_on_return);
        grid.addKeyListener(save_on_return);
        county.addKeyListener(save_on_return);
        country.addKeyListener(save_on_return);
        fists_nr.addKeyListener(save_on_return);
        fists_cc.addKeyListener(save_on_return);
        flying_pigs.addKeyListener(save_on_return);
    }

    /** Create GUI components
     *  @param parent Parent widget
     */
    private void createGUI(final Composite parent)
    {
        parent.setLayout(new FillLayout());
        flag_images = new ImageRegistry(parent.getDisplay());

        // Create 'scrolled' box within the parent
        final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        final Composite scrolled = new Composite(sc, SWT.NONE);
        sc.setContent(scrolled);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 8;
        scrolled.setLayout(layout);

        /* 0        1           2           3      4    5    6    7
           Call:    _______________________ F  L  A  G  Distance: ________
           Name:    _______________________ F  L  A  G  [QRZ Lookup]
           Address: ___________________________________________________
           City:    _______________________ State: __   ZIP: ______
           County: _______________________  Grid:  __________
           Country: ______________________  DXCC:  __
           Fists #: __________   CC:       _______ FP:  ______
        */

        // Row 1
        Label l = new Label(scrolled, SWT.LEFT);
        l.setText("Call:");
        l.setLayoutData(new GridData());

        call = new Text(scrolled, SWT.BORDER);
        call.setToolTipText("Callsign");
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        call.setLayoutData(gd);

        flag = new Label(scrolled, SWT.LEFT);
        flag.setText("<flag>"); //$NON-NLS-1$
        gd = new GridData();
        gd.widthHint = FLAG_WIDTH;
        gd.heightHint = FLAG_HEIGHT;
        gd.horizontalSpan = 2;
        gd.verticalSpan = 2;
        flag.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("Distance:");
        gd = new GridData();
        gd.horizontalSpan = 2;
        l.setLayoutData(gd);

        distance = new Text(scrolled, SWT.LEFT);
        distance.setEditable(false);
        gd = new GridData();
        distance.setLayoutData(gd);

        // Row 2
        l = new Label(scrolled, SWT.LEFT);
        l.setText("Name:");
        l.setLayoutData(new GridData());

        name = new Text(scrolled, SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        name.setLayoutData(gd);

        // -- rest of flag

        qrz_loookup = new Button(scrolled, SWT.PUSH);
        qrz_loookup.setText("QRZ");
        qrz_loookup.setToolTipText("Open QRZ.COM page for callsign");
        gd = new GridData();
        gd.horizontalSpan = 3;
        gd.horizontalAlignment = SWT.FILL;
        qrz_loookup.setLayoutData(gd);

        // Row 3
        l = new Label(scrolled, SWT.LEFT);
        l.setText("Address:");
        l.setLayoutData(new GridData());

        addr = new Text(scrolled, SWT.BORDER);
        addr.setToolTipText("Address: Street, ...");
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        addr.setLayoutData(gd);

        // Row 4
        l = new Label(scrolled, SWT.LEFT);
        l.setText("City:");
        l.setLayoutData(new GridData());

        city = new Text(scrolled, SWT.BORDER);
        city.setToolTipText("Town or city");
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        city.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("State:");
        l.setLayoutData(new GridData());

        state = new Text(scrolled, SWT.BORDER);
        state.setToolTipText("State");
        gd = new GridData();
        gd.widthHint = 2*CHAR_WIDTH;
        state.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("Zip:");
        l.setLayoutData(new GridData());

        zip = new Text(scrolled, SWT.BORDER);
        zip.setToolTipText("Zip code:");
        gd = new GridData();
        gd.horizontalSpan = 2;
        zip.setLayoutData(gd);

        // Row 5
        l = new Label(scrolled, SWT.LEFT);
        l.setText("County:");
        l.setLayoutData(new GridData());

        county = new Text(scrolled, SWT.BORDER);
        county.setToolTipText("County within State");
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        county.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("Grid:");
        l.setLayoutData(new GridData());

        grid = new Text(scrolled, SWT.BORDER);
        grid.setToolTipText("Maidenhead grid locator");
        gd = new GridData();
        gd.widthHint = 6*CHAR_WIDTH;
        gd.horizontalSpan = 4;
        grid.setLayoutData(gd);

        // Row 6
        l = new Label(scrolled, SWT.LEFT);
        l.setText("Country:");
        l.setLayoutData(new GridData());

        country = new Text(scrolled, SWT.BORDER);
        country.setToolTipText("Country");
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        country.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("DXCC:");
        l.setLayoutData(new GridData());

        dx_cc = new Text(scrolled, SWT.BORDER);
        dx_cc.setToolTipText("DX-CC Entity");
        gd = new GridData();
        gd.horizontalSpan = 4;
        gd.widthHint = 4*CHAR_WIDTH;
        dx_cc.setLayoutData(gd);

        // Row 7
        l = new Label(scrolled, SWT.LEFT);
        l.setText("Fists:");
        l.setLayoutData(new GridData());

        fists_nr = new Text(scrolled, SWT.BORDER);
        fists_nr.setToolTipText("Fists CW club number");
        gd = new GridData();
        gd.widthHint = 6*CHAR_WIDTH;
        fists_nr.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("..CC");
        l.setLayoutData(new GridData());

        fists_cc = new Text(scrolled, SWT.BORDER);
        fists_cc.setToolTipText("Fists CC Number");
        gd = new GridData();
        gd.widthHint = 5*CHAR_WIDTH;
        fists_cc.setLayoutData(gd);

        l = new Label(scrolled, SWT.LEFT);
        l.setText("FP:");
        l.setLayoutData(new GridData());

        flying_pigs = new Text(scrolled, SWT.BORDER);
        flying_pigs.setToolTipText("Flying Pigs Number");
        gd = new GridData();
        gd.widthHint = 5*CHAR_WIDTH;
        gd.horizontalSpan = 3;
        flying_pigs.setLayoutData(gd);

        sc.setMinSize(scrolled.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /** Set image
     *  @param country Name of country
     */
    private void setFlagImage(final String country)
    {
        try
        {
            if (country.isEmpty())
                setFlagImage((File) null);
            else
                setFlagImage(FlagImageFile.get(country));
        }
        catch (final Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Cannot obtain flag for '" + country + "'", ex);
            setFlagImage((File) null);
        }
    }

    /** Set image
     *  @param file Image {@link File} or <code>null</code>
     */
    private void setFlagImage(final File file)
    {
        Image image;
        if (file == null)
            image = null;
        else
        {
            final String filename = file.getName();
            image = flag_images.get(filename);
            if (image == null)
            {
                final Display display = flag.getDisplay();
                final Image orig = new Image(display, file.getAbsolutePath());

                // Scale image from original width x height to FLAG_WIDTH x FLAG_HEIGHT
                // 255 -> 30..230
                final int width = orig.getBounds().width;
                final int height = orig.getBounds().height;
                image = new Image(display, FLAG_WIDTH, FLAG_HEIGHT);
                final GC gc = new GC(image);

                // Fudge: Original images are 255 pixel high,
                // but have white border, ~30 pixel, at top and bottom
                final int border = height * 30 / 255;
                gc.drawImage(orig, 0, border, width, height-2*border, 0, 0, FLAG_WIDTH, FLAG_HEIGHT);

                gc.dispose();
                orig.dispose();

                // Remember image and allow auto-disposal
                flag_images.put(filename, image);
            }
        }
        flag.setImage(image);
    }

    /** Set focus */
    @Focus
    public void onFocus()
    {
        call.setFocus();
        selectCall();
    }

    /** Select the complete call so that typing another char
     *  will replace the call
     */
    private void selectCall()
    {
        call.setSelection(0, call.getText().length());
    }

    /** Clear all GUI elements */
    public void clear()
    {
        display(new CallInfo(""));
    }

    /** Display call info
     * @param info {@link CallInfo}
     */
    public void display(final CallInfo info)
    {
        call.setText(info.getCall());
        name.setText(info.getName());
        addr.setText(info.getAddress());
        city.setText(info.getCity());
        state.setText(info.getState());
        zip.setText(info.getZip());
        grid.setText(info.getGrid());
        county.setText(info.getCounty());
        country.setText(info.getCountry());
        dx_cc.setText(info.getDxcc());
        fists_nr.setText(Integer.toString(info.getFists_nr()));
        fists_cc.setText(Integer.toString(info.getFists_cc()));
        flying_pigs.setText(Integer.toString(info.getFp_nr()));

        // TODO What if country is Canary Islands, Balearic Island, ... for Spain?
        setFlagImage(info.getCountry());

        // If grid is known, compute distance
        if (info.getGrid().length() > 0)
        {
            final double km = Earth.getDistance(my_location, Maidenhead.fromGrid(info.getGrid()));
            distance.setText(String.format("%.1f km", km));
        }
        else
            distance.setText("");

        if (event_broker != null)
        {
            posted_call = info;
            event_broker.post(Activator.TOPIC, info);
        }
    }

    /** Remove resources */
    @PreDestroy
    public void dispose()
    {
        // NOP
    }

    /** Update GUI when currently selected QSO or call changes
     *
     *  <p>Called via {@link IEventBroker} mechanism and injection
     *  @param call_or_qso Currently selected {@link Callsign} or {@link QSO}
     */
    @Inject
    public void setCallsign(@Optional @UIEventTopic(Activator.TOPIC) final Callsign call_or_qso)
    {
        if (call_or_qso == posted_call  ||  call.isDisposed())
            return;
        logger.log(Level.FINE, "Received {0}", call_or_qso);
        if (call_or_qso != null)
        {
            call.setText(call_or_qso.getCall());
            startCallLookup(call_or_qso.getCall());
        }
    }

    /** Start lookup of call book info for callsign
     *  @param callsign Call sign
     */
    private void startCallLookup(final String callsign)
    {
        final Job job = new Job("Callbook lookup")
        {
            @Override
            protected IStatus run(final IProgressMonitor monitor)
            {
                try
                (
                    RDB rdb = new RDB();
                )
                {
                    final CallBook calls = new CombinedCallBook(rdb);
                    final CallInfo info = calls.lookup(callsign);
                    if (call != null  &&  !call.isDisposed())
                        call.getDisplay().asyncExec(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (call == null  ||  call.isDisposed())
                                    return;
                                display(info);
                            }
                        });
                }
                catch (final Exception ex)
                {
                    logger.log(Level.WARNING, "Callbook lookup error", ex);
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    /** Save call info */
    private void save()
    {
        final CallInfo info = getCallInfo();
        try
        {
            try
            (
                RDB rdb = new RDB();
            )
            {
                logger.log(Level.FINE, "Save: ", info);
                final RDBCallbook calls = new RDBCallbook(rdb);
                calls.save(info);
            }
        }
        catch (final Exception ex)
        {
            logger.log(Level.SEVERE, "Cannot save call info", ex);
        }
    }

    /** Read currently displayed/entered info
     *  @return {@link CallInfo}
     */
    private CallInfo getCallInfo()
    {
        final CallInfo info = new CallInfo(call.getText());
        info.setName(name.getText());
        info.setAddress(addr.getText());
        info.setCity(city.getText());
        info.setState(state.getText());
        info.setZip(zip.getText());
        info.setGrid(grid.getText());
        info.setCounty(county.getText());
        info.setCountry(country.getText());
        info.setFists_nr(getNumber(fists_nr));
        info.setFists_cc(getNumber(fists_cc));
        info.setFp_nr(getNumber(flying_pigs));
        return info;
    }

    /** Fetch number from widget, correcting it to "0" on format errors
     *  @param widget {@link Text} widget
     *  @return Number
     */
    private int getNumber(final Text widget)
    {
        final String text = widget.getText().trim();
        try
        {
            return Integer.valueOf(text);
        }
        catch (final NumberFormatException ex)
        {
            widget.setText("0");
        }
        return 0;
    }
}