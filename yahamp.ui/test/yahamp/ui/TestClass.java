/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import javax.inject.Inject;

import yahamp.model.QSO;

/** Class that needs QSO
 *  @author Kay Kasemir
 */
public class TestClass
{
    final private QSO qso;
    
    /** Initialize
     *  @param qso {@link QSO}, either passed directly or injected from context
     */
    @Inject
    public TestClass(final QSO qso)
    {
        this.qso = qso;
    }
    
    public QSO getQSO()
    {
        return qso;
    }
}
