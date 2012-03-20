package com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.core.v10.InputDocuments;
import com.telventi.afirma.mschema.dss.core.v10.SignatureType;
import com.telventi.afirma.mschema.dss.profiles.ades.v10.SignatureForm;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.HashAlgorithm;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.ReferenceId;
import com.telventi.afirma.mschema.xmldsig.core.X509Data;

public class AdditionalSignatureInfo extends AElement {
	
	private X509Data certificate = null;
	
	private ReferenceId reference = null;
	
	private SignatureType signatureType = null;
	
	private SignatureForm signatureForm = null;
	
	private HashAlgorithm hashAlgorithm = null;
	
	private boolean storeDocument = false;
	
	private InputDocuments inputDocuments = null;

	public X509Data getCertificate() {
		return certificate;
	}

	public void setCertificate(X509Data certificate) {
		this.certificate = certificate;
	}

	public HashAlgorithm getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public ReferenceId getReference() {
		return reference;
	}

	public void setReference(ReferenceId reference) {
		this.reference = reference;
	}

	public SignatureForm getSignatureForm() {
		return signatureForm;
	}

	public void setSignatureForm(SignatureForm signatureForm) {
		this.signatureForm = signatureForm;
	}

	public SignatureType getSignatureType() {
		return signatureType;
	}

	public void setSignatureType(SignatureType signatureType) {
		this.signatureType = signatureType;
	}

	public boolean isStoreDocument() {
		return storeDocument;
	}

	public void setStoreDocument(boolean storeDocument) {
		this.storeDocument = storeDocument;
	}

	/**
	 * @return Returns the inputDocuments.
	 */
	public InputDocuments getInputDocuments() {
		return inputDocuments;
	}

	/**
	 * @param inputDocuments The inputDocuments to set.
	 */
	public void setInputDocuments(InputDocuments inputDocuments) {
		this.inputDocuments = inputDocuments;
	}

}
