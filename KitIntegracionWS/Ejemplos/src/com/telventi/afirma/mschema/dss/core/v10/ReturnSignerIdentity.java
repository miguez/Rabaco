package com.telventi.afirma.mschema.dss.core.v10;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

public class ReturnSignerIdentity extends AElement {

	public void marshal(StringWriter writer) throws Exception {
		writer.write("<ReturnSignerIdentity/>");
	}
	
}
