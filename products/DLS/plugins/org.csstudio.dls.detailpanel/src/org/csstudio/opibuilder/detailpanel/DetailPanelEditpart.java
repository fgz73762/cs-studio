package org.csstudio.opibuilder.detailpanel;

import java.util.LinkedList;

import org.csstudio.opibuilder.detailpanel.DetailPanelFigure;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.RGB;

public class DetailPanelEditpart extends AbstractContainerEditpart {

	// There should really be a row edit part.
	private LinkedList<DetailPanelEditpartRow> rows;
	
	/* Constructor */
	public DetailPanelEditpart() {
		rows = new LinkedList<DetailPanelEditpartRow>(); 
	}
	
	/* Create and initialise the figure*/
	@Override
	protected IFigure doCreateFigure() {
		DetailPanelFigure theFigure = new DetailPanelFigure();
		new DetailPanelDividerEditpart(this, theFigure.getVerticalDivider(), 
				/*horizontal=*/true, DetailPanelModel.PROP_VERT_DIVIDER_POS);
		return theFigure;
	}

	/* Get the right kind of model object */
	@Override
	public DetailPanelModel getWidgetModel() {
		return (DetailPanelModel)super.getWidgetModel();
	}
	
	/* Get the right kind of figure object */
	@Override
	public DetailPanelFigure getFigure() {
		return (DetailPanelFigure)super.getFigure();
	}
	
	/* A class that allows row property change handlers to be registered. */
	class RowPropertyChangeHandler implements IWidgetPropertyChangeHandler {
		protected int rowNumber;
		public RowPropertyChangeHandler(int rowNumber) {
			this.rowNumber = rowNumber;
		}
		public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
			return true;
		}
	}

	/* Property change handlers receive the notifications of property changes. */
	@Override
	protected void registerPropertyChangeHandlers() {
		// The main properties
		setPropertyChangeHandler(DetailPanelModel.PROP_ROW_COUNT, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setRows((Integer)newValue);
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_VERT_DIVIDER_POS, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setVerticalDividerPos((int)newValue);
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_EVEN_ROW_BACK, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setEvenRowBackgroundColor(((OPIColor)newValue).getRGBValue());
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_ODD_ROW_BACK, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setOddRowBackgroundColor(((OPIColor)newValue).getRGBValue());
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_EVEN_ROW_FORE, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setEvenRowForegroundColor(((OPIColor)newValue).getRGBValue());
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_COLOR_ODD_ROW_FORE, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setOddRowForegroundColor(((OPIColor)newValue).getRGBValue());
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_BORDER_COLOR, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setBorderColor(((OPIColor)newValue).getRGBValue());
				}
				return false;
			}
		});
		setPropertyChangeHandler(DetailPanelModel.PROP_DISPLAY_LEVEL, new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
				if(newValue != null) {
					DetailPanelEditpart.this.setShown(DetailPanelModel.DisplayLevel.values()[(int)newValue]);
				}
				return false;
			}
		});
		// The row properties
		for(int i=0; i<DetailPanelModel.MAX_ROW_COUNT; i++) {
			setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_MODE, i), new RowPropertyChangeHandler(i) {
				public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
					if(newValue != null) {
						DetailPanelEditpart.this.setRowMode(rowNumber, DetailPanelModelRow.Mode.values()[(int)newValue]);
					}
					return false;
				}
			});
			setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_NAME, i), new RowPropertyChangeHandler(i) {
				public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
					if(newValue != null) {
						DetailPanelEditpart.this.setRowName(rowNumber, (String)newValue);
					}
					return false;
				}
			});
			setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_HEIGHT, i), new RowPropertyChangeHandler(i) {
				public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
					if(newValue != null) {
						DetailPanelEditpart.this.setRowDividerPos(rowNumber, (int)newValue);
					}
					return false;
				}
			});
			setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_TOOLTIP, i), new RowPropertyChangeHandler(i) {
				public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
					if(newValue != null) {
						DetailPanelEditpart.this.setRowTooltip(rowNumber, (String)newValue);
					}
					return false;
				}
			});
			setPropertyChangeHandler(DetailPanelModelRow.makePropertyName(DetailPanelModelRow.PROP_ROW_LEVEL, i), new RowPropertyChangeHandler(i) {
				public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
					if(newValue != null) {
						DetailPanelEditpart.this.setRowLevel(rowNumber, DetailPanelModel.DisplayLevel.values()[(int)newValue]);
					}
					return false;
				}
			});
		}
	}

	/* The widget is being activated in the editor.  This function
	 * is called whenever a new figure is created.  We need to register
	 * any figure listeners and initialise the figure from the model data. */
	private int requiredHeight = 0;
	@Override
	public void activate() {
		// Base class
		super.activate();
		// Initialise the figure from the model
		setRows(getWidgetModel().getRowCount());
		getFigure().setOddRowBackgroundColor(getWidgetModel().getOddRowBackgroundColor());
		getFigure().setEvenRowBackgroundColor(getWidgetModel().getEvenRowBackgroundColor());
		getFigure().setOddRowForegroundColor(getWidgetModel().getOddRowForegroundColor());
		getFigure().setEvenRowForegroundColor(getWidgetModel().getEvenRowForegroundColor());
		getFigure().setVerticalDividerPos(getWidgetModel().getVerticalDividerPos());
		for(DetailPanelModelRow row: getWidgetModel().getRows()) {
			getFigure().setRowMode(row.getRowNumber(), row.getMode());
			getFigure().setRowName(row.getRowNumber(), row.getName());
			getFigure().setRowDividerPos(row.getRowNumber(), row.getHeight());
			getFigure().setRowLevel(row.getRowNumber(), row.getLevel());
		}
		// Set initial visibility
		getFigure().setEditMode(getExecutionMode() == ExecutionMode.EDIT_MODE);
		setAllGroupCollapse(true);
		setShown(getWidgetModel().getDisplayLevel());
		// Register a layout listener
		getFigure().addLayoutListener(new LayoutListener.Stub() {
			@Override
			public void postLayout(IFigure Container) {
				// Layout the model.  In the layout, when the vertical scroll bar is displayed we want
				// to reduce the width of the available display by the width of the scroll bar so that
				// we don't need a horizontal scroll bar.  To do this, we use the height arrived at by
				// the previous layout to predict whether the scroll bar will appear or not.  The success
				// of this relies on the fact that there are quite a few screen redraws whenever the
				// layout changes.
				requiredHeight = getWidgetModel().adjustLayout(getFigure().getDrawingArea(requiredHeight));
				// Tell the figure
				for(DetailPanelModelRow row: getWidgetModel().getRows()) {
					getFigure().setRowNameArea(row.getRowNumber(), row.getNameArea());
					getFigure().setVisibleRowNumber(row.getRowNumber(), row.getVisibleRowNumber());
				}
			}
		});
	}
	
	/* A row mode property has been changed */
	public synchronized void setRowMode(int rowNumber, DetailPanelModelRow.Mode mode) {
		getFigure().setRowMode(rowNumber, mode);
		setAllGroupCollapse(false);
	}
	
	/* A row level property has been changed */
	public synchronized void setRowLevel(int rowNumber, DetailPanelModel.DisplayLevel level) {
		getFigure().setRowLevel(rowNumber, level);
	}
	
	/* A row name property has been changed */
	public synchronized void setRowName(int rowNumber, String name) {
		getFigure().setRowName(rowNumber, name);
	}
	
	/* A row tooltip property has been changed */
	public synchronized void setRowTooltip(int rowNumber, String tip) {
		getFigure().setRowTooltip(rowNumber, tip);
	}
	
	/* The number of rows property has been changed */
	public synchronized void setRows(int numberOfRows) {
		getWidgetModel().setRows(numberOfRows);
		while(rows.size() > numberOfRows) {
			getFigure().removeRow();
			rows.getLast().dispose();
			rows.removeLast();
		}
		while(rows.size() < numberOfRows) {
			DetailPanelModelRow row = getWidgetModel().getRow(rows.size());
			getFigure().addRow();
			rows.add(new DetailPanelEditpartRow(this, getFigure().getRow(rows.size()), row));
			getFigure().setRowMode(row.getRowNumber(), row.getMode());
			getFigure().setRowName(row.getRowNumber(), row.getName());
			getFigure().setRowDividerPos(row.getRowNumber(), row.getHeight());
		}
		setAllGroupCollapse(false);
	}
	
	/* The vertical divider position has changed */
	public synchronized void setVerticalDividerPos(int pos) {
		getFigure().setVerticalDividerPos(pos);
	}
	
	/* A row divider position has changed */
	public synchronized void setRowDividerPos(int rowNumber, int pos) {
		getFigure().setRowDividerPos(rowNumber, pos);
	}
	
	/* The odd row background colour has changed. */
	public synchronized void setOddRowBackgroundColor(RGB color) {
		getWidgetModel().colorsChanged();
		getFigure().setOddRowBackgroundColor(color);
	}
	
	/* The even row background colour has changed. */
	public synchronized void setEvenRowBackgroundColor(RGB color) {
		getWidgetModel().colorsChanged();
		getFigure().setEvenRowBackgroundColor(color);
	}

	/* The odd row foreground colour has changed. */
	public synchronized void setOddRowForegroundColor(RGB color) {
		getFigure().setOddRowForegroundColor(color);
	}
	
	/* The even row foreground colour has changed. */
	public synchronized void setEvenRowForegroundColor(RGB color) {
		getFigure().setEvenRowForegroundColor(color);
	}

	/* The border colour has changed. */
	public synchronized void setBorderColor(RGB color) {
		getFigure().setBorderColor(color);
	}

	/* Not sure what this does */
	@Override
	protected final EditPart createChild(final Object model) {
		EditPart result = super.createChild(model);
		if (result instanceof AbstractBaseEditPart) {
			((AbstractBaseEditPart) result).setSelectable(false);
		}
		return result;
	}

	/* Not sure what this does */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
	}

	/* Clean up the figure. */
	@Override
	public void deactivate() {
		getFigure().dispose();
		super.deactivate();
	}

	@Override
	public IFigure getContentPane() {
		return getFigure().getContentPane();
	}
	
	/* Set the collapse state of all the row.  Set the initialState flag
	 * to true when the widget is being activated the first time. */
	public void setAllGroupCollapse(boolean initialState) {
		boolean collapse = false;
		for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
			DetailPanelEditpartRow row = rows.get(rowIndex);
			if(row.isGroup()) {
				collapse = row.getGroupCollapse();
				if(initialState && row.isCollapsedGroup()) {
					getFigure().setGroupCollapse(rowIndex, true);
					collapse = true;
				}
			} else {
				row.setCollapse(collapse);
				getWidgetModel().setCollapse(rowIndex, collapse);
			}
		}
	}

	/* Set the collapse state of the group and change the display state
	 * of the group's rows */
	public void setGroupCollapse(int groupRowNumber, boolean collapse) {
		// Change the group collapse state
		getFigure().setGroupCollapse(groupRowNumber, collapse);
		// Show or hide the group's rows
		int rowIndex = groupRowNumber;
		boolean going = true; 
		while(going) {
			rowIndex++;
			if(rowIndex >= rows.size()) {
				going = false;
			} else {
				DetailPanelEditpartRow row = rows.get(rowIndex);
				if(row.isGroup()) {
					going = false;
				} else {
					row.setCollapse(collapse);
					getWidgetModel().setCollapse(rowIndex, collapse);
				}
			}
		}
	}
	
	/* Set the shown state of the rows. */
	public void setShown(DetailPanelModel.DisplayLevel displayLevel) {
		DetailPanelModel.DisplayLevel groupLevel = DetailPanelModel.DisplayLevel.LOW;
		// For each row...
		for(int rowIndex=0; rowIndex<rows.size(); rowIndex++) {
			DetailPanelEditpartRow row = rows.get(rowIndex);
			// Get the level of this row
			DetailPanelModel.DisplayLevel rowLevel = getFigure().getRowLevel(rowIndex);
			// Record the level of the prevailing group
			if(row.isGroup()) {
				groupLevel = rowLevel;
			}
			// The level of this row cannot be less than its containing group
			if(rowLevel.ordinal() < groupLevel.ordinal()) {
				rowLevel = groupLevel;
			}
			// Show the row if its level is less than or equal to the display level
			boolean showRow = rowLevel.ordinal() <= displayLevel.ordinal(); 
			row.setShown(showRow);
			getWidgetModel().setShown(rowIndex, showRow);
		}
	}
	
	/* Move a row.*/
	public void moveRow(int oldIndex, int newIndex) {
		if(oldIndex > newIndex) {
			for(int i=newIndex; i<oldIndex; i++) {
				getWidgetModel().swapRowProperties(oldIndex,  i);
			}
		} else if(oldIndex < newIndex) {
			for(int i=newIndex-1; i>oldIndex; i--) {
				getWidgetModel().swapRowProperties(oldIndex,  i);
			}
		}
	}
}
