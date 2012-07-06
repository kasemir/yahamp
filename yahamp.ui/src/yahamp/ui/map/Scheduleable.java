/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.util.Calendar;

import yahamp.model.UTC;

/** Interface that describes a scheduable item:
 *  It's like Runnable which also hands out a time when it would like
 *  to be run next.
 *  @author Kay Kasemir
 */
public interface Scheduleable
{
    /** Get the next desired run time.
     *  @return Milliseconds for the next time when this item is due,
     *          in the same units as Calendar.getTimeInMillis
     *  @see Calendar#getTimeInMillis()
     */
    public long getNextDueTimeInMillis();

    /** Run the scheduable item.
     *  <p>
     *  The Scheduleable should only expect to be "run" very close
     *  to its due time.
     *  It usually updates its due time for the "next" due time.
     *  @param now The current wall clock
     */
    public void run(UTC now);
}
