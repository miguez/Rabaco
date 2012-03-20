package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class SignaturePtr extends AElement {
	
	private String whichDocument =null;
	
	private String xpath = null;

	public String getWhichDocument() {
		return whichDocument;
	}

	public void setWhichDocument(String whichDocument) {
		this.whichDocument = whichDocument;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	
}
