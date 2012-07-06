/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.app.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import yahamp.app.Activator;

public class AboutHandler
{
	@Execute
	public void execute(
			@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) throws Exception
	{
	    final StringBuilder message = new StringBuilder();
	    message.append("YAHAMP\n");
	    message.append("Yet Another HAM Program\n");
        message.append("\n");
        message.append("Based on Eclipse 4\n");
        message.append("\n");
        message.append("Copyright (c) 2010, 2012 Kay Kasemir, AI4IC\n");
        message.append("\n");
        message.append("Version ").append(Activator.getVersion());
        message.append("\n");
        message.append("See https://github.com/kasemir/yahamp\n");
		MessageDialog.openInformation(shell,  "About", message.toString());
	}
}
