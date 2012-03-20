package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class Result extends AElement {

	private String resultMajor = null;
	
	private String resultMinor = null;
	
	private String message = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResultMajor() {
		return resultMajor;
	}

	public void setResultMajor(String resultMajor) {
		this.resultMajor = resultMajor;
	}

	public String getResultMinor() {
		return resultMinor;
	}

	public void setResultMinor(String resultMinor) {
		this.resultMinor = resultMinor;
	}
}
