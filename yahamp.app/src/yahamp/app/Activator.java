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

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import yahamp.logging.LogConfigurator;

/** Bundle activator
 *
 *  TODO: Need -clearPersistedState in product as program arg.
 *
 *  Without that, the saved workbench.xmi
 *  is not loaded properly:
 *
 *
 *  Exception occurred while rendering: org.eclipse.e4.ui.model.application.ui.basic.impl.PartStackImpl@2da0b7ca (elementId: yahamp.app.partstack.bottom, tags: [], contributorURI: platform:/plugin/yahamp.app) (widget: CTabFolder {}, renderer: org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer@2a30aa7d, toBeRendered: true, onTop: false, visible: true, containerData: 3000, accessibilityPhrase: null)
!STACK 0
java.lang.NullPointerException
    at org.eclipse.e4.ui.workbench.renderers.swt.LazyStackRenderer.showTab(LazyStackRenderer.java:156)
 *
 * The LazyStackRenderer.java:156 code is  ~part.getParent()... and parent returns null.
 *
 *  With a new workspace, or  -clearPersistedState in the args, all is fine.
 *
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

    //  TODO Preferences should get injected...
    @Inject
    @Preference(nodePath="yahamp.app", value="log_level")
    private String log_level = "FINE";

	/** {@inheritDoc} */
	@Override
    public void start(final BundleContext context) throws Exception
	{
	    bundle = context.getBundle();

	    // Read preferences from scope because injection fails
	    IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(ID);
        log_level = prefs.get("log_level", "INFO");

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
