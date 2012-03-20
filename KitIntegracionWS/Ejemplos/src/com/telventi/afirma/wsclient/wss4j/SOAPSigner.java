/** 
* <p>Fichero: SOAPSigner.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 08-ene-2007</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.wss4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.message.SOAPEnvelope;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.message.WSSecUsernameToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Clase que permite firmar un SOAP, acorde a la configuración
 * dada en el fichero de properties proporcionado al constructor.
 * 
 * @author SEJLHA
 *
 */
public class SOAPSigner
{
	private static final long serialVersionUID = 1L;

	// Opciones de seguridad
	
	// Seguridad UserNameToken
	public static final String USERNAMEOPTION = WSConstants.USERNAME_TOKEN_LN;
	// Seguridad BinarySecurityToken
	public static final String CERTIFICATEOPTION = WSConstants.BINARY_TOKEN_LN;
	// Sin seguridad
	public static final String NONEOPTION = "None";
		
	// Opción de seguridad del objeto actual
	private String securityOption = null;
	
	// Usuario para el token de seguridad UserNameToken. 
	private String userTokenUserName = null;
	
	// Password para el token de seguridad UserNameToken 
	private String userTokenUserPassword = null;
	
	// Tipo de password para el UserNameTokenPassword
	private String userTokenUserPasswordType = null;
	
	// Localización del keystore con certificado y clave privada de usuario
	private String keystoreLocation = null;
	
	// Tipo de keystore
	private String keystoreType = null;
	
	// Clave del keystore
	private String keystorePassword = null;
	
	// Alias del certificado usado para firmar el tag soapBody de la petición y que será alojado en el token BinarySecurityToken
	private String keystoreCertAlias = null;

	// Password del certificado usado para firmar el tag soapBody de la petición y que será alojado en el token BinarySecurityToken
	private String keystoreCertPassword = null;

	/**
	 * Constructor que inicializa el atributo securityOption
	 * 
	 * @param securityOption opción de seguridad.
	 * @throws Exception
	 */
	public SOAPSigner(Properties config)
	{
		if(config == null)
		{
			System.err.println("Fichero de configuracion de propiedades nulo");
        	System.exit(-1);
		}

		try
		{
			securityOption = config.getProperty("security.mode");
			if (securityOption != null)
				securityOption=securityOption.trim();
			
			userTokenUserName = config.getProperty("security.usertoken.user").trim();
			if (userTokenUserName != null)
				userTokenUserName=userTokenUserName.trim();
			
			userTokenUserPassword = config.getProperty("security.usertoken.password").trim();
			if (userTokenUserPassword != null)
				userTokenUserPassword=userTokenUserPassword.trim();
			
			userTokenUserPasswordType = config.getProperty("security.usertoken.passwordType").trim();
			if (userTokenUserPasswordType != null)
				userTokenUserPasswordType=userTokenUserPasswordType.trim();
			
			keystoreLocation = config.getProperty("security.keystore.location").trim();		
			if (keystoreLocation != null)
				keystoreLocation=keystoreLocation.trim();
			
			keystoreType = config.getProperty("security.keystore.type").trim();
			if (keystoreType != null)
				keystoreType=keystoreType.trim();
			
			keystorePassword = config.getProperty("security.keystore.password").trim();
			if (keystorePassword != null)
				keystorePassword=keystorePassword.trim();
			
			keystoreCertAlias = config.getProperty("security.keystore.cert.alias").trim();
			if (keystoreCertAlias != null)
				keystoreCertAlias=keystoreCertAlias.trim();
			
			keystoreCertPassword = config.getProperty("security.keystore.cert.password").trim();
			if (keystoreCertPassword != null)
				keystoreCertPassword=keystoreCertPassword.trim();
		}
		catch (Exception e)
		{
			System.err.println("Error leyendo el fichero de configuración de securización");
			System.exit(-1);
		}
		
		if(!securityOption.equals(USERNAMEOPTION) && !securityOption.equals(CERTIFICATEOPTION) && !securityOption.equals(NONEOPTION))
		{
			System.err.println("Opcion de seguridad no valida: " + securityOption);
			System.exit(-1);
		}
	}
	
	public String sign(String soap) 
	{
		String secMsg;
		Document doc = null;

		secMsg = null;

		try 
		{
        	doc = new SOAPEnvelope(new ByteArrayInputStream(soap.getBytes())).getAsDocument();
        	
        	//Securización de la petición SOAP según la opcion de seguridad configurada
        	if(this.securityOption.equals(USERNAMEOPTION))
        		secMsg = this.createUserNameToken(doc);
        	else if(this.securityOption.equals(CERTIFICATEOPTION))
        		secMsg = this.createBinarySecurityToken(doc);
        	else
        		return soap;
        	
        	return secMsg;
		} 
		catch (Exception e) 
		{
			System.out.println("Tu viejaaaaaa");
        	System.err.println(e.getMessage());
        	System.exit(-1);
        	return null;
        }
	}

	/**
	 * Securiza, mediante el tag userNameToken, una petición SOAP no securizada.
	 * 
	 * @param soapRequest Documento xml que representa la petición SOAP sin securizar.
	 * @return Un mensaje SOAP que contiene la petición SOAP de entrada securizada 
	 * mediante el tag userNameToken.
	 */
	private String createUserNameToken(Document soapEnvelopeRequest)
	{
		ByteArrayOutputStream baos;
		Document secSOAPReqDoc;
		DOMSource source;
		Element element;
		StreamResult streamResult;
		String secSOAPReq;
		WSSecUsernameToken wsSecUsernameToken;
		WSSecHeader wsSecHeader;

		try
		{
			//Inserción del tag wsse:Security y userNameToken
			wsSecHeader = new WSSecHeader(null,false);
			wsSecUsernameToken = new WSSecUsernameToken();
			wsSecUsernameToken.setPasswordType(this.userTokenUserPasswordType);
			wsSecUsernameToken.setUserInfo(this.userTokenUserName, this.userTokenUserPassword);
			wsSecHeader.insertSecurityHeader(soapEnvelopeRequest);
			wsSecUsernameToken.prepare(soapEnvelopeRequest);
			
	    	//Añadimos una marca de tiempo inidicando la fecha de creación del tag
			wsSecUsernameToken.addCreated();
			wsSecUsernameToken.addNonce();
			
			//Modificación de la petición
			secSOAPReqDoc = wsSecUsernameToken.build(soapEnvelopeRequest,wsSecHeader);
			element = secSOAPReqDoc.getDocumentElement();
	
	    	//Transformación del elemento DOM a String
	        source = new DOMSource(element);
	        baos = new ByteArrayOutputStream();
	        streamResult = new StreamResult(baos);
	        TransformerFactory.newInstance().newTransformer().transform(source, streamResult);
	        secSOAPReq = new String(baos.toByteArray());
	
			return secSOAPReq;
		}
		catch (Exception e) 
		{
        	System.err.println(e.getMessage());
        	System.exit(-1);
        	return null;
        }
	}
	
	/**
	 * Securiza, mediante el tag BinarySecurityToken y firma una petición SOAP no securizada.
	 * 
	 * @param soapEnvelopeRequest Documento xml que representa la petición SOAP sin securizar.
	 * @return Un mensaje SOAP que contiene la petición SOAP de entrada securizada 
	 * mediante el tag BinarySecurityToken.
	 */
	private String createBinarySecurityToken(Document soapEnvelopeRequest)
	{
		ByteArrayOutputStream baos;
		Crypto crypto;
		Document secSOAPReqDoc;
		DOMSource source;
		Element element;
		StreamResult streamResult;
		String secSOAPReq;
		WSSecSignature wsSecSignature; 
		WSSecHeader wsSecHeader;

		try
		{
			//Inserción del tag wsse:Security y BinarySecurityToken
			wsSecHeader = new WSSecHeader(null, false);
			wsSecSignature = new WSSecSignature();
			crypto = CryptoFactory.getInstance("org.apache.ws.security.components.crypto.Merlin", this.initializateCryptoProperties());
			
			//Indicación para que inserte el tag BinarySecurityToken
			wsSecSignature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
			
			//wsSecSignature.setKeyIdentifierType(WSConstants.ISSUER_SERIAL);
			wsSecSignature.setUserInfo(this.keystoreCertAlias, this.keystoreCertPassword);
			wsSecHeader.insertSecurityHeader(soapEnvelopeRequest);
			wsSecSignature.prepare(soapEnvelopeRequest,crypto,wsSecHeader);
	
			//Modificación y firma de la petición
			secSOAPReqDoc = wsSecSignature.build(soapEnvelopeRequest,crypto,wsSecHeader);
			element = secSOAPReqDoc.getDocumentElement();
			
	    	//Transformación del elemento DOM a String
	        source = new DOMSource(element);
	        baos = new ByteArrayOutputStream();
	        streamResult = new StreamResult(baos);
	        TransformerFactory.newInstance().newTransformer().transform(source, streamResult);
	        secSOAPReq = new String(baos.toByteArray());
	
			return secSOAPReq;
		}
		catch (Exception e) 
		{
        	System.err.println(e.getMessage());
        	System.exit(-1);
        	return null;
        }
	}
	
	/**
	 * Establece el conjunto de propiedades con el que será inicializado el gestor criptográfico de WSS4J.
	 * 
	 * @return Devuelve el conjunto de propiedades con el que será inicializado el gestor criptográfico de WSS4J. 
	 */
	private Properties initializateCryptoProperties()
	{
		Properties res = new Properties();
		res.setProperty("org.apache.ws.security.crypto.provider","org.apache.ws.security.components.crypto.Merlin");
		res.setProperty("org.apache.ws.security.crypto.merlin.keystore.type",this.keystoreType);
		res.setProperty("org.apache.ws.security.crypto.merlin.keystore.password",this.keystorePassword);
		res.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias",this.keystoreCertAlias);
		res.setProperty("org.apache.ws.security.crypto.merlin.alias.password",this.keystoreCertPassword);
		res.setProperty("org.apache.ws.security.crypto.merlin.file",this.keystoreLocation);
		
		return res;
	}
}
