/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.flags;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
    private static Bundle bundle;

    @Override
    public void start(final BundleContext context) throws Exception
    {
        bundle = context.getBundle();
    }

    @Override
    public void stop(final BundleContext context) throws Exception
    {
        bundle = null;
    }

    /** @param directory Directory within this plugin
     *  @return {@link File} for that directory
     *  @throws Exception on error
     */
    public static File getDirectory(final String directory) throws Exception
    {
        final File bundle_file;
        if (bundle == null)
        {
            // Running as JUnit test without bundle?
            // Assume we are in one of the yahmap.* direcotories,
            // so going '..' allows us to get into this plugin dir
            bundle_file = new File("../yahamp.flags");
        }
        else
            bundle_file = FileLocator.getBundleFile(bundle);
        return new File(bundle_file, directory);
    }
}
