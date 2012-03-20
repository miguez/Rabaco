package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.xmldsig.core.DigestMethod;
import com.telventi.afirma.mschema.xmldsig.core.DigestValue;

public class AttachmentReference extends AElement {

	private String attRefURI = null;
	
	private String mimeType = null;
	
	private DigestMethod digestMethod = null;
	
	private DigestValue digestValue = null;

	public String getAttRefURI() {
		return attRefURI;
	}

	public void setAttRefURI(String attRefURI) {
		this.attRefURI = attRefURI;
	}

	public DigestMethod getDigestMethod() {
		return digestMethod;
	}

	public void setDigestMethod(DigestMethod digestMethod) {
		this.digestMethod = digestMethod;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public DigestValue getDigestValue() {
		return digestValue;
	}

	public void setDigestValue(DigestValue digestValue) {
		this.digestValue = digestValue;
	}
}
