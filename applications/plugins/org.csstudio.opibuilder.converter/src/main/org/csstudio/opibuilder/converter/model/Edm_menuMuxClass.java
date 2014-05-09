package org.csstudio.opibuilder.converter.model;

/**EDM Menu Mux widget
 * @author Xihui Chen
 *
 */
public class Edm_menuMuxClass extends EdmWidget {

	@EdmAttributeAn private int numItems;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbolTag;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings symbol0;
	@EdmAttributeAn @EdmOptionalAn private EdmMultiStrings value0;

	public Edm_menuMuxClass(EdmEntity genericEntity) throws EdmException {
		super(genericEntity);
	}

	public int getNumItems() {
		return numItems;
	}

	public EdmMultiStrings getSymbolTags() {
		return symbolTag;
	}

	public EdmMultiStrings getSymbolZero() {
		return symbol0;
	}

	public EdmMultiStrings getValueZero() {
		return value0;
	}
}
