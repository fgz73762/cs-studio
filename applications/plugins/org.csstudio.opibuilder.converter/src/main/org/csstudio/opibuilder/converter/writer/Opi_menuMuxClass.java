package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmMultiStrings;
import org.csstudio.opibuilder.converter.model.Edm_menuMuxClass;
import org.w3c.dom.Element;

public class Opi_menuMuxClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_menuMuxClass");
	private static final String typeId = "MuxMenu";
	private static final String name = "EDM MenuMux";
	private static final String version = "1.0";


	/**
	 * Converts the Edm_menuMuxClass to OPI combobox widget XML.
	 */
	public Opi_menuMuxClass(Context con, Edm_menuMuxClass r) {
		super(con, r);

		setTypeId(typeId);
		setName(name);
		setVersion(version);

		Element widget = (Element)con.getDocument().getElementsByTagName("widget").item(0);
		Element itemsElement = con.getDocument().createElement("items");
		widget.appendChild(itemsElement);

		EdmMultiStrings edmSymbols = r.getSymbolTags();

		for (int index = 0; index < r.getNumItems(); index++)
		{
			String symbol = edmSymbols.getValue(index).split("\"")[1];
			Element itemElement = con.getDocument().createElement("s");
			itemElement.appendChild(con.getDocument().createTextNode(symbol));
			itemsElement.appendChild(itemElement);
		}

		Element valuesElement = con.getDocument().createElement("values");
		widget.appendChild(valuesElement);
		EdmMultiStrings edmZero = r.getValueZero();

		for (int index = 0; index < r.getNumItems(); index++)
		{
			String symbol = edmZero.getValue(index).split("\"")[1];
			Element valueElement = con.getDocument().createElement("v");
			valueElement.appendChild(con.getDocument().createTextNode(symbol));
			valuesElement.appendChild(valueElement);
		}

		Element zerosElement = con.getDocument().createElement("zeros");
		widget.appendChild(zerosElement);
		EdmMultiStrings edmVals = r.getSymbolZero();

		for (int index = 0; index < r.getNumItems(); index++)
		{
			String symbol = edmVals.getValue(index).split("\"")[1];
			Element zeroElement = con.getDocument().createElement("z");
			zeroElement.appendChild(con.getDocument().createTextNode(symbol));
			zerosElement.appendChild(zeroElement);
		}

//		Element actionsElement = con.getDocument().createElement("actions");
//		actionsElement.setAttribute("hook", "False");
//		actionsElement.setAttribute("hook_all", "False");
//		
//		Element actionElement = con.getDocument().createElement("action");
//		actionElement.setAttribute("type", "OPEN_DISPLAY");
//		actionsElement.appendChild(actionElement);
//
//		Element pathElement = con.getDocument().createElement("path");
//		pathElement.appendChild(con.getDocument().createTextNode("tbiPATH"));
//		actionElement.appendChild(pathElement);
//	
//		Element macrosElement = con.getDocument().createElement("macros");
//		Element parentMacrosElement = con.getDocument().createElement("include_parent_macros");
//		parentMacrosElement.appendChild(con.getDocument().createTextNode("true"));
//		macrosElement.appendChild(parentMacrosElement);
//		actionElement.appendChild(macrosElement);
//				
//		Element replaceElement = con.getDocument().createElement("replace");
//		replaceElement.appendChild(con.getDocument().createTextNode("1"));
//		actionElement.appendChild(replaceElement);
//		
//		Element descriptionElement = con.getDocument().createElement("description");
//		descriptionElement.appendChild(con.getDocument().createTextNode("Reload page on MuxMenu change"));
//		actionElement.appendChild(descriptionElement);
//
//		widget.appendChild(actionsElement);

		log.debug("Edm_menuMuxClass written.");
	}

}
