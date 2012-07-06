/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.util.Calendar;

import yahamp.model.UTC;
import yahamp.model.earth.Maidenhead;
import yahamp.model.earth.Position;

/** One DX beacon. */
public class Beacon implements Scheduleable
{
    private final static boolean debug = false;
    private static final int STANDOUT_MILLIS = 1*1000;
    private static final int ON_TIME_MILLIS = 10*1000;
    private final static int SECS_PER_MIN = 60;

    /** Beacon name. */
    private final String name;

    /** Beacon position. */
    private final Maidenhead position;

    /** If a beacon is active at 02:10, then every 3 minutes on,
     *  the fiducial_seconds would be 2*60+10, and the period_seconds
     *  would be 3*60.
     */
    private final int fiducial_seconds;

    /** @see #fiducial_seconds */
    private final int period_seconds;

    /** Next time we become visible.*/
    private UTC due_time;

    enum State
    {
        /** Just became active */
        standout,

        /** Still show it */
        visible,

        /** Silent */
        off
    }
    /** <code>true</code> if the due_time is for becoming visible */
    private State state = State.off;

    /** Create beacon
     *
     *  @param name Name or callsign
     *  @param grid Maidenhead grid
     *  @param minutes Minutes of the hour when first active
     *  @param seconds Second of the hour when first active
     *  @param period_seconds Period between 'on' times in seconds
     */
    public Beacon(final String name,
                  final String grid,
                  final int minutes, final int seconds,
                  final int period_seconds)
    {
        this.name = name;
        this.position = Maidenhead.fromGrid(grid);
        this.fiducial_seconds = minutes * SECS_PER_MIN + seconds;
        this.period_seconds = period_seconds;
        determineNextDueTime(new UTC());
    }

    /** @return Beacon callsign */
    public String getName()
    {
        return name;
    }

    /** @return Location of the beacon */
    public Position getLocation()
    {
        return position;
    }

    /** @return <code>true</code> if the beacon is active */
    public boolean isVisible()
    {
        return state != State.off;
    }

    /** @return <code>true</code> if the beacon is just came on */
    public boolean doStandout()
    {
        return state == State.standout;
    }

    /** {@inheritDoc} */
    @Override
    public long getNextDueTimeInMillis()
    {
        final long millis = due_time.getTimeInMillis();
        switch (state)
        {
        case standout:
            // We are standout, return time when simply visible
            return millis + STANDOUT_MILLIS;
        case visible:
            // We are visible, return time when turning off
            return millis + ON_TIME_MILLIS;
        case off:
        }
        // We are invisible, return the time when we'll become standout
        return millis;
    }

    /** Implements the <code>Runnable</code> part of the
     *  <code>Scheduleable</code> interface.
     */
    @Override
    @SuppressWarnings("nls")
    public void run(final UTC now)
    {
        final State old_state = state;
        switch (old_state)
        {
        case standout:
            state = State.visible;
            break;
        case visible:
            state = State.off;
            determineNextDueTime(now);
            break;
        case off:
            state = State.standout;
            break;
        }
        // In case the time for 'standout' or 'visible'
        // already passed, compute the next time slot
        if (getNextDueTimeInMillis() < now.getTimeInMillis())
            determineNextDueTime(now);

        if (debug)
            System.out.println(now + ": " + name + " changes from " + old_state
                + " to "+ state);
    }

    /** Compute the next time the beacon becomes active. */
    private void determineNextDueTime(final UTC now)
    {
        final int seconds = now.getMinutes() * SECS_PER_MIN + now.getSeconds();
        // Note: has to be floor(), so that -1.8 -> -2,
        // and not (int)-1.8 == -1.
        final double d_periods = (double)(seconds - fiducial_seconds)
                                     / period_seconds;
        final int periods = (int) Math.floor(d_periods);
        final int next_seconds = fiducial_seconds + (periods+1) * period_seconds;
        // Determine date for those next_seconds,
        // where Calendar.add() also handles possible rollover
        // of seconds into minutes.
        final Calendar cal = now.getCalendar();
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.SECOND, next_seconds - seconds);
        due_time = new UTC(cal.getTimeInMillis());
        if (debug)
            System.out.println(name + " next due " + due_time.toString()); //$NON-NLS-1$

        // In case the computer was put to sleep,
        // we'll typically be 'off', then wake up and run immediately.
        // In that case, state is incremented to 'standout'
        // and the next time would actually be in the future,
        // i.e. we should really stay 'off':
        if (due_time.getTimeInMillis() > now.getTimeInMillis())
            state = State.off;
    }

    @Override
    public String toString()
    {
        final int minutes = fiducial_seconds / SECS_PER_MIN;
        final int seconds = fiducial_seconds - minutes * SECS_PER_MIN;
        return String.format("Beacon '%s': %02d:%02d + N*%d minutes", //$NON-NLS-1$
                             name, minutes, seconds, period_seconds / SECS_PER_MIN) +
             " : " + state;
    }
}
