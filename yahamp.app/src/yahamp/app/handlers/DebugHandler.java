/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.app.handlers;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class DebugHandler
{
	@Execute
	public void execute(final Shell shell,
			MApplication app,
			MWindow window,
			IWorkbench workbench) throws Exception
	{
		System.out.println(window.getContributorURI());
		System.out.println("Variables: ");
		for (String var : window.getVariables())
			System.out.println(var);
		
		final Map<String, String> properties = window.getProperties();
		final Iterator<String> keys = properties.keySet().iterator();
		while (keys.hasNext())
		{
			final String key = keys.next();
			System.out.println(key + " = " + properties.get(key));
		}
	}
}
