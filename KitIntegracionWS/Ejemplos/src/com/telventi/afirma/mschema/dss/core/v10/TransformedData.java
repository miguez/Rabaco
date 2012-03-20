package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.xmldsig.core.Transforms;


public class TransformedData extends AElement {
	
	private Base64Data base64Data = null;
	
	private Transforms transforms = null;
	
	private String whichReference = null;

	public Base64Data getBase64Data() {
		return base64Data;
	}

	public void setBase64Data(Base64Data base64Data) {
		this.base64Data = base64Data;
	}

	public Transforms getTransforms() {
		return transforms;
	}

	public void setTransforms(Transforms transforms) {
		this.transforms = transforms;
	}

	public String getWhichReference() {
		return whichReference;
	}

	public void setWhichReference(String whichReference) {
		this.whichReference = whichReference;
	}
	
	
	
}
