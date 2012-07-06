/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model.countries;

import junit.framework.Assert;

import org.junit.Test;

/** JUnit test/demo of the PrefixDatabase
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PrefixDatabaseTest
{
	@Test
    public void testDatabase() throws Exception
    {
        final PrefixDatabase db = new PrefixDatabase();
        PrefixInfo info = db.find("W1AW");
        System.out.println(info);
		Assert.assertEquals(info.getDXCC(), "K");
        Assert.assertEquals(info.getCountry(), "United States");

		info = db.find("ti7cbt");
        System.out.println(info);
		Assert.assertEquals(info.getDXCC(), "TI");
        Assert.assertEquals(info.getCountry(), "Costa Rica");

        info = db.find("co6wd");
        System.out.println(info);
		Assert.assertEquals(info.getCountry(), "Cuba");
    }
}
