package com.telventi.afirma.mschema;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.telventi.afirma.wsclient.utils.UtilsBase64;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

//import com.telventi.afirma.utilidades.UtilsBase64;
///import com.telventi.afirma.utilidades.UtilsXML;

public abstract class AElement implements IElement{

	protected static final Hashtable schemaLocation = new Hashtable();
	
	static{
		String urlAfirma = "http://"+UtilsWebService.getAfirmaIP()+"/afirmaws/";
		//Localización del schema del Core-DSS
		schemaLocation.put("urn:oasis:names:tc:dss:1.0:core:schema","http://docs.oasis-open.org/dss/v1.0/oasis-dss-core-schema-v1.0-os.xsd");
		//Localización del schema de XMLDsign
		schemaLocation.put("http://www.w3.org/2000/09/xmldsig#","http://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd");
		//Localización del schema AdES de OASIS
		schemaLocation.put("urn:oasis:names:tc:dss:1.0:profiles:AdES:schema#","http://docs.oasis-open.org/dss/v1.0/oasis-dss-profiles-AdES-schema-v1.0-os.xsd");
		if(urlAfirma !=null){
			//Localización del schema XSS de OASIS-DSS
			schemaLocation.put("urn:oasis:names:tc:dss:1.0:profiles:XSS",urlAfirma+"xsd/dss/oasis-dss-1.0-profiles-XSS-schema-wd02.xsd");
			//Localización del schema XSS de AFIRMA
			schemaLocation.put("urn:afirma:dss:1.0:profile:XSS:schema",urlAfirma+"xsd/dss/afirma-dss-1.0-profiles-XSS-schema.xsd");
			//Localización del schema Archive de OASIS-DSS
			schemaLocation.put("urn:oasis:names:tc:dss:1.0:profiles:archive",urlAfirma+"xsd/dss/oasis-dss-1.0-profiles-archive-schema.xsd");
			//Localización del schema Archive de AFIRMA
			schemaLocation.put("urn:afirma:dss:1.0:profile:archive:schema",urlAfirma+"xsd/dss/afirma-dss-1.0-profiles-archive-schema.xsd");
			//Localización del schema VR de OASIS
			schemaLocation.put("urn:oasis:names:tc:dss:1.0:profiles:verificationreport:schema#",urlAfirma+"xsd/dss/oasis-dss-1.0-profile-verification-report-wd.xsd");
		}
	}
	
	public void includeSchemaLocation(StringWriter writer) throws Exception{
		DOMParser parser = new DOMParser();
		// parsear
		//System.out.println("DOM-->"+writer.toString());
		parser.parse(new InputSource(new StringReader(writer.toString())));
		Document doc = parser.getDocument();
		NamedNodeMap atts =doc.getDocumentElement().getAttributes();
		ArrayList ns = new ArrayList();
		String location = "";
		for(int i = 0;i<atts.getLength();i++){
			Node att = atts.item(i);
			if(att.getNodeName().indexOf("xmlns")!=-1)
				ns.add(att.getNodeValue());
		}
		for(int i=0;i<ns.size();i++){
			if(schemaLocation.containsKey((String) ns.get(i))){
				location = location + (String)ns.get(i) + " "+schemaLocation.get(ns.get(i))+" ";
			}
		}
		location = location.trim();
		if(!location.equals("")){
			writer.getBuffer().insert(writer.getBuffer().indexOf(">")," xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\""+location+"\"");
		}
	}
	public void marshal(StringWriter writer) throws Exception{
		String className = this.getClass().getName();
		ClassMapped classMapped = Mapping.getMappedByClass(className);
		if(classMapped != null){
			String nodeName = classMapped.getNodeName();
			ClassMapped classMappedExt = null;
			if(classMapped.getPrefix()!=null)
				nodeName = classMapped.getPrefix()+":"+ nodeName;
			String xml = "<";
			xml = xml +nodeName;
			ArrayList listField = listField = classMapped.getFields(); 
			if(classMapped.getClassExtend()!= null){
				classMappedExt = Mapping.getMappedByClass(classMapped.getClassExtend());
				listField.addAll(classMappedExt.getFields());
			}
			xml = xml +getAttributes(listField);
			xml = xml +">";
			writer.write(xml);
			writerChilds(writer,listField);
			writer.write("</"+nodeName+">");
			if(classMapped.getPrefix()!=null && writer.toString().indexOf("xmlns:"+classMapped.getPrefix()+"=")==-1)
				writer.getBuffer().insert(writer.getBuffer().indexOf(">")," xmlns:"+classMapped.getPrefix()+"=\""+classMapped.getNamespace()+"\"");
			if(classMappedExt!=null && classMappedExt.getPrefix()!=null && writer.toString().indexOf("xmlns:"+classMappedExt.getPrefix()+"=")==-1)
				writer.getBuffer().insert(writer.getBuffer().indexOf(">")," xmlns:"+classMappedExt.getPrefix()+"=\""+classMappedExt.getNamespace()+"\"");
			
		}
	}
	
	private String getAttributes(ArrayList listField) throws Exception{
		String aux = "";
		for(int i=0;i<listField.size();i++){
			FieldMapped field = (FieldMapped) listField.get(i); 
			if(field.getNodeType().equals(node_type_attribute) && field.getNodeName()!=null){
				String getMethod = field.getGetMethod();
				if(getMethod == null){
					if(field.getType().equals(field_type_boolean))
						getMethod = "is"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
					else
						getMethod = "get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
				}
				String value = String.valueOf( this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{}));
				if(value !=null && ! value.equals("null") )
					aux= aux+" "+field.getNodeName()+"=\""+value+"\"";
			}
		}
		return aux;
	}
	
	private void writerChilds(StringWriter writer,ArrayList listField)throws Exception{
		for(int i=0;i<listField.size();i++){
			FieldMapped field = (FieldMapped) listField.get(i); 
			if(field.getNodeType().equals(node_type_element)){
				writerChildByElement(writer,field);
			}else if (field.getNodeType().equals(node_type_text)){
				writerChildByText(writer,field);
			}else if (field.getNodeType().equals("")){
				writerChildByElement(writer,field);
			}
		}
	}
	private void writerChildByElement(StringWriter writer,FieldMapped field) throws Exception{
		String nodeName = field.getNodeName();
		if(field.getPrefix()!=null)
			nodeName = field.getPrefix()+":"+ nodeName;
		String getMethod = field.getGetMethod();
		if(getMethod == null){
			if(field.getType().equals(field_type_boolean))
				getMethod = "is"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
			else
				getMethod = "get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		}
		if(field.getType().equals(field_type_string) || field.getType().equals(field_type_boolean)){
			String value = String.valueOf( this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{}));
			if(value != null && ! value.equals("null"))
				writer.write("<"+nodeName+">"+value+"</"+nodeName+">");
		}else if(field.getType().equals(field_type_bytes)){
			byte[] datos = (byte[])this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{});
			if(datos !=null ){
				UtilsBase64 utilBase64 = new UtilsBase64();
				writer.write("<"+nodeName+"><![CDATA["+utilBase64.encodeBytes(datos).replaceAll("\r", "").replaceAll("\n", "")+"]]></"+nodeName+">");
			}
		}else if(field.getType().equals(field_type_vector)){
			Vector v = (Vector) this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{});
			for(int i=0;i<v.size();i++){
				AElement element = (AElement) v.get(i);
				element.marshal(writer);
			}
		}else{
			Object object = this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{});
			
			if(object !=null && (object.getClass().getSuperclass().getName().equals("com.telventi.afirma.mschema.AElement") || 
					(object.getClass().getSuperclass().getSuperclass()!=null && object.getClass().getSuperclass().getSuperclass().getName().equals("com.telventi.afirma.mschema.AElement")))){
				AElement itemClass = (AElement)object;
				itemClass.marshal(writer);
			}
		}
		
	}
	
	private void writerChildByText(StringWriter writer,FieldMapped field) throws Exception{
		String getMethod = field.getGetMethod();
		if(getMethod == null){
			if(field.getType().equals(field_type_boolean))
				getMethod = "is"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
			else
				getMethod = "get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		}	
		if(field.getType().equals(field_type_string) || field.getType().equals(field_type_boolean)){
			String value =String.valueOf( this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{}));
			if(value != null && ! value.equals("null"))
				writer.write(value);
		}else if(field.getType().equals(field_type_bytes)){
			byte[] datos = (byte[])this.getClass().getMethod(getMethod,null).invoke(this,new Object[]{});
			if(datos !=null ){
				UtilsBase64 utilBase64 = new UtilsBase64();
				writer.write("<![CDATA["+utilBase64.encodeBytes(datos).replaceAll("\r", "").replaceAll("\n", "")+"]]>");
			}
		}
	}
	
	public void unmarshal(Node node) throws Exception{
		if(node != null)
			fillItems(node);
	}
	
	
	private void  fillItems(Node node)throws Exception{
		String nodeName = node.getNodeName().substring(node.getNodeName().indexOf(":")+1);
		//Obtenemos la clase de mapeo
		ClassMapped classMapped = Mapping.getMappedByNodeName(nodeName);
		if(classMapped !=null){
			ArrayList listField = classMapped.getFields();
			if(classMapped.getClassExtend()!= null){
				ClassMapped classMappedExt = Mapping.getMappedByClass(classMapped.getClassExtend());
				listField.addAll(classMappedExt.getFields());
			}
			for(int i =0;i<listField.size();i++){
				FieldMapped field = (FieldMapped) listField.get(i);
				if(field.getNodeType().equals(node_type_element)){
					int j = 0;
					NodeList items = node.getChildNodes();
					Node item = null;
					while(j<items.getLength() && item  == null){
						if(items.item(j).getNodeName().substring(items.item(j).getNodeName().indexOf(":")+1).equals(field.getNodeName()))
							item = items.item(j);
							j++;
					}
					if(item != null)
						fillItemByElement(item, field);
				}else if(field.getNodeType().equals(node_type_attribute)){
					fillItemByAttribute(node,field);
				}else if(field.getNodeType().equals(node_type_text)){
					fillItemByText(node,field);
				}else if(field.getNodeType().equals("")){
					fillItemByElement(node, field);
				}
			}
		}
	}
	
	private void fillItemByText(Node item, FieldMapped field) throws Exception{
		String setMethod = field.getSetMethod();
		String nodeValue = null;
		NodeList nodeChilds = item.getChildNodes();
		int i =0;
		while( i<nodeChilds.getLength() && nodeChilds.item(i).getNodeValue() !=null && (nodeValue==null||nodeValue.equals(""))){
			nodeValue = nodeChilds.item(i).getNodeValue().trim();
			i++;
		}
		if(setMethod == null)
			setMethod = "set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		if(field.getType().equals(field_type_string))
			this.getClass().getMethod(setMethod,new Class[]{String.class}).invoke(this,new Object[]{nodeValue});
		else if (field.getType().equals(field_type_boolean)){
			if(nodeValue.equalsIgnoreCase("true"))
				this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.TRUE});
			else if (nodeValue.equalsIgnoreCase("false"))
				this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.FALSE}); 
		}
		else if(field.getType().equals(field_type_bytes)){
			UtilsBase64 utilBase64 = new UtilsBase64();	
			byte[] datos = utilBase64.decode(nodeValue);
			this.getClass().getMethod(setMethod,new Class[]{byte[].class}).invoke(this,new Object[]{datos});
		}
	}
	
	private void fillItemByAttribute(Node item, FieldMapped field) throws Exception{
		if(item.getAttributes().getNamedItem(field.getNodeName())!=null){
			String nodeValue = item.getAttributes().getNamedItem(field.getNodeName()).getNodeValue();
			String setMethod = field.getSetMethod();
			if(setMethod == null)
				setMethod = "set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
			if(field.getType().equals(field_type_string))
				this.getClass().getMethod(setMethod,new Class[]{String.class}).invoke(this,new Object[]{nodeValue});
			else if(field.getType().equals(field_type_boolean)){
				if(nodeValue.equalsIgnoreCase("true"))
					this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.TRUE});
				else if (nodeValue.equalsIgnoreCase("false"))
					this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.FALSE}); 
			}
		}
	}
	
	private void fillItemByElement(Node item, FieldMapped field)throws Exception{
		String setMethod = field.getSetMethod();
		if(setMethod == null)
			setMethod = "set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		if(field.getType().equals(field_type_string)){
			String nodeValue = null;
			NodeList nodeChilds = item.getChildNodes();
			int i =0;
			while( i<nodeChilds.getLength() && (nodeValue==null||nodeValue.equals(""))){
				nodeValue = nodeChilds.item(i).getNodeValue().trim();
				i++;
			}
			if(nodeValue!=null && !nodeValue.equals(""))
				this.getClass().getMethod(setMethod,new Class[]{String.class}).invoke(this,new Object[]{nodeValue});
		}else if(field.getType().equals(field_type_boolean)){
			String nodeValue = null;
			NodeList nodeChilds = item.getChildNodes();
			int i =0;
			while( i<nodeChilds.getLength() && (nodeValue==null||nodeValue.equals(""))){
				nodeValue = nodeChilds.item(i).getNodeValue().trim();
				i++;
			}
			if(nodeValue!=null && !nodeValue.equals("")){
				if(nodeValue.equalsIgnoreCase("true"))
					this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.TRUE});
				else if (nodeValue.equalsIgnoreCase("false"))
					this.getClass().getMethod(setMethod,new Class[]{boolean.class}).invoke(this,new Object[]{Boolean.FALSE}); 
			}
		}else if(field.getType().equals(field_type_bytes)){
			String nodeValue = null;
			NodeList nodeChilds = item.getChildNodes();
			int i =0;
			while( i<nodeChilds.getLength() && (nodeValue==null||nodeValue.equals(""))){
				nodeValue = nodeChilds.item(i).getNodeValue().trim();
				i++;
			}
			if(nodeValue!=null && !nodeValue.equals("")){
				UtilsBase64 utilBase64 = new UtilsBase64();	
				byte[] datos = utilBase64.decode(nodeValue);
				this.getClass().getMethod(setMethod,new Class[]{byte[].class}).invoke(this,new Object[]{datos});
			}
		}else if(field.getType().equals(field_type_vector)){
			NodeList childs = item.getChildNodes();
			Vector v = new Vector();
			for(int i = 0 ;i<childs.getLength();i++){
				if(childs.item(i).getNodeType()==Node.ELEMENT_NODE){
					Node child = childs.item(i);
					String nodeName = child.getNodeName().substring(child.getNodeName().indexOf(":")+1);
					ClassMapped classMapped = Mapping.getMappedByNodeName(nodeName);
					if(classMapped != null){
						AElement itemClass = (AElement) Class.forName(classMapped.getClassName()).newInstance();
						itemClass.unmarshal(child);
						v.add(itemClass);
					}
				}
			}
			if(v.size()>0)
				this.getClass().getMethod(setMethod,new Class[]{Vector.class}).invoke(this,new Object[]{v});
		}else{
			Object object = Class.forName(field.getType()).newInstance();
			if(object !=null && (object.getClass().getSuperclass().getName().equals("com.telventi.afirma.mschema.AElement") || 
					(object.getClass().getSuperclass().getSuperclass()!=null && object.getClass().getSuperclass().getSuperclass().getName().equals("com.telventi.afirma.mschema.AElement")))){
				AElement itemClass = (AElement)object;
				itemClass.unmarshal(item);
				this.getClass().getMethod(setMethod,new Class[]{itemClass.getClass()}).invoke(this,new Object[]{itemClass});
			}
		}
	}
}
