/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

import static yahamp.rig.internal.Hex.hex;

import java.util.logging.Level;

import yahamp.rig.RigCtrl;
import yahamp.rig.RigInfo;
import yahamp.rig.RigModel;

/** Serial interface to a Yaesu rig.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Yaesu extends RigCtrl
{
    /** Command code */
    final public static int CMD_READ_FREQ = 0x03;

    /** Command code */
    final public static int CMD_WRITE_FREQ = 0x01;

    /** Serial port connection to rig */
    final private SimpleSerial port;

    /** Constructor
     *  @param port_name Serial port device name
     *  @param rate Baud rate
     *  @throws Exception on error
     */
    public Yaesu(final String port_name, final int rate) throws Exception
    {
        // Yaesu CAT allows 4800, 9600 or 38400 baud, no parity, 2 stop bits
        if (rate != 4800  &&  rate != 9600  &&  rate != 38400)
            throw new Exception("Unsupported baud rate " + rate);

        if (RigCtrl.debug)
        	System.out.println("Opening Yaese at " + port_name + ", " + rate + " baud");
        port = new SimpleSerial(port_name, rate, 8,
                                SimpleSerial.Parity.None, 2, 5.0);
    }

    /** {@inheritDoc}  */
    @Override
    public void close()
    {
        port.close();
    }

    /** Send a 5-byte command to the rig and get the response.
     *  @param command 5 byte command array
     *  @return 5 byte response
     *  @throws Exception on error
     */
    private byte [] query(final byte [] command) throws Exception
    {
        if (command.length != 5)
            throw new Exception("Expected 5-byte command, got " + hex(command));
        port.write(command);
        final byte response[] = port.read(5);
        if (response.length != 5)
            throw new Exception("Expected 5-byte response, got " + hex(response));
        return response;
    }

    /** Decode the 'mode' result of CMD_READ_FREQ */
    public static String decodeMode(final byte mode)
    {
        switch (mode & 0x0F)
        {
        case 0: return "LSB";
        case 1: return "USB";
        case 2: return "CW";
        case 3: return "CW";
        case 4: return "AM";
        case 6: return "WFM";
        case 8: return "FM";
        case 10: return "DIG";
        case 12: return "PKT";
        }
        return mode + "?";
    }

    /** {@inheritDoc}  */
    @Override
    public boolean test()
    {
    	try
    	{
            final byte[] response = query(new byte[] { 0, 0, 0, 0, CMD_READ_FREQ });
        	System.out.println(hex(response));
    		return false;
    	}
    	catch (final Exception ex)
    	{
    		return false;
    	}
    }
    /** {@inheritDoc}  */
    @Override
    public RigInfo poll() throws Exception
    {
        final byte[] response = query(new byte[] { 0, 0, 0, 0, CMD_READ_FREQ });
        if (RigCtrl.debug)
        	System.out.println(hex(response));
        final long new_freq_Hz = BCD.decode(response[0]) * 10000000 +
                                 BCD.decode(response[1]) * 100000 +
                                 BCD.decode(response[2]) * 1000 +
                                 BCD.decode(response[3]) * 10;
        final String mode = decodeMode(response[4]);
        return new RigInfo(RigModel.Yaesu, new_freq_Hz/1e6, mode);
    }

    /** {@inheritDoc}  */
    @Override
    public void setFreq(final double freq) throws Exception
    {
        // 439.12345 MHz needs to be written as BCD 43-91-23-45
        long scaled = (long)(freq * 100000);
        final byte cmd[] = new byte[5];
        long num = scaled / 1000000;
        scaled -= num * 1000000;
        cmd[0] = (byte) BCD.encode((int)num);

        num = scaled / 10000;
        scaled -= num * 10000;
        cmd[1] = (byte) BCD.encode((int)num);

        num = scaled / 100;
        scaled -= num * 100;
        cmd[2] = (byte) BCD.encode((int)num);

        num = scaled;
        cmd[3] = (byte) BCD.encode((int)num);

        cmd[4] = CMD_WRITE_FREQ;

        logger.log(Level.FINE, "Set freq {0}", freq);
        port.write(cmd);

        // Didn't find this documented, but my FT-817 via RIGblaster p&p
        // returns a 0x00 after writing a new freq
        // Read that, but ignore timeout in case there's nothing
        try
        {
            port.read(1);
        }
        catch (final Exception ex)
        {
            // Ignore
        }
    }
}
