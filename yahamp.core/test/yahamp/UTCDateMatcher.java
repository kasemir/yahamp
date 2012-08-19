/*******************************************************************************
 * Copyright (c) 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import yahamp.model.UTC;

/** Hamcrest Matcher for UTC based on date/time string representation
 *  @author Kay Kasemir
 */
// Some versions of Hamcrest include TypeSafeMatcher.
// Eclipse package has this in junit...internal, resulting in warning..
class UTCDateMatcher extends TypeSafeMatcher<UTC>
{
    final private String date_text;

    public UTCDateMatcher(final String date_text)
    {
        this.date_text = date_text;
    }

    @Override
    public void describeTo(final Description desc)
    {
        desc.appendText("Date and time ").appendValue(date_text);
    }

    @Override
    public boolean matchesSafely(final UTC utc)
    {
        return utc.toString().equalsIgnoreCase(date_text);
    }

    @Factory
    public static Matcher<UTC> isDate(final String date_text)
    {
        return new UTCDateMatcher(date_text);
    }
}