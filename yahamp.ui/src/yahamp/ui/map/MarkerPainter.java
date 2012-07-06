/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** Helper for painting a marker (x/y location and text)
 *  into a GC.
 *  @author Kay Kasemir
 */
public class MarkerPainter
{
    private static final int MARKER_SIZE = 2;

	private Color marker_fg_color, marker_bg_color, marker_shadow_color;
    private Color standout_color;
	
    public MarkerPainter()
    {
        this(false);
    }
    
	public MarkerPainter(boolean alternate)
    {
        if (alternate)
        {
    	    marker_fg_color = new Color(null, 255, 200, 200);
            standout_color = new Color(null, 255, 100, 100);
        }
        else
        {
            marker_fg_color = new Color(null, 255, 255, 100);
            standout_color = new Color(null, 255, 255, 255);
        }
        marker_shadow_color = new Color(null, 0, 0, 0);
        marker_bg_color = new Color(null, 255, 0, 0);
	}

	public void dispose()
	{
        standout_color.dispose();
	    marker_fg_color.dispose();
	    marker_bg_color.dispose();
	    marker_shadow_color.dispose();
	}

    public void paint(Rectangle area, GC gc, int x, int y, String text)
    {
        paint(area, gc, x, y, text, false);
    }
    
	public void paint(Rectangle area, GC gc, int x, int y, String text,
                      boolean standout)
	{	
		//    TEXT
		//    ----
		//   /
		//  O
        if (text != null)
        {
    		final Point size = gc.textExtent(text);
            int text_x = x-2*MARKER_SIZE - size.x;
            final int text_y = y-2*MARKER_SIZE-size.y;
            int hook_x = x-2*MARKER_SIZE;
            final int text_base = y-2*MARKER_SIZE;

            if (x+2*MARKER_SIZE+size.x <= area.width)
            {   // No room to paint the text to the right of the point.
        		text_x = x+2*MARKER_SIZE;
                hook_x = text_x;
            }
            gc.setForeground(marker_shadow_color);
            // Text Shadow
            gc.drawText(text, text_x+2, text_y+2, true);
            gc.setForeground(marker_fg_color);
            gc.setBackground(marker_bg_color);
            // The '/' or '\' part of the line
            gc.drawLine(x, y, hook_x, text_base);
            // Underline for text
            gc.drawLine(text_x, text_base, text_x+size.x, text_base);
            if (standout)
                gc.setForeground(standout_color);
            gc.drawText(text, text_x, text_y, true);
        }
        else
        {
            gc.setForeground(marker_fg_color);
            gc.setBackground(marker_bg_color);
        }
        // The marker itself, centered at {x,y}
		gc.fillOval(x-MARKER_SIZE, y-MARKER_SIZE,
				    2*MARKER_SIZE, 2*MARKER_SIZE);
		gc.drawOval(x-MARKER_SIZE, y-MARKER_SIZE,
				    2*MARKER_SIZE, 2*MARKER_SIZE);
	}
}
