/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import yahamp.rig.internal.Preferences;
import yahamp.rig.internal.SimpleSerial;

/** [Headless] JUnit Plug-in Test of RxTx library.
 *
 *  <p>Loopback test assumes, well, loopback connected to serial port.
 *  Icom cable also functions as loopback.
 *  Turn rig off to avoid sending test message to rig.
 *
 *  <p>When run as Plug-in Test, RxTx library should be located via
 *  Bundle-NativeCode and Bundle-ClassPath in MANIFEST.MF.
 *
 *  <p>Can also run as plain JUnit test
 *  when setting ((DY)LD_LIBRARY_)PATH to location of RxTx JNI library.
 *
 *  <p>See RxTx Examples for more.
 *
 *  <p>RXTX library issues:
 *  Older version 2.1.7 did not inlude win64 binaries.
 *  The 2.2pre update offers win64, but generates a version mismatch
 *  warning between its JAR file and DLLs.
 *
 *  <p>USB Issues:
 *  The RigBlaster Plug&Play USB-2-serial converter fails when plugged
 *  into a USB-3 port: CommPortIdentifier code will hang in a read test
 *  method. On USB-2 it works fine.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SerialTests
{
	final private static String MESSAGE = "Loopback works OK!";

	@Before
	public void configLogger()
	{
	    final Logger logger = Logger.getLogger("");
	    logger.setLevel(Level.FINE);
	    for (final Handler handler : logger.getHandlers())
	        handler.setLevel(Level.FINE);
	}

	/** List available serial ports */
    @Test
    public void listPorts()
    {
    	final int port_count = SimpleSerial.listPorts();
        System.out.println("Ports: " + port_count);
        assertTrue("No ports", port_count > 0);
    }

    @Test // (timeout=10000)
    public void loopbackTest() throws Exception
    {
    	// If there's only one serial port, try that one.
    	final String port;
    	final String[] ports = SimpleSerial.getSerialPorts();
    	if (ports.length == 1)
    		port = ports[0];
    	else // use preferences
    		port = Preferences.getPort();
    	try
    	(
	        final SimpleSerial serial =
	            new SimpleSerial(port, Preferences.getRate(),
	                    8, SimpleSerial.Parity.None, 1, 5.0);
        )
        {
            serial.write(MESSAGE);

            final String response = serial.readString(MESSAGE.length());
            System.out.println(response);
            assertEquals(MESSAGE, response);
        }
    }
}
