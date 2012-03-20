package com.telventi.afirma.mschema.dss.profiles.xss.draft;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

public class ParallelSignature extends AElement {

	public void marshal(StringWriter writer) throws Exception {
		if(writer.toString().indexOf("xmlns:xss=")==-1)
			writer.getBuffer().insert(writer.getBuffer().indexOf(">")," xmlns:xss=\"urn:oasis:names:tc:dss:1.0:profiles:XSS\"");
		writer.write("<xss:ParallelSignature/>");
	}

}
