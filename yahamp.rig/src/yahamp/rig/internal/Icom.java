/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

import static yahamp.rig.internal.Hex.hex;

import java.util.Arrays;
import java.util.logging.Level;

import yahamp.rig.RigCtrl;
import yahamp.rig.RigInfo;
import yahamp.rig.RigModel;

/** Serial interface to an Icom rig.
 *  @author Kay Kasemir
 */
public class Icom extends RigCtrl
{
    /** Start of message */
    final public static int PREAMBLE = 0xFE;

    /** End of message */
    final public static int END = 0xFD;

    /** Command code */
    final public static int CMD_READ_FREQ = Yaesu.CMD_READ_FREQ;

    /** Command code */
    final public static int CMD_READ_MODE = 0x04;

    /** Command code */
    final public static int CMD_WRITE_FREQ = 0x00;

    /** Serial port connection to rig */
    final private SimpleSerial port;

    final public static int address = 0x66;

    public Icom(final String port_name, final int rate) throws Exception
    {
        // Icom allows 4800, 9600 or 38400 baud??, no parity, 1 stop bits
        if (rate != 4800  &&  rate != 9600  &&  rate != 38400)
            throw new Exception("Unsupported baud rate " + rate);
        RigCtrl.logger.log(Level.FINER,
                "Opening Icom at {0}, {1} baud",
                new Object[] { port_name, rate });
        port = new SimpleSerial(port_name, rate, 8,
                                SimpleSerial.Parity.None, 1, 5.0);
    }

    @Override
	public void close()
    {
        port.close();
    }

    public static String checkResponse(final byte[] response)
    {
        if (response[0] != (byte) PREAMBLE)
            return "Missing preamble in response " + hex(response);
        if (response[1] != (byte) PREAMBLE)
            return "Missing preamble 2 in response " + hex(response);
        if (response[2] != (byte) 0xE0)
            return "Missing controller ID in response " + hex(response);
        if (response[3] != (byte) address)
            return "Missing rig address in response " + hex(response);
        if (response[response.length-1] != (byte) END)
            return "Missing end marker in response " + hex(response);
        return null;
    }

    /** Send a command to the rig and get the response.
     *  @param command command array
     *  @param reply_size Size of reply (not including the 'echo')
     *  @return Response or <code>null</code> if requested reply size was 0
     *  @throws Exception on error
     */
    private byte [] query(final byte [] command, final int reply_size) throws Exception
    {
        port.write(command);
        final byte echo[] = port.read(command.length);
        if (! Arrays.equals(command, echo))
            throw new Exception("Expected 'echo' " + hex(command)
                    + " but got " + hex(echo));
        if (reply_size <= 0)
            return null;
        final byte response[] = port.read(reply_size);
        final String error = checkResponse(response);
        if (error != null)
        	throw new Exception(error);
        return response;
    }

    /** {@inheritDoc}  */
    @Override
    public boolean test()
    {
    	try
    	{
            final byte[] response = query(new byte[]
            {
                (byte)PREAMBLE, (byte)PREAMBLE, (byte)address, (byte)0xE0,
                CMD_READ_FREQ, (byte)END
            }, 11);
            /* Echo: 0xfe,0xfe,0x66,0xe0,0x03,0xfd,
             * Data: 0xfe,0xfe,0xe0,0x66,0x03,0x00,0x50,0x12,0x50,0x00,0xfd
             *       for 0050.12500 MHz
             */
        	System.out.println(hex(response));
            return response[0] == (byte)0xfe &&
                   response[1] == (byte)0xfe &&
                   response[4] == (byte)CMD_READ_FREQ;
    	}
    	catch (final Exception ex)
    	{
    		return false;
    	}
    }

    public static long decodeFreqHz(final byte[] response)
    {
        return BCD.decode(response[9]) * 100000000 +
               BCD.decode(response[8]) * 1000000 +
               BCD.decode(response[7]) * 10000 +
               BCD.decode(response[6]) * 100  +
               BCD.decode(response[5]);
    }

    /** {@inheritDoc}  */
    @Override
    public RigInfo poll() throws Exception
    {
        byte[] response = query(new byte[]
        {
            (byte)PREAMBLE, (byte)PREAMBLE, (byte)address, (byte)0xE0,
            CMD_READ_FREQ, (byte)END
        }, 11);
        /* Echo: 0xfe,0xfe,0x66,0xe0,0x03,0xfd,
         * Data: 0xfe,0xfe,0xe0,0x66,0x03,0x00,0x50,0x12,0x50,0x00,0xfd
         *       for 0050.12500 MHz
         */
        if (RigCtrl.logger.isLoggable(Level.FINER))
            RigCtrl.logger.log(Level.FINER, "Response: {0}", hex(response));
        final long new_freq_Hz = decodeFreqHz(response);
        response = query(new byte[]
        {
            (byte)PREAMBLE, (byte)PREAMBLE, (byte)address, (byte)0xE0,
            CMD_READ_MODE, (byte)END
        }, 8);
        // 0xfe,fe,e0,66,04,01,01,fd : Mode 01, VFO A
        // 0xfe,fe,e0,66,04,03,02,fd : Mode 03, VFO B
        final String mode = decodeMode(response[5]);
        return new RigInfo(RigModel.Icom, new_freq_Hz/1e6, mode);
    }

    /** Decode the 'mode' result of CMD_READ_MODE*/
    private String decodeMode(final byte mode)
    {
        switch (mode & 0x0F)
        {
        case 0: return "LSB";
        case 1: return "USB";
        case 2: return "AM";
        case 3: return "CW";
        case 4: return "RTTY";
        case 5: return "FM";
        case 7: return "CW";
        case 8: return "RTTY";
        }
        return mode + "?";
    }

    @Override
    public void setFreq(final double freq_Mhz) throws Exception
    {
        final byte[] cmd = new byte[]
        {
            (byte)PREAMBLE, (byte)PREAMBLE, (byte)address, (byte)0xE0,
            CMD_WRITE_FREQ, 0, 0, 0, 0, 0, (byte)END
        };
        // 0xfe,0xfe,0x66,0xe0,0x00,0x00,0x50,0x12,0x50,0x00,0xfd
        //       for 0050.12500 MHz
        long scaled = (long)(freq_Mhz * 1000000);
        long num = scaled / 100000000;
        scaled -= num * 100000000;
        cmd[9] = (byte) BCD.encode((int)num);

        num = scaled / 1000000;
        scaled -= num * 1000000;
        cmd[8] = (byte) BCD.encode((int)num);

        num = scaled / 10000;
        scaled -= num * 10000;
        cmd[7] = (byte) BCD.encode((int)num);

        num = scaled / 100;
        scaled -= num * 100;
        cmd[6] = (byte) BCD.encode((int)num);

        num = scaled;
        cmd[5] = (byte) BCD.encode((int)num);

        query(cmd, 0);
    }
}
