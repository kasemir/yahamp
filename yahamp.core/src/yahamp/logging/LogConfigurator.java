/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Configure <code>java.util.logging</code>
 *  @author Kay Kasemir
 */
public class LogConfigurator
{
	/** @param level Enable logging for all messages with this level and higher */
	public static void setLevel(final Level level)
	{
		final Logger root = Logger.getLogger("");
		root.setLevel(level);
		final Formatter formatter = new LogFormatter();
		for (Handler handler : root.getHandlers())
		{
			handler.setLevel(level);
			handler.setFormatter(formatter);
		}
	}
}
