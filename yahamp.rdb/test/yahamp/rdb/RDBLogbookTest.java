/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import static org.junit.Assert.*;

import org.junit.Test;

import yahamp.model.Logbook;
import yahamp.model.QSO;
import yahamp.rdb.RDBLogbook;
import yahamp.rdb.RDB;

/** JUnit test of the {@link RDBLogbook}
 * 
 *  <p>Assumes certain existing QSOs
 *  @author Kay Kasemir
 */
public class RDBLogbookTest
{
	@Test
	public void testLogbook() throws Exception
	{
		try (final RDB rdb = new RDB())
    	{
    		final Logbook log = new RDBLogbook(rdb);
    		// Must set cat. to trigger lookup
    		log.setCategory(null);
    		for (int i=0; i<log.getQsoCount(); ++i)
    			System.out.println(log.getQso(i));
    		System.out.println(log.getQsoCount() + " QSOs");
    		
    		assert(log.getQsoCount() > 0);
    	}
	}

	@Test
	public void testFindQSOs() throws Exception
	{
		try (final RDB rdb = new RDB())
    	{
    		final Logbook log = new RDBLogbook(rdb);
    		final QSO[] qsos = log.findQSOs("EA6UN");
    		for (QSO qso : qsos)
    			System.out.println(qso);
    		assertTrue(qsos.length > 0);
    	}
	}
}
