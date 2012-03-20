package com.telventi.afirma.mschema.dss.core.v10;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.wsclient.dss.DSSUtils;

public class SignatureObject extends AElement {
	
	private String dsig_escape = null;
	
	private Timestamp timestamp = null;
	
	private Base64Signature b64Signature = null;
	
	private SignaturePtr signaturePtr = null;
	
	private String schemaRefs = null;
	
	private Other other = null;

	public Base64Signature getB64Signature() {
		return b64Signature;
	}

	public void setB64Signature(Base64Signature signature) {
		b64Signature = signature;
	}

	public String getSchemaRefs() {
		return schemaRefs;
	}

	public void setSchemaRefs(String schemaRefs) {
		this.schemaRefs = schemaRefs;
	}

	

	public String getXMLDSignature() {
		return dsig_escape;
	}

	public void setXMLDSignature(String dsig_escape) {
		//Comprobamos si tiene instrucciones de procesado para quitarselas
		if(dsig_escape!=null && dsig_escape.indexOf("?>")>0)
			this.dsig_escape = dsig_escape.substring(dsig_escape.indexOf("?>")+2);
		else
			this.dsig_escape = dsig_escape;
	}

	public SignaturePtr getSignaturePtr() {
		return signaturePtr;
	}

	public void setSignaturePtr(SignaturePtr signaturePtr) {
		this.signaturePtr = signaturePtr;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void unmarshal(Node node) throws Exception {
		NodeList list = node.getChildNodes();
		int i= 0;
		boolean unmarshal = false;
		while(i<list.getLength() && !unmarshal){
			Node child = list.item(i);
			String nodeNameSig = child.getPrefix()!=null?(child.getPrefix()+":Signature"):"Signature";
			if(child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(nodeNameSig)){
				this.setXMLDSignature(DSSUtils.getStringXml(child));
				unmarshal = true;
			}
			i++;
		}
		if(!unmarshal)
			super.unmarshal(node);
	}

	public Other getOther() {
		return other;
	}

	public void setOther(Other other) {
		this.other = other;
	}
}
