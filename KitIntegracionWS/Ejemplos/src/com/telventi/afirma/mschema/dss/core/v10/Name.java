package com.telventi.afirma.mschema.dss.core.v10;


import com.telventi.afirma.mschema.saml.core.v11.NameIdentifierType;

public class Name extends NameIdentifierType {
	
	private String nameIdentifier = null;

	public String getNameIdentifier() {
		return nameIdentifier;
	}

	public void setNameIdentifier(String nameIdentifier) {
		this.nameIdentifier = nameIdentifier;
	}
	
	
}
