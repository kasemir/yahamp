/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.call;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import yahamp.model.Callsign;
import yahamp.ui.Activator;

/** QRZ web display for a Call
 *  @author Kay Kasemir
 */
public class QRZCallDisplayPart
{
    final private Logger logger = Logger.getLogger(getClass().getName());

    // GUI Elements
    private Browser browser;

    /** Create GUI components
     *  @param parent Parent widget
     */
    @PostConstruct
    public void createPartControl(final Composite parent)
    {
        parent.setLayout(new FillLayout());

        browser = new Browser(parent, 0);
        browser.setUrl("www.qrz.com");
    }

    /** Set focus */
    @Focus
    public void onFocus()
    {
        browser.setFocus();
    }

    @PreDestroy
    public void dispose()
    {
        // TODO Saw a crash in Browser widget (windows 64)
        //      on dispose. Anything that can be done on shutdown
        //      to avoid it?
    }

    /** Update GUI when currently selected call changes
     *
     *  <p>Called via {@link IEventBroker} mechanism and injection
     *  @param call Currently selected {@link Callsign}
     */
    @Inject
    public void setCallsign(@Optional @UIEventTopic(Activator.TOPIC) final Callsign call)
    {
        if (call == null)
            return;

        logger.log(Level.FINE, "Received {0}", call);
        browser.setUrl("www.qrz.com/db/" + call.getCall());
   }
}