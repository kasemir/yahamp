/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.app;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import yahamp.logging.LogConfigurator;

/** Bundle activator
 *  @author Kay Kasemir
 */
public class Activator implements BundleActivator
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "yahamp.app";

    /** Retain references to them because otherwise they
     *  can be garbage-collected and then they'll be re-initialized
     *  with default log levels.
     */
    private final List<Logger> log_refs = new ArrayList<>();
    private static Bundle bundle;

    //  Preference injection only works for e4 model elements...
    @Inject
    @Preference(nodePath="yahamp.app", value="log_level")
    private String log_level = "FINE";

	/** {@inheritDoc} */
	@Override
    public void start(final BundleContext context) throws Exception
	{
	    bundle = context.getBundle();

	    // Read preferences from scope because injection fails
        final IPreferencesService prefs = Platform.getPreferencesService();
        log_level = prefs.getString(ID, "log_level", "INFO", null);

	    LogConfigurator.setLevel(Level.parse(log_level));
	    log_refs.add(Logger.getLogger(""));

	    // Reduce log levels of code outside of yahamp
	    for (final String name : new String[] { "com", "sun", "javax" })
	    {
	        final Logger logger = Logger.getLogger(name);
	        log_refs.add(logger);
	        logger.setLevel(Level.WARNING);
	    }
	}

    /** {@inheritDoc} */
	@Override
    public void stop(final BundleContext context) throws Exception
	{
	    // NOP
	}

    public static String getVersion()
    {
        if (bundle == null)
            return "?";
        return bundle.getVersion().toString();
    }
}
