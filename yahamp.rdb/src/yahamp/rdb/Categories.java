/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yahamp.model.Category;

/** Logbook categories
 *  @author Kay Kasemir
 */
public class Categories
{
    final private RDB rdb;
	final private List<Category> categories = new ArrayList<>();
    final private Map<String, Category> ids = new HashMap<>();

	/** Initialize
	 *  @param rdb {@link RDB}
	 *  @throws Exception on error
	 */
	public Categories(final RDB rdb) throws Exception
	{
	    this.rdb = rdb;
		try
		(
			final PreparedStatement statement =
			    rdb.getConnection().prepareStatement(rdb.sel_categories)
		)
		{
			final ResultSet result = statement.executeQuery();
			while (result.next())
			{
				final int id = result.getInt(1);
				final Category category = new Category(id, result.getString(2));
                categories.add(category);
                ids.put(category.getName(), category);
            }
		}
	}

	/** @return All currently defined categories */
	public Category[] get()
	{
	    return categories.toArray(new Category[categories.size()]);
	}

	/** Get or add category by name
	 *  @param category_name Name of category
	 *  @return Existing or newly created {@link Category}
	 *  @throws Exception on error
	 */
    public Category get(final String category_name) throws Exception
    {
        Category category = ids.get(category_name);
        if (category != null)
            return category;
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(rdb.insert_category, Statement.RETURN_GENERATED_KEYS);
        )
        {
            statement.setString(1,  category_name);
            statement.executeUpdate();
            final ResultSet keys = statement.getGeneratedKeys();
            if (! keys.next())
                throw new Exception("Could not obtain new category");
            final int id = keys.getInt(1);
            category = new Category(id, category_name);
            categories.add(category);
        }
        return category;
    }

    @Override
    public String toString()
    {
    	return categories.toString();
    }
}
