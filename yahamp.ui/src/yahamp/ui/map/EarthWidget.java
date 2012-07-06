/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import yahamp.model.UTC;
import yahamp.model.earth.Position;

/**
 * A view of earth as an SWT widget. Implemented following the Eclipse 3.1
 * "Platform Plug-In Developer Guide", sub-section "Standard Widget Toolkit",
 * "Widgets", Custom Widgets", which lead to "Creating Your Own Widgets using
 * SWT" on http://www.eclipse.org/articles.
 *
 * @author Kay Kasemir
 */
public class EarthWidget extends Canvas implements DisposeListener,
        PaintListener
{
    private static final boolean debug = false;
	private final Color border_color;
    private final EarthImage earth;
    private final MarkerPainter marker_painter, beacon_painter;
    private final Beacon beacons[] = Beacons.getBeacons();
    class Marker
    {
    	private final Position location;
    	private final String text;

		public Marker(final Position location, final String text)
		{
			this.location = location;
			this.text = text;
		}

		public Position getLocation()
		{	return location; }

		public String getText()
		{	return text;  }
    }

    private final ArrayList<Marker> markers = new ArrayList<Marker>();

    private final Scheduler scheduler = new Scheduler();
    private final Scheduleable clock_task = new ClockTask();
    /** Runnable for Display.timerExec to toggle a redraw */
    private final Runnable redraw_task = new Runnable()
    {
        @Override
        public void run()
        {
            if (debug)
                System.out.println("\nRedraw Task at " + new UTC().toString());
            if (EarthWidget.this.isDisposed())
                return;
            redraw();
            scheduleRedraw();
        }
    };

    /** Image of sunlit earth. */
    private Image earth_image;

    /** Time in millis when <code>earth_image</code> was last evaluted. */
    private long earth_image_millis;

    /** Time between updates of <code>earth_image</code> in millis. */
    private static final int EARTH_IMAGE_EVAL_PERIOD_IN_MILLIS = 10*60*1000;
    // 10 minutes

    /** Constructor
     *  @param parent The usual SWT parent
     *  @param style Usual SWT style
     *  @param size One of the EarthImage.Size constants
     *  @throws Exception
     */
    public EarthWidget(final Composite parent, final int style, final EarthImage.Size size) throws Exception
    {
    	this(parent, style,
    	     new EarthImage(parent.getDisplay(), size));
    }

    /** Constructor for standalone test where EarthImage cannot
     *  read the image streams from the plugin.
     */
    public EarthWidget(final Composite parent, final InputStream day_stream, final InputStream night_stream)
    {
    	this(parent, 0,
    		new EarthImage(parent.getDisplay(), day_stream, night_stream));
    }

    private EarthWidget(final Composite parent, final int style, final EarthImage earth)
    {
        // Don't clear the background (paint white) before redraw
        // to reduce flicker
    	super(parent,
    	      style | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
        border_color = new Color(null, 255, 0, 0);
    	this.earth = earth;
    	marker_painter = new MarkerPainter();
        beacon_painter = new MarkerPainter(true);
        addDisposeListener(this);
        addPaintListener(this);
        for (final Beacon beacon : beacons)
            scheduler.addScheduleable(beacon);
        scheduler.addScheduleable(clock_task);
        scheduleRedraw();
    }

    /** Start a UI timer for updating the earth widget. */
    private void scheduleRedraw()
    {
        final int delay = scheduler.schedule();
        if (debug)
            System.out.println("scheduleRedraw delay " + delay);
        Display.getDefault().timerExec(delay, redraw_task);
    }

    /** Remove all markers.
     *  Does not by itself toggle a redraw!
     */
	public void clearMarkers()
	{
		markers.clear();
	}

    /** Add a marker.
     *  Does not by itself toggle a redraw!
     */
	public void addMarker(final Position location, final String text)
    {
    	markers.add(new Marker(location, text));
    }

    /** @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean) */
    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed)
    {
        int width, height;
        final Rectangle image_size = earth.getBounds();
        width = image_size.width;
        height = image_size.height;
        if (wHint != SWT.DEFAULT)
            width = wHint;
        if (hHint != SWT.DEFAULT)
            height = hHint;
        return new Point(width, height);
    }

    /** @see org.eclipse.swt.events.DisposeListener */
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        earth.dispose();
        border_color.dispose();
        marker_painter.dispose();
    }

    /** @see org.eclipse.swt.events.PaintListener */
    @Override
    public void paintControl(final PaintEvent e)
    {
        final GC gc = e.gc;
        final Rectangle client = getClientArea();
        // When used with gc.drawRectangle, this correction is needed to fit
        // 100%:
        --client.width;
        --client.height;

        final UTC now = new UTC();
        // Re-evaluate the earth image?
        if (earth_image == null  ||
            now.getTimeInMillis()
                   > earth_image_millis + EARTH_IMAGE_EVAL_PERIOD_IN_MILLIS)
        {
            if (debug)
                System.out.println("Update earth's image");
            earth_image = earth.getCurrentImage();
            earth_image_millis = now.getTimeInMillis();
        }
        final Rectangle image_size = earth.getBounds();
        gc.drawImage(earth_image, 0, 0, image_size.width,
                image_size.height, 0, 0, client.width, client.height);

        final Display display = getDisplay();
        // Show time
        final String time = now.toString();
        gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
        gc.drawString(time, 10, client.height - 15, true);
        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.drawString(time, 9, client.height - 14, true);

        // Show beacon info
        final int bi_x = client.width - gc.textExtent(Beacons.getInfo()).x;
        gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
        gc.drawString(Beacons.getInfo(), bi_x, client.height - 15, true);
        gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.drawString(Beacons.getInfo(), bi_x-1, client.height - 14, true);
        for (final Beacon beacon : beacons)
        {
            if (beacon.isVisible())
            {
                final double north = beacon.getLocation().getLatitude().getDegrees();
                final double east = beacon.getLocation().getLongitude().getDegrees();
                final int x = (int) (client.width/2 + east/180.0 * client.width/2);
                final int y = (int) (client.height/2 - north/90.0 * client.height/2);
                beacon_painter.paint(client, gc, x, y,
                                     beacon.getName(), beacon.doStandout());
            }
        }

        for (final Marker marker : markers)
        {
        	final double north = marker.getLocation().getLatitude().getDegrees();
        	final double east = marker.getLocation().getLongitude().getDegrees();
        	final int x = (int) (client.width/2 + east/180.0 * client.width/2);
        	final int y = (int) (client.height/2 - north/90.0 * client.height/2);
        	marker_painter.paint(client, gc, x, y, marker.getText());
        }
    }
}
