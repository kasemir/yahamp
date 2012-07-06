/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** Junit test of QSOParser.
 *  @author Kay Kasemir
 */
public class QSOParserTest
{
    @SuppressWarnings("nls")
    @Test
    public void testAnalyze()
    {
        final QSO template = new QSO("none");
        template.setCategory("Default");
        template.setFreq("14.002");
        template.setRstSent("5nn");
        template.setRstRcvd("5nn");
        template.setMode("cw");
        template.setQsl("n");
        template.setInfo("Contest");

        // Full contest exchange
        QSO qso = QSOParser.analyze("w1aw 5nn-001 5nn-987", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN-001", qso.getRstSent());
        assertEquals("5NN-987", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // Only received contest exchange
        qso = QSOParser.analyze("w1aw 5nn-001 5nn", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN-001", qso.getRstSent());
        assertEquals("5NN", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // space triggers end of call
        qso = QSOParser.analyze("w1aw ", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN", qso.getRstSent());
        assertEquals("5NN", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // Without the space, it's probably not a full call sign and ignored
        qso = QSOParser.analyze("w1aw", template);
        assertNull(qso);

        // Only serial (start with 0), using '5nn'
        qso = QSOParser.analyze("w1aw 05 099 IOTA NA-28", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN-05", qso.getRstSent());
        assertEquals("5NN-099", qso.getRstRcvd());
        assertEquals("IOTA NA-28", qso.getInfo());

        // Echoing the same RST
        qso = QSOParser.analyze("w1aw 57n", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("57N", qso.getRstSent());
        assertEquals("57N", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // Echoing the same RST, but only the received serial
        qso = QSOParser.analyze("w1aw 5nn-42", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN-42", qso.getRstSent());
        assertEquals("5NN", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // Only RSTs
        qso = QSOParser.analyze("w1aw 57n 5nn Fred,   100w dipole", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("57N", qso.getRstSent());
        assertEquals("5NN", qso.getRstRcvd());
        assertEquals("Fred, 100w dipole", qso.getInfo());

        // Contest with non-numeric check
        qso = QSOParser.analyze("w1aw 57n-TN 5nn-kw", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("57N-TN", qso.getRstSent());
        assertEquals("5NN-KW", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());

        // Mixed check, no RST
        qso = QSOParser.analyze("w1aw -42 -xy", template);
        assertEquals("W1AW", qso.getCall());
        assertEquals("5NN-42", qso.getRstSent());
        assertEquals("5NN-XY", qso.getRstRcvd());
        assertEquals(template.getInfo(), qso.getInfo());
    }
}
