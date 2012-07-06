/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import yahamp.qrz.internal.QRZCallInfo;
import yahamp.qrz.internal.QRZDatabase;

/** JUnit test of XML handling for {@link QRZCallInfo}
 *
 *  <p>Uses test files, no network connection.
 *
 *  @author Kay Kasemir
 */
public class CallInfoUnitTest
{
	/** Read call info from qrz.com XML interface description
	 *  @throws Exception on error
	 */
	@Test
	public void testCallsignXML() throws Exception
	{
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

		final QRZDatabase qrz_database =
			(QRZDatabase) unmarshaller.unmarshal(new File("example/lookup.xml"));

		final QRZCallInfo callsign = qrz_database.getCallInfo();
		assertNotNull(callsign);
		System.out.println(callsign);
		assertEquals("AA7BQ", callsign.getCall());
	}

    /** Read call info from qrz.com XML interface description
     *  @throws Exception on error
     */
    @Test
    public void testUnknownCallsignXML() throws Exception
    {
        final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
        final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

        final QRZDatabase qrz_database =
            (QRZDatabase) unmarshaller.unmarshal(new File("example/lookup_error.xml"));
        System.out.println(qrz_database.getSessionInfo());

        final QRZCallInfo callsign = qrz_database.getCallInfo();
        assertNull(callsign);
        assertEquals(false,  qrz_database.isValid());
        assertTrue(qrz_database.getSession().getError().contains("Not found"));
    }
}
