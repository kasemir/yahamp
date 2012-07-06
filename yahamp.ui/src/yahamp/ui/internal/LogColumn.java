/*******************************************************************************
 * Copyright (c) 2010, 2012 Kay Kasemir. All rights reserved.
 * Made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package yahamp.ui.internal;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.TableColumn;

import yahamp.model.QSO;

/** Column in log table
 *  @author Kay Kasemir
 */
abstract public class LogColumn
{
	/** Initialize
	 *  @param layout {@link TableColumnLayout}
	 *  @param viewer {@link TableViewer}
	 *  @param header Column header
	 *  @param weight Resize weight
	 *  @param width Minimum width
	 */
	public LogColumn(final TableColumnLayout layout, final TableViewer viewer,
			final String header, final int weight, final int width)
	{
		final TableViewerColumn column = new TableViewerColumn(viewer, 0);
        final TableColumn col = column.getColumn();
		col.setText(header);
		col.setMoveable(true);
        layout.setColumnData(col, new ColumnWeightData(weight, width));
        column.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
            	final QSO qso = (QSO) cell.getElement();
            	LogColumn.this.update(cell, qso);
            }
        });
	}
	
	/** Update cell with QSO data
	 *  @param cell {@link ViewerCell} to update
	 *  @param qso {@link QSO} to show in cell
	 */
	abstract protected void update(final ViewerCell cell, final QSO qso);
}
