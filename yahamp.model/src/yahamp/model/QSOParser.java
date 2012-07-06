/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;


/** Parser for mining a text line for QSO Info.
 *  <p>
 *  Basic Idea: Call RST-sent RST-received Info
 *
 *  @author Kay Kasemir
 */
public class QSOParser
{
    private static final String RST_5NN = "5nn";

    /** Try to extract qso info from "call rst-sent rst-rcvd info".
     *  @param line Line to analyze
     *  @param qso QSO data to use for 'other' fields
     *  @returns Partially filled qso info or <code>null</code>
     */
    @SuppressWarnings("nls")
    public static QSO analyze(final String line, final QSO template)
    {
        // call SPACE sent SPACE received SPACE info
        final String[] split = line.split("\\s+");
        // Nothing?
        if (split.length < 1)
            return null;

        // Only a potentially partial call sign, not followed by space?
        if (split.length == 1  && !line.endsWith(" "))
        	return null;

        final String call = split[0].trim();
        if (call.length() < 0)
            return null;

        final String rst_sent = (split.length >= 2)
            ? patchRST(split[1])
            : RST_5NN;

        String rst_rcvd;
        if (split.length >= 3)
            rst_rcvd = patchRST(split[2]);
        else
        {   // Use 'sent' rst for received except for the exchange
            final int exchange = rst_sent.indexOf("-");
            if (exchange >= 0)
                rst_rcvd = patchRST(rst_sent.substring(0, exchange));
            else
                rst_rcvd = patchRST(rst_sent);
        }
        // Add rest as info
        String info = template.getInfo();
        if (split.length >= 4)
        {
            info = split[3];
            // Somewhat iffy: Paste rest with " ",
            // no matter which "\s+" it really was...
            for (int i=4; i<split.length; ++i)
                info += " " + split[i];
        }

        final QSO qso = new QSO(call);
        qso.setUTC(new UTC());
        qso.setRstSent(rst_sent);
        qso.setRstRcvd(rst_rcvd);
        qso.setFreq(template.getFreq());
        qso.setMode(template.getMode());
        qso.setCategory(template.getCategory());
        qso.setQsl(template.getQsl());
        qso.setInfo(info);

        return qso;
    }

    @SuppressWarnings("nls")
    private static String patchRST(final String rst)
    {
        if (rst == null)
            return RST_5NN;
        if (rst.startsWith("0"))
            return RST_5NN + "-" + rst;
        if (rst.startsWith("-"))
            return RST_5NN + rst;
        return rst;
    }
}
