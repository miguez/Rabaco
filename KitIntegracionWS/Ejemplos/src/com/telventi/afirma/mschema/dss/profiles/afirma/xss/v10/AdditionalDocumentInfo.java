package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;

public class AdditionalDocumentInfo extends AElement {
	
	private String name = null;
	
	private String type = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
