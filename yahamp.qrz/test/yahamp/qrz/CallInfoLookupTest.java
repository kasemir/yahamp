/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.junit.Test;

import yahamp.logging.LogConfigurator;
import yahamp.model.CallInfo;

/** JUnit test of {@link QRZCallBook}
 *
 *  <p>Requires network connection.
 *
 *  <p>When run as plugin test, preferences will be used.
 *  For plain JUnit test, set properties "qrz_user" and "qrz_password".
 *
 *  @author Kay Kasemir
 */
public class CallInfoLookupTest
{
	@Test //(timeout=10000)
	public void testLookup() throws Exception
	{
		LogConfigurator.setLevel(Level.FINE);
//		Logger.getLogger("javax.xml.bind").setLevel(Level.INFO);
//		Logger.getLogger("com").setLevel(Level.INFO);
//		Logger.getLogger("sun").setLevel(Level.INFO);
//		Logger.getLogger("com").setLevel(Level.INFO);

		final QRZCallBook lookup = new QRZCallBook();
		final CallInfo callinfo = lookup.lookup("W1AW");
		System.out.println(callinfo);
        System.out.println(callinfo.getName());
		assertTrue(callinfo.getName().contains("ARRL"));
	}
}
