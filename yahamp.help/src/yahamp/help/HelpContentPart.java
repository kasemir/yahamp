/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package yahamp.help;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/** Display online help
 *
 *  <p>Would like to use a help system as in Eclipse 3,
 *  but for now this simply opens a web browser
 *  with a file path to the help files within this plugin,
 *  which therefore must be installed in expanded form,
 *  not as a JAR.
 *
 *  @author Kay Kasemir
 */
public class HelpContentPart
{
	private Browser browser;

	@PostConstruct
	public void postConstruct(final Composite parent) throws Exception
	{
	    parent.setLayout(new FillLayout());

	    browser = new Browser(parent, 0);
	    browser.setUrl(Activator.getFileURL("doc/yahamp.html"));
	}

	@Focus
	public void setFocus()
	{
	    browser.setFocus();
	}
}