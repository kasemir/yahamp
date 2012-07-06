/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of {@link Categories}
 *
 *  <p>Assumes that there are some categories already defined.
 *  @author Kay Kasemir
 */
public class CategoriesTest
{
    @Test
    public void testCategories() throws Exception
    {
    	final RDB rdb = new RDB();
    	final Categories categories = new Categories(rdb);
    	System.out.println(categories);
    	assertNotNull(categories);
    	rdb.close();
    }

    @Test
    public void testGetNewCategory() throws Exception
    {
        final RDB rdb = new RDB();
        final Categories categories = new Categories(rdb);
        final int id = categories.get("XYTestCategory").getId();
        System.out.println("New ID: " + id);
        assertTrue(id > 0);
        // Delete the test category
        rdb.getConnection().createStatement().executeUpdate(
            "DELETE FROM categories WHERE id=" + id);
        rdb.close();
    }
}
