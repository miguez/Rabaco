package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;


public class Timestamp extends AElement {
 
	private byte[] rfc3161TST = null;
	
	private String xmlTST = null;

	public byte[] getRFC3161TST() {
		return rfc3161TST;
	}

	public void setRFC3161TST(byte[] rfc3161TST) {
		this.rfc3161TST = rfc3161TST;
	}

	public String getXMLTST() {
		return xmlTST;
	}

	public void setXMLTST(String xmlTST) {
		this.xmlTST = xmlTST;
	}
}
