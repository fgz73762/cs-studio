package org.csstudio.opibuilder.widgets.edm.editparts;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
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
import org.csstudio.opibuilder.scriptUtil.PVUtil;

/**The editpart of a muxMenu.
 *
 * This is based on ComboEditPart by Xihui Chen
 * @author Nick Battam
 *
 */
public final class MuxMenuEditPart extends AbstractPVWidgetEditPart {

	private IPVListener monitorListener;
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

		List<String> items = getWidgetModel().getItems();
		if (items == null) {
			System.err.println("NULL ITEMS");
			
		} else {
			System.err.println("Got items");
		}
		
		if(comboSelectionListener !=null)
			combo.removeSelectionListener(comboSelectionListener);
		
		comboSelectionListener = new MuxMenuSelectionListener(this);
		combo.addSelectionListener(comboSelectionListener);

		updateCombo(items);
		
		return comboFigure;
	}
	
	private class MuxMenuSelectionListener extends SelectionAdapter {
		private AbstractBaseEditPart parent;
		
		public MuxMenuSelectionListener(AbstractBaseEditPart parent) {
			this.parent = parent;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {

			MuxMenuModel model = getWidgetModel();

			int selectedIdx = combo.getSelectionIndex();
			List<String> targets = (List<String>)model.getPropertyValue(MuxMenuModel.PROP_TARGETS);
			List<String> values = (List<String>)model.getPropertyValue(MuxMenuModel.PROP_VALUES);

			if (monitoredPV != null) {
				monitoredPV.removeListener(monitorListener);
				monitoredPV.stop();
			}
			// Create a monitored PV
			try {
				monitoredPV = PVUtil.createPV(values.get(selectedIdx), parent);
				monitorListener = new PVPipedListener("loc://" + targets.get(selectedIdx));
				monitoredPV.addListener(monitorListener);
			}
			catch (Exception ex) {
				// do nothing
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
	
	private final class PVPipedListener extends IPVListener.Stub {		

		private String targetPV;

		public PVPipedListener(String targetPV) {
			System.out.println("Creating listenter for " + targetPV);
			this.targetPV = targetPV;
		}

		@Override
		public void valueChanged(IPV pv) {
			/// On value change events, push the monitored PV value
			/// to the target.
			Object val = pv.getValue();
			if (val != null) {
				System.out.println("Pushing " + val + " to " + targetPV);
				PVUtil.writePV(targetPV, pv.getValue());
			}
		}
	}

}
