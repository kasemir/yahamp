/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.previous_qsos;

import java.util.List;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import yahamp.model.QSO;

/** Content provider for 'lazy' table that displays array of {@link QSO}
 *  @author Kay Kasemir
 */
public class LogTableContentProvider implements ILazyContentProvider
{
    private TableViewer viewer;

    private List<QSO> qsos;

    @SuppressWarnings("unchecked")
    @Override
    public void inputChanged(final Viewer viewer, final Object old_input, final Object input)
    {
        if (viewer instanceof TableViewer)
            this.viewer = (TableViewer) viewer;

        if (input instanceof List<?>)
        {
            qsos = (List<QSO>) input;
            final int size = qsos.size();
            this.viewer.setItemCount(size);
            // Select the last, i.e. most recent QSO
            if (size > 0)
                this.viewer.setSelection(new StructuredSelection(qsos.get(size-1)), true);
            else
                this.viewer.setSelection(null);
        }
        else
            this.viewer.setItemCount(0);
    }

    @Override
    public void updateElement(final int index)
    {
        viewer.replace(qsos.get(index), index);
    }

    @Override
    public void dispose()
    {
        qsos = null;
    }
}
