package com.telventi.afirma.mschema.xmldsig.core;

import com.telventi.afirma.mschema.AElement;

public class KeyInfo extends AElement {
	
	private String keyName = null;

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
}
