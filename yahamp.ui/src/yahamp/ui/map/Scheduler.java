/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.util.ArrayList;

import yahamp.model.UTC;

/** (Passive) Scheduler.
 *  <p>
 *  Maintains a list of tasks, runs them, determines delay until
 *  next due time.
 *  Does not perform the actual delay, so leaves the Thread.delay() or
 *  GUI timer business to somebody else.
 *  @author Kay Kasemir
 */
public class Scheduler
{
    private final ArrayList<Scheduleable> tasks = new ArrayList<Scheduleable>();

    /** Add a task to the scheduler. */
    public void addScheduleable(final Scheduleable task)
    {
        tasks.add(task);
    }

    /** Schedule all registered tasks.
     *  <p>
     *  Runs tasks which are due, determines the delay to the next
     *  task that will be due.
     *  @return Delay in milliseconds until the next task is due.
     */
    public int schedule()
    {
        int delay_to_next = Integer.MAX_VALUE;
        final UTC utc = new UTC();
        final long now = utc.getTimeInMillis();
        for (final Scheduleable task : tasks)
        {
            long next_due = task.getNextDueTimeInMillis();
            if (next_due <= now)
            {   // Task is/was due, run it
                task.run(utc);
                next_due = task.getNextDueTimeInMillis();
                if (next_due <= now)
                    throw new Error("Schedule error: Task "
                            + task.getClass().getName()
                            + " just ran at " + utc.toString()
                            + ", but next due time is "
                            + (now - next_due)
                            + " seconds back.");
            }
            // Update the next due time
            final int this_delay = (int)(next_due - now);
            if (this_delay < delay_to_next)
                delay_to_next = this_delay;
        }
        return delay_to_next;
    }
}
