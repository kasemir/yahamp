/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import yahamp.model.Category;
import yahamp.model.Logbook;
import yahamp.model.QSO;
import yahamp.model.UTC;

/** Logbook: List of QSOs
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBLogbook implements Logbook
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    /** RDB Connection */
	final private RDB rdb;

	final private Categories categories;

	/** Logbook category. <code>null</code> for "all" */
	private Category category;

    /** All the QSOs in the current <code><category/code> */
    final private List<QSO> qsos = new ArrayList<QSO>();

	/** Initialize
	 *  @param rdb RDB
	 *  @throws Exception on error
	 */
	public RDBLogbook(final RDB rdb) throws Exception
	{
		this.rdb = rdb;
		categories = new Categories(rdb);
	}

    /** {@inheritDoc} */
    @Override
    public Category[] getCategories()
    {
        return categories.get();
    }

    /** {@inheritDoc} */
    @Override
	public void setCategory(final Category category) throws Exception
    {
        this.category = category;
        readQSOs();
	}

    /** Read all QSOs in current category */
	private void readQSOs() throws Exception
	{
        qsos.clear();

		try
		(
			final PreparedStatement statement =
				rdb.getConnection().prepareStatement(
						category == null ? rdb.sel_qsos : rdb.sel_qsos_for_category);
		)
		{
		    if (category != null)
		        statement.setInt(1, category.getId());
			final ResultSet result = statement.executeQuery();
			int number = 0;
			while (result.next())
			{
				final QSO qso = readQSO(++number, result);
				qsos.add(qso);
			}
		}
	}

	/** Read QSO from RDB
	 *  @param result ResultSet positioned on a QSO
	 *  @throws Exception on error
	 */
	private QSO readQSO(final int number, final ResultSet result) throws Exception
    {
		final QSO qso = new QSO(result.getString(3));
	    qso.setNumber(number);
	    qso.setCategory(result.getString(1));

        // The 'utc' column contains UTC datetime,
        // but when getting that as a "Timestamp",
        // Java interpretes it as local time.
        // This way, we get the string and
        // parse that as UTC ourselves.
        final String utc_string = result.getString(2);
        // 2007-05-27 17:27:08.0
        //System.out.println(utc_string);
        final String utc_date = utc_string.substring(0, 10);
        final String utc_time = utc_string.substring(11, 19);
        //System.out.println(utc_date);
        //System.out.println(utc_time);
        qso.setUTC(new UTC(utc_date, utc_time));

		qso.setFreq(result.getString(4));
		qso.setRstSent(result.getString(5));
		qso.setRstRcvd(result.getString(6));
		qso.setMode(result.getString(7));
		qso.setInfo(result.getString(8));
		qso.setQsl(RDB.nonNull(result.getString(9)));

		return qso;
    }

    /** {@inheritDoc} */
    @Override
	public Category getCategory()
    {
        return category;
    }

    /** {@inheritDoc} */
	@Override
	public int getQsoCount()
	{
		return qsos.size();
	}

    /** {@inheritDoc} */
	@Override
	public QSO getQso(final int index)
	{
		return qsos.get(index);
	}

    /** {@inheritDoc} */
    @Override
	public QSO[] findQSOs(final String call) throws Exception
    {
    	final ArrayList<QSO> qsos = new ArrayList<QSO>();
    	try
		(
			final PreparedStatement statement =
				rdb.getConnection().prepareStatement(rdb.sel_qsos_for_call);
		)
		{
			statement.setString(1, call);
			final ResultSet result = statement.executeQuery();
			int number = 0;
			while (result.next())
			{
				final QSO qso = readQSO(++number, result);
				qsos.add(qso);
			}
		}
		return qsos.toArray(new QSO[qsos.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QSO qso) throws Exception
    {
        try
        (
            final PreparedStatement statement =
                rdb.getConnection().prepareStatement(rdb.replace_qso);
        )
        {
            statement.setInt(1, categories.get(qso.getCategory()).getId());
            statement.setString(2, qso.getUTC().toString());
            statement.setString(3, qso.getCall());
            statement.setString(4, qso.getFreq());
            statement.setString(5, qso.getRstSent());
            statement.setString(6, qso.getRstRcvd());
            statement.setString(7, qso.getMode());
            statement.setString(8, qso.getQsl());
            statement.setString(9, qso.getInfo());
            final int rows = statement.executeUpdate();
            if (rows == 1)
                logger.log(Level.FINE, "Inserted new QSO " + qso);
            else if (rows == 2)
                logger.log(Level.FINE, "Replaced QSO " + qso);
            else
                throw new Exception("RDB QSO update failed, rows = " + rows);
        }
    }
}
