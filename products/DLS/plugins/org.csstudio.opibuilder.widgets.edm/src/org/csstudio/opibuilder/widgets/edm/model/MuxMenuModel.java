package org.csstudio.opibuilder.widgets.edm.model;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.model.ComboModel;
import org.eclipse.swt.graphics.RGB;

public class MuxMenuModel extends ComboModel {


	public final String ID = "org.csstudio.opibuilder.widgets.edm.muxmenuwidget";//$NON-NLS-1$
	/**
	 * Items of the combo.
	 */
	public static final String PROP_ITEMS = "items";//$NON-NLS-1$
	public static final String PROP_VALUES = "values";//$NON-NLS-1$
	public static final String PROP_ZEROS = "zeros";//$NON-NLS-1$

	/**
	 * True if items are read from the input PV which must be an Enum PV.
	 */
	public static final String PROP_ITEMS_FROM_PV = "items_from_pv";//$NON-NLS-1$

	public MuxMenuModel() {
		setBackgroundColor(new RGB(255,255,255));
		setForegroundColor(new RGB(0,0,0));
		setScaleOptions(true, false, false);
	}

	@Override
	protected void configureProperties() {
		addProperty(new StringListProperty(
				PROP_ITEMS, "Items", WidgetPropertyCategory.Behavior, new ArrayList<String>()));

		addProperty(new StringListProperty(
				PROP_ITEMS, "Values", WidgetPropertyCategory.Behavior, new ArrayList<String>()));

		addProperty(new StringListProperty(
				PROP_ITEMS, "Zeros", WidgetPropertyCategory.Behavior, new ArrayList<String>()));

		addProperty(new BooleanProperty(
				PROP_ITEMS_FROM_PV, "Items From PV", WidgetPropertyCategory.Behavior, false));
	}

	@SuppressWarnings("unchecked")
	public List<String> getValues(){
		return (List<String>)getPropertyValue(PROP_VALUES);
	}

	@SuppressWarnings("unchecked")
	public List<String> getZeros(){
		return (List<String>)getPropertyValue(PROP_ZEROS);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getItems(){
		return (List<String>)getPropertyValue(PROP_ITEMS);
	}

	@Override
	public boolean isItemsFromPV(){
		return (Boolean)getPropertyValue(PROP_ITEMS_FROM_PV);
	}


	@Override
	public String getTypeID() {
		return ID;
	}
}
