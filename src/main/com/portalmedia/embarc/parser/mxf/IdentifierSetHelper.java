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
		List<String> valList = DelimitedListCodec.splitOnDelimiterRun(values, slash.charAt(0), slash.length());
		for (String v : valList) {
			if (!v.isEmpty()) idents.add(createIdentifierFromString(v));
		}
		return idents;
	}

	public AS07DMSIdentifierSetImpl createIdentifierFromString(String values) {
		List<String> valList = DelimitedListCodec.splitOnDelimiterRun(values, comma.charAt(0), comma.length());
		AS07DMSIdentifierSetImpl ident = new AS07DMSIdentifierSetImpl();
		if (valList.size() > 0) ident.setIdentifierValue(DelimitedListCodec.unescapeField(valList.get(0)));
		if (valList.size() > 1) ident.setIdentifierRole(DelimitedListCodec.unescapeField(valList.get(1)));
		if (valList.size() > 2) ident.setIdentifierType(DelimitedListCodec.unescapeField(valList.get(2)));
		if (valList.size() > 3) ident.setIdentifierComment(DelimitedListCodec.unescapeField(valList.get(3)));
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

		try { val = id.getIdentifierValue(); } catch (PropertyNotPresentException ex) {}
		try { role = id.getIdentifierRole(); } catch (PropertyNotPresentException ex) {}
		try { type = id.getIdentifierType(); } catch (PropertyNotPresentException ex) {}
		try { comm = id.getIdentifierComment(); } catch (PropertyNotPresentException ex) {}

		return DelimitedListCodec.escapeField(val) + comma + DelimitedListCodec.escapeField(role) + comma
				+ DelimitedListCodec.escapeField(type) + comma + DelimitedListCodec.escapeField(comm);
	}
}
