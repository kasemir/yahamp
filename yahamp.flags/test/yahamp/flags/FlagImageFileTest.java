/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.flags;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FlagImageFileTest
{
    @Test
    public void testFlagImage() throws Exception
    {
        final File file = FlagImageFile.get("United States");
        System.out.println(file);
        assertNotNull(file);
        assertTrue(file.toString().contains(".png"));
        assertTrue(file.toString().contains("United"));
    }
}
