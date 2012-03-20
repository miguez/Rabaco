package com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.DocumentArchiveId;

public class AdditionalArchiveInfo extends AElement {
	
	private DocumentArchiveId documentArchiveId = null;
	
	private EvidenceOfESignature evidence = null;

	

	/**
	 * @return Returns the documentArchiveId.
	 */
	public DocumentArchiveId getDocumentArchiveId() {
		return documentArchiveId;
	}

	/**
	 * @param documentArchiveId The documentArchiveId to set.
	 */
	public void setDocumentArchiveId(DocumentArchiveId documentArchiveId) {
		this.documentArchiveId = documentArchiveId;
	}

	public EvidenceOfESignature getEvidence() {
		return evidence;
	}

	public void setEvidence(EvidenceOfESignature evidence) {
		this.evidence = evidence;
	}
	
}
