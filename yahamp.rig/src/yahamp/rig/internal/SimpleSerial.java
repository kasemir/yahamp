/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

import static yahamp.rig.internal.Hex.hex;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Serial support that can read with timeout.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimpleSerial implements AutoCloseable
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** Read timeout */
    final private double timeout;

    /** The port */
    final private SerialPort port;

    /** Output stream for <code>port</code> */
    final private OutputStream out;

    /** Input stream for <code>port</code> */
    final private InputStream in;

    /** Parity values */
    public enum Parity
    {
        Even, Odd, None
    }

    /** Constructor.
     *  @param port_name The device name, "/dev/tty...." or "COM4".
     *  @param data_bits Number of data bits: 5, 6, 7, 8
     *  @param parity Parity
     *  @param stop_bits Number of stop bits: 1, 2
     *  @param timeout_secs (read) timeout in seconds.
     *  @throws Exception on error
     */
    public SimpleSerial(final String port_name,
            final int baud,
            final int data_bits,
            final Parity parity,
            final int stop_bits,
            final double timeout_secs) throws Exception
    {
        timeout = timeout_secs;
        port = openSerialPort(port_name);
        port.setDTR(false);
        port.setRTS(false);

        // This was in some example. Probably no difference...
        port.notifyOnOutputEmpty(true);

        // Configure basic settings
        port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        int stop_bits_code;
        switch (stop_bits)
        {
        case 1:
            stop_bits_code = SerialPort.STOPBITS_1;
            break;
        case 2:
            stop_bits_code = SerialPort.STOPBITS_2;
            break;
        default:
            throw new Exception("Unsupported number of stop bits: " + stop_bits);
        }
        int parity_code;
        switch (parity)
        {
        case Even:
            parity_code = SerialPort.PARITY_EVEN;
            break;
        case Odd:
            parity_code = SerialPort.PARITY_ODD;
            break;
        default:
            parity_code = SerialPort.PARITY_NONE;
        }
        port.setSerialPortParams(baud, data_bits, stop_bits_code, parity_code);

        // Get in/out streams
        out = port.getOutputStream();
        in = port.getInputStream();
    }

    /** Print info about detected serial and parallel ports
     *  @return Number of detected ports
     */
    @SuppressWarnings("unchecked")
	public static int listPorts()
    {
        final Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        int port_count = 0;
        System.out.format("%-40s  %-10s  %s\n", "Port", "Type", "Current Owner");
        System.out.println("--------------------------------------------------------------------");
        while (ports.hasMoreElements())
        {
            final CommPortIdentifier port = ports.nextElement();
            System.out.format("%-40s  %-10s  %s\n",
                    port.getName(),
                    getPortTypeName(port.getPortType()),
                    port.getCurrentOwner());
			++port_count ;
        }
        return port_count;
    }

    /** Determine all serial ports
     *  @return Names of all serial ports
     */
    @SuppressWarnings("unchecked")
	public static String[] getSerialPorts()
    {
    	final List<String> serial = new ArrayList<>();
        final Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements())
        {
            final CommPortIdentifier port = ports.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL)
            	serial.add(port.getName());
        }
        return serial.toArray(new String[serial.size()]);
    }

    /** @param portType Port identifier from RxTx library
     *  @return String representation
     */
    static String getPortTypeName(final int portType)
    {
        switch (portType)
        {
        case CommPortIdentifier.PORT_I2C:
            return "I2C";
        case CommPortIdentifier.PORT_PARALLEL:
            return "Parallel";
        case CommPortIdentifier.PORT_RAW:
            return "Raw";
        case CommPortIdentifier.PORT_RS485:
            return "RS485";
        case CommPortIdentifier.PORT_SERIAL:
            return "Serial";
        default:
            return "unknown type";
        }
    }

    /** Open serial port of given name.
     *  @param port_name The device name, "/dev/tty...." or "COM4".
     *  @return opened serial port
     *  @throws Exception on error
     */
    private SerialPort openSerialPort(final String port_name) throws Exception
    {
        logger.log(Level.FINE, "Locate {0}", port_name);
        final CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(port_name);
        if (port != null  &&
            port.getPortType() == CommPortIdentifier.PORT_SERIAL &&
            port.getName().equals(port_name))
        {
            try
            {
                return (SerialPort) port.open("SimpleSerial", 2000);
            }
            catch (final PortInUseException ex)
            {
                throw new Exception("Port in use", ex);
            }
        }
        throw new Exception("Unknown port '" + port_name + "'");
    }

    /** Close the port. */
    @Override
    public void close()
    {
        try
        {
            port.close();
            logger.log(Level.FINE, "Closed");
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Write to port.
     *  @param bytes Bytes to write
     *  @throws Exception on error
     */
    public void write(final byte... bytes) throws Exception
    {
        logger.log(Level.FINER, "Write: {0}", hex(bytes));
        out.write(bytes);
    }

    /** Write to port.
     *  @param text String to write
     *  @throws Exception on error
     */
    public void write(final String text) throws Exception
    {
        write(text.getBytes());
    }

    /** Read from port (with timeout).
     *  @param requested_length Number of characters to read
     *  @return String of that length
     *  @throws TimeoutException on timeout
     *  @throws Exception on error
     */
    public byte[] read(final int requested_length) throws Exception, TimeoutException
    {
        final long timeout_milli =
            System.currentTimeMillis() + (long)(1000 * timeout);
        final byte buf[] = new byte[requested_length];
        int got = 0;
        while (System.currentTimeMillis() <= timeout_milli)
        {
            int to_get = in.available();
            if (to_get <= 0)
            {   // Nothing there? Wait a little...
                Thread.sleep(100);
                continue;
            }
            // Else: See if that's actually more than we need
            if (got + to_get > requested_length)
                to_get = requested_length - got;
            final int new_chars = in.read(buf, got, to_get);
            if (new_chars > 0)
            {
                got += new_chars;
                if (got >= requested_length)
                {
                    logger.log(Level.FINER, " Read:  {0}", hex(requested_length, buf));
                    if (got > requested_length)
                        logger.log(Level.WARNING, "Got more than requested:  {0}", hex(got, buf));
                    return buf;
                }
            }
        }
        throw new TimeoutException("Exceeded timeout of " + timeout + " seconds");
    }

    /** Read from port (with timeout).
     *  @param requested_length Number of characters to read
     *  @return String of that length
     *  @throws Exception on error, including timeout
     */
    public String readString(final int requested_length) throws Exception
    {
        return new String(read(requested_length));
    }
}
