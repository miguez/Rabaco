package com.telventi.afirma.mschema.dss.core.v10;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

public class SignRequest extends AElement {
	
	private String profile = null;
	
	private String requestID = null;
	
	private OptionalInputs optionalInputs = null;
	
	private InputDocuments inputDocuments = null;

	public InputDocuments getInputDocuments() {
		return inputDocuments;
	}

	public void setInputDocuments(InputDocuments inputDocuments) {
		this.inputDocuments = inputDocuments;
	}

	public OptionalInputs getOptionalInputs() {
		return optionalInputs;
	}

	public void setOptionalInputs(OptionalInputs optionalInputs) {
		this.optionalInputs = optionalInputs;
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
	
	public void marshal(StringWriter writer) throws Exception {
		super.marshal(writer);
		super.includeSchemaLocation(writer);
		writer.getBuffer().insert(0,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}
}
