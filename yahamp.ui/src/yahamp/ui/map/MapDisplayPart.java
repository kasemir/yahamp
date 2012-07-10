/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import yahamp.model.CallInfo;
import yahamp.model.earth.Maidenhead;
import yahamp.model.earth.Position;
import yahamp.ui.Activator;
import yahamp.ui.internal.Preferences;

/** Map display
 *  @author Kay Kasemir
 */
public class MapDisplayPart
{
    final private String my_call;

    final private Maidenhead my_location;

	private EarthWidget earth;

	@Inject
	public MapDisplayPart(
        @Preference(nodePath=Activator.ID, value=Preferences.CALL)
        final String call,
        @Preference(nodePath=Activator.ID, value=Preferences.GRID)
        final String grid)
	{
	    my_call = call;
	    my_location = Maidenhead.fromGrid(grid);
	}

	@PostConstruct
	public void createPartControl(final Composite parent) throws Exception
	{
	    parent.setLayout(new FillLayout());
	    earth = new EarthWidget(parent, 0, EarthImage.Size.Large);
	}

	@Focus
	public void onFocus()
	{
	    earth.setFocus();
	}

	/** Set markers to display on map
	 *  @param locations_and_texts List of locations and associated texts.
	 *         Must be an array of Position 1, label 1, Position 2, label 2, ...
	 */
	public void mark(final Object... locations_and_texts)
	{
	    mark(Arrays.asList(locations_and_texts));
	}

	/** Set markers to display on map
     *  @param locations_and_texts List of locations and associated texts.
     *         Must be an array of Position 1, label 1, Position 2, label 2, ...
     */
	public void mark(final List<Object> locations_and_texts)
	{
	    earth.clearMarkers();
	    if ((locations_and_texts.size() % 2) != 0)
	        throw new IllegalArgumentException("Need matched number of locations and texts");
	    for (int i=0; i<locations_and_texts.size(); i+=2)
	    {
	        if (! (locations_and_texts.get(i) instanceof Position))
	            throw new IllegalArgumentException("Expected location, got " + locations_and_texts.get(i));
	        earth.addMarker((Position) locations_and_texts.get(i),
	                        locations_and_texts.get(i+1).toString());
	    }
	}

   /** Mark a call on map
    *
    *  <p>Called via {@link IEventBroker} mechanism and injection
    *  @param callinfo {@link CallInfo} to highlight on map
    */
   @Inject
   @Optional
   public void markCall(@UIEventTopic(Activator.TOPIC) final CallInfo callinfo)
   {
       final List<Object> markers = new ArrayList<>();
       if (my_location != null  &&  my_call != null)
       {
           markers.add(my_location);
           markers.add(my_call);
       }
       final String grid = callinfo.getGrid();
       if (! grid.isEmpty())
       {
           final Maidenhead location = Maidenhead.fromGrid(grid);
           markers.add(location);
           markers.add(callinfo.getCall());
       }
       mark(markers);
   }
}