/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import static org.junit.Assert.*;

import org.junit.Test;

import yahamp.rdb.RDB;

/** JUnit test of the RDB support
 *  @author Kay Kasemir
 */
public class RDBTest
{
    @Test
    public void testConnect() throws Exception
    {
    	try (final RDB rdb = new RDB())
    	{
    		assertNotNull(rdb.getConnection());
    	}
    }
}
