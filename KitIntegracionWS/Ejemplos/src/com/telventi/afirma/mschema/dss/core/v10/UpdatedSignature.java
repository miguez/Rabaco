package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class UpdatedSignature extends AElement {
	
	private String type = null;
	
	private SignatureObject  signatureObject = null;

	public SignatureObject getSignatureObject() {
		return signatureObject;
	}

	public void setSignatureObject(SignatureObject signatureObject) {
		this.signatureObject = signatureObject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
