/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig;

public class RigInfo
{
    final private RigModel model;
    final private double freq_MHz;
    final private String mode;

    public RigInfo(final RigModel model, final double freq_MHz, final String mode)
    {
        this.model = model;
        this.freq_MHz = freq_MHz;
        this.mode = mode;
    }

    public RigModel getModel()
    {
        return model;
    }

    public double getFreqMHz()
    {
        return freq_MHz;
    }

    public String getFreq()
    {
        return String.format("%.3f", freq_MHz);
    }

    public String getMode()
    {
        return mode;
    }

    @Override
    public String toString()
    {
        return model + ": " + freq_MHz + " MHz (" + mode + ")";
    }

    @Override
    public boolean equals(final Object other)
    {
        if (! (other instanceof RigInfo))
            return false;
        final RigInfo rhs = (RigInfo) other;
        return freq_MHz == rhs.freq_MHz &&
               mode.equals(rhs.mode);
    }
}
