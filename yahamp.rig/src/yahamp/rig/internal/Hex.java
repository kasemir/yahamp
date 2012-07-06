/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.rig.internal;

/** Helper for Hex strings.
 *  @author Kay Kasemir
 */
public class Hex
{
    /** @param byte Bytes
     *  @return Hex-string of the given bytes
     */
    public static String hex(final byte[] bytes)
    {
        return hex(bytes.length, bytes);
    }

    /** @param count Number of bytes to format
     *  @param byte Bytes
     *  @return Hex-string of the given bytes
     */
    public static String hex(final int count, final byte[] bytes)
    {
        final StringBuffer result = new StringBuffer("0x");
        for (int i = 0; i < count; i++)
            result.append(String.format("%02x", (bytes[i]) & 0xFF));
        return result.toString();
    }
}
