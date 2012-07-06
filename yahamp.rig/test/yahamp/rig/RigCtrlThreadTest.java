/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import static org.junit.Assert.assertSame;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

/** [Headless] JUnit Plug-in test.
 *  Needs plugin mechanism to get native libs and preferences.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RigCtrlThreadTest implements RigListener
{
    @Before
    public void configLogger()
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        for (final Handler handler : logger.getHandlers())
            handler.setLevel(Level.FINE);
    }

    @Test
    public void testRigThread() throws Exception
    {
        final RigCtrlThread thread1 = RigCtrlThread.getInstance();
        final RigCtrlThread thread2 = RigCtrlThread.getInstance();
        thread2.addListener(this);
        // Should be singleton
        assertSame(thread1, thread2);
        System.out.println("Listening to changes from rig...");
        Thread.sleep(20*1000);
        thread2.removeListener(this);
        thread2.release();
        thread1.release();
        System.out.println("Done.");
    }

    @Override
	public void newRigInfo(final RigInfo info)
    {
        System.out.println(info);
    }

    @Override
	public void rigError(final Exception ex)
    {
        ex.printStackTrace();
    }
}

