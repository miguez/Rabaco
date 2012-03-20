package com.telventi.afirma.mschema.xmldsig.core;

import com.telventi.afirma.mschema.AElement;

public class DigestMethod extends AElement {
	
	private String algorithm = null;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	
}
