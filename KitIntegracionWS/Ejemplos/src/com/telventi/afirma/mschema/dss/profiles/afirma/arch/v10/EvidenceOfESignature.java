package com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.core.v10.SignatureObject;
import com.telventi.afirma.mschema.dss.core.v10.SignatureType;
import com.telventi.afirma.mschema.dss.profiles.ades.v10.SignatureForm;

public class EvidenceOfESignature extends AElement {
	
	private SignatureObject signature = null;
	
	private SignatureType signatureType = null;
	
	private SignatureForm signatureForm = null;

	/**
	 * @return Returns the signatureForm.
	 */
	public SignatureForm getSignatureForm() {
		return signatureForm;
	}

	/**
	 * @param signatureForm The signatureForm to set.
	 */
	public void setSignatureForm(SignatureForm signatureForm) {
		this.signatureForm = signatureForm;
	}

	/**
	 * @return Returns the signatureType.
	 */
	public SignatureType getSignatureType() {
		return signatureType;
	}

	/**
	 * @param signatureType The signatureType to set.
	 */
	public void setSignatureType(SignatureType signatureType) {
		this.signatureType = signatureType;
	}

	public SignatureObject getSignature() {
		return signature;
	}

	public void setSignature(SignatureObject signature) {
		this.signature = signature;
	} 
	
}
