package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.saml.core.v11.NameIdentifierType;

public class SignerIdentity extends NameIdentifierType {
	
	private String  identity = null;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}
