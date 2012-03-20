package com.telventi.afirma.mschema.xmldsig.core;

import com.telventi.afirma.mschema.AElement;

public class X509Data extends AElement {
	
	private byte[] x509Certificate = null;

	public byte[] getX509Certificate() {
		return x509Certificate;
	}

	public void setX509Certificate(byte[] certificate) {
		x509Certificate = certificate;
	}
	
}
