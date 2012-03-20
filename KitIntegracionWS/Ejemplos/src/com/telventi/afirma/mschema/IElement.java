package com.telventi.afirma.mschema;

import java.io.StringWriter;

import org.w3c.dom.Node;

public interface IElement {
	
	public String node_type_attribute = "attribute";
	
	public String node_type_element = "element";
	
	public String node_type_text = "text";
	
	public String field_type_string = "string";
	
	public String field_type_boolean = "boolean";
	
	public String field_type_bytes = "bytes";
	
	public String field_type_vector = "vector";
	
	public void unmarshal(Node node) throws Exception;
	
	public void marshal(StringWriter writer) throws Exception;
}
