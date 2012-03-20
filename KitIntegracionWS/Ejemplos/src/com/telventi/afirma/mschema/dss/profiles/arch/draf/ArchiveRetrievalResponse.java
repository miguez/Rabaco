package com.telventi.afirma.mschema.dss.profiles.arch.draf;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.core.v10.OptionalOutputs;
import com.telventi.afirma.mschema.dss.core.v10.Result;
import com.telventi.afirma.mschema.dss.core.v10.SignatureObject;

public class ArchiveRetrievalResponse extends AElement {
	
	private String profile = null;
	
	private String requestID = null;
	
	private Result result=null;
	
	private OptionalOutputs optionalOutputs = null;
	
	private SignatureObject signatureObject = null;

	public OptionalOutputs getOptionalOutputs() {
		return optionalOutputs;
	}

	public void setOptionalOutputs(OptionalOutputs optionalOutputs) {
		this.optionalOutputs = optionalOutputs;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public SignatureObject getSignatureObject() {
		return signatureObject;
	}

	public void setSignatureObject(SignatureObject signatureObject) {
		this.signatureObject = signatureObject;
	}
	
	public void marshal(StringWriter writer) throws Exception {
		super.marshal(writer);
		super.includeSchemaLocation(writer);
		writer.getBuffer().insert(0,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}
}
