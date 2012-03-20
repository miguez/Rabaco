package com.telventi.afirma.mschema;

import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * Clase que captura toda la información de configuración 
 * del mapeo de XML <--> Objeto 
 * 
 * @author SEJRL
 *
 */
public class Mapping{
	
	private static Hashtable class_mapping = new Hashtable();
	
	private static Hashtable element_class = new Hashtable();
	
	static{
		try {
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		    javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		    org.w3c.dom.Document doc = db.parse(Mapping.class.getResourceAsStream("/AfirmaMapping.xml"));
			NodeList nodesClass = doc.getDocumentElement().getChildNodes();
			for(int i=0;i<nodesClass.getLength();i++){
				Node nodeClass = nodesClass.item(i);
				if(nodeClass.getNodeName().equals("class")){
					ClassMapped classMapped = new ClassMapped();
					classMapped.setClassName( nodeClass.getAttributes().getNamedItem("name").getNodeValue());
					if(nodeClass.getAttributes().getNamedItem("extend")!=null)
						classMapped.setClassExtend( nodeClass.getAttributes().getNamedItem("extend").getNodeValue());
					
					NodeList nodesFields = nodeClass.getChildNodes();
					for(int j=0;j<nodesFields.getLength();j++){
						Node nodeField = nodesFields.item(j);
							if(nodeField.getNodeName().equals("map-to")){
								classMapped.setNodeName(nodeField.getAttributes().getNamedItem("xml").getNodeValue());
								if(nodeField.getAttributes().getNamedItem("ns-prefix")!=null)
									classMapped.setPrefix(nodeField.getAttributes().getNamedItem("ns-prefix").getNodeValue());
								if(nodeField.getAttributes().getNamedItem("ns-uri")!=null)
									classMapped.setNamespace(nodeField.getAttributes().getNamedItem("ns-uri").getNodeValue());
							}else if (nodeField.getNodeName().equals("field")){
								//System.out.println("Entrooooo");
								FieldMapped field = new FieldMapped();
								field.setName(nodeField.getAttributes().getNamedItem("name").getNodeValue());
								field.setType(nodeField.getAttributes().getNamedItem("type").getNodeValue());
								if(nodeField.getAttributes().getNamedItem("set-method")!=null)
									field.setSetMethod(nodeField.getAttributes().getNamedItem("set-method").getNodeValue());
								if(nodeField.getAttributes().getNamedItem("get-method")!=null)
									field.setGetMethod(nodeField.getAttributes().getNamedItem("get-method").getNodeValue());
								NodeList fieldItems = nodeField.getChildNodes();
								for(int k=0;k<fieldItems.getLength();k++){
									Node fieldItem = fieldItems.item(k);
									if(fieldItem.getNodeName().equals("bind-xml")){		
										if(fieldItem.getAttributes().getNamedItem("node") != null)
											field.setNodeType(fieldItem.getAttributes().getNamedItem("node").getNodeValue());
										if(fieldItem.getAttributes().getNamedItem("name") != null)
											field.setNodeName(fieldItem.getAttributes().getNamedItem("name").getNodeValue());
										if(fieldItem.getAttributes().getNamedItem("QName-prefix") != null)
											field.setPrefix(fieldItem.getAttributes().getNamedItem("QName-prefix").getNodeValue());
									}
									
								}
								classMapped.addField(field);
							}
						
					}
					class_mapping.put(classMapped.getClassName(),classMapped);
					if(classMapped.getNodeName()!=null)
						element_class.put(classMapped.getNodeName(),classMapped.getClassName());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error al obtener el XML de mapeo",e);
		} 
	}
	
	/**
	 * Método que permite obtener a partir del nombre de la clase la información de mapeo 
	 * correspondiente a un nodo de un documento DOM
	 * @param nameClass  	Nombre de la clase que esta configurada para recoger 
	 * 						la información contenida en el nodo
	 * @return 				Clase que contiene la información de mapeo.
	 */
	public static ClassMapped getMappedByClass(String nameClass){
		return (ClassMapped) class_mapping.get(nameClass);
	}
	
	/**
	 * Método que permite obtener a partir nombre del nodo la información de mapeo 
	 * correspondiente a un nodo de un documento DOM
	 * @param nodeName		Nombre del nodo.
	 * @return				Clase que contiene la información de mapeo.
	 */
	public static ClassMapped getMappedByNodeName(String nodeName){
		String nameClass = (String) element_class.get(nodeName);
		if(nameClass != null)
			 return getMappedByClass(nameClass);
		else
			return null;
	}
}
