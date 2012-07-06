/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import yahamp.model.UTC;
import yahamp.model.earth.Degree;
import yahamp.model.earth.Earth;
import yahamp.model.earth.JulianDay;
import yahamp.ui.Activator;

/**
 * A view of earth as an SWT widget. Based on SunClock.js from the "SunClock
 * Dashboard Widget" by SoftBend <http://softbend.free.fr>, Version 1.0 - June
 * 3, 2005.
 *
 * To understand the math, one should probably study
 * "Astronomical Algorithms" by Jean Meeus....
 * <p>
 * Map images are copyright NASA.
 * <p>
 * Implemented following the Eclipse 3.1 "Platform Plug-In Developer Guide",
 * sub-section "Standard Widget Toolkit", "Widgets", Custom Widgets", which lead
 * to "Creating Your Own Widgets using SWT" on http://www.eclipse.org/articles.
 *
 * @author Kay Kasemir
 */
public class EarthImage
{
    private final Display display;
    private Image day, night, current;

    // Code from SunClock.js,
    // originally written by Nate Barkei for the
    // EarthFall Konfabulator widget?
    private class SunInfo
    {
        private final static double TWOPI = 2.0 * Math.PI;
        private final double sunS;
        private double sunS2;
        private final double stange_g;

        final double CosEPS = 0.91748;
        final double SinEPS = 0.39778;

        public SunInfo(final int year, final int month, final int day, final double hour)
        {
            final double MJDHour = getNormalizedDay(year, month, day, hour);
            // MJDHour = JulianDay - 2400000.5
            stange_g = 24.0 * frac(0.67239 + 1.00273781 * (MJDHour - 40000.0));

            final JulianDay JD = new JulianDay(year,  month, day, hour);
            // Julian centuries since J2000 (2000 January 1.5 TD)
            final double T = JD.getJ2000Centuries();
            // mean anomaly
            final double M = TWOPI * frac(0.993133 + 99.997361 * T);
            // equation of center??
            final double DL = 6893.0 * Math.sin(M) + 72.0 * Math.sin(M * 2.0);

            final double ma = Degree.norm(Earth.meanAnomaly(JD));
            final double center = Degree.norm(Earth.equationOfCenter(JD, ma));
            System.out.format("M = %.1f deg, ma = %.1f deg\n",
                    Math.toDegrees(M), ma);
            System.out.format("DL = %.1f deg, center = %.1f deg\n",
                    Degree.norm(Math.toDegrees(DL)), center);


            final double L = TWOPI * frac(0.7859453 + M / TWOPI + (6191.2 * T + DL)
                            / 1296000);

            final double X = Math.cos(L);
            final double Y = CosEPS * Math.sin(L);
            final double Z = SinEPS * Math.sin(L);
            final double Rho = Math.sqrt(1.0 - Z * Z);
            sunS = (360.0 / TWOPI) * Math.atan2(Z, Rho);
            sunS2 = (48.0 / TWOPI) * Math.atan2(Y, (X + Rho));
            if (sunS2 < 0.0)
                sunS2 += 24.0;
        }

        double sunS()
        {
            return sunS;
        }

        @SuppressWarnings("unused")
        double sunS2()
        {
            return sunS2;
        }

        public double dp(final double kio)
        {
            double cta = stange_g + kio / 15.0;
            cta = (Math.floor(cta) % 24) + frac(cta);
            if (cta > 24)
                cta = cta - 24;
            final double jk = (cta - sunS2) * 15.0;
            return Math.toDegrees(
                    Math.atan(-1 * (Math.cos(Math.toRadians(jk))
                               / Math.tan(Math.toRadians(sunS)))));
        }

        private double getNormalizedDay(int year, int month, final int day, final double hours)
        {
            if (month <= 2)
            {
                month += 12;
                year -= 1;
            }
            final int a = year / 100;
            return Math.floor(365.25 * year) -a + a/4
                    + Math.floor(30.59 * (month - 2)) + day
                    - 678912 + hours / 24.0;
        }

        private double frac(final double d)
        {
            return d - Math.floor(d);
        }
    }

    /** Image size selector */
    public enum Size { Small, Medium, Large }

    /** Constructor
     *  @throws Exception on error with image files
     */
    @SuppressWarnings("nls")
    public EarthImage(final Display display, final Size size) throws Exception
    {
        this.display = display;
        // Load image.
        final String res_names[] = { "360", "720", "1000" };
        final int res = size.ordinal();
        final String day_jpg   = "maps/map" + res_names[res] + ".jpg";
        final String night_jpg = "maps/map" + res_names[res] + "n.jpg";
        final InputStream day_stream = Activator.openStream(day_jpg);
        final InputStream night_stream = Activator.openStream(night_jpg);
        loadImages(day_stream, night_stream);
    }

    /** Constructor */
    public EarthImage(final Display display, final InputStream day_stream, final InputStream night_stream)
    {
        this.display = display;
        try
        {
            loadImages(day_stream, night_stream);
        }
        catch (final Exception e)
        {
            day = null;
            night = null;
            current = new Image(display, 300, 200);
        }
    }

    private void loadImages(final InputStream day_stream, final InputStream night_stream)
    {
        day = new Image(display, day_stream);
        night = new Image(display, night_stream);
        current = new Image(display, day, SWT.IMAGE_COPY);
    }

    public void dispose()
    {
        current.dispose();
        if (day != null)
            day.dispose();
        if (night != null)
            night.dispose();
    }

    public Rectangle getBounds()
    {
        return current.getBounds();
    }

    /** @see org.eclipse.swt.events.PaintListener */
    public Image getCurrentImage()
    {
        final GC gc = new GC(current);
        if (day == null || night == null)
        {
            gc.drawText("NO IMAGE", 0, 0);
        }
        else
        {
            gc.drawImage(day, 0, 0);
            // Based on SunClock.js function updateMap(), which has comment:
            // "this function was originally written by Nate Barkei
            // for the EarthFall Konfabulator widget and was modified
            // by SoftBend to fit with Dashboard specifics"
            final UTC now = new UTC();
            final int yyy = now.getYear();
            final int mmm = now.getMonth();
            final int ddd = now.getDay();
            final double hhh = now.getHours() + now.getMinutes() / 60.0;
            final int width = getBounds().width;
            final int height = getBounds().height;
            final SunInfo si = new SunInfo(yyy, mmm, ddd, hhh);
            for (int x = 0; x < width; x++)
            {
                final double lk = x * 360.0 / width - 180.0;
                int y = (int) (Math.floor(si.dp(lk) + 90)
                                / 180 * height);
                int y0;
                if (si.sunS() >= 0.0)
                {
                	if (y%2 == 1)
                		--y;
                    y0 = height - y;
                }
                else
                {
                    y0 = 0;
                    y = height - y;
                }
                gc.drawImage(night, x, y0, 1, y,
                                    x, y0, 1, y);
            }

            // Draw the sun
//            try
//            {
//                final JulianDay JD = new JulianDay(new UTC());
//                SunPosition sp = new SunPosition(JD);
//                gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
//                double north = Degree.norm(sp.getDeclination());
//                double east = Degree.norm(sp.getHourAngle());
//                if (east > 180.0)
//                    east -= 360.0;
//
//                System.out.format("EarthImage: Sun @ %.1f N, %.1f\n",
//                                  north, east);
//
//                int x = (int) (width/2 + east/180.0 * width/2);
//                int y = (int) (height/2 - north/90.0 * height/2);
//                final int sun_size = 10;
//                gc.fillOval(x-sun_size/2, y-sun_size/2, sun_size, sun_size);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
        }
        gc.dispose();
        return current;
    }
}
