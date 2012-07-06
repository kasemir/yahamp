/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import yahamp.logging.LogConfigurator;

/** JUnit demo of logger
 *  @author Kay Kasemir
 */
public class LogDemo
{
	@Test
	public void testLookup() throws Exception
	{
		LogConfigurator.setLevel(Level.FINE);
		Logger.getLogger(getClass().getName()).fine("Should see this");
		Logger.getLogger(getClass().getName()).finer("Should NOT see this");
	}
}
