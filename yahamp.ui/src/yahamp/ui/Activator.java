/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import yahamp.model.Callsign;
import yahamp.model.QSO;

public class Activator implements BundleActivator
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "yahamp.ui";

    /** Topic used for {@link IEventBroker}
     *  to publish the current {@link QSO} or {@link Callsign}
     */
    final public static String TOPIC = "yahamp";

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

    /** Open stream for file in bundle
     *  @param path Path within bundle
     *  @return {@link InputStream}
     *  @throws Exception on error
     */
    public static InputStream openStream(final String path) throws Exception
    {
        if (bundle == null)
        {
            // Running as JUnit test without bundle?
            // Assume we are in one of the yahmap.* direcotories,
            // so going '..' allows us to get into this plugin dir
            final File bundle_file = new File("../yahamp.ui", path);
            return new FileInputStream(bundle_file);
        }
        else
            return FileLocator.openStream(bundle, new Path(path), false);
    }
}
