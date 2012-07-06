/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import org.junit.Test;

import yahamp.model.UTC;
import yahamp.ui.map.Scheduleable;
import yahamp.ui.map.Scheduler;

public class SchedulerTest
{
    class TestTask implements Scheduleable
    {
        private long due_time;

        public TestTask()
        {
            due_time = 0;
        }

        @Override
        public long getNextDueTimeInMillis()
        {
            return due_time;
        }

        @Override
        public void run(final UTC now)
        {
            final long diff = now.getTimeInMillis() - due_time;
            System.out.println("It's: " + now + " (" + diff + ")");
            due_time = now.getTimeInMillis() + 2000;
        }
    }

    @Test
    public void testGetDelayToNext() throws Exception
    {
        final TestTask task = new TestTask();
        final Scheduler scheduler = new Scheduler();
        scheduler.addScheduleable(task);
        for (int i=0; i<10; ++i)
        {
            final int delay = scheduler.schedule();
            Thread.sleep(delay);
        }
    }
}
