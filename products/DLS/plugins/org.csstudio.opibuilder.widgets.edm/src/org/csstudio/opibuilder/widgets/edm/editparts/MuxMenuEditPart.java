package org.csstudio.opibuilder.widgets.edm.editparts;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.edm.figures.MuxMenuFigure;
import org.csstudio.opibuilder.widgets.edm.model.MuxMenuModel;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/**The editpart of a muxMenu.
 *
 * This is based on ComboEditPart by Xihui Chen
 * @author Nick Battam
 *
 */
public final class MuxMenuEditPart extends AbstractPVWidgetEditPart {

	private IPVListener loadItemsFromPVListener;

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

		List<String> items = getWidgetModel().getItems();
		if (items == null) {
			System.err.println("NULL ITEMS");
			
		} else {
			System.err.println("Got items");
		}
		updateCombo(items);
		return comboFigure;
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

			//write value to pv if pv name is not empty
			if(getWidgetModel().getPVName().trim().length() > 0){
				if(comboSelectionListener !=null)
					combo.removeSelectionListener(comboSelectionListener);
				comboSelectionListener = new SelectionAdapter(){
						@Override
						public void widgetSelected(SelectionEvent e) {
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, combo.getText());
						}
				};
				combo.addSelectionListener(comboSelectionListener);
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
		if(getWidgetModel().isItemsFromPV()){
			IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null && loadItemsFromPVListener !=null){
				pv.removeListener(loadItemsFromPVListener);
			}
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
