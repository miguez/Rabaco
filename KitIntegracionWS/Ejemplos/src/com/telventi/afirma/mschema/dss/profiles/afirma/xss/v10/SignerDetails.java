package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;

public class SignerDetails extends AElement {
	
	private X509SignerIdentity x509SignerIdentity = null;
	
	private String timestamp = null;
	
	private TSASignerIdentity tSASignerIdentity = null;


	
	public TSASignerIdentity getTSASignerIdentity() {
		return tSASignerIdentity;
	}

	public void setTSASignerIdentity(TSASignerIdentity signerIdentity) {
		tSASignerIdentity = signerIdentity;
	}

	public X509SignerIdentity getX509SignerIdentity() {
		return x509SignerIdentity;
	}

	public void setX509SignerIdentity(X509SignerIdentity signerIdentity) {
		x509SignerIdentity = signerIdentity;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
