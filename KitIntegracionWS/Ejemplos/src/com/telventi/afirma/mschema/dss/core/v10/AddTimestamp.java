package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class AddTimestamp extends AElement {
	
	private boolean givenSignature = false;
	
	private String type = null;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isGivenSignature() {
		return givenSignature;
	}

	public void setGivenSignature(boolean givenSignature) {
		this.givenSignature = givenSignature;
	}
}
