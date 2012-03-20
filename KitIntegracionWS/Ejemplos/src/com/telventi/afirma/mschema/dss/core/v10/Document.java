package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;



public class Document extends AElement  {
	
	private byte[] base64XML = null;
	
	private Base64Data base64Data = null;
	
	private String escapedXML = null;
	
	private InlineXML inlineXML = null;
	
	private String id = null;
	
	private String refURI = null;
	
	private String refType = null;
	
	private String schemaRefs = null;
	
	private AttachmentReference attachment = null;

	public byte[] getBase64XML() {
		return base64XML;
	}

	public void setBase64XML(byte[] base64XML) {
		this.base64XML = base64XML;
	}

	public String getEscapedXML() {
		return escapedXML;
	}

	public void setEscapedXML(String escapedXML) {
		this.escapedXML = escapedXML;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public String getRefURI() {
		return refURI;
	}

	public void setRefURI(String refURI) {
		this.refURI = refURI;
	}

	public String getSchemaRefs() {
		return schemaRefs;
	}

	public void setSchemaRefs(String schemaRefs) {
		this.schemaRefs = schemaRefs;
	}

	public Base64Data getBase64Data() {
		return base64Data;
	}

	public void setBase64Data(Base64Data base64Data) {
		this.base64Data = base64Data;
	}

	public AttachmentReference getAttachment() {
		return attachment;
	}

	public void setAttachment(AttachmentReference attachment) {
		this.attachment = attachment;
	}

	public InlineXML getInlineXML() {
		return inlineXML;
	}

	public void setInlineXML(InlineXML inlineXML) {
		this.inlineXML = inlineXML;
	}

	
	
}
