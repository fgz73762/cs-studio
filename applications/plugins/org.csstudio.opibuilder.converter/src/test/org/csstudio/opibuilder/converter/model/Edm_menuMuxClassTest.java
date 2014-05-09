package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class Edm_menuMuxClassTest extends TestCase {

	private final String edlFile;
	private Edm_menuMuxClass mux;

	public Edm_menuMuxClassTest() {
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
	}

	public void test_menuMuxClass_builds_object() throws EdmException {

		EdmModel.getInstance();
		EdmDisplay display = EdmModel.getDisplay(edlFile);
		//this entity represents activeRectClass in example file
		EdmEntity entity = display.getSubEntity(0);
		assertTrue(entity instanceof Edm_menuMuxClass);
	}

	public void test_menuMuxClass_contains_baseWidget_fields() throws EdmException {

		EdmAttribute fontAttr = new EdmAttribute("arial-medium-r-12.0");
		EdmFont expectedFont = new EdmFont(fontAttr, true);
		assertEquals(expectedFont.getName(), mux.getFont().getName());
		assertEquals(expectedFont.getSize(), mux.getFont().getSize());
		assertEquals(expectedFont.isBold(), mux.getFont().isBold());

		assertEquals(100, mux.getX());
		assertEquals(24, mux.getY());

		assertEquals(72, mux.getW());
		assertEquals(24, mux.getH());

		EdmColor expectedFgColour = new EdmColor(new EdmAttribute("index 14"), false);
		assertEquals(expectedFgColour.getRed(), mux.getFgColor().getRed());
		assertEquals(expectedFgColour.getGreen(), mux.getFgColor().getGreen());
		assertEquals(expectedFgColour.getBlue(), mux.getFgColor().getBlue());

		EdmColor expectedBgColour = new EdmColor(new EdmAttribute("index 4"), false);
		assertEquals(expectedBgColour.getRed(), mux.getBgColor().getRed());
		assertEquals(expectedBgColour.getGreen(), mux.getBgColor().getGreen());
		assertEquals(expectedBgColour.getBlue(), mux.getBgColor().getBlue());
	}

	public void test_menuMuxClass_contains_numItems() throws EdmException {

		assertEquals(mux.getNumItems(), 3);
	}

	public void test_menuMuxClass_contains_list_of_symbolTag() throws EdmException {

		EdmMultiStrings tags = mux.getSymbolTags();
		assertEquals(tags.getValueCount(), 3);

		String[] expectedTags = new String[]{ "0 \"volts\"", "1 \"dBm\"", "2 \"watts\"" };

		for (int index = 0; index < tags.getValueCount(); index++) {
			assertEquals(expectedTags[index], tags.getValue(index));
		}
	}

	public void test_menuMuxClass_contains_list_of_symbol0() throws EdmException {

		EdmMultiStrings tags = mux.getSymbolZero();
		assertEquals(tags.getValueCount(), 3);

		String[] expectedSymbols = new String[]{ "0 \"d\"", "1 \"e\"", "2 \"f\"" };

		for (int index = 0; index < tags.getValueCount(); index++) {
			assertEquals(expectedSymbols[index], tags.getValue(index));
		}
	}

}
