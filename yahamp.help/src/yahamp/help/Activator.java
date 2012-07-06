/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.help;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator implements BundleActivator
{
    private static Bundle bundle = null;

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

    /** Get path to file
     *  @param path Path within plugin
     *  @return Path to that file
     *  @throws Exception
     */
    public static String getFileURL(final String path) throws Exception
    {
        final File bundle_file = FileLocator.getBundleFile(bundle);
        return new File(bundle_file, path).getAbsolutePath();
    }
}
