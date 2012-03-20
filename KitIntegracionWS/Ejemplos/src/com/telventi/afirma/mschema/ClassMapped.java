package com.telventi.afirma.mschema;

import java.util.ArrayList;

public class ClassMapped {
	
	private String className = null;
	
	private String nodeName = null;
	
	private String prefix = null;
	
	private String namespace = null;
	
	private String classExtend = null;
	
	private ArrayList fields = new ArrayList();

	public ArrayList getFields() {
		return fields;
	}

	public void setFields(ArrayList fields) {
		this.fields = fields;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void addField(FieldMapped field){
		fields.add(field);
	}

	public String getClassExtend() {
		return classExtend;
	}

	public void setClassExtend(String classExtend) {
		this.classExtend = classExtend;
	}
}
