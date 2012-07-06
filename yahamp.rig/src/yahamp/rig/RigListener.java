/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

/** Interface for listener to <code>RigCtrlThread</code>
 *  @author Kay Kasemir
 */
public interface RigListener
{
    /** Called by RigCtrlThread to notify about operating frequency and mode
     *  @param info Latest rig info
     */
    public void newRigInfo(RigInfo info);
    
    /** Called by RigCtrlThread to notify about error
     *  @param ex Error info
     */
    public void rigError(Exception ex);
}
