/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
	/** Date format */
    final private SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    /** Formatter, must be thread safe
     *  @param record {@link LogRecord} to format 
     *  @return String
     */
    @Override
	public String format(final LogRecord record)
	{
		final StringBuilder buf = new StringBuilder();
		final Date date = new Date(record.getMillis());
		final String date_txt;
		synchronized (date_format)
		{
			date_txt = date_format.format(date);
		}
		buf.append(date_txt);
		
		buf.append(' ').append(record.getLevel().getName());
		buf.append(" [").append(record.getLoggerName()).append("] ");
		
		// Class, method
	    if (record.getSourceClassName() != null)
	    {
	    	buf.append(record.getSourceClassName());
	    	buf.append(" ");
	    }
	    if (record.getSourceMethodName() != null)
	    {
	    	buf.append("(");
	    	buf.append(record.getSourceMethodName());
	        buf.append(") ");
	    }
		
		buf.append(formatMessage(record));
		
		 // Stack trace
        final Throwable thrown = record.getThrown();
        if (thrown != null)
        {
            try
            {
                buf.append("\n");
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                thrown.printStackTrace(pw);
                pw.close();
                buf.append(sw.toString());
            }
            catch (Exception ex)
            {   // Cannot dump detail of logged exception?
                // Log just the class name.
            	buf.append(" (");
            	buf.append(thrown.getClass().getName());
            	buf.append(")");
            }
        }
        // Final "\n" is important!
        // Messages will otherwise simply not show up because
        // the output is not 'flushed' in time.
        buf.append("\n");
        
        final String result = buf.toString();
        // System.out.println(result);
        return result;
	}

}
