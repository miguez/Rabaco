package com.telventi.afirma.mschema.xmldsig.core;

import com.telventi.afirma.mschema.AElement;

public class DigestValue extends AElement{
	
	private byte[] digest = null;

	public byte[] getDigest() {
		return digest;
	}

	public void setDigest(byte[] digest) {
		this.digest = digest;
	}
	
}
