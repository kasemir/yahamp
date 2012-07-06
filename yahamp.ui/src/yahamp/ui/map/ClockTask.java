/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.util.Timer;

import yahamp.model.UTC;

/** Schedulable that requests to run every second. */
public class ClockTask implements Scheduleable
{
    private long due_time = 0;

    /** {@inheritDoc} */
    @Override
    public long getNextDueTimeInMillis()
    {
        return due_time;
    }

    /** {@inheritDoc} */
    @Override
    public void run(final UTC now)
    {
        // Round 'now' up to next full second
        due_time = (now.getTimeInMillis() / 1000 + 1) * 1000;
        final Timer timer = new Timer();
    }
}
