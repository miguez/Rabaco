package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.xmldsig.core.X509Data;

public class TSASignerIdentity extends AElement {
	private X509Data x509Data = null;

	public X509Data getX509Data() {
		return x509Data;
	}

	public void setX509Data(X509Data data) {
		x509Data = data;
	}
}
