/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;

import yahamp.model.QSO;

/** [Headless] JUnit Plug-In test of injection
 *  @author Kay Kasemir
 */
public class TestClassHeadlessTest
{
    @Test
    public void testTestClass()
    {
        // Create context for injection
        final IEclipseContext context = EclipseContextFactory.create();
        
        // Add a QSO to context.
        // Can use the class, or the name of the class.
        // Not anything looking for an injected QSO will receive one with W1AW
        context.set(QSO.class, new QSO("W1AW"));
        
        // Create test class that needs a QSO injected into constructor
        final TestClass test = ContextInjectionFactory.make(TestClass.class, context);
        assertNotNull(test);
        System.out.println(test.getQSO());
        assertEquals("W1AW", test.getQSO().getCall());
    }
}
