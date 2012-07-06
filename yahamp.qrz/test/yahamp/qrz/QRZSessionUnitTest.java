/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.qrz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import yahamp.qrz.internal.QRZDatabase;
import yahamp.qrz.internal.QRZSession;

/** JUnit test of XML handling for {@link QRZSession}
 * 
 *  <p>Uses test files, no network connection.
 *  
 *  @author Kay Kasemir
 */
public class QRZSessionUnitTest
{
	private static final String KEY = "12345abcdef";
	private static String xml;

	/** Print data as XML to see if JAXB treats
	 *  data model classes as expected
	 *  @throws Exception on error
	 */
	@Test
	public void testWriteDemoXML() throws Exception
	{
		// Create Demo data
		final QRZDatabase qrz_database = new QRZDatabase();
		final QRZSession session = new QRZSession();
		session.setKey(KEY);
		qrz_database.setSession(session);

		// Write XML
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Marshaller m = jaxb.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(qrz_database, out);
		out.close();

		xml = out.toString();
		System.out.println(xml);

		assertTrue(xml.startsWith("<?xml"));
		assertTrue(xml.contains("</QRZDatabase>"));
		assertTrue(xml.contains("<Session>"));
		assertTrue(xml.contains("<Key>" + KEY + "</Key>"));
	}

	/** Read XML that JAXB itself has created.
	 *  'Loopback' test.
	 *  @throws Exception on error
	 */
	@Test
	public void testReadDemoXML() throws Exception
	{
		if (xml == null)
			testWriteDemoXML();
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

		final ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
		final QRZDatabase qrz_database =
			(QRZDatabase) unmarshaller.unmarshal(in); // new File("example/login.xml"));

		assertEquals(KEY, qrz_database.getSession().getKey());
	}

	/** Read login info from qrz.com XML interface description
	 *  @throws Exception on error
	 */
	@Test
	public void testReadQrzXML() throws Exception
	{
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

		final QRZDatabase qrz_database =
			(QRZDatabase) unmarshaller.unmarshal(new File("example/login.xml"));

		final QRZSession session = qrz_database.getSession();
		System.out.println(session);
		assertEquals(KEY, session.getKey());
	}


	/** Read login error from qrz.com XML interface description
	 *  @throws Exception on error
	 */
	@Test
	public void testReadQrzErrorsXML() throws Exception
	{
		final JAXBContext jaxb = JAXBContext.newInstance(QRZDatabase.class);
		final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

		QRZDatabase qrz_database =
			(QRZDatabase) unmarshaller.unmarshal(new File("example/login_error.xml"));
		QRZSession session = qrz_database.getSession();
		System.out.println(session);
		assertFalse(session.isValid());
		assertTrue(session.getError().contains("incorrect"));
		assertTrue(session.getMessage().contains("account"));

		qrz_database =
				(QRZDatabase) unmarshaller.unmarshal(new File("example/login_error2.xml"));
		session = qrz_database.getSession();
		System.out.println(session);
		assertFalse(session.isValid());
		assertTrue(session.getError().contains("unknown"));
		assertEquals(0, session.getMessage().length());
	}

	/** Check session timeout handling
	 *  @throws Exception on error
	 */
	@Test
	public void testExpiration() throws Exception
	{
		final QRZSession session = new QRZSession();
		assertFalse(session.isValid());

		session.setKey("bogus");
		assertFalse(session.isValid());

		// isValid checks if the session would be
		// valid for another minute.
		// 'now' it's considered expired
		final Date now = new Date();
		session.setExpiration(now);
		System.out.println("Now    :                             " + now);
		System.out.println("Expired: " + session);
		assertFalse(session.isValid());

		session.setExpiration(new Date(now.getTime() + 120*1000L));
		System.out.println("Valid  : " + session);
		assertTrue(session.isValid());
	}
}
