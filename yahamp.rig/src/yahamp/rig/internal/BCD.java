/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

/** Binary Coded Decimal.
 *  @author Kay Kasemir
 */
public class BCD
{
    /** @return Decimal number read from Binary Coded Decimal */
    final public static long decode(final int b)
    {
        return ((b & 0xF0) >> 4) * 10 + (b & 0x0F);
    }

    /** @return Encode number as Binary Coded Decimal */
    final public static int encode(final int number)
    {
        final int tens = number / 10;
        final int ones = number % 10;
        return  (((tens << 4) | ones) & 0xFF);
    }
}
