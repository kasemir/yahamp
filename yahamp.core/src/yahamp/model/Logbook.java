/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

/** Logbook: List of {@link QSO}s
 *  @author Kay Kasemir
 */
public interface Logbook
{
    /** Obtain list of all currently available categories
     *  @return Array of {@link Category}
     */
    public Category[] getCategories();

	/** Set Category, then read QSOs for that category from RDB
	 *  @param category Desired category, or <code>null</code> for all entries.
	 *  @throws Exception on error
	 */
	public void setCategory(Category category) throws Exception;

	/** @return Logbook category, or <code>null</code> for all entries */
	public Category getCategory();

	/** @return Number of QSOs in Logbook */
	public int getQsoCount();

	/** @param index QSO index, 0 ... getQsoCount()-1
	 *  @return QSO
	 */
	public QSO getQso(int index);

	/** Locate all QSOs with a call
	 *  @param call Call sign
	 *  @return Array of QSOs. May be empty array, never <code>null</code>
	 *  @throws Exception on error
	 */
	public QSO[] findQSOs(String call) throws Exception;

	/** @param qso New {@link QSO} to add to logbook
	 *  @throws Exception on error
	 */
    public void save(final QSO qso) throws Exception;
}