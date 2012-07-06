/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import yahamp.rig.internal.Preferences;

/** [Headless] JUnit Plug-in test of the rig controller
 *  @author Kay Kasemir
 */
public class RigTest
{
	final public static String PORT = Preferences.getPort();
	final public static int RATE = Preferences.getRate();

	@Before
    public void configLogger()
    {
        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        for (final Handler handler : logger.getHandlers())
            handler.setLevel(Level.FINE);
    }


	@Test
	public void testRigLookup() throws Exception
	{
		final RigModel model = RigCtrl.detectRig(PORT, RATE);
		System.out.println("Detected rig model: " + model);
	}

	@Test
	public void testPoll() throws Exception
	{
		final RigModel model = RigCtrl.detectRig(PORT, RATE);
		if (model == null)
			return;
		try
		(
	        final RigCtrl rig = RigCtrl.getRig(model, PORT, RATE);
		)
		{
    		for (int i=0; i<10; ++i)
    		{
    			final RigInfo info = rig.poll();
    			System.out.println(info);
    			Thread.sleep(1000);
    		}
		}
	}
}
