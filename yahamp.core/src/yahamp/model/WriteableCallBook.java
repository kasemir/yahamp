/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

import yahamp.model.CallInfo;

/** {@link {@link CallBook}} that supports writing
 *  @author Kay Kasemir
 */
public interface WriteableCallBook extends CallBook
{
	/** Write call info to call book
	 *  @param call {@link CallInfo} to write
	 *  @throws Exception on error
	 */
	public void save(CallInfo call) throws Exception;

	/** Delete call from call book
	 *  @param info {@link CallInfo} to delete
	 *  @throws Exception on error
	 */
	public void delete(CallInfo info) throws Exception;
}
