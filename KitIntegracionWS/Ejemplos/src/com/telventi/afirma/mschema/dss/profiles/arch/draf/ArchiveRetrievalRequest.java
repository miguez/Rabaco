package com.telventi.afirma.mschema.dss.profiles.arch.draf;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.core.v10.OptionalInputs;

public class ArchiveRetrievalRequest extends AElement {
	
	private String profile = null;
	
	private String requestID = null;
	
	private OptionalInputs optionalInputs = null;
	
	private String archiveIdentifier = null;

	

	public String getArchiveIdentifier() {
		return archiveIdentifier;
	}

	public void setArchiveIdentifier(String archiveIdentifier) {
		this.archiveIdentifier = archiveIdentifier;
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
