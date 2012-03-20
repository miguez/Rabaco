package com.telventi.afirma.mschema.saml.core.v11;

import com.telventi.afirma.mschema.AElement;

public class NameIdentifierType extends AElement {
	
	private String nameQualifier = null;
	
	private String format = null;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getNameQualifier() {
		return nameQualifier;
	}

	public void setNameQualifier(String nameQualifier) {
		this.nameQualifier = nameQualifier;
	}
	
}
