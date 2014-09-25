/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.opibuilder.converter.model.EdmDouble;
import org.csstudio.opibuilder.converter.model.Edm_activeSymbolClass;
import org.w3c.dom.Element;

/**
 * XML conversion class for Edm_activeSymbolClass
 * 
 * @author Xihui Chen
 */
public class Opi_activeSymbolClass extends OpiWidget {

	private static final String typeId = "edm.symbolwidget";
	private static final String name = "EDM Symbol";
	private static final String version = "1.0";
	
	
	private static HashMap<String, Integer> pngSizes = new HashMap<String, Integer>();
	
	static {
		pngSizes.put("vacuumValve-symbol.edl", Integer.valueOf(34));
		pngSizes.put("mks937aPirg-symbol.edl", Integer.valueOf(34));
		pngSizes.put("mks937aImg-symbol.edl", Integer.valueOf(34));
		pngSizes.put("digitelMpcIonp-symbol.edl", Integer.valueOf(34));
		pngSizes.put("digitelMpcTsp-symbol.edl", Integer.valueOf(34));
		pngSizes.put("rga-symbol.edl", Integer.valueOf(34));
		pngSizes.put("absorber-symbol.edl", Integer.valueOf(18));
		pngSizes.put("MPStempabs.edl", Integer.valueOf(21));
		pngSizes.put("linev.edl", Integer.valueOf(21));
		pngSizes.put("mirror.edl", Integer.valueOf(91));
		pngSizes.put("absvw.edl", Integer.valueOf(101));
		pngSizes.put("shuttersvw.edl", Integer.valueOf(26));
		pngSizes.put("valvesvw.edl", Integer.valueOf(101));
		pngSizes.put("fvalve.edl", Integer.valueOf(101));
		pngSizes.put("leds.edl", Integer.valueOf(22));
	}

	/**
	 * Converts the Edm_activeSymbolClass to OPI symbol widget XML.
	 */
	public Opi_activeSymbolClass(Context con, Edm_activeSymbolClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);

		new OpiInt(widgetContext, "border_style", 0);
		new OpiInt(widgetContext, "symbol_number", 0);
		
		String s = r.getFile();

		if (r.getFile() != null) {
			int lastSlash = s.lastIndexOf('/');
			String basename = s.substring(lastSlash + 1, s.length());
			if (pngSizes.containsKey(basename)) {
				int width = pngSizes.get(basename);
				new OpiString(widgetContext, "image_file", convertToPng(r.getFile(), width));
				new OpiInt(widgetContext, "sub_image_width", width);
			} else {
				System.out.println("No size for symbol " + s + " basename " + basename);
			}
		}	
		
		//single pv, no truth table
		if (!r.isTruthTable() && r.getNumPvs() == 1 && r.getControlPvs()!=null) {
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();

			// Handle invalid values
			Element invalidNode = widgetContext.getDocument().createElement("value");
			invalidNode.setTextContent("0");
			expressions.put("PVUtil.getSeverity(pvs[0]) == -1", invalidNode);
			
			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0;
				double max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				expressions.put("pv0>=" + min + "&&pv0<" + max, valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);

			new OpiRule(widgetContext, "symbol_single_pv", "pv_value", true, Arrays.asList(
					convertPVName(r.getControlPvs().getEdmAttributesMap().get("0").get())), expressions);
		}
		/*
		}else if(r.isTruthTable() && r.getNumPvs() >0 && r.getControlPvs()!=null){ //binary truth table
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();
			StringBuilder pvsb = new StringBuilder("(");
			for(int i=0; i<r.getNumPvs(); i++){
				if(i!=0)
					pvsb.append("+");
				pvsb.append("(pv").append(i).append("==0?0:1)*").append(Math.pow(2, i));				
			}
			pvsb.append(")");
			String pvs = pvsb.toString();
			
			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0, max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				StringBuilder sb = new StringBuilder();
				sb.append(pvs).append(">=").append(min).append("&&").append(pvs).append("<").append(max);				
				expressions.put(sb.toString(), valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);
			
			List<String> pvnames = new ArrayList<String>();
			for(int i=0; i<r.getNumPvs(); i++){
				pvnames.add(convertPVName(
						r.getControlPvs().getEdmAttributesMap().get(""+i).get()));
			}
			new OpiRule(widgetContext, "symbol_binary_truth_table", "group_name", false, pvnames, expressions);
		}else if(!r.isTruthTable() && r.getNumPvs()>1 && r.getControlPvs()!=null){ //multiple bit fields, no binary truth table
			LinkedHashMap<String, Element> expressions = new LinkedHashMap<String, Element>();
			Map<String, EdmDouble> minMap = r.getMinValues().getEdmAttributesMap();
			Map<String, EdmDouble> maxMap = r.getMaxValues().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> andMap = r.getAndMask().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> xorMap = r.getXorMask().getEdmAttributesMap();
			LinkedHashMap<String, EdmInt> shiftMap = r.getShiftCount().getEdmAttributesMap();

			StringBuilder pvsb = new StringBuilder("(");
			for(int i=0; i<r.getNumPvs(); i++){
				if(i!=0)
					pvsb.append("|");
				int andMask=0, xorMask =0, shiftCount=0;
				String key = ""+i;
				if(andMap.containsKey(key)){
					andMask = andMap.get(key).get();
				}
				if(xorMap.containsKey(key)){
					xorMask = xorMap.get(key).get();
				}
				if(shiftMap.containsKey(key)){
					shiftCount = shiftMap.get(key).get();
				}				
				pvsb.append("(pvInt").append(i).append('&').append(andMask).append('^').append(xorMask).append("<<").append(shiftCount).append(')');				
			}
			pvsb.append(")");
			String pvs = pvsb.toString();
			
			for (int i = 0; i < r.getNumStates(); i++) {
				Element valueNode = widgetContext.getDocument().createElement("value");
				double min = 0, max = 0;
				if (minMap.get("" + i) != null)
					min = minMap.get("" + i).get();
				if (maxMap.get("" + i) != null)
					max = maxMap.get("" + i).get();
				valueNode.setTextContent("" + i);
				StringBuilder sb = new StringBuilder();
				sb.append(pvs).append(">=").append(min).append("&&").append(pvs).append("<").append(max);				
				expressions.put(sb.toString(), valueNode);
			}
			Element valueNode = widgetContext.getDocument().createElement("value");
			valueNode.setTextContent("0");
			expressions.put("true", valueNode);
			
			List<String> pvnames = new ArrayList<String>();
			for(int i=0; i<r.getNumPvs(); i++){
				pvnames.add(convertPVName(
						r.getControlPvs().getEdmAttributesMap().get(""+i).get()));
			}
			new OpiRule(widgetContext, "symbol_multi_pvs", "group_name", false, pvnames, expressions);
		
		}
		*/

	}
	
	public static  String convertToPng(String originPath, int width) {
	    if (originPath.endsWith(".edl")) {
	        originPath = originPath.replace(".edl", "-" + width + ".png");
	    } else {
	        originPath = originPath + "-" + width + ".png";
	    }
	  return originPath;
	}
	
}
