/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.model;

/** Call sign
 *  @author Kay Kasemir
 */
public class Callsign
{
	protected String call;

	/** Initialize
	 *  @param call Call sign
	 */
	public Callsign(final String call)
    {
		this.call = call.trim().toUpperCase();
    }

	/** Derived classes can use no-arg constructor */
	protected Callsign()
    {
        this.call = "";
    }

	/** @return Call sign */
    public String getCall()
    {
        return call;
    }

    @Override
    public int hashCode()
    {
        return call.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof Callsign))
            return false;
        final Callsign other = (Callsign) obj;
        return call.equals(other.call);
    }

    @Override
    public String toString()
    {
	    return "Callsign " + call;
    }
}
