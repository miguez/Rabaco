package com.telventi.afirma.mschema.xmldsig.core;

import com.telventi.afirma.mschema.AElement;

public class X509DataType extends AElement {
	
	private X509Data x509Data = null;

	public X509Data getX509Data() {
		return x509Data;
	}

	public void setX509Data(X509Data data) {
		x509Data = data;
	}

}
