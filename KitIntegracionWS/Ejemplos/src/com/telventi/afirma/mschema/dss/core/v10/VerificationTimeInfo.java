package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class VerificationTimeInfo extends AElement {
	
	private String time = null;
	
	private  AdditionalTimeInfo  additionalTimeInfo = null;

	public AdditionalTimeInfo getAdditionalTimeInfo() {
		return additionalTimeInfo;
	}

	public void setAdditionalTimeInfo(AdditionalTimeInfo additionalTimeInfo) {
		this.additionalTimeInfo = additionalTimeInfo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
}
