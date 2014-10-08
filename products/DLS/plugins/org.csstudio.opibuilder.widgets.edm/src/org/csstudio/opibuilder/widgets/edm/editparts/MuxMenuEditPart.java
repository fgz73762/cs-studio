package org.csstudio.opibuilder.widgets.edm.editparts;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.edm.figures.MuxMenuFigure;
import org.csstudio.opibuilder.widgets.edm.model.MuxMenuModel;
import org.csstudio.simplepv.IPV;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.csstudio.opibuilder.scriptUtil.PVUtil;

/**The editpart of a muxMenu.
 *
 * This is based on ComboEditPart by Xihui Chen
 * @author Nick Battam
 *
 */
public final class MuxMenuEditPart extends AbstractPVWidgetEditPart {

	private IPV monitoredPV;
	private Combo combo;
	private SelectionListener comboSelectionListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final MuxMenuModel model = getWidgetModel();

		if (model == null) {
			System.err.println("NULL model");
			
		} else {
			System.err.println("Got model");
		}

		MuxMenuFigure comboFigure = new MuxMenuFigure(this);
		
		combo = comboFigure.getSWTWidget();
		if (combo == null) {
			System.err.println("NULL COMBO");
			
		} else {
			System.err.println("Got combo");
		}

		List<String> items = model.getItems();
		if (items == null) {
			System.err.println("NULL ITEMS");
			
		} else {
			System.err.println("Got items");
		}
		
		if(comboSelectionListener !=null)
			combo.removeSelectionListener(comboSelectionListener);
		
		comboSelectionListener = new MuxMenuSelectionListener();
		combo.addSelectionListener(comboSelectionListener);

		updateCombo(items);

		String initialState = model.getInitialState();
		if (initialState == null) {
			System.err.println("NULL initialState");
		}
		else {
			System.err.println("Got initialState: " + initialState);
		}
		setInitialSelection(initialState);

		return comboFigure;
	}

	private void setInitialSelection(String initialState) {
		if (combo.getItemCount() > 0) {
			// Default selection is the first element
			combo.select(0);
			if (initialState != null && !initialState.isEmpty()) {
				try {
					int selectedIndex = Integer.parseInt(initialState);
					combo.select(selectedIndex);
				}
				catch (NumberFormatException ex) {
					System.err.println("Invalid initial state: " + initialState);
				}
			}
			// force a selection change event to set the associated loc:// pv
			comboSelectionListener.widgetSelected(null);
		}
	}

	private class MuxMenuSelectionListener extends SelectionAdapter {
		/// Selection change handler for the MenuMux Combobox
		@Override
		public void widgetSelected(SelectionEvent e) {
			/// On selected change put the selected PV name to the associated local pv (e.g. $d)
			MuxMenuModel model = getWidgetModel();

			int selectedIdx = combo.getSelectionIndex();
			List<String> targets = model.getTargets();
			List<String> values = model.getValues();

			if (selectedIdx < targets.size() && selectedIdx < values.size()) {
				String macro_pv = "loc://" + targets.get(selectedIdx);
				String target_pv = values.get(selectedIdx);
				System.out.println("Setting " + macro_pv + " to " + target_pv);
				PVUtil.writePV(macro_pv, target_pv);
			}
		}
	}

	/**
	 * @param items
	 */
	private void updateCombo(List<String> items) {
		if(items !=null && getExecutionMode() == ExecutionMode.RUN_MODE){
			combo.removeAll();

			for(String item : items){
				combo.add(item);
			}
		}
	}

	@Override
	public MuxMenuModel getWidgetModel() {
		return (MuxMenuModel)getModel();
	}
	
	@Override
	protected void doDeActivate() {
		super.doDeActivate();

		if (monitoredPV != null) {
			monitoredPV.stop();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		

		autoSizeWidget((MuxMenuFigure) getFigure());

		// Items
		IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null && newValue instanceof List){
					updateCombo((List<String>)newValue);
				}
				return true;
			}
		};
		setPropertyChangeHandler(MuxMenuModel.PROP_ITEMS, itemsHandler);


		//size change handlers--always apply the default height
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				autoSizeWidget((MuxMenuFigure)figure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handle);
		setPropertyChangeHandler(MuxMenuModel.PROP_FONT, handle);
	}

	private void autoSizeWidget(MuxMenuFigure comboFigure) {
		Dimension d = comboFigure.getAutoSizeDimension();
		getWidgetModel().setSize(getWidgetModel().getWidth(), d.height);
	}

	@Override
	public String getValue() {
		return combo.getText();
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof String)
			combo.setText((String) value);
		else if (value instanceof Number)
			combo.select(((Number)value).intValue());
		else
			super.setValue(value);
	}
	
}
