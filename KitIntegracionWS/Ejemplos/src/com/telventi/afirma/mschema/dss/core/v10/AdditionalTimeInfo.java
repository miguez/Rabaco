package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class AdditionalTimeInfo extends AElement {
	private String time = null;
	
	private String type = null;
	
	private String ref = null;

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
