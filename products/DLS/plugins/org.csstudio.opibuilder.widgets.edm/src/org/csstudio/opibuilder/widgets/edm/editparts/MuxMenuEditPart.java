package org.csstudio.opibuilder.widgets.edm.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
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
import org.csstudio.simplepv.VTypeHelper;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.epics.vtype.VEnum;
import org.epics.vtype.VType;

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
		
		updatePropSheet(model.isItemsFromPV());
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

		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);

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
	protected void doActivate() {
		super.doActivate();
		registerLoadItemsListener();
	}

	/**
	 *
	 */
	private void registerLoadItemsListener() {
		//load items from PV
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			if(getWidgetModel().isItemsFromPV()){
				IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){
					if(loadItemsFromPVListener == null)
						loadItemsFromPVListener = new IPVListener.Stub() {
							@Override
							public void valueChanged(IPV pv) {
								VType value = pv.getValue();
								if (value != null && value instanceof VEnum){
									List<String> items = ((VEnum)value).getLabels();
										getWidgetModel().setPropertyValue(MuxMenuModel.PROP_ITEMS, items);
									}
							}
						};
					pv.addListener(loadItemsFromPVListener);
				}
			}
		}
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
//		((ComboFigure)getFigure()).dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				registerLoadItemsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);


		autoSizeWidget((MuxMenuFigure) getFigure());
		// PV_Value
		IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null){
					String stringValue = VTypeHelper.getString((VType)newValue);
					if(Arrays.asList(combo.getItems()).contains(stringValue))
						combo.setText(stringValue);
					else
						combo.deselectAll();
//
//					if(getWidgetModel().isBorderAlarmSensitve())
//							autoSizeWidget((ComboFigure) refreshableFigure);
				}

				return true;
			}
		};
		setPropertyChangeHandler(MuxMenuModel.PROP_PVVALUE, pvhandler);

		// Items
		IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null && newValue instanceof List){
					updateCombo((List<String>)newValue);
					if(getWidgetModel().isItemsFromPV())
						combo.setText(VTypeHelper.getString(getPVValue(AbstractPVWidgetModel.PROP_PVNAME)));
				}
				return true;
			}
		};
		setPropertyChangeHandler(MuxMenuModel.PROP_ITEMS, itemsHandler);

		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			@Override
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				updatePropSheet((Boolean) newValue);
				return false;
			}
		};
		getWidgetModel().getProperty(MuxMenuModel.PROP_ITEMS_FROM_PV).
			addPropertyChangeListener(new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
		});


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

		/**
		* @param actionsFromPV
		*/
	private void updatePropSheet(final boolean itemsFromPV) {
		getWidgetModel().setPropertyVisible(
				MuxMenuModel.PROP_ITEMS, !itemsFromPV);
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
