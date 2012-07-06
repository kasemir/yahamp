/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;


/** Interface to a call book that keeps {@link CallInfo} entries
 *  by call sign
 *  @author Kay Kasemir
 */
public interface CallBook
{
	/** Obtain information for call sign
	 *  @param callsign
	 *  @return Callinfo or <code>null</code> if nothing found
	 *  @throws Exception on error
	 */
	public CallInfo lookup(String callsign) throws Exception;
}
