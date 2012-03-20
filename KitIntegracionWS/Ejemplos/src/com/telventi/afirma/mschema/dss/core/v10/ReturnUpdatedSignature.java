package com.telventi.afirma.mschema.dss.core.v10;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

public class ReturnUpdatedSignature extends AElement {
	
	private String type = null;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void marshal(StringWriter writer) throws Exception {
		writer.write("<dss:ReturnUpdatedSignature"+(type!=null?(" Type=\""+type+"\""):"")+"/>");
	}
	

}
