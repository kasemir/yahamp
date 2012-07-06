/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.flags;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Provide flag image for most countries
 *
 *  Flag images are the 256-wide icons from
 *  http://www.customicondesign.com/free-icon/
 *  Notice on that web site states:
 *  "All the icons contained in this set are free for non-commercial use."
 *
 *  @author Kay Kasemir
 */
public class FlagImageFile
{
    /** Singleton */
    private static FlagImageFile instance;

    /** Map of country names to flag image files */
    final private Map<String, File> flags = new HashMap<>();

    /** Width resp. height of flag images */
    final public static int WIDTH = 256, HEIGHT = 256;

    /** @return Singleton instance
     *  @throws Exception on error accessing flag images
     */
    private static synchronized FlagImageFile getInstance() throws Exception
    {
        if (instance == null)
            instance = new FlagImageFile();
        return instance;
    }

    /** Initialize
     *
     *  <p>Locates all flag images.
     *  @throws Exception on error accessing flag images
     */
    private FlagImageFile() throws Exception
    {
        final File directory = Activator.getDirectory("flags");
        final File[] files = directory.listFiles();
        for (final File file : files)
        {
            // Turn "....\flags\United-States-Flag-256.png" into "united states"
            String country = file.getName();
            // Some images end in "-Flag-256.png"
            int sep = country.indexOf("-Flag-256.png");
            if (sep < 0)
            {
                // Others use just "-256.png"
                sep = country.indexOf("-256.png");
                if (sep < 0)
                {
                    Logger.getLogger(getClass().getName()).log(Level.INFO,
                            "Ignoring flag file {0}", file);
                    continue;
                }
            }
            country = country.substring(0, sep);
            country = country.replace('-', ' ');
            country = country.toLowerCase();
            flags.put(country, file);
        }
    }

    /** Get flag for country
     *  @aram country Name of country
     *  @return Flag file or <code>null</code>
     *  @throws Exception on error accessing flag images
     */
    public static File get(String country) throws Exception
    {
        country = country.toLowerCase().replace('-', ' ');
        return getInstance().flags.get(country);
    }
}
