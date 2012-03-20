package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class XMLTimeStamp extends AElement {
	
	Timestamp timestamp = null;

	public Timestamp getTIMESTAMP() {
		return timestamp;
	}

	public void setTIMESTAMP(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
