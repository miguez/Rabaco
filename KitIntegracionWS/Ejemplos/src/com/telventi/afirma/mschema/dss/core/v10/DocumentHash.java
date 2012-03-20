package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.xmldsig.core.DigestMethod;
import com.telventi.afirma.mschema.xmldsig.core.DigestValue;
import com.telventi.afirma.mschema.xmldsig.core.Transforms;

public class DocumentHash extends AElement {
	
	private String id = null;
	
	private String refURI = null;
	
	private String refType = null;
	
	private String schemaRefs = null;
	
	private String whichReference = null;
	
	private Transforms transforms = null;
	
	private DigestMethod digestMethod = null;
	
	private DigestValue digestValue = null;

	public DigestMethod getDigestMethod() {
		return digestMethod;
	}

	public void setDigestMethod(DigestMethod digestMethod) {
		this.digestMethod = digestMethod;
	}

	public DigestValue getDigestValue() {
		return digestValue;
	}

	public void setDigestValue(DigestValue digestValue) {
		this.digestValue = digestValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public String getRefURI() {
		return refURI;
	}

	public void setRefURI(String refURI) {
		this.refURI = refURI;
	}

	public String getSchemaRefs() {
		return schemaRefs;
	}

	public void setSchemaRefs(String schemaRefs) {
		this.schemaRefs = schemaRefs;
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
