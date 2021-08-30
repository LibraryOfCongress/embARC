package com.portalmedia.embarc.parser.mxf;

import java.util.ArrayList;
import java.util.List;

import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

public class IdentifierSetHelper {
	private String comma = ",,,,";
	private String slash = "////";

	public ArrayList<AS07DMSIdentifierSetImpl> createIdentifierListFromString(String values) {
		ArrayList<AS07DMSIdentifierSetImpl> idents = new ArrayList<AS07DMSIdentifierSetImpl>();
		String[] valList = values.split(slash);
		for (String v : valList) {
			if (v != "") idents.add(createIdentifierFromString(v));
		}
		return idents;
	}

	public AS07DMSIdentifierSetImpl createIdentifierFromString(String values) {
		String[] valList = values.split(comma);
		AS07DMSIdentifierSetImpl ident = new AS07DMSIdentifierSetImpl();
		if (valList.length > 0) ident.setIdentifierValue(valList[0]);
		if (valList.length > 1) ident.setIdentifierRole(valList[1]);
		if (valList.length > 2) ident.setIdentifierType(valList[2]);
		if (valList.length > 3) ident.setIdentifierComment(valList[3]);
		return ident;
	}
	
	public String identifiersToString(List<AS07DMSIdentifierSetImpl> identifiers) {
		String toReturn =  "";
		for (int i = 0; i < identifiers.size(); i++) {
			AS07DMSIdentifierSetImpl ident = identifiers.get(i);
			toReturn += identifierToString(ident);
			if (i < identifiers.size() - 1) {
				toReturn += slash;
			}
		}
		return toReturn;
	}

	public String identifierToString(AS07DMSIdentifierSetImpl id) {
		String val = null;
		String role = null;
		String type = null;
		String comm = null;

		try {
			val = id.getIdentifierValue();
			role = id.getIdentifierRole();
			type = id.getIdentifierType();
			comm = id.getIdentifierComment();
		} catch (PropertyNotPresentException ex) {}

		return val + comma + role + comma + type + comma + comm;
	}
}
