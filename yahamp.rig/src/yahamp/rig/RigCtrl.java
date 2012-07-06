/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import yahamp.rig.internal.Icom;
import yahamp.rig.internal.SimpleSerial;
import yahamp.rig.internal.Yaesu;

/** Abstract interface to a rig, providing frequency and mode info.
 *
 *  @see RigCtrlThread
 *  @author Kay Kasemir
 */
public abstract class RigCtrl implements AutoCloseable
{
    final protected static Logger logger = Logger.getLogger(RigCtrl.class.getName());

    /** Compile-time flag to enable debug messages */
	final public static boolean debug = false;

	/** Detect rig
	 *
	 *  <p>Issues test commands to determine the rig that it
	 *  currently connected to the port
	 *  @param port_name Port name
	 *  @param rate Serial rate
	 *  @return {@link RigModel} or <code>null</code>
	 *  @throws Exception if serial port cannot be used at all
	 */
	@Test
	public static RigModel detectRig(final String port_name, final int rate) throws Exception
	{
		try
		(
	        final SimpleSerial port = new SimpleSerial(port_name, rate, 8,
	                SimpleSerial.Parity.None, 2, 5.0);
		)
		{
		    logger.log(Level.FINE, "Detecting rig");

		    // Icom CMD_READ_FREQ without END is also a valid Yaesu CMD_READ_FREQ
	        final byte[] query = new byte[]
	        {
	           (byte)Icom.PREAMBLE, (byte)Icom.PREAMBLE, (byte)Icom.address, (byte)0xE0,
	           Icom.CMD_READ_FREQ
	        };
	        port.write(query);
	        // Yaesu would reply with freq.
	        // Icom would simply echo the request
	        byte response[] = port.read(5);
            logger.log(Level.FINE, "Received {0} byte response", response.length);

            if (response.length == 5  &&
	            ! Arrays.equals(query, response))
	        {
		        final String mode = Yaesu.decodeMode(response[4]);
		        if (! mode.contains("?"))
		        	return RigModel.Yaesu;
	        }

	        // Assume it's Icom, missing the 'END'
	        port.write((byte) Icom.END);
	        // Expect echo of the END
	        response = port.read(1);
	        // Expect freq. info
	        response = port.read(11);
	        if (Icom.checkResponse(response) == null)
	        	return RigModel.Icom;
		}
		catch (final TimeoutException ex)
		{
            logger.log(Level.FINE, "Timeout");
			// Ignore
		}
		return null;
	}


	/** Obtain interface to rig */
    final public static RigCtrl getRig(final RigModel model,
            final String port_name, final int rate) throws Exception
    {
        switch (model)
        {
        case Icom:
            return new Icom(port_name, rate);
        case Yaesu:
            return new Yaesu(port_name, rate);
        default:
            throw new Exception("Unknown model " + model.name());
        }
    }

    /** Test communication with rig
     *
     *  <p>Used to detect if the rig supports the protocol,
     *  i.e. if the correct rig is connected to the serial line.
     *  Command sent is hopefully benign if the wrong
     *  rig is connected.
     *
     *  @return <code>true</code> if rig responds OK
     */
    public abstract boolean test();

    /** Close the connection. */
    @Override
    public abstract void close();

    /** Must be called periodically
     *  to query rig and send updates to listeners.
     */
    public abstract RigInfo poll() throws Exception;

    /** Program rig to a new frequency.
     *  @param freq_Mhz New freq. [MHz]
     */
    abstract public void setFreq(double freq_Mhz) throws Exception;
}
