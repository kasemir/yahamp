/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Helper for connecting to RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDB implements AutoCloseable
{
    final public String sel_categories = "SELECT id, category FROM categories ORDER BY category";

    final public String insert_category = "INSERT INTO categories(category) VALUES (?)";

    final public String sel_qsos =
    	"SELECT c.category, q.utc, q.callsign, q.freq, q.rst_sent, q.rst_rcvd, q.mode, q.info, q.qsl" +
		" FROM qsos q" +
		" JOIN categories c on q.category_id = c.id" +
		" ORDER BY utc";

    final public String sel_qsos_for_category =
    	"SELECT c.category, q.utc, q.callsign, q.freq, q.rst_sent, q.rst_rcvd, q.mode, q.info, q.qsl" +
		" FROM qsos q" +
		" JOIN categories c on q.category_id = c.id" +
		" WHERE category_id=?" +
		" ORDER BY utc";

    final public String sel_qsos_for_call =
        "SELECT c.category, q.utc, q.callsign, q.freq, q.rst_sent, q.rst_rcvd, q.mode, q.info, q.qsl" +
        " FROM qsos q" +
        " JOIN categories c on q.category_id = c.id" +
        " WHERE callsign=?" +
        " ORDER BY utc";

    final public String replace_qso =
        "REPLACE INTO qsos (" +
        " category_id,utc,callsign,freq,rst_sent,rst_rcvd,mode,qsl,info)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";

    final public String delete_qso = "DELETE FROM qsos WHERE utc=?";

	final public String sel_callinfo = "SELECT "
        + "name,addr,city,state,"
        + "zip,grid,county,country,"
        + "fists_nr,fists_cc,fp_nr FROM CALLS "
        + "WHERE callsign=?";

	final public String replace_callinfo = "REPLACE INTO calls ("
        + "callsign,name,addr,city,state,"
        + "zip,grid,county,country,"
        + "fists_nr,fists_cc,fp_nr) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

	final public String delete_callinfo = "DELETE FROM calls WHERE callsign=?";

	final private Connection connection;


	/** Initialize with URL from preferences
	 *  @throws Exception on error
	 */
	public RDB() throws Exception
	{
		this(Preferences.getURL());
	}

	/** Initialize
	 *  @param url RDB URL
	 *  @throws Exception on error
	 */
	public RDB(final String url) throws Exception
    {
        // Get class loader to find the driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(url);
        // Basic database info
//        final DatabaseMetaData meta = connection.getMetaData();
//        System.out.println("MySQL connection: " + meta.getDatabaseProductName()
//                        + " " + meta.getDatabaseProductVersion());
    }


	public Connection getConnection()
    {
    	return connection;
    }

	/** Must be called to release resources */
	@Override
	public void close()
    {
		try
        {
	        connection.close();
        }
		catch (final SQLException e)
        {
			// Ignore, shutting down anyway
        }
    }

	/** @return Non-null string */
    public static String nonNull(final String string)
    {
        if (string == null)
            return "";
        return string;
    }

    /** Set JDBC statement parameter to NULL for null or empty strings
     *  @param statement
     *  @param index
     *  @param value
     *  @throws Exception
     */
	public static void setStringOrNULL(final PreparedStatement statement,
			final int index, final String value)  throws Exception
	{
        if (value == null  ||  value.length() == 0)
        	statement.setNull(index, java.sql.Types.VARCHAR);
        else
        	statement.setString(index, value);
	}

    /** Set JDBC statement parameter to NULL for zero
     *  @param statement
     *  @param index
     *  @param value
     *  @throws Exception
     */
	public static void setIntOrNULL(final PreparedStatement statement,
			final int index, final int value)  throws Exception
	{
        if (value == 0)
        	statement.setNull(index, java.sql.Types.INTEGER);
        else
        	statement.setInt(index, value);
	}
}
