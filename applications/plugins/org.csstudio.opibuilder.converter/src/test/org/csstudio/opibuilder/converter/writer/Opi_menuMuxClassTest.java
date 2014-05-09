package org.csstudio.opibuilder.converter.writer;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmFont;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.csstudio.opibuilder.converter.model.Edm_menuMuxClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class Opi_menuMuxClassTest extends TestCase {

	private final String edlFile;
	private Edm_menuMuxClass mux;
	private Context context;
	private Document doc;
	private Opi_menuMuxClass opi_menuMux;
	Element generatedElement;

	public Opi_menuMuxClassTest() {
		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		edlFile = "src/test/resources/menu_mux.edl";
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		EdmModel.getInstance();
		EdmDisplay display = EdmModel.getDisplay(edlFile);
		EdmEntity entity = display.getSubEntity(0);
		mux = (entity instanceof Edm_menuMuxClass) ? (Edm_menuMuxClass)entity : null;


		doc = XMLFileHandler.createDomDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);

		context = new Context(doc, root, display, 0, 0);

		opi_menuMux = new Opi_menuMuxClass(context, mux);

		generatedElement = (Element)doc.getElementsByTagName("widget").item(0);
	}

	public void testOpi_TextupdateClass_creates_ComboBox() throws EdmException {
		Opi_menuMuxClass o = new Opi_menuMuxClass(context, mux);
		assertTrue(o instanceof OpiWidget);

		Element e = (Element)doc.getElementsByTagName("widget").item(0);
		assertEquals("org.csstudio.opibuilder.widgets.ComboBox", e.getAttribute("typeId"));
		assertEquals("1.0", e.getAttribute("version"));
		XMLFileHandler.isElementEqual("EDM MenuMux", "name", e);
	}

	public void test_sets_common_widget_properties() throws EdmException {

		XMLFileHandler.writeXML(doc);

		XMLFileHandler.isElementEqual("100", "x", generatedElement);
		XMLFileHandler.isElementEqual("24", "y", generatedElement);
		XMLFileHandler.isElementEqual("73", "width", generatedElement);
		XMLFileHandler.isElementEqual("25", "height", generatedElement);

		XMLFileHandler.isColorElementEqual(new EdmColor(14), "foreground_color", generatedElement);
		XMLFileHandler.isColorElementEqual(new EdmColor(4), "background_color", generatedElement);

		XMLFileHandler.isFontElementEqual("arial-medium-r-12.0", "font", generatedElement);
	}

	public void test_sets_symbolTag_as_items() throws EdmException {

		String[] expectedItems = new String[] {"volts", "dBm", "watts"};
		XMLFileHandler.isListElementEqual(expectedItems, "items", generatedElement);
	}
}
