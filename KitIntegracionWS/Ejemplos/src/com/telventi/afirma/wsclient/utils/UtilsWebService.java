/** 
* <p>Fichero: UtilsWebService.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 23-jun-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Use;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.wss4j.ClientHandler;
import com.telventi.afirma.wsclient.wss4j.SOAPSigner;

/**
 * @author SEJLHA
 *
 */
public class UtilsWebService
{
	private static final String PROXY_NONE = "NONE";
	private static final String PROXY_NONE_AUTHENTICATION = "NONE_AUTHENTICATION";
	private static final String PROXY_BASIC_AUTHENTICATION = "BASIC_AUTHENTICATION";
	private static final String PROXY_NTLM_AUTHENTICATION = "NTLM_AUTHENTICATION";
	
	private static javax.xml.parsers.DocumentBuilder db = null;
	
	private static Properties webServicesConfiguration = null;
	private static Properties securityConfiguration = null;
	
	// Timeout configurado para las llamadas a los servicios Web
	private static int TIMER;
	// Endpoint donde se localizan los servicios Web 
	protected static String ENDPOINT = null;
	
	protected static String afirmaIP=null;
	protected static int afirmaPort;

	// Modo de operación respecto al proxy
	protected static String proxyOperational=null;
	// IP del proxy
	protected static String proxyIP=null;
	// Puerto de escucha del proxy
	protected static int proxyPort;
	// Nombre de usuario (Proxy con autenticación)
	protected static String proxyUser=null;
	// Clave de usuario (Proxy con autenticación)
	protected static String proxyPassword=null;
	// Dominio de usuario (Proxy con autenticación)
	protected static String proxyDomain=null;

	
	static 
	{
		// Obtención de un parseador de XML
		javax.xml.parsers.DocumentBuilderFactory dbf =javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		try
		{
			db = dbf.newDocumentBuilder();
		}
		catch (Exception e)
		{
			System.err.println("Error en inicialización");
			System.exit(-1);
		}
		
		// Carga del fichero de configuración de propiedades de seguridad
		securityConfiguration = new Properties();
		try
		{
			URL url =  ClassLoader.getSystemResource("securityConfiguration.properties");
			securityConfiguration.load(new FileInputStream(new File(url.getFile())));
		}
		catch (Exception e)
		{
			System.err.println("Error cargando el fichero de properties securityConfiguration.properties");
			System.exit(-1);
		}	

		// Carga del fichero de configuración de propiedades de los servicios Web
		webServicesConfiguration = new Properties();
		try
		{
			URL url =  ClassLoader.getSystemResource("webServicesConfiguration.properties");
			webServicesConfiguration.load(new FileInputStream(new File(url.getFile())));
			
			// 				Configuración de la Plataforma
			
			// protocolo
			String afirmaProtocol = webServicesConfiguration.getProperty("afirma.protocol");
			afirmaProtocol = (afirmaProtocol==null || afirmaProtocol.trim().equals("")) ? "http" : afirmaProtocol.trim();
			
			// ip
			afirmaIP = webServicesConfiguration.getProperty("afirma.ip");
			if (afirmaIP == null || afirmaIP.equals(""))
				throw new Exception ("La dirección IP debe definirse");
			else
				afirmaIP = afirmaIP.trim();

			// puerto
			String afirmaPortAux = webServicesConfiguration.getProperty("afirma.port");
			afirmaPort = (afirmaPortAux==null || afirmaPortAux.trim().equals("")) ? 80 : Integer.parseInt(afirmaPortAux.trim());
			
			// sufijo
			String afirmaSufix = webServicesConfiguration.getProperty("afirma.sufix");
			afirmaSufix = (afirmaSufix==null) ? "" : afirmaSufix.trim();			
			
			// Dirección IP donde está localizada la plataforma @firma 5.0 + ruta a los servicios dentro del servidor Web
			ENDPOINT = afirmaProtocol + "://" + afirmaIP + ":" + afirmaPort + afirmaSufix;
			
			// Timer para el timeout en el socket
			// TIMER = 0 --> Socket bloqueante
			String timer = webServicesConfiguration.getProperty("socket.timer");
			TIMER = (timer == null || timer.equals("")) ? 0 : Integer.parseInt(timer.trim());
			
			// Keystore con los certificados de confianza de la Plataforma @Firma 5.0 para las comunicaciones sobre SSL
			String sslKeystore = webServicesConfiguration.getProperty("afirma.ssl.keystore");
			if (sslKeystore != null && !sslKeystore.trim().equals(""))
				System.setProperty("javax.net.ssl.trustStore", sslKeystore.trim());
			
			//				 Configuración del PROXY
			proxyOperational = webServicesConfiguration.getProperty("proxy.operational");

			if (proxyOperational == null || proxyOperational.equals("") || (!proxyOperational.equals(PROXY_NONE) &&
					!proxyOperational.equals(PROXY_NONE_AUTHENTICATION) && !proxyOperational.equals(PROXY_BASIC_AUTHENTICATION) && 
					!proxyOperational.equals(PROXY_NTLM_AUTHENTICATION)))
				proxyOperational = PROXY_NONE;
			else
				proxyOperational = proxyOperational.trim();
			
			// En caso de usar configuración por proxy
			if (!proxyOperational.equals(PROXY_NONE))
			{
				System.setProperty("proxySet", "true");

				proxyIP = webServicesConfiguration.getProperty("proxy.ip");
				String proxyPortAux = webServicesConfiguration.getProperty("proxy.port");
				
				if (proxyIP != null && !proxyIP.trim().equals(""))
				{
					proxyIP = proxyIP.trim();
					
					System.setProperty("http.proxyHost", proxyIP);
					
					if (proxyPortAux != null && !proxyPortAux.trim().equals(""))
					{
						proxyPortAux = proxyPortAux.trim();
						proxyPort = Integer.parseInt(proxyPortAux);
						System.setProperty("http.proxyPort", proxyPortAux);
					}
					else
						proxyPort = 80;
				}
				else
					throw new Exception ("La dirección IP del Proxy debe definirse");
				
				if  (proxyOperational.equals(PROXY_BASIC_AUTHENTICATION) || proxyOperational.equals(PROXY_NTLM_AUTHENTICATION))
				{
					proxyUser = webServicesConfiguration.getProperty("proxy.user.name");
					proxyPassword = webServicesConfiguration.getProperty("proxy.user.password");					

					if (proxyUser != null && !proxyUser.trim().equals(""))
					{
						proxyUser = proxyUser.trim();
						
						System.setProperty("http.proxyUser", proxyUser);
						
						if (proxyPassword != null && !proxyPassword.trim().equals(""))
						{
							proxyPassword = proxyPassword.trim();
							System.setProperty("http.proxyPassword", proxyPassword);
						}
						else
							throw new Exception ("La clave de usuario debe definirse");
					}
					else
						throw new Exception ("El nombre de usuario debe definirse");
				}
				
				if  (proxyOperational.equals(PROXY_NTLM_AUTHENTICATION))
				{
					proxyDomain = webServicesConfiguration.getProperty("proxy.domain");					
					proxyDomain = (proxyDomain == null || proxyDomain.trim().equals("")) ? "" : proxyDomain.trim();
				}
//				System.setProperty("http.auth.ntlm.domain", proxyDomain);
//				Authenticator.setDefault(new HTTPProxyAuthenticator(proxyUser, proxyPassword));
			}
		}
		catch (Exception e)
		{
			System.err.println("Error cargando el fichero de properties webServicesConfiguration.properties");
			e.printStackTrace();
			System.exit(-1);
		}	
	}
	
	// CUSTODIA
	
	/**
	 * Prepara la petición al Servicio Web "AlmacenarDocumento", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param fileName Nombre del documento
	 * @param fileType Formato del documento
	 * @param fileContent Contenido del documento codificado en Base64
	 */
	public static synchronized Document prepareCustodyDocumentRequest(String appId, String fileName, String fileType, String fileContentBase64Encoded)
	{
		try
		{			
			Document custodyDocumentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.CustodyDocumentRequest.getBytes("UTF-8")));	
			
			// Id de aplicación
			NodeList applicationNode = custodyDocumentRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = custodyDocumentRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			// Contenido del documento en Base64
			NodeList documentNode = custodyDocumentRequest.getElementsByTagName("documento");
			CDATASection documentValueNode = custodyDocumentRequest.createCDATASection(fileContentBase64Encoded);
			documentNode.item(0).appendChild(documentValueNode);
			
			// Nombre del documento
			NodeList documentNameNode = custodyDocumentRequest.getElementsByTagName("nombreDocumento");
			Text documentNameValueNode = custodyDocumentRequest.createTextNode(fileName);
			documentNameNode.item(0).appendChild(documentNameValueNode);
			
			// Tipo del documento
			NodeList documentTypeNode = custodyDocumentRequest.getElementsByTagName("tipoDocumento");
			Text documentTypeValueNode = custodyDocumentRequest.createTextNode(fileType);
			documentTypeNode.item(0).appendChild(documentTypeValueNode);
			
			return custodyDocumentRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de custodia del documento " + fileName);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerContenidoDocumento", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param transactionId Identificador de la transacción sobre el documento del cual se desea recuperar en contenido
	 */
	public static synchronized Document prepareGetDocumentContentRequest(String appId, String transactionId)
	{
		try
		{
			Document getDocumentContentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentContentRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getDocumentContentRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getDocumentContentRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList transactionIdNode = getDocumentContentRequest.getElementsByTagName("idTransaccion");
			Text transactionIdValueNode=getDocumentContentRequest.createTextNode(transactionId);
			transactionIdNode.item(0).appendChild(transactionIdValueNode);
			
			return getDocumentContentRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención del contenido del documento.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerContenidoDocumentoId", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param docId Identificador del documento del cual se desea recuperar el contenido
	 */
	public static synchronized Document prepareGetDocumentContentIdRequest(String appId, String docId)
	{
		try
		{
			Document getDocumentContentIdRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentContentIdRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getDocumentContentIdRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getDocumentContentIdRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList docIdNode = getDocumentContentIdRequest.getElementsByTagName("idDocumento");
			Text docIdValueNode=getDocumentContentIdRequest.createTextNode(docId);
			docIdNode.item(0).appendChild(docIdValueNode);
			
			return getDocumentContentIdRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención del contenido del documento por id de documento.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerIdDocumento", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param transactionId Identificador de transacción sobre el documento firmado del cual se desea recuperar el identificador
	 */
	public static synchronized Document prepareGetDocumentIdRequest(String appId, String transactionId)
	{
		try
		{
			Document getDocumentIdRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentIdRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getDocumentIdRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getDocumentIdRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList transactionIdNode = getDocumentIdRequest.getElementsByTagName("idTransaccion");
			Text transactionIdValueNode=getDocumentIdRequest.createTextNode(transactionId);
			transactionIdNode.item(0).appendChild(transactionIdValueNode);
			
			return getDocumentIdRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención del identificador del documento por id de transacción.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "EliminarContenidoDocumento", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param docId Identificador del documento del cual se desea borrar el contenido
	 */
	public static synchronized Document prepareDeleteDocumentContentRequest(String appId, String docId)
	{
		try
		{
			Document deleteDocumentContentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.DeleteDocumentContentRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = deleteDocumentContentRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=deleteDocumentContentRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList docIdNode = deleteDocumentContentRequest.getElementsByTagName("idDocumento");
			Text docIdValueNode=deleteDocumentContentRequest.createTextNode(docId);
			docIdNode.item(0).appendChild(docIdValueNode);
			
			return deleteDocumentContentRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de borrado del contenido del documento.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ActualizarReferencia", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param transactionId Identificador de la transacción de la que se desea actualizar la referencia
	 */
	public static synchronized Document prepareSetExternalReferenceRequest(String appId, String transactionId, String externalReference)
	{
		try
		{
			Document setExternalReferenceRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.SetExternalReferenceRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = setExternalReferenceRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=setExternalReferenceRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de transacción
			NodeList transactionIdNode = setExternalReferenceRequest.getElementsByTagName("idTransaccion");
			Text transactionIdValueNode=setExternalReferenceRequest.createTextNode(transactionId);
			transactionIdNode.item(0).appendChild(transactionIdValueNode);
			
			//Referencia externa
			NodeList externalReferenceNode = setExternalReferenceRequest.getElementsByTagName("referencia");
			Text externalReferenceValueNode=setExternalReferenceRequest.createTextNode(externalReference);
			externalReferenceNode.item(0).appendChild(externalReferenceValueNode);

			return setExternalReferenceRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de actualización de referencia externa de transacción.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerTransaccionesReferencia", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param externalReference Referencia externa mediante la cual realizar la búsqueda
	 */
	public static synchronized Document prepareGetTransactionsByExternalReference(String appId, String externalReference)
	{
		try
		{
			Document getTransactionsByExternalReferenceRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsByExternalReferenceRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getTransactionsByExternalReferenceRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getTransactionsByExternalReferenceRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Referencia externa
			NodeList externalReferenceNode = getTransactionsByExternalReferenceRequest.getElementsByTagName("idReferencia");
			Text externalReferenceValueNode=getTransactionsByExternalReferenceRequest.createTextNode(externalReference);
			externalReferenceNode.item(0).appendChild(externalReferenceValueNode);

			return getTransactionsByExternalReferenceRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de busqueda de transacciones por referencia externa.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerTransaccionesPorFecha", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param startDate Fecha de inicio
	 * @param endDate Fecha de fin
	 */
	public static synchronized Document prepareGetTransactionsByDateRequest(String appId, String startDate, String endDate)
	{
		try
		{
			Document getTransactionsByDateRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsByDateRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getTransactionsByDateRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getTransactionsByDateRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Fecha de inicio
			NodeList startDateNode = getTransactionsByDateRequest.getElementsByTagName("fechaInicial");
			Text startDateValueNode=getTransactionsByDateRequest.createTextNode(startDate);
			startDateNode.item(0).appendChild(startDateValueNode);

			//Fecha de fin
			NodeList endDateNode = getTransactionsByDateRequest.getElementsByTagName("fechaFinal");
			Text endDateValueNode=getTransactionsByDateRequest.createTextNode(endDate);
			endDateNode.item(0).appendChild(endDateValueNode);

			return getTransactionsByDateRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de busqueda de transacciones por fechas.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Prepara la petición al Servicio Web "ObtenerTransacciones", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 */
	public static synchronized Document prepareGetTransactionsRequest(String appId)
	{
		try
		{
			Document getTransactionsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getTransactionsRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getTransactionsRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			return getTransactionsRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de busqueda de transacciones.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerFirmaTransaccion", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de la transaccion de la cual se desea extraer la Firma Electrónica
	 */
	public static synchronized Document prepareGetESignatureRequest(String appId, String idTransaction)
	{
		try
		{
			Document getESignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetESignatureRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getESignatureRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getESignatureRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de la transacción
			NodeList idTransationNode = getESignatureRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=getESignatureRequest.createTextNode(idTransaction);
			idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return getESignatureRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de la Firma Electrónica de una transacción.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}	
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerBloqueFirmas", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas
	 */
	public static synchronized Document prepareGetSignaturesBlockRequest(String appId, String idTransaction)
	{
		try
		{
			Document getSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetSignaturesBlockRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getSignaturesBlockRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getSignaturesBlockRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de la transacción
			NodeList idTransationNode = getSignaturesBlockRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=getSignaturesBlockRequest.createTextNode(idTransaction);
			idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return getSignaturesBlockRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de un bloque de firmas.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}	

	// FIRMA
	
	/**
	 * Prepara la petición al Servicio Web "ValidarFirma", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param eSignature Firma Electrónica a validar (codificada en Base64)
	 * @param eSignatureFormat Formato de la Firma Electrónica a validar
	 * @param hash Hash de los datos firmados codificado en Base64.
	 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
	 * @param data Datos originales firmados codificados en Base64.
	 */
	public static synchronized Document prepareValidateSignatureRequest(String appId, String eSignature, String eSignatureFormat, byte[] hash, String hashAlgorithm, byte[] data)
	{
		try
		{
			Document signatureValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.SignatureValidationRequest.getBytes("UTF-8")));
			
			// Id de aplicación
			NodeList applicationNode = signatureValidationRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = signatureValidationRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			// Firma Electrónica a validar
			NodeList eSignatureNode = signatureValidationRequest.getElementsByTagName("firmaElectronica");
			CDATASection eSignatureValueNode = signatureValidationRequest.createCDATASection(eSignature);
			eSignatureNode.item(0).appendChild(eSignatureValueNode);
			
			// Formato de firma
			NodeList eSignatureFormatNode = signatureValidationRequest.getElementsByTagName("formatoFirma");
			Text eSignatureFormatValueNode = signatureValidationRequest.createTextNode(eSignatureFormat);
			eSignatureFormatNode.item(0).appendChild(eSignatureFormatValueNode);

			// Algoritmo de hash
			if (hash != null)
			{
				NodeList hashNode = signatureValidationRequest.getElementsByTagName("hash");
				CDATASection hashValueNode = signatureValidationRequest.createCDATASection(new String(hash));
				hashNode.item(0).appendChild(hashValueNode);

				NodeList hashAlgorithmNode = signatureValidationRequest.getElementsByTagName("algoritmoHash");
				Text hashAlgorithmValueNode = signatureValidationRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
			}	
			
			// Datos
			if (data != null)
			{
				NodeList dataNode = signatureValidationRequest.getElementsByTagName("datos");
				CDATASection dataValueNode = signatureValidationRequest.createCDATASection(new String(data));
				dataNode.item(0).appendChild(dataValueNode);
			}
			
			return signatureValidationRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de validación de firma");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaServidor", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param docId Identificador del documento
	 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
	 * @param signatureFormat Formato de Firma Electrónica a generar
	 */
	public static synchronized Document prepareServerSignatureRequest(String appId, String docId, String aliasServerCert, String referenceId, String hashAlgorithm, String signatureFormat)
	{
		try
		{
			Document serverSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureRequest.getBytes("UTF-8")));
			
			// Id de aplicación
			NodeList applicationNode = serverSignatureRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = serverSignatureRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			// Id del documento a firmar
			NodeList idDocumentNode = serverSignatureRequest.getElementsByTagName("idDocumento");
			Text idDocumentValueNode = serverSignatureRequest.createTextNode(docId);
			idDocumentNode.item(0).appendChild(idDocumentValueNode);
			
			// Alias del certificado servidor a emplear en la Firma Electrónica
			NodeList signerNode = serverSignatureRequest.getElementsByTagName("firmante");
			Text signerValueNode = serverSignatureRequest.createTextNode(aliasServerCert);
			signerNode.item(0).appendChild(signerValueNode);
				
			// Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = serverSignatureRequest.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = serverSignatureRequest.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}
			
			// Algoritmo de hash
			NodeList hashAlgorithmNode = serverSignatureRequest.getElementsByTagName("algoritmoHash");
			Text hashAlgorithmValueNode = serverSignatureRequest.createTextNode(hashAlgorithm);
			hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
			
			// Formato de firma
			NodeList signatureFormatNode = serverSignatureRequest.getElementsByTagName("formatoFirma");
			Text signatureFormatValueNode = serverSignatureRequest.createTextNode(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
			
			return serverSignatureRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma servidor del documento con id=" + docId);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Mentodo que obtiene una petición de firma de Servidor incluyendo los datos a ser firmados.
	 * @param appId				Identificador de aplicación
	 * @param document			Datos a ser firmados (Codificado en Base 64)
	 * @param aliasServerCert	Firmante.
	 * @param referenceId		Identificador de referencia.
	 * @param hashAlgorithm		Algoritmo de hash.
	 * @param signatureFormat	Formato de firma
	 * @return					XML de peticion.
	 */
	public static synchronized Document prepareServerSignatureIncDocRequest(String appId, String document, String aliasServerCert, String referenceId, String hashAlgorithm, String signatureFormat, String documentName, String documentType)
	{
		try
		{
			Document serverSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureIncDocRequest.getBytes("UTF-8")));
			
			// Id de aplicación
			NodeList applicationNode = serverSignatureRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = serverSignatureRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			//Documento a firmar
			NodeList documentNode = serverSignatureRequest.getElementsByTagName("documento");
			Text documentValueNode = serverSignatureRequest.createCDATASection(document);
			documentNode.item(0).appendChild(documentValueNode);
			
			
			// Alias del certificado servidor a emplear en la Firma Electrónica
			NodeList signerNode = serverSignatureRequest.getElementsByTagName("firmante");
			Text signerValueNode = serverSignatureRequest.createTextNode(aliasServerCert);
			signerNode.item(0).appendChild(signerValueNode);
				
			// Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = serverSignatureRequest.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = serverSignatureRequest.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}
			
			// Algoritmo de hash
			NodeList hashAlgorithmNode = serverSignatureRequest.getElementsByTagName("algoritmoHash");
			Text hashAlgorithmValueNode = serverSignatureRequest.createTextNode(hashAlgorithm);
			hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
			
			// Formato de firma
			NodeList signatureFormatNode = serverSignatureRequest.getElementsByTagName("formatoFirma");
			Text signatureFormatValueNode = serverSignatureRequest.createTextNode(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
			
			//nombre documento
			NodeList documentNameNode = serverSignatureRequest.getElementsByTagName("nombreDocumento");
			Text documentNameNodeValueNode=serverSignatureRequest.createTextNode(documentName);
			documentNameNode.item(0).appendChild(documentNameNodeValueNode);
			
			//tipo documento
			NodeList documentTypeNode = serverSignatureRequest.getElementsByTagName("tipoDocumento");
			Text documentTypeNodeValueNode=serverSignatureRequest.createTextNode(documentType);
			documentTypeNode.item(0).appendChild(documentTypeNodeValueNode);
			
			return serverSignatureRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma servidor");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	/**
	 * Prepara la petición al Servicio Web "FirmaServidorCoSign", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transaccion de firma sobre la que se desea hacer la multifirma cosign
	 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
	 */
	public static synchronized Document prepareServerSignatureCoSignRequest(String appId, String idTransaction, String aliasServerCert, String referenceId, String hashAlgorithm)
	{
		try
		{
			Document serverSignatureCoSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureCoSignRequest.getBytes("UTF-8")));
			
			// Id de aplicación
			NodeList applicationNode = serverSignatureCoSignRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = serverSignatureCoSignRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			// Id de transaccion
			NodeList idTransactionNode = serverSignatureCoSignRequest.getElementsByTagName("idTransaccion");
			Text idTransactionValueNode = serverSignatureCoSignRequest.createTextNode(idTransaction);
			idTransactionNode.item(0).appendChild(idTransactionValueNode);
			
			// Alias del certificado servidor a emplear en la Firma Electrónica
			NodeList signerNode = serverSignatureCoSignRequest.getElementsByTagName("firmante");
			Text signerValueNode = serverSignatureCoSignRequest.createTextNode(aliasServerCert);
			signerNode.item(0).appendChild(signerValueNode);
			
			// Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = serverSignatureCoSignRequest.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = serverSignatureCoSignRequest.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}

			// Algoritmo de hash
			NodeList hashAlgorithmNode = serverSignatureCoSignRequest.getElementsByTagName("algoritmoHash");
			Text hashAlgorithmValueNode = serverSignatureCoSignRequest.createTextNode(hashAlgorithm);
			hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);			

			return serverSignatureCoSignRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma servidor cosign de la transaccion con id=" + idTransaction);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaServidorCoSign", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transaccion de firma sobre la que se desea hacer la multifirma cosign
	 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
	 * @param firmanteObjetivo Certificado X09 codificado en Base 64 del firmante sobre el que realizar la firma counterSign
	 */
	public static synchronized Document prepareServerSignatureCounterSignRequest(String appId, String idTransaction, String aliasServerCert, String referenceId, String hashAlgorithm, byte[] signer)
	{
		try
		{
			Document serverSignatureCounterSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureCounterSignRequest.getBytes("UTF-8")));
			
			// Id de aplicación
			NodeList applicationNode = serverSignatureCounterSignRequest.getElementsByTagName("idAplicacion");
			Text applicationValueNode = serverSignatureCounterSignRequest.createTextNode(appId);
			applicationNode.item(0).appendChild(applicationValueNode);
			
			// Id de transaccion
			NodeList idTransactionNode = serverSignatureCounterSignRequest.getElementsByTagName("idTransaccion");
			Text idTransactionValueNode = serverSignatureCounterSignRequest.createTextNode(idTransaction);
			idTransactionNode.item(0).appendChild(idTransactionValueNode);
			
			// Alias del certificado servidor a emplear en la Firma Electrónica
			NodeList signerNode = serverSignatureCounterSignRequest.getElementsByTagName("firmante");
			Text signerValueNode = serverSignatureCounterSignRequest.createTextNode(aliasServerCert);
			signerNode.item(0).appendChild(signerValueNode);
			
			// Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = serverSignatureCounterSignRequest.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = serverSignatureCounterSignRequest.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}			

			// Algoritmo de hash
			NodeList hashAlgorithmNode = serverSignatureCounterSignRequest.getElementsByTagName("algoritmoHash");
			Text hashAlgorithmValueNode = serverSignatureCounterSignRequest.createTextNode(hashAlgorithm);
			hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
			
			// Firmante Objetivo
			if (signer!=null)
			{
				NodeList objectiveSignerNode = serverSignatureCounterSignRequest.getElementsByTagName("firmanteObjetivo");
				Text objectiveSignerValueNode = serverSignatureCounterSignRequest.createCDATASection(new String(signer));
				objectiveSignerNode.item(0).appendChild(objectiveSignerValueNode);
			}			

			return serverSignatureCounterSignRequest;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma servidor countersign de la transaccion con id=" + idTransaction);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idDoc Identificador del documento a firmar
	 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
	 */
	public static synchronized Document prepareThreePhasesUserSignatureF1Request(String appId, String idDoc, String hashAlgorithm)
	{
		try
		{
			Document threeFasesUserSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1Request.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = threeFasesUserSignatureRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=threeFasesUserSignatureRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList documentNode = threeFasesUserSignatureRequest.getElementsByTagName("idDocumento");
			Text documentValueNode=threeFasesUserSignatureRequest.createTextNode(idDoc);
			documentNode.item(0).appendChild(documentValueNode);			
			
			//Algoritmo Hash
			NodeList algorithmHashNode = threeFasesUserSignatureRequest.getElementsByTagName("algoritmoHash");
			Text algorithmHashNodeValueNode=threeFasesUserSignatureRequest.createTextNode(hashAlgorithm);
			algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
			
			return threeFasesUserSignatureRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario 3 Fases F1.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1CoSign", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transacción de firma sobre la que se desea hacer la multifirma CoSign
	 * @param hashAlgorithm Algoritmo de hash a emplear en la firma.
	 */
	public static synchronized Document prepareThreePhasesUserSignaturesF1CoSignRequest(String appId, String idTransaction, String hashAlgorithm)
	{
		try
		{
			Document threePhasesUserSignaturesCoSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1CoSignRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id Transaccion
			NodeList idTransactionNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(idTransaction);
			idTransactionNode.item(0).appendChild(idTransactionNodeValueNode);
			

			//Algoritmo Hash
			NodeList hashAlgorithmNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("algoritmoHash");
			Text hashAlgorithmValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(hashAlgorithm);
			hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
			
			return threePhasesUserSignaturesCoSignRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario 3 fases F1 cosign.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1CounterSign", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transacción de firma sobre la que se desea hacer la multifirma CounterSign
	 */
	public static synchronized Document prepareThreePhasesUserSignaturesF1CounterSignRequest(String appId, String idTransaction)
	{
		try
		{
			Document threePhasesUserSignaturesCounterSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1CounterSignRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = threePhasesUserSignaturesCounterSignRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=threePhasesUserSignaturesCounterSignRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id Transaccion
			NodeList idTransactionNode = threePhasesUserSignaturesCounterSignRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=threePhasesUserSignaturesCounterSignRequest.createTextNode(idTransaction);
			idTransactionNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return threePhasesUserSignaturesCounterSignRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario 3 fases F1 countersign.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF3", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transaccion a finalizar
	 * @param signature Firma Electrónica codificada en base 64
	 * @param signCertificate Certificado con el que se ha realizado la firma
	 * @param SignatureFormat Formato de la Firma 
	 */
	public static synchronized Document prepareThreePhasesUserSignatureF3Request(String appId, String idTransaction, String signature, String signCertificate, String signatureFormat, String updateSignatureFormat, String referenceId)
	{
		try
		{
			Document threeFasesUserSignatureF3Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF3Request.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = threeFasesUserSignatureF3Request.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=threeFasesUserSignatureF3Request.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de transaccion
			NodeList transactionNode = threeFasesUserSignatureF3Request.getElementsByTagName("idTransaccion");
			Text transactionValueNode=threeFasesUserSignatureF3Request.createTextNode(idTransaction);
			transactionNode.item(0).appendChild(transactionValueNode);
			
			//Firma Electronica
			NodeList signatureNode = threeFasesUserSignatureF3Request.getElementsByTagName("firmaElectronica");
			Text signatureValueNode=threeFasesUserSignatureF3Request.createTextNode(signature);
			signatureNode.item(0).appendChild(signatureValueNode);
			
			//Certificado Firmante
			NodeList signCertificateNode = threeFasesUserSignatureF3Request.getElementsByTagName("certificadoFirmante");
			Text signCertificateValueNode=threeFasesUserSignatureF3Request.createTextNode(signCertificate);
			signCertificateNode.item(0).appendChild(signCertificateValueNode);
			
			//Formato Firma
			NodeList signatureFormatNode = threeFasesUserSignatureF3Request.getElementsByTagName("formatoFirma");
			Text signatureFormatNodeValueNode=threeFasesUserSignatureF3Request.createTextNode(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatNodeValueNode);
			
			// Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = threeFasesUserSignatureF3Request.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = threeFasesUserSignatureF3Request.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}
			
			//Extender Formato de Firma
//			NodeList updateSignatureFormatNode = threeFasesUserSignatureF3Request.getElementsByTagName("extenderFormatoFirma");
//			Text updateSignatureFormatNodeValueNode=threeFasesUserSignatureF3Request.createTextNode(updateSignatureFormat);
//			updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);
			
			return threeFasesUserSignatureF3Request;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario 3 Fases F3.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuario2FasesF2", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param signature Firma Electrónica codificada en base 64
	 * @param signCertificate Certificado con el que se ha realizado la firma
	 * @param SignatureFormat Formato de la Firma 
	 */
	public static synchronized Document prepareTwoPhasesUserSignatureF2Request(String appId, String signature, String signCertificate, String signatureFormat,String file, String fileType, String fileName, String hashAlgorithm, String updateSignatureFormat, String referenceId,boolean custodyDoc)
	{
		try
		{
			Document twoFasesUserSignatureF2Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.TwoPhasesUserSignatureF2Request.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = twoFasesUserSignatureF2Request.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=twoFasesUserSignatureF2Request.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Firma Electronica
			NodeList signatureNode = twoFasesUserSignatureF2Request.getElementsByTagName("firmaElectronica");
			CDATASection signatureValueNode=twoFasesUserSignatureF2Request.createCDATASection(signature);
			signatureNode.item(0).appendChild(signatureValueNode);
			
			//Certificado Firmante
			NodeList signCertificateNode = twoFasesUserSignatureF2Request.getElementsByTagName("certificadoFirmante");
			CDATASection signCertificateValueNode=twoFasesUserSignatureF2Request.createCDATASection(signCertificate);
			signCertificateNode.item(0).appendChild(signCertificateValueNode);
			
			//Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = twoFasesUserSignatureF2Request.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = twoFasesUserSignatureF2Request.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}
			
			//Formato Firma
			NodeList signatureFormatNode = twoFasesUserSignatureF2Request.getElementsByTagName("formatoFirma");
			CDATASection signatureFormatNodeValueNode=twoFasesUserSignatureF2Request.createCDATASection(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatNodeValueNode);
			
			//documento
			NodeList documentNode = twoFasesUserSignatureF2Request.getElementsByTagName("documento");
			CDATASection documentNodeValueNode=twoFasesUserSignatureF2Request.createCDATASection(file);
			documentNode.item(0).appendChild(documentNodeValueNode);
			
			//nombre documento
			NodeList documentNameNode = twoFasesUserSignatureF2Request.getElementsByTagName("nombreDocumento");
			Text documentNameNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(fileName);
			documentNameNode.item(0).appendChild(documentNameNodeValueNode);
			
			//tipo documento
			NodeList documentTypeNode = twoFasesUserSignatureF2Request.getElementsByTagName("tipoDocumento");
			Text documentTypeNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(fileType);
			documentTypeNode.item(0).appendChild(documentTypeNodeValueNode);
			
			//Algoritmo Hash
			NodeList algorithmHashNode = twoFasesUserSignatureF2Request.getElementsByTagName("algoritmoHash");
			Text algorithmHashNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(hashAlgorithm);
			algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
			
			//Custodiar documento
			NodeList custodiarDocNode = twoFasesUserSignatureF2Request.getElementsByTagName("custodiarDocumento");
			Text custodiarDocValueNode=twoFasesUserSignatureF2Request.createTextNode(""+custodyDoc);
			custodiarDocNode.item(0).appendChild(custodiarDocValueNode);
			
			
			//Extender Formato de Firma
			/*NodeList updateSignatureFormatNode = twoFasesUserSignatureF2Request.getElementsByTagName("extenderFormatoFirma");
			Text updateSignatureFormatNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(updateSignatureFormat);
			updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);*/
			
			
			return twoFasesUserSignatureF2Request;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario 2 Fases F2.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuarioBloquesF1", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idDocs Identificadores de documentos a firmar
	 * @param idTransactions Identificadores de transacciones de firma por bloques
	 * @param aliasServerCert Alias del certificado servidor con el cual hacer las Firmas Electrónicas Servidor a insertar en el bloque
	 * @param selectiveBlocks Información respecto a los documentos de otros bloques a multifirmar
	 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
	 */
	public static synchronized Document prepareBlockUserSignatureF1Request(String appId, String[] idDocs, long[] idTransactions, String aliasServerCert, HashMap selectiveBlocks, String hashAlgorithm)
	{
		try
		{
			Document blockUserSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockUserSignatureF1Request.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = blockUserSignatureRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=blockUserSignatureRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			// Alias del certificado servidor a emplear en las Firmas Electrónicas por bloques
			NodeList signerNode = blockUserSignatureRequest.getElementsByTagName("firmante");
			Text signerValueNode = blockUserSignatureRequest.createTextNode(aliasServerCert);
			signerNode.item(0).appendChild(signerValueNode);
			
			//Id documentos
			if (idDocs!=null)
			{
				NodeList documentsNode = blockUserSignatureRequest.getElementsByTagName("idDocumentos");
				Element documentElement=null;
				Text idDocValueNode=null;
				for (int i=0; i<idDocs.length;i++)
				{
					documentElement=blockUserSignatureRequest.createElement("idDocumento");
					idDocValueNode=blockUserSignatureRequest.createTextNode(idDocs[i]);
					documentElement.appendChild(idDocValueNode);
					documentsNode.item(0).appendChild(documentElement);
				}
			}
			
			//Id transacciones
			if (idTransactions!=null)
			{
				NodeList transactionNode = blockUserSignatureRequest.getElementsByTagName("idTransacciones");
				
				for (int i=0; i<idTransactions.length;i++)
				{
					Element transactionElement=blockUserSignatureRequest.createElement("idTransaccion");
					Text idTranValueNode=blockUserSignatureRequest.createTextNode(new Long(idTransactions[i]).toString());
					transactionElement.appendChild(idTranValueNode);
					transactionNode.item(0).appendChild(transactionElement);					
				}
			}
			
			// Documentos a Multifirmar
			if (selectiveBlocks!=null)
			{
				NodeList multiNode = blockUserSignatureRequest.getElementsByTagName("documentosMultifirma");
				
				Iterator it = selectiveBlocks.keySet().iterator();
				while(it.hasNext())
				{
					Long key = (Long)it.next();
					long value[] = (long[])selectiveBlocks.get(key);
					
					Element blockElement=blockUserSignatureRequest.createElement("bloqueSeleccionado");
					Element blockIdElement=blockUserSignatureRequest.createElement("idTransaccionBloque");
					Text blockIdValueNode=blockUserSignatureRequest.createTextNode(key.toString());
					
					blockIdElement.appendChild(blockIdValueNode);
					blockElement.appendChild(blockIdElement);

					Element selectedDocsElement=blockUserSignatureRequest.createElement("documentosSeleccionados");
					for (int i=0; i<value.length; i++)
					{
						Element selectedDocIdElement=blockUserSignatureRequest.createElement("idTransaccion");
						Text selectedDocIdValueNode=blockUserSignatureRequest.createTextNode(new Long(value[i]).toString());
						selectedDocIdElement.appendChild(selectedDocIdValueNode);
						selectedDocsElement.appendChild(selectedDocIdElement);
					}
					
					blockElement.appendChild(selectedDocsElement);
					
					multiNode.item(0).appendChild(blockElement);
				}
			}
			
			//Algoritmo Hash
			NodeList algorithmHashNode = blockUserSignatureRequest.getElementsByTagName("algoritmoHash");
			Text algorithmHashNodeValueNode=blockUserSignatureRequest.createTextNode(hashAlgorithm);
			algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
			
			return blockUserSignatureRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario por bloques F1.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "FirmaUsuarioBloquesF3", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de transaccion a finalizar
	 * @param signature Firma Electrónica codificada en base 64
	 * @param signCertificate Certificado con el que se ha realizado la firma
	 * @param SignatureFormat Formato de la Firma 
	 */
	public static synchronized Document prepareBlockUserSignatureF3Request(String appId, String idTransaction, String signature, String signCertificate, String signatureFormat, String updateSignatureFormat, String referenceId)
	{
		try
		{
			Document blockUserSignatureF3Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockUserSignatureF3Request.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = blockUserSignatureF3Request.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=blockUserSignatureF3Request.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de transaccion
			NodeList transactionNode = blockUserSignatureF3Request.getElementsByTagName("idTransaccion");
			Text transactionValueNode=blockUserSignatureF3Request.createTextNode(idTransaction);
			transactionNode.item(0).appendChild(transactionValueNode);
			
			//Firma Electronica
			NodeList signatureNode = blockUserSignatureF3Request.getElementsByTagName("firmaElectronica");
			Text signatureValueNode=blockUserSignatureF3Request.createTextNode(signature);
			signatureNode.item(0).appendChild(signatureValueNode);
			
			//Certificado Firmante
			NodeList signCertificateNode = blockUserSignatureF3Request.getElementsByTagName("certificadoFirmante");
			Text signCertificateValueNode=blockUserSignatureF3Request.createTextNode(signCertificate);
			signCertificateNode.item(0).appendChild(signCertificateValueNode);
			
			//Formato Firma
			NodeList algorithmHashNode = blockUserSignatureF3Request.getElementsByTagName("formatoFirma");
			Text algorithmHashNodeValueNode=blockUserSignatureF3Request.createTextNode(signatureFormat);
			algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
			
			//Extender Formato de Firma
//			NodeList updateSignatureFormatNode = blockUserSignatureF3Request.getElementsByTagName("extenderFormatoFirma");
//			Text updateSignatureFormatNodeValueNode=blockUserSignatureF3Request.createTextNode(updateSignatureFormat);
//			updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);
			
			//Id de referencia externo (proporcionado por la aplicacion)
			if (referenceId != null) {
				NodeList idReferenceNode = blockUserSignatureF3Request.getElementsByTagName("idReferencia");
				Text idReferenceValueNode = blockUserSignatureF3Request.createTextNode(referenceId);
				idReferenceNode.item(0).appendChild(idReferenceValueNode);
			}
			
			return blockUserSignatureF3Request;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de firma de usuario por bloques F3.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ValidarFirmaBloquesCompleto", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param signature Firma electronica del bloque a validar
	 * @param signaturesBlock Bloque de firas correspondiente a la Firma Electronica a validar.
	 * @param signatureFormat Formato de la firma.
	 */
	public static synchronized Document prepareBlockSignatureFullValidationRequest(String appId, String signature, String signaturesBlock, String signatureFormat)
	{
		try
		{
			Document blockSignatureFullValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockSignatureFullValidacionRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = blockSignatureFullValidationRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=blockSignatureFullValidationRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Firma electronica
			NodeList signtureNode = blockSignatureFullValidationRequest.getElementsByTagName("firmaElectronica");
			CDATASection signtureNodeValueNode=blockSignatureFullValidationRequest.createCDATASection(signature);
			signtureNode.item(0).appendChild(signtureNodeValueNode);
			
			//Bloque de firmas
			NodeList signturesBlockNode = blockSignatureFullValidationRequest.getElementsByTagName("bloqueFirmas");
			CDATASection signturesBlockNodeValueNode=blockSignatureFullValidationRequest.createCDATASection(signaturesBlock);
			signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
			
			//Formato de firma
			NodeList signatureFormatNode = blockSignatureFullValidationRequest.getElementsByTagName("formatoFirma");
			Text signatureFormatValueNode=blockSignatureFullValidationRequest.createTextNode(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
			
			return blockSignatureFullValidationRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de validación de firma por bloques completo.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ValidarFirmaBloquesDocumento", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param signature Firma electronica del bloque a validar
	 * @param document Documento original sobre el que se calculó la firma servidor.
	 * @param idDocument Identificador del documento sobre el que se desea validar la Firma Electronica.
	 * @param signatureFormat Formato de la firma.
	 */
	public static synchronized Document prepareBlockSignatureDocumentValidationRequest(String appId, String signature, String document, String idDocument, String signatureFormat)
	{
		try
		{
			Document blockSignatureDocumentValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockSignatureDocumentValidacionRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = blockSignatureDocumentValidationRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=blockSignatureDocumentValidationRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Firma electronica
			NodeList signtureNode = blockSignatureDocumentValidationRequest.getElementsByTagName("firmaElectronica");
			CDATASection signtureNodeValueNode=blockSignatureDocumentValidationRequest.createCDATASection(signature);
			signtureNode.item(0).appendChild(signtureNodeValueNode);
			
			//documento
			NodeList documentNode = blockSignatureDocumentValidationRequest.getElementsByTagName("documento");
			CDATASection documentNodeValueNode=blockSignatureDocumentValidationRequest.createCDATASection(document);
			documentNode.item(0).appendChild(documentNodeValueNode);
			
			//Identificador de documento
			NodeList idDocumentNode = blockSignatureDocumentValidationRequest.getElementsByTagName("idDocumento");
			Text idDocumentNodeValueNode=blockSignatureDocumentValidationRequest.createTextNode(idDocument);
			idDocumentNode.item(0).appendChild(idDocumentNodeValueNode);
			
			//Formato de firma
			NodeList signatureFormatNode = blockSignatureDocumentValidationRequest.getElementsByTagName("formatoFirma");
			Text signatureFormatValueNode=blockSignatureDocumentValidationRequest.createTextNode(signatureFormat);
			signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
			
			return blockSignatureDocumentValidationRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de validación de firma por bloques documento.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerIdDocumentosBloqueFirmas", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desean extraer los id de documentos
	 */
	public static synchronized Document prepareGetDocIdSignaturesBlockRequest(String appId, String idTransaction)
	{
		try
		{
			Document getDocIdSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocIdSignaturesBlockRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getDocIdSignaturesBlockRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getDocIdSignaturesBlockRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList idTransationNode = getDocIdSignaturesBlockRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=getDocIdSignaturesBlockRequest.createTextNode(idTransaction);
			idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return getDocIdSignaturesBlockRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de ids de documentos de un bloque de firmas.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerIdDocumentosBloqueFirmasBackwards", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param signaturesBlock Firma PCKS7 implícita con el bloque de Firmas incluido
	 */
	public static synchronized Document prepareGetDocIdSignaturesBlockBackwardsRequest(String appId, String signaturesBlock)
	{
		try
		{
			Document getDocIdSignturesBlockBackwardsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocIdSignaturesBlockBackwardsRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getDocIdSignturesBlockBackwardsRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getDocIdSignturesBlockBackwardsRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList signturesBlockNode = getDocIdSignturesBlockBackwardsRequest.getElementsByTagName("bloqueFirmas");
			CDATASection signturesBlockNodeValueNode=getDocIdSignturesBlockBackwardsRequest.createCDATASection(signaturesBlock);
			signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
			
			return getDocIdSignturesBlockBackwardsRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de ids de documentos de un bloque de firmas generado por @firma 4.0.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerInformationBloqueFirmas", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desean extraer los id de documentos
	 */
	public static synchronized Document prepareGetInformationSignaturesBlockRequest(String appId, String idTransaction)
	{
		try
		{
			Document getInformationSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetInformationSignaturesBlockRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getInformationSignaturesBlockRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getInformationSignaturesBlockRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList idTransationNode = getInformationSignaturesBlockRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=getInformationSignaturesBlockRequest.createTextNode(idTransaction);
			idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return getInformationSignaturesBlockRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de informacion de un bloque de firmas.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerInformacionBloqueFirmasBackwards", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param signaturesBlock Firma PCKS7 implícita con el bloque de Firmas incluido
	 */
	public static synchronized Document prepareGetInformationSignaturesBlockBackwardsRequest(String appId, String signaturesBlock)
	{
		try
		{
			Document getInformationSignturesBlockBackwardsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetInformationSignaturesBlockBackwardsRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getInformationSignturesBlockBackwardsRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getInformationSignturesBlockBackwardsRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList signturesBlockNode = getInformationSignturesBlockBackwardsRequest.getElementsByTagName("bloqueFirmas");
			CDATASection signturesBlockNodeValueNode=getInformationSignturesBlockBackwardsRequest.createCDATASection(signaturesBlock);
			signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
			
			return getInformationSignturesBlockBackwardsRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de informacion de un bloque de firmas generado por @firma 4.0.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerInfoCompletaBloqueFirmas", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desea extraer la información
	 */
	public static synchronized Document prepareGetCompleteInfoSignaturesBlockRequest(String appId, String idTransaction)
	{
		try
		{
			Document getCompleteInfoSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetCompleteInfoSignaturesBlockRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getCompleteInfoSignaturesBlockRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getCompleteInfoSignaturesBlockRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//Id de documento
			NodeList idTransationNode = getCompleteInfoSignaturesBlockRequest.getElementsByTagName("idTransaccion");
			Text idTransactionNodeValueNode=getCompleteInfoSignaturesBlockRequest.createTextNode(idTransaction);
			idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
			
			return getCompleteInfoSignaturesBlockRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de obtención de informacion completa de un bloque de firmas.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	// MODULO DE VALIDACION
	/**
	 * Prepara la petición al Servicio Web "ValidarCertificado", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param certificate Certificado a validar
	 * @param validationMode Modo de validacion 
	 * @param certificateInfo Obtener informacion del certificado
	 */
	public static synchronized Document prepareCertificateValidationRequest(String appId, String certificate, int validationMode, boolean certificateInfo)
	{
		try
		{
			Document certificateValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.CertificateValidationRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = certificateValidationRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=certificateValidationRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//certificado
			NodeList certificateNode = certificateValidationRequest.getElementsByTagName("certificado");
			CDATASection certificateValueNode=certificateValidationRequest.createCDATASection(certificate);
			certificateNode.item(0).appendChild(certificateValueNode);
			
			//ModoValidacion
			NodeList validationModeNode = certificateValidationRequest.getElementsByTagName("modoValidacion");
			Text validationModeValueNode=certificateValidationRequest.createTextNode(new Integer(validationMode).toString());
			validationModeNode.item(0).appendChild(validationModeValueNode);
			
			//Obtener Info
			NodeList certificateInfoNode = certificateValidationRequest.getElementsByTagName("obtenerInfo");
			Text certificateInfoValueNode=certificateValidationRequest.createTextNode(new Boolean(certificateInfo).toString());
			certificateInfoNode.item(0).appendChild(certificateInfoValueNode);
			
			
			return certificateValidationRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de validación de certificados.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Prepara la petición al Servicio Web "ObtenerInfoCertificado", con los parámetros indicados.
	 *
	 * @param appId Identificador de la aplicación
	 * @param certificate Certificado a validar
	 */
	public static synchronized Document prepareGetCertificateInfoRequest(String appId, String certificate)
	{
		try
		{
			Document getCertificateInfoRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetCertificateInfoRequest.getBytes("UTF-8")));
			
			//Id de aplicación
			NodeList aplicationNode = getCertificateInfoRequest.getElementsByTagName("idAplicacion");
			Text aplicationValueNode=getCertificateInfoRequest.createTextNode(appId);
			aplicationNode.item(0).appendChild(aplicationValueNode);
			
			//certificado
			NodeList certificateNode = getCertificateInfoRequest.getElementsByTagName("certificado");
			CDATASection certificateValueNode=getCertificateInfoRequest.createCDATASection(certificate);
			certificateNode.item(0).appendChild(certificateValueNode);			
			
			return getCertificateInfoRequest;
		}
		catch(Exception e)
		{
			System.err.println("Se ha producido un error generando la petición de validación de certificados.");
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	// METODOS AUXILIARES
	/**
	 * Método que permite enviar una petición a un servicio Web determinado, usando AXIS.
	 * 
	 * @param webService Nombre del servicio Web al que invocar
	 * @param operation  Nombre de la operación a realizar
	 * @param request Petición a enviar
	 * @return Respuesta devuelta por el servicio Web
	 */
	public static synchronized String launchRequest(String webService, String operation, Document request)
	{
		if (proxyOperational.equals(PROXY_NTLM_AUTHENTICATION))
			return launchRequestNTLMAuthenticationMode(webService, operation, request);
		else
			return lanchRequestOtherOperationalMode(webService, operation, request);
	}
	
	/**
	 * Método que permite enviar una petición a un servicio Web determinado, usando AXIS.
	 * 
	 * @param webService Nombre del servicio Web al que invocar
	 * @param request Petición a enviar
	 * @return Respuesta devuelta por el servicio Web
	 */
	public static synchronized String launchRequest(String webService, Document request)
	{
		if (proxyOperational.equals(PROXY_NTLM_AUTHENTICATION))
			return launchRequestNTLMAuthenticationMode(webService, webService, request);
		else
			return lanchRequestOtherOperationalMode(webService, webService, request);
	}
	
	private static String launchRequestNTLMAuthenticationMode(String webService,String operation, Document request)
	{
		try
		{	
			List authPrefs = new ArrayList();
	        authPrefs.add(AuthPolicy.NTLM);

	        HttpClient proxyclient = new HttpClient();

	        proxyclient.getHostConfiguration().setHost(afirmaIP, afirmaPort);
	        proxyclient.getHostConfiguration().setProxy(proxyIP, proxyPort);
	        
	        proxyclient.getState().setProxyCredentials(
	            new AuthScope(null, 8080, null),
	            new NTCredentials(proxyUser, proxyPassword, "", proxyDomain));

	        proxyclient.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
	        proxyclient.getParams().setSoTimeout(TIMER);
	        proxyclient.getParams().setContentCharset("utf-8");
	        proxyclient.getParams().setParameter(HttpClientParams.USER_AGENT, "Axis/1.4");
	        
   	        PostMethod post = new PostMethod(ENDPOINT + webService);
   	        post.addRequestHeader("Content-Type", "text/xml; charset=utf-8");
   	        post.addRequestHeader("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
   	        post.addRequestHeader("SOAPAction", "");
	        
	        String soapRequest = "";
	        soapRequest += 
	        	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	        	"<soapenv:Envelope " +
	        	"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
	        	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	        	"<soapenv:Body>" +
	        	"<" + operation + " xmlns=\"http://soapinterop.org/\">" +
	        	"<" + operation + "Request xsi:type=\"xsd:string\" xmlns=\"\">" +
	        	XMLUtils.xmlEncodeString(XMLUtils.DocumentToString(request)) +
	        	"</" + operation + "Request>" +
	        	"</" + operation + "></soapenv:Body>" +
	        	"</soapenv:Envelope>";
	        
	        // Realizamos las operaciones de securización del SOAP (en caso necesario)
	        String signedSoapRequest = new SOAPSigner(securityConfiguration).sign(soapRequest);
	        
			if (StartingClass.ALMACENAR_PETICION) {
				UtilsFileSystem.writeDataToFileSystem(signedSoapRequest.getBytes(), StartingClass.construyeRutaPetRes(true, webService));
			}
	        
	        post.setRequestEntity(new StringRequestEntity(signedSoapRequest));
	        
			long requestTime = System.currentTimeMillis();

	        int status = proxyclient.executeMethod(post);
	        if (status != 200)
	        	throw new Exception ("Petición incorrecta (" + status + ")");

	        long tiempoRespuesta = System.currentTimeMillis();
			
			long resultado = tiempoRespuesta - requestTime;
	
			System.out.println("Tiempo de Respuesta ["+resultado+"]");
			
	        int count = 0;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        InputStream is=post.getResponseBodyAsStream();
	        byte[] body = new byte[2048];
	        
	        while ( (count = is.read(body)) > 0)
	        	baos.write(body , 0, count);

	        String ret = baos.toString();
			
	        if (StartingClass.ALMACENAR_RESPUESTA) {
				UtilsFileSystem.writeDataToFileSystem(ret.getBytes(), StartingClass.construyeRutaPetRes(false, webService));
			}
	        
	        // Obtenemos el mensaje de respuesta
	        String result = getInfoFromDocumentNode(ret, operation + "Return");
	        if (result == null)
	        	return ret;
	        else
	        	return result;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error enviando la petición a " + ENDPOINT + webService);
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
		      
	private static String lanchRequestOtherOperationalMode(String webService,String operation, Document request)
	{
		ClientHandler sender = null;
		
		try
		{	
			// Creacion del manejador que securizará la petición SOAP
	    	sender = new ClientHandler(securityConfiguration);

			Service service = new Service();
	        
	        Call call = (Call) service.createCall();
	
	        call.setTargetEndpointAddress( new java.net.URL(ENDPOINT + webService) );
	        call.setOperationName(new QName("http://soapinterop.org/", operation));
	        call.setOperationUse(Use.LITERAL);	     
		   	call.setTimeout(new Integer(TIMER));		   	
			call.setClientHandlers(sender, null);
					
			String param = operation + "Request";
				
			call.addParameter(param, org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(org.apache.axis.Constants.XSD_STRING);
			
			long requestTime = System.currentTimeMillis();
			
	        String soapRequest = 
	        	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	        	"<soapenv:Envelope " +
	        	"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
	        	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	        	"<soapenv:Body>" +
	        	"<" + operation + " xmlns=\"http://soapinterop.org/\">" +
	        	"<" + operation + "Request xsi:type=\"xsd:string\" xmlns=\"\">" +
	        	XMLUtils.xmlEncodeString(XMLUtils.DocumentToString(request)) +
	        	"</" + operation + "Request>" +
	        	"</" + operation + "></soapenv:Body>" +
	        	"</soapenv:Envelope>";
			
			SOAPEnvelope soapPeticion = new SOAPEnvelope(new ByteArrayInputStream(soapRequest.getBytes()));
			if (StartingClass.ALMACENAR_PETICION) {
				UtilsFileSystem.writeDataToFileSystem(soapPeticion.getAsString().getBytes(), StartingClass.construyeRutaPetRes(true, webService));
			}
			SOAPEnvelope soapRespuesta = call.invoke(soapPeticion); 
			String resp = XMLUtils.DocumentToString(soapRespuesta.getBody().getFirstChild().getFirstChild().getFirstChild().getOwnerDocument());
			resp = soapRespuesta.getBody().getFirstChild().getFirstChild().getFirstChild().getNodeValue();
			//String resp = soapRespuesta.getBody().getOwnerDocument();
			
			if (StartingClass.ALMACENAR_RESPUESTA) {
				UtilsFileSystem.writeDataToFileSystem(soapRespuesta.getAsString().getBytes(), StartingClass.construyeRutaPetRes(false, webService));
			}
			
			long tiempoRespuesta = System.currentTimeMillis();
			
			long resultado = tiempoRespuesta - requestTime;
	
			System.out.println("Tiempo de Respuesta ["+resultado+"]");

			return resp;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error enviando la petición a " + ENDPOINT + webService);
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Método que obtiene la información del nodo indicado del documento XML proporcionado.
	 * 
	 * @param document Documento XML
	 * @param nodeTag Nombre del nodo del cual extrae la información
	 * @return Valor del nodo anterior
	 */
	public static synchronized String getInfoFromDocumentNode(Document document, String nodeTag)
	{
		try
		{			
			Document doc = db.parse(new ByteArrayInputStream(XMLUtils.DocumentToString(document).getBytes("UTF-8")));
			
			NodeList docNode = doc.getElementsByTagName(nodeTag);
			
			return docNode.item(0).getFirstChild().getNodeValue();
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error obteniendo el valor del parámetro " + nodeTag);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Método que obtiene la información del nodo indicado del documento XML proporcionado.
	 * 
	 * @param document Documento XML
	 * @param nodeTag Nombre del nodo del cual extrae la información
	 * @return Valor del nodo anterior
	 */
	public static synchronized String getInfoFromDocumentNode(String document, String nodeTag)
	{
		try
		{			
			Document doc = db.parse(new ByteArrayInputStream(document.getBytes("UTF-8")));
			
			NodeList docNode = doc.getElementsByTagName(nodeTag);
			
			return docNode.item(0).getFirstChild().getNodeValue();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Método que obtiene la información del nodo indicado del documento XML proporcionado.
	 * 
	 * @param document Documento XML
	 * @param nodeTag Nombre del nodo del cual extrae la información
	 * @return Valor del nodo anterior
	 */
	public static synchronized String getInfoFromDocumentNode2(String document, String nodeTag)
	{
		try
		{			
			Document doc = db.parse(new ByteArrayInputStream(document.getBytes("UTF-8")));
			
			NodeList docNode = doc.getElementsByTagName(nodeTag);
			
			String res = "";
			String separador =", ";
			int i=0;
			for (i=0;i<docNode.getLength()-2;i++) {
				res = res + docNode.item(i).getFirstChild().getNodeValue() + separador;
			}
			res = res + docNode.item(i).getFirstChild().getNodeValue();
			return res;
			//return docNode.item(0).getFirstChild().getNodeValue();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Método que obtiene la información XML (los hijos) del nodo indicado del documento XML proporcionado.
	 * 
	 * @param document Documento XML
	 * @param nodeTag Nombre del nodo del cual extrae la información
	 * @return Valor XML contenido a partir del nodo indicado
	 */
	public static synchronized String getXMLChildsFromDocumentNode(String document, String nodeTag)
	{
		try
		{			
			Document doc = db.parse(new ByteArrayInputStream(document.getBytes("UTF-8")));
			
			NodeList docNode = doc.getElementsByTagName(nodeTag);
			
			return XMLUtils.ElementToString((Element)docNode.item(0).getFirstChild());
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Método que indica si la respuesta de la plataforma ha sido correcta o no
	 * 
	 * @param response Respuesta XML de la plataforma
	 * @return true en caso afirmativo, false en caso contrario
	 */
	public static synchronized boolean isCorrect(String response)
	{
		Document responseDoc = null;
		
		try
		{
			responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
			System.exit(-1);
			return false;			
		}
		
		try
		{						
			NodeList statusNode = null;

			statusNode = responseDoc.getElementsByTagName("estado");
			
			return new Boolean(statusNode.item(0).getFirstChild().getNodeValue()).booleanValue();
		}
		catch (Exception e)
		{
			// Comprobamos si hemos recibido una excepcion
			try
			{
				// Codigo de error
				NodeList errorCodeNode = responseDoc.getElementsByTagName("codigoError");
				// descripcion del error
				NodeList descripcionErrorNode = responseDoc.getElementsByTagName("descripcion");
				
				System.err.println(
						errorCodeNode.item(0).getFirstChild().getNodeValue() + ": " + 
						descripcionErrorNode.item(0).getFirstChild().getNodeValue());
				return false;
			}
			catch (Exception ee)
			{
				System.err.println(response);
				return false;
			}
		}
	}
	
	/**
	 * Método que obtiene el resultado de una peticion de validacion de certificado a la plataforma
	 * 
	 * @param response Respuesta XML de la plataforma
	 * @return resultado de la validacion
	 */
	public static synchronized String getResultCertificateValidationRequest(String response)
	{
		try
		{			
			Document responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
			
			NodeList statusNode = null;
			
			statusNode = responseDoc.getElementsByTagName("descripcion");
			
			if (statusNode.getLength()==0)
			{
				System.err.println("La petición de Validación del certificado no ha sido satisfactoria. Saliendo ...");
				System.exit(-1);
				return null;				
			}
		
			return statusNode.item(0).getFirstChild().getNodeValue();
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Método que indica si la respuesta de una peticion de Obtencion de Informacion de Certificado a la plataforma ha sido correcta o no
	 * 
	 * @param response Respuesta XML de la plataforma
	 * @return true en caso afirmativo, false en caso contrario
	 */
	public static synchronized boolean isCorrectGetCertificateInfoRequest(String response)
	{
		try
		{			
			Document responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
			
			NodeList statusNode = null;
			
			//Si existe el nodo InfoCertificado significa que la peticion ha sido satisfactoria
			statusNode = responseDoc.getElementsByTagName("InfoCertificado");
			
			if (statusNode.getLength()==0)
				return false;				

			return true;
		}
		catch (Exception e)
		{
			System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
			System.out.println(e.getMessage());
			System.exit(-1);
			return false;
		}
	}
	
	
	//SERVICIOS WEB EN INGLES
	
//	 CUSTODY
		
		/**
		 * Prepara la petición al Servicio Web "StoreDocument", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param fileName Nombre del documento
		 * @param fileType Formato del documento
		 * @param fileContent Contenido del documento codificado en Base64
		 */
		public static synchronized Document prepareCustodyDocumentRequestEng(String appId, String fileName, String fileType, String fileContentBase64Encoded)
		{
			try
			{			
				Document custodyDocumentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.CustodyDocumentRequesteng.getBytes("UTF-8")));	
				
				// Id de aplicación
				NodeList applicationNode = custodyDocumentRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = custodyDocumentRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);
				
				// Contenido del documento en Base64
				NodeList documentNode = custodyDocumentRequest.getElementsByTagName("document");
				CDATASection documentValueNode = custodyDocumentRequest.createCDATASection(fileContentBase64Encoded);
				documentNode.item(0).appendChild(documentValueNode);
				
				// Nombre del documento
				NodeList documentNameNode = custodyDocumentRequest.getElementsByTagName("documentName");
				Text documentNameValueNode = custodyDocumentRequest.createTextNode(fileName);
				documentNameNode.item(0).appendChild(documentNameValueNode);
				
				// Tipo del documento
				NodeList documentTypeNode = custodyDocumentRequest.getElementsByTagName("documentType");
				Text documentTypeValueNode = custodyDocumentRequest.createTextNode(fileType);
				documentTypeNode.item(0).appendChild(documentTypeValueNode);
				
				return custodyDocumentRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de custodia del documento " + fileName);
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "GetDocumentContent", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param transactionId Identificador de la transacción sobre el documento del cual se desea recuperar en contenido
		 */
		public static synchronized Document prepareGetDocumentContentRequestEng(String appId, String transactionId)
		{
			try
			{
				Document getDocumentContentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentContentRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getDocumentContentRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getDocumentContentRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList transactionIdNode = getDocumentContentRequest.getElementsByTagName("transactionId");
				Text transactionIdValueNode=getDocumentContentRequest.createTextNode(transactionId);
				transactionIdNode.item(0).appendChild(transactionIdValueNode);
				
				return getDocumentContentRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención del contenido del documento.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "GetDocumentContentByDocId", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param docId Identificador del documento del cual se desea recuperar el contenido
		 */
		public static synchronized Document prepareGetDocumentContentIdRequestEng(String appId, String docId)
		{
			try
			{
				Document getDocumentContentIdRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentContentIdRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getDocumentContentIdRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getDocumentContentIdRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList docIdNode = getDocumentContentIdRequest.getElementsByTagName("documentId");
				Text docIdValueNode=getDocumentContentIdRequest.createTextNode(docId);
				docIdNode.item(0).appendChild(docIdValueNode);
				
				return getDocumentContentIdRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención del contenido del documento por id de documento.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerIdDocumento", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param transactionId Identificador de transacción sobre el documento firmado del cual se desea recuperar el identificador
		 */
		public static synchronized Document prepareGetDocumentIdRequestEng(String appId, String transactionId)
		{
			try
			{
				Document getDocumentIdRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocumentIdRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getDocumentIdRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getDocumentIdRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);

				//Id de documento
				NodeList transactionIdNode = getDocumentIdRequest.getElementsByTagName("transactionId");
				Text transactionIdValueNode=getDocumentIdRequest.createTextNode(transactionId);
				transactionIdNode.item(0).appendChild(transactionIdValueNode);
				
				return getDocumentIdRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención del identificador del documento por id de transacción.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "EliminarContenidoDocumento", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param docId Identificador del documento del cual se desea borrar el contenido
		 */
		public static synchronized Document prepareDeleteDocumentContentRequestEng(String appId, String docId)
		{
			try
			{
				Document deleteDocumentContentRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.DeleteDocumentContentRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = deleteDocumentContentRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=deleteDocumentContentRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList docIdNode = deleteDocumentContentRequest.getElementsByTagName("documentId");
				Text docIdValueNode=deleteDocumentContentRequest.createTextNode(docId);
				docIdNode.item(0).appendChild(docIdValueNode);
				
				return deleteDocumentContentRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de borrado del contenido del documento.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ActualizarReferencia", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param transactionId Identificador de la transacción de la que se desea actualizar la referencia
		 */
		public static synchronized Document prepareSetExternalReferenceRequestEng(String appId, String transactionId, String externalReference)
		{
			try
			{
				Document setExternalReferenceRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.SetExternalReferenceRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = setExternalReferenceRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=setExternalReferenceRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de transacción
				NodeList transactionIdNode = setExternalReferenceRequest.getElementsByTagName("transactionId");
				Text transactionIdValueNode=setExternalReferenceRequest.createTextNode(transactionId);
				transactionIdNode.item(0).appendChild(transactionIdValueNode);
				
				//Referencia externa
				NodeList externalReferenceNode = setExternalReferenceRequest.getElementsByTagName("reference");
				Text externalReferenceValueNode=setExternalReferenceRequest.createTextNode(externalReference);
				externalReferenceNode.item(0).appendChild(externalReferenceValueNode);

				return setExternalReferenceRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de actualización de referencia externa de transacción.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}

		/**
		 * Prepara la petición al Servicio Web "ObtenerTransaccionesReferencia", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param externalReference Referencia externa mediante la cual realizar la búsqueda
		 */
		public static synchronized Document prepareGetTransactionsByExternalReferenceEng(String appId, String externalReference)
		{
			try
			{
				Document getTransactionsByExternalReferenceRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsByExternalReferenceRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getTransactionsByExternalReferenceRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getTransactionsByExternalReferenceRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Referencia externa
				NodeList externalReferenceNode = getTransactionsByExternalReferenceRequest.getElementsByTagName("reference");
				Text externalReferenceValueNode=getTransactionsByExternalReferenceRequest.createTextNode(externalReference);
				externalReferenceNode.item(0).appendChild(externalReferenceValueNode);

				return getTransactionsByExternalReferenceRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de busqueda de transacciones por referencia externa.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerTransaccionesPorFecha", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param startDate Fecha de inicio
		 * @param endDate Fecha de fin
		 */
		public static synchronized Document prepareGetTransactionsByDateRequestEng(String appId, String startDate, String endDate)
		{
			try
			{
				Document getTransactionsByDateRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsByDateRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getTransactionsByDateRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getTransactionsByDateRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Fecha de inicio
				NodeList startDateNode = getTransactionsByDateRequest.getElementsByTagName("startDate");
				Text startDateValueNode=getTransactionsByDateRequest.createTextNode(startDate);
				startDateNode.item(0).appendChild(startDateValueNode);

				//Fecha de fin
				NodeList endDateNode = getTransactionsByDateRequest.getElementsByTagName("endDate");
				Text endDateValueNode=getTransactionsByDateRequest.createTextNode(endDate);
				endDateNode.item(0).appendChild(endDateValueNode);

				return getTransactionsByDateRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de busqueda de transacciones por fechas.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}

		/**
		 * Prepara la petición al Servicio Web "ObtenerTransacciones", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 */
		public static synchronized Document prepareGetTransactionsRequestEng(String appId)
		{
			try
			{
				Document getTransactionsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetTransactionsRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getTransactionsRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getTransactionsRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				return getTransactionsRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de busqueda de transacciones.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerFirmaTransaccion", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de la transaccion de la cual se desea extraer la Firma Electrónica
		 */
		public static synchronized Document prepareGetESignatureRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document getESignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetESignatureRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getESignatureRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getESignatureRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de la transacción
				NodeList idTransationNode = getESignatureRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=getESignatureRequest.createTextNode(idTransaction);
				idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return getESignatureRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de la Firma Electrónica de una transacción.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}	
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerBloqueFirmas", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas
		 */
		public static synchronized Document prepareGetSignaturesBlockRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document getSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetSignaturesBlockRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getSignaturesBlockRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getSignaturesBlockRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de la transacción
				NodeList idTransationNode = getSignaturesBlockRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=getSignaturesBlockRequest.createTextNode(idTransaction);
				idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return getSignaturesBlockRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de un bloque de firmas.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}	

		// FIRMA
		
		/**
		 * Prepara la petición al Servicio Web "ValidarFirma", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param eSignature Firma Electrónica a validar (codificada en Base64)
		 * @param eSignatureFormat Formato de la Firma Electrónica a validar
		 * @param hash Hash de los datos firmados codificado en Base64.
		 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
		 * @param data Datos originales firmados codificados en Base64.
		 */
		public static synchronized Document prepareValidateSignatureRequestEng(String appId, String eSignature, String eSignatureFormat, byte[] hash, String hashAlgorithm, byte[] data)
		{
			try
			{
				Document signatureValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.SignatureValidationRequesteng.getBytes("UTF-8")));
				
				// Id de aplicación
				NodeList applicationNode = signatureValidationRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = signatureValidationRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);
				
				// Firma Electrónica a validar
				NodeList eSignatureNode = signatureValidationRequest.getElementsByTagName("eSignature");
				CDATASection eSignatureValueNode = signatureValidationRequest.createCDATASection(eSignature);
				eSignatureNode.item(0).appendChild(eSignatureValueNode);
				
				// Formato de firma
				NodeList eSignatureFormatNode = signatureValidationRequest.getElementsByTagName("eSignatureFormat");
				Text eSignatureFormatValueNode = signatureValidationRequest.createTextNode(eSignatureFormat);
				eSignatureFormatNode.item(0).appendChild(eSignatureFormatValueNode);

				// Algoritmo de hash
				if (hash != null)
				{
					NodeList hashNode = signatureValidationRequest.getElementsByTagName("hash");
					CDATASection hashValueNode = signatureValidationRequest.createCDATASection(new String(hash));
					hashNode.item(0).appendChild(hashValueNode);

					NodeList hashAlgorithmNode = signatureValidationRequest.getElementsByTagName("hashAlgorithm");
					Text hashAlgorithmValueNode = signatureValidationRequest.createTextNode(hashAlgorithm);
					hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
				}	
				
				// Datos
				if (data != null)
				{
					NodeList dataNode = signatureValidationRequest.getElementsByTagName("data");
					CDATASection dataValueNode = signatureValidationRequest.createCDATASection(new String(data));
					dataNode.item(0).appendChild(dataValueNode);
				}
				
				return signatureValidationRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de validación de firma");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaServidor", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param docId Identificador del documento
		 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
		 * @param signatureFormat Formato de Firma Electrónica a generar
		 */
		public static synchronized Document prepareServerSignatureRequestEng(String appId, String docId, String aliasServerCert, String referenceId, String hashAlgorithm, String signatureFormat)
		{
			try
			{
				Document serverSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureRequesteng.getBytes("UTF-8")));
				
				// Id de aplicación
				NodeList applicationNode = serverSignatureRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = serverSignatureRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);
				
				// Id del documento a firmar
				NodeList idDocumentNode = serverSignatureRequest.getElementsByTagName("documentId");
				Text idDocumentValueNode = serverSignatureRequest.createTextNode(docId);
				idDocumentNode.item(0).appendChild(idDocumentValueNode);
				
				// Alias del certificado servidor a emplear en la Firma Electrónica
				NodeList signerNode = serverSignatureRequest.getElementsByTagName("signer");
				Text signerValueNode = serverSignatureRequest.createTextNode(aliasServerCert);
				signerNode.item(0).appendChild(signerValueNode);

				// Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = serverSignatureRequest.getElementsByTagName("reference");
					Text idReferenceValueNode = serverSignatureRequest.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}
				
				// Algoritmo de hash
				NodeList hashAlgorithmNode = serverSignatureRequest.getElementsByTagName("hashAlgorithm");
				Text hashAlgorithmValueNode = serverSignatureRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
				
				// Formato de firma
				NodeList signatureFormatNode = serverSignatureRequest.getElementsByTagName("eSignatureFormat");
				Text signatureFormatValueNode = serverSignatureRequest.createTextNode(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
				
				return serverSignatureRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma servidor del documento con id=" + docId);
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaServidorCoSign", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transaccion de firma sobre la que se desea hacer la multifirma cosign
		 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
		 */
		public static synchronized Document prepareServerSignatureCoSignRequestEng(String appId, String idTransaction, String aliasServerCert, String referenceId, String hashAlgorithm)
		{
			try
			{
				Document serverSignatureCoSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureCoSignRequesteng.getBytes("UTF-8")));

				// Id de aplicación
				NodeList applicationNode = serverSignatureCoSignRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = serverSignatureCoSignRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);

				// Id de transaccion
				NodeList idTransactionNode = serverSignatureCoSignRequest.getElementsByTagName("transactionId");
				Text idTransactionValueNode = serverSignatureCoSignRequest.createTextNode(idTransaction);
				idTransactionNode.item(0).appendChild(idTransactionValueNode);
				
				// Alias del certificado servidor a emplear en la Firma Electrónica
				NodeList signerNode = serverSignatureCoSignRequest.getElementsByTagName("signer");
				Text signerValueNode = serverSignatureCoSignRequest.createTextNode(aliasServerCert);
				signerNode.item(0).appendChild(signerValueNode);
				
				// Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = serverSignatureCoSignRequest.getElementsByTagName("reference");
					Text idReferenceValueNode = serverSignatureCoSignRequest.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}

				// Algoritmo de hash
				NodeList hashAlgorithmNode = serverSignatureCoSignRequest.getElementsByTagName("hashAlgorithm");
				Text hashAlgorithmValueNode = serverSignatureCoSignRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);			

				return serverSignatureCoSignRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma servidor cosign de la transaccion con id=" + idTransaction);
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaServidorCoSign", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transaccion de firma sobre la que se desea hacer la multifirma cosign
		 * @param hashAlgorithm Algoritmo de hash a emplear en la firma servidor
		 * @param firmanteObjetivo Certificado X09 codificado en Base 64 del firmante sobre el que realizar la firma counterSign
		 */
		public static synchronized Document prepareServerSignatureCounterSignRequestEng(String appId, String idTransaction, String aliasServerCert, String referenceId, String hashAlgorithm, byte[] signer)
		{
			try
			{
				Document serverSignatureCounterSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureCounterSignRequesteng.getBytes("UTF-8")));
				
				// Id de aplicación
				NodeList applicationNode = serverSignatureCounterSignRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = serverSignatureCounterSignRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);

				// Id de transaccion
				NodeList idTransactionNode = serverSignatureCounterSignRequest.getElementsByTagName("transactionId");
				Text idTransactionValueNode = serverSignatureCounterSignRequest.createTextNode(idTransaction);
				idTransactionNode.item(0).appendChild(idTransactionValueNode);
				
				// Alias del certificado servidor a emplear en la Firma Electrónica
				NodeList signerNode = serverSignatureCounterSignRequest.getElementsByTagName("signer");
				Text signerValueNode = serverSignatureCounterSignRequest.createTextNode(aliasServerCert);
				signerNode.item(0).appendChild(signerValueNode);
				
				// Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = serverSignatureCounterSignRequest.getElementsByTagName("reference");
					Text idReferenceValueNode = serverSignatureCounterSignRequest.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}

				// Algoritmo de hash
				NodeList hashAlgorithmNode = serverSignatureCounterSignRequest.getElementsByTagName("hashAlgorithm");
				Text hashAlgorithmValueNode = serverSignatureCounterSignRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
				
				// Firmante Objetivo
				if (signer!=null)
				{
					NodeList objectiveSignerNode = serverSignatureCounterSignRequest.getElementsByTagName("targetSigner");
					Text objectiveSignerValueNode = serverSignatureCounterSignRequest.createCDATASection(new String(signer));
					objectiveSignerNode.item(0).appendChild(objectiveSignerValueNode);
				}			

				return serverSignatureCounterSignRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma servidor countersign de la transaccion con id=" + idTransaction);
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idDoc Identificador del documento a firmar
		 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
		 */
		public static synchronized Document prepareThreePhasesUserSignatureF1RequestEng(String appId, String idDoc, String hashAlgorithm)
		{
			try
			{
				Document threeFasesUserSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1Requesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = threeFasesUserSignatureRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=threeFasesUserSignatureRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList documentNode = threeFasesUserSignatureRequest.getElementsByTagName("documentId");
				Text documentValueNode=threeFasesUserSignatureRequest.createTextNode(idDoc);
				documentNode.item(0).appendChild(documentValueNode);			
				
				//Algoritmo Hash
				NodeList algorithmHashNode = threeFasesUserSignatureRequest.getElementsByTagName("hashAlgorithm");
				Text algorithmHashNodeValueNode=threeFasesUserSignatureRequest.createTextNode(hashAlgorithm);
				algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
				
				return threeFasesUserSignatureRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario 3 Fases F1.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1CoSign", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transacción de firma sobre la que se desea hacer la multifirma CoSign
		 * @param hashAlgorithm Algoritmo de hash a emplear en la firma.
		 */
		public static synchronized Document prepareThreePhasesUserSignaturesF1CoSignRequestEng(String appId, String idTransaction, String hashAlgorithm)
		{
			try
			{
				Document threePhasesUserSignaturesCoSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1CoSignRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id Transaccion
				NodeList idTransactionNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(idTransaction);
				idTransactionNode.item(0).appendChild(idTransactionNodeValueNode);
				

				//Algoritmo Hash
				NodeList hashAlgorithmNode = threePhasesUserSignaturesCoSignRequest.getElementsByTagName("hashAlgorithm");
				Text hashAlgorithmValueNode=threePhasesUserSignaturesCoSignRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
				
				return threePhasesUserSignaturesCoSignRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario 3 fases F1 cosign.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF1CounterSign", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transacción de firma sobre la que se desea hacer la multifirma CounterSign
		 */
		public static synchronized Document prepareThreePhasesUserSignaturesF1CounterSignRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document threePhasesUserSignaturesCounterSignRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF1CounterSignRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = threePhasesUserSignaturesCounterSignRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=threePhasesUserSignaturesCounterSignRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id Transaccion
				NodeList idTransactionNode = threePhasesUserSignaturesCounterSignRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=threePhasesUserSignaturesCounterSignRequest.createTextNode(idTransaction);
				idTransactionNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return threePhasesUserSignaturesCounterSignRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario 3 fases F1 countersign.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuario3FasesF3", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transaccion a finalizar
		 * @param signature Firma Electrónica codificada en base 64
		 * @param signCertificate Certificado con el que se ha realizado la firma
		 * @param SignatureFormat Formato de la Firma 
		 */
		public static synchronized Document prepareThreePhasesUserSignatureF3RequestEng(String appId, String idTransaction, String signature, String signCertificate, String signatureFormat, String updateSignatureFormat, String referenceId)
		{
			try
			{
				Document threeFasesUserSignatureF3Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.ThreePhasesUserSignatureF3Requesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = threeFasesUserSignatureF3Request.getElementsByTagName("applicationId");
				Text aplicationValueNode=threeFasesUserSignatureF3Request.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de transaccion
				NodeList transactionNode = threeFasesUserSignatureF3Request.getElementsByTagName("transactionId");
				Text transactionValueNode=threeFasesUserSignatureF3Request.createTextNode(idTransaction);
				transactionNode.item(0).appendChild(transactionValueNode);
				
				//Firma Electronica
				NodeList signatureNode = threeFasesUserSignatureF3Request.getElementsByTagName("eSignature");
				Text signatureValueNode=threeFasesUserSignatureF3Request.createTextNode(signature);
				signatureNode.item(0).appendChild(signatureValueNode);
				
				//Certificado Firmante
				NodeList signCertificateNode = threeFasesUserSignatureF3Request.getElementsByTagName("signerCertificate");
				Text signCertificateValueNode=threeFasesUserSignatureF3Request.createTextNode(signCertificate);
				signCertificateNode.item(0).appendChild(signCertificateValueNode);
				
				//Formato Firma
				NodeList signatureFormatNode = threeFasesUserSignatureF3Request.getElementsByTagName("eSignatureFormat");
				Text signatureFormatNodeValueNode=threeFasesUserSignatureF3Request.createTextNode(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatNodeValueNode);

				//Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = threeFasesUserSignatureF3Request.getElementsByTagName("reference");
					Text idReferenceValueNode = threeFasesUserSignatureF3Request.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}
				
				//Extender Formato de Firma
//				NodeList updateSignatureFormatNode = threeFasesUserSignatureF3Request.getElementsByTagName("updateSignatureFormat");
//				Text updateSignatureFormatNodeValueNode=threeFasesUserSignatureF3Request.createTextNode(updateSignatureFormat);
//				updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);
				
				return threeFasesUserSignatureF3Request;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario 3 Fases F3.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuario2FasesF2", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param signature Firma Electrónica codificada en base 64
		 * @param signCertificate Certificado con el que se ha realizado la firma
		 * @param SignatureFormat Formato de la Firma 
		 */
		public static synchronized Document prepareTwoPhasesUserSignatureF2RequestEng(String appId, String signature, String signCertificate, String signatureFormat,String file, String fileType, String fileName, String hashAlgorithm, String updateSignatureFormat, String referenceId, boolean custodyDoc)
		{
			try
			{
				Document twoFasesUserSignatureF2Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.TwoPhasesUserSignatureF2Requesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = twoFasesUserSignatureF2Request.getElementsByTagName("applicationId");
				Text aplicationValueNode=twoFasesUserSignatureF2Request.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Firma Electronica
				NodeList signatureNode = twoFasesUserSignatureF2Request.getElementsByTagName("eSignature");
				CDATASection signatureValueNode=twoFasesUserSignatureF2Request.createCDATASection(signature);
				signatureNode.item(0).appendChild(signatureValueNode);
				
				//Certificado Firmante
				NodeList signCertificateNode = twoFasesUserSignatureF2Request.getElementsByTagName("signerCertificate");
				CDATASection signCertificateValueNode=twoFasesUserSignatureF2Request.createCDATASection(signCertificate);
				signCertificateNode.item(0).appendChild(signCertificateValueNode);
				
				//Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = twoFasesUserSignatureF2Request.getElementsByTagName("reference");
					Text idReferenceValueNode = twoFasesUserSignatureF2Request.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}
				
				//Formato Firma
				NodeList signatureFormatNode = twoFasesUserSignatureF2Request.getElementsByTagName("eSignatureFormat");
				CDATASection signatureFormatNodeValueNode=twoFasesUserSignatureF2Request.createCDATASection(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatNodeValueNode);
				
				//documento
				NodeList documentNode = twoFasesUserSignatureF2Request.getElementsByTagName("document");
				CDATASection documentNodeValueNode=twoFasesUserSignatureF2Request.createCDATASection(file);
				documentNode.item(0).appendChild(documentNodeValueNode);
				
				//nombre documento
				NodeList documentNameNode = twoFasesUserSignatureF2Request.getElementsByTagName("documentName");
				Text documentNameNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(fileName);
				documentNameNode.item(0).appendChild(documentNameNodeValueNode);
				
				//tipo documento
				NodeList documentTypeNode = twoFasesUserSignatureF2Request.getElementsByTagName("documentType");
				Text documentTypeNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(fileType);
				documentTypeNode.item(0).appendChild(documentTypeNodeValueNode);
				
				//Algoritmo Hash
				NodeList algorithmHashNode = twoFasesUserSignatureF2Request.getElementsByTagName("hashAlgorithm");
				Text algorithmHashNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(hashAlgorithm);
				algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
				
				//Custodiar documento
				NodeList custodiarDocNode = twoFasesUserSignatureF2Request.getElementsByTagName("storeDocument");
				Text custodiarDocValueNode=twoFasesUserSignatureF2Request.createTextNode(""+custodyDoc);
				custodiarDocNode.item(0).appendChild(custodiarDocValueNode);
				
//				//Extender Formato de Firma
//				NodeList updateSignatureFormatNode = twoFasesUserSignatureF2Request.getElementsByTagName("updateSignatureFormat");
//				Text updateSignatureFormatNodeValueNode=twoFasesUserSignatureF2Request.createTextNode(updateSignatureFormat);
//				updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);
				
				return twoFasesUserSignatureF2Request;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario 2 Fases F2.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuarioBloquesF1", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idDocs Identificadores de documentos a firmar
		 * @param idTransactions Identificadores de transacciones de firma por bloques
		 * @param aliasServerCert Alias del certificado servidor con el cual hacer las Firmas Electrónicas Servidor a insertar en el bloque
		 * @param selectiveBlocks Información respecto a los documentos de otros bloques a multifirmar
		 * @param hashAlgorithm Algoritmo de hash empleado en el cálculo del hash anterior
		 */
		public static synchronized Document prepareBlockUserSignatureF1RequestEng(String appId, String[] idDocs, long[] idTransactions, String aliasServerCert, HashMap selectiveBlocks, String hashAlgorithm)
		{
			try
			{
				Document blockUserSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockUserSignatureF1Requesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = blockUserSignatureRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=blockUserSignatureRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				// Alias del certificado servidor a emplear en las Firmas Electrónicas por bloques
				NodeList signerNode = blockUserSignatureRequest.getElementsByTagName("signer");
				Text signerValueNode = blockUserSignatureRequest.createTextNode(aliasServerCert);
				signerNode.item(0).appendChild(signerValueNode);
				
				//Id documentos
				if (idDocs!=null)
				{
					NodeList documentsNode = blockUserSignatureRequest.getElementsByTagName("idDocuments");
					Element documentElement=null;
					Text idDocValueNode=null;
					for (int i=0; i<idDocs.length;i++)
					{
						documentElement=blockUserSignatureRequest.createElement("documentId");
						idDocValueNode=blockUserSignatureRequest.createTextNode(idDocs[i]);
						documentElement.appendChild(idDocValueNode);
						documentsNode.item(0).appendChild(documentElement);
					}
				}
				
				//Id transacciones
				if (idTransactions!=null)
				{
					NodeList transactionNode = blockUserSignatureRequest.getElementsByTagName("idTransactions");
					
					for (int i=0; i<idTransactions.length;i++)
					{
						Element transactionElement=blockUserSignatureRequest.createElement("transactionId");
						Text idTranValueNode=blockUserSignatureRequest.createTextNode(new Long(idTransactions[i]).toString());
						transactionElement.appendChild(idTranValueNode);
						transactionNode.item(0).appendChild(transactionElement);					
					}
				}
				
				// Documentos a Multifirmar
				if (selectiveBlocks!=null)
				{
					NodeList multiNode = blockUserSignatureRequest.getElementsByTagName("multiSignDocuments");
					
					Iterator it = selectiveBlocks.keySet().iterator();
					while(it.hasNext())
					{
						Long key = (Long)it.next();
						long value[] = (long[])selectiveBlocks.get(key);
						
						Element blockElement=blockUserSignatureRequest.createElement("selectedBlock");
						Element blockIdElement=blockUserSignatureRequest.createElement("idTransactionBlock");
						Text blockIdValueNode=blockUserSignatureRequest.createTextNode(key.toString());
						
						blockIdElement.appendChild(blockIdValueNode);
						blockElement.appendChild(blockIdElement);

						Element selectedDocsElement=blockUserSignatureRequest.createElement("selectedDocuments");
						for (int i=0; i<value.length; i++)
						{
							Element selectedDocIdElement=blockUserSignatureRequest.createElement("transactionId");
							Text selectedDocIdValueNode=blockUserSignatureRequest.createTextNode(new Long(value[i]).toString());
							selectedDocIdElement.appendChild(selectedDocIdValueNode);
							selectedDocsElement.appendChild(selectedDocIdElement);
						}
						
						blockElement.appendChild(selectedDocsElement);
						
						multiNode.item(0).appendChild(blockElement);
					}
				}
				
				//Algoritmo Hash
				NodeList algorithmHashNode = blockUserSignatureRequest.getElementsByTagName("hashAlgorithm");
				Text algorithmHashNodeValueNode=blockUserSignatureRequest.createTextNode(hashAlgorithm);
				algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
				
				return blockUserSignatureRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario por bloques F1.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "FirmaUsuarioBloquesF3", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de transaccion a finalizar
		 * @param signature Firma Electrónica codificada en base 64
		 * @param signCertificate Certificado con el que se ha realizado la firma
		 * @param SignatureFormat Formato de la Firma 
		 */
		public static synchronized Document prepareBlockUserSignatureF3RequestEng(String appId, String idTransaction, String signature, String signCertificate, String signatureFormat, String updateSignatureFormat, String referenceId)
		{
			try
			{
				Document blockUserSignatureF3Request = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockUserSignatureF3Requesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = blockUserSignatureF3Request.getElementsByTagName("applicationId");
				Text aplicationValueNode=blockUserSignatureF3Request.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de transaccion
				NodeList transactionNode = blockUserSignatureF3Request.getElementsByTagName("transactionId");
				Text transactionValueNode=blockUserSignatureF3Request.createTextNode(idTransaction);
				transactionNode.item(0).appendChild(transactionValueNode);
				
				//Firma Electronica
				NodeList signatureNode = blockUserSignatureF3Request.getElementsByTagName("eSignature");
				Text signatureValueNode=blockUserSignatureF3Request.createTextNode(signature);
				signatureNode.item(0).appendChild(signatureValueNode);
				
				//Certificado Firmante
				NodeList signCertificateNode = blockUserSignatureF3Request.getElementsByTagName("signerCertificate");
				Text signCertificateValueNode=blockUserSignatureF3Request.createTextNode(signCertificate);
				signCertificateNode.item(0).appendChild(signCertificateValueNode);
				
				//Formato Firma
				NodeList algorithmHashNode = blockUserSignatureF3Request.getElementsByTagName("eSignatureFormat");
				Text algorithmHashNodeValueNode=blockUserSignatureF3Request.createTextNode(signatureFormat);
				algorithmHashNode.item(0).appendChild(algorithmHashNodeValueNode);
				
//				//Extender Formato de Firma
//				NodeList updateSignatureFormatNode = blockUserSignatureF3Request.getElementsByTagName("updateSignatureFormat");
//				Text updateSignatureFormatNodeValueNode=blockUserSignatureF3Request.createTextNode(updateSignatureFormat);
//				updateSignatureFormatNode.item(0).appendChild(updateSignatureFormatNodeValueNode);
				
				//Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = blockUserSignatureF3Request.getElementsByTagName("reference");
					Text idReferenceValueNode = blockUserSignatureF3Request.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}
				
				return blockUserSignatureF3Request;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma de usuario por bloques F3.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ValidarFirmaBloquesCompleto", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param signature Firma electronica del bloque a validar
		 * @param signaturesBlock Bloque de firas correspondiente a la Firma Electronica a validar.
		 * @param signatureFormat Formato de la firma.
		 */
		public static synchronized Document prepareBlockSignatureFullValidationRequestEng(String appId, String signature, String signaturesBlock, String signatureFormat)
		{
			try
			{
				Document blockSignatureFullValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockSignatureFullValidacionRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = blockSignatureFullValidationRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=blockSignatureFullValidationRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Firma electronica
				NodeList signtureNode = blockSignatureFullValidationRequest.getElementsByTagName("eSignature");
				CDATASection signtureNodeValueNode=blockSignatureFullValidationRequest.createCDATASection(signature);
				signtureNode.item(0).appendChild(signtureNodeValueNode);

				//Bloque de firmas
				NodeList signturesBlockNode = blockSignatureFullValidationRequest.getElementsByTagName("block");
				CDATASection signturesBlockNodeValueNode=blockSignatureFullValidationRequest.createCDATASection(signaturesBlock);
				signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
				
				//Formato de firma
				NodeList signatureFormatNode = blockSignatureFullValidationRequest.getElementsByTagName("eSignatureFormat");
				Text signatureFormatValueNode=blockSignatureFullValidationRequest.createTextNode(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
				
				return blockSignatureFullValidationRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de validación de firma por bloques completo.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ValidarFirmaBloquesDocumento", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param signature Firma electronica del bloque a validar
		 * @param document Documento original sobre el que se calculó la firma servidor.
		 * @param idDocument Identificador del documento sobre el que se desea validar la Firma Electronica.
		 * @param signatureFormat Formato de la firma.
		 */
		public static synchronized Document prepareBlockSignatureDocumentValidationRequestEng(String appId, String signature, String document, String idDocument, String signatureFormat)
		{
			try
			{
				Document blockSignatureDocumentValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.BlockSignatureDocumentValidacionRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = blockSignatureDocumentValidationRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=blockSignatureDocumentValidationRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Firma electronica
				NodeList signtureNode = blockSignatureDocumentValidationRequest.getElementsByTagName("eSignature");
				CDATASection signtureNodeValueNode=blockSignatureDocumentValidationRequest.createCDATASection(signature);
				signtureNode.item(0).appendChild(signtureNodeValueNode);
				
				//documento
				NodeList documentNode = blockSignatureDocumentValidationRequest.getElementsByTagName("document");
				CDATASection documentNodeValueNode=blockSignatureDocumentValidationRequest.createCDATASection(document);
				documentNode.item(0).appendChild(documentNodeValueNode);
				
				//Identificador de documento
				NodeList idDocumentNode = blockSignatureDocumentValidationRequest.getElementsByTagName("documentId");
				Text idDocumentNodeValueNode=blockSignatureDocumentValidationRequest.createTextNode(idDocument);
				idDocumentNode.item(0).appendChild(idDocumentNodeValueNode);
				
				//Formato de firma
				NodeList signatureFormatNode = blockSignatureDocumentValidationRequest.getElementsByTagName("eSignatureFormat");
				Text signatureFormatValueNode=blockSignatureDocumentValidationRequest.createTextNode(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
				
				return blockSignatureDocumentValidationRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de validación de firma por bloques documento.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerIdDocumentosBloqueFirmas", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desean extraer los id de documentos
		 */
		public static synchronized Document prepareGetDocIdSignaturesBlockRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document getDocIdSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocIdSignaturesBlockRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getDocIdSignaturesBlockRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getDocIdSignaturesBlockRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList idTransationNode = getDocIdSignaturesBlockRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=getDocIdSignaturesBlockRequest.createTextNode(idTransaction);
				idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return getDocIdSignaturesBlockRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de ids de documentos de un bloque de firmas.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerIdDocumentosBloqueFirmasBackwards", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param signaturesBlock Firma PCKS7 implícita con el bloque de Firmas incluido
		 */
		public static synchronized Document prepareGetDocIdSignaturesBlockBackwardsRequestEng(String appId, String signaturesBlock)
		{
			try
			{
				Document getDocIdSignturesBlockBackwardsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetDocIdSignaturesBlockBackwardsRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getDocIdSignturesBlockBackwardsRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getDocIdSignturesBlockBackwardsRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList signturesBlockNode = getDocIdSignturesBlockBackwardsRequest.getElementsByTagName("block");
				CDATASection signturesBlockNodeValueNode=getDocIdSignturesBlockBackwardsRequest.createCDATASection(signaturesBlock);
				signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
				
				return getDocIdSignturesBlockBackwardsRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de ids de documentos de un bloque de firmas generado por @firma 4.0.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerInformationBloqueFirmas", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desean extraer los id de documentos
		 */
		public static synchronized Document prepareGetInformationSignaturesBlockRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document getInformationSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetInformationSignaturesBlockRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getInformationSignaturesBlockRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getInformationSignaturesBlockRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList idTransationNode = getInformationSignaturesBlockRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=getInformationSignaturesBlockRequest.createTextNode(idTransaction);
				idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return getInformationSignaturesBlockRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de informacion de un bloque de firmas.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerInformacionBloqueFirmasBackwards", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param signaturesBlock Firma PCKS7 implícita con el bloque de Firmas incluido
		 */
		public static synchronized Document prepareGetInformationSignaturesBlockBackwardsRequestEng(String appId, String signaturesBlock)
		{
			try
			{
				Document getInformationSignturesBlockBackwardsRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetInformationSignaturesBlockBackwardsRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getInformationSignturesBlockBackwardsRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getInformationSignturesBlockBackwardsRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList signturesBlockNode = getInformationSignturesBlockBackwardsRequest.getElementsByTagName("block");
				CDATASection signturesBlockNodeValueNode=getInformationSignturesBlockBackwardsRequest.createCDATASection(signaturesBlock);
				signturesBlockNode.item(0).appendChild(signturesBlockNodeValueNode);
				
				return getInformationSignturesBlockBackwardsRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de informacion de un bloque de firmas generado por @firma 4.0.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerInfoCompletaBloqueFirmas", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param idTransaction Identificador de la transaccion asociada al bloque de firmas del cual se desea extraer la información
		 */
		public static synchronized Document prepareGetCompleteInfoSignaturesBlockRequestEng(String appId, String idTransaction)
		{
			try
			{
				Document getCompleteInfoSignaturesBlockRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetCompleteInfoSignaturesBlockRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getCompleteInfoSignaturesBlockRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getCompleteInfoSignaturesBlockRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//Id de documento
				NodeList idTransationNode = getCompleteInfoSignaturesBlockRequest.getElementsByTagName("transactionId");
				Text idTransactionNodeValueNode=getCompleteInfoSignaturesBlockRequest.createTextNode(idTransaction);
				idTransationNode.item(0).appendChild(idTransactionNodeValueNode);
				
				return getCompleteInfoSignaturesBlockRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de obtención de informacion completa de un bloque de firmas.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		// MODULO DE VALIDACION
		/**
		 * Prepara la petición al Servicio Web "ValidarCertificado", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param certificate Certificado a validar
		 * @param validationMode Modo de validacion 
		 * @param certificateInfo Obtener informacion del certificado
		 */
		public static synchronized Document prepareCertificateValidationRequestEng(String appId, String certificate, int validationMode, boolean certificateInfo)
		{
			try
			{
				Document certificateValidationRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.CertificateValidationRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = certificateValidationRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=certificateValidationRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//certificado
				NodeList certificateNode = certificateValidationRequest.getElementsByTagName("certificate");
				CDATASection certificateValueNode=certificateValidationRequest.createCDATASection(certificate);
				certificateNode.item(0).appendChild(certificateValueNode);
				
				//ModoValidacion
				NodeList validationModeNode = certificateValidationRequest.getElementsByTagName("validationMode");
				Text validationModeValueNode=certificateValidationRequest.createTextNode(new Integer(validationMode).toString());
				validationModeNode.item(0).appendChild(validationModeValueNode);
				
				//Obtener Info
				NodeList certificateInfoNode = certificateValidationRequest.getElementsByTagName("getInfo");
				Text certificateInfoValueNode=certificateValidationRequest.createTextNode(new Boolean(certificateInfo).toString());
				certificateInfoNode.item(0).appendChild(certificateInfoValueNode);
				
				
				return certificateValidationRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de validación de certificados.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Prepara la petición al Servicio Web "ObtenerInfoCertificado", con los parámetros indicados.
		 *
		 * @param appId Identificador de la aplicación
		 * @param certificate Certificado a validar
		 */
		public static synchronized Document prepareGetCertificateInfoRequestEng(String appId, String certificate)
		{
			try
			{
				Document getCertificateInfoRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.GetCertificateInfoRequesteng.getBytes("UTF-8")));
				
				//Id de aplicación
				NodeList aplicationNode = getCertificateInfoRequest.getElementsByTagName("applicationId");
				Text aplicationValueNode=getCertificateInfoRequest.createTextNode(appId);
				aplicationNode.item(0).appendChild(aplicationValueNode);
				
				//certificado
				NodeList certificateNode = getCertificateInfoRequest.getElementsByTagName("certificate");
				CDATASection certificateValueNode=getCertificateInfoRequest.createCDATASection(certificate);
				certificateNode.item(0).appendChild(certificateValueNode);			
				
				return getCertificateInfoRequest;
			}
			catch(Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de validación de certificados.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		
		
		
		/**
		 * Método que indica si la respuesta de la plataforma ha sido correcta o no
		 * 
		 * @param response Respuesta XML de la plataforma
		 * @return true en caso afirmativo, false en caso contrario
		 */
		public static synchronized boolean isCorrectEng(String response)
		{
			Document responseDoc = null;
			
			try
			{
				responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
				System.exit(-1);
				return false;			
			}
			
			try
			{						
				NodeList statusNode = null;

				statusNode = responseDoc.getElementsByTagName("status");
				
				return new Boolean(statusNode.item(0).getFirstChild().getNodeValue()).booleanValue();
			}
			catch (Exception e)
			{
				// Comprobamos si hemos recibido una excepcion
				try
				{
					// Codigo de error
					NodeList errorCodeNode = responseDoc.getElementsByTagName("errorCode");
					// descripcion del error
					NodeList descripcionErrorNode = responseDoc.getElementsByTagName("description");
					
					System.err.println(
							errorCodeNode.item(0).getFirstChild().getNodeValue() + ": " + 
							descripcionErrorNode.item(0).getFirstChild().getNodeValue());
					return false;
				}
				catch (Exception ee)
				{
					System.err.println(response);
					return false;
				}
			}
		}
		
		/**
		 * Método que obtiene el resultado de una peticion de validacion de certificado a la plataforma
		 * 
		 * @param response Respuesta XML de la plataforma
		 * @return resultado de la validacion
		 */
		public static synchronized String getResultCertificateValidationRequestEng(String response)
		{
			try
			{			
				Document responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
				
				NodeList statusNode = null;
				
				statusNode = responseDoc.getElementsByTagName("description");
				
				if (statusNode.getLength()==0)
				{
					System.err.println("La petición de Validación del certificado no ha sido satisfactoria. Saliendo ...");
					System.exit(-1);
					return null;				
				}
			
				return statusNode.item(0).getFirstChild().getNodeValue();
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
				System.exit(-1);
				return null;
			}
		}
		
		/**
		 * Método que indica si la respuesta de una peticion de Obtencion de Informacion de Certificado a la plataforma ha sido correcta o no
		 * 
		 * @param response Respuesta XML de la plataforma
		 * @return true en caso afirmativo, false en caso contrario
		 */
		public static synchronized boolean isCorrectGetCertificateInfoRequestEng(String response)
		{
			try
			{			
				Document responseDoc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
				
				NodeList statusNode = null;
				
				//Si existe el nodo InfoCertificate significa que la peticion ha sido satisfactoria
				statusNode = responseDoc.getElementsByTagName("InfoCertificate");
				
				if (statusNode.getLength()==0)
					return false;				

				return true;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error obteniendo el estado de la respuesta");
				System.out.println(e.getMessage());
				System.exit(-1);
				return false;
			}
		}

		public static String getAfirmaIP() {
			return afirmaIP;
		}

		public static Document prepareServerSignatureIncDocRequestEng(String appId, String document, String aliasServerCert, String referenceId, String hashAlgorithm, String signatureFormat,String fileName, String fileType) {
			try
			{
				Document serverSignatureRequest = db.parse(new ByteArrayInputStream(WebServicesAvailable.ServerSignatureIncDocRequestEng.getBytes("UTF-8")));
				
				// Id de aplicación
				NodeList applicationNode = serverSignatureRequest.getElementsByTagName("applicationId");
				Text applicationValueNode = serverSignatureRequest.createTextNode(appId);
				applicationNode.item(0).appendChild(applicationValueNode);
				
				// Documento a firmar
				NodeList documentNode = serverSignatureRequest.getElementsByTagName("document");
				documentNode.item(0).appendChild(serverSignatureRequest.createCDATASection(document));
				
				// Alias del certificado servidor a emplear en la Firma Electrónica
				NodeList signerNode = serverSignatureRequest.getElementsByTagName("signer");
				Text signerValueNode = serverSignatureRequest.createTextNode(aliasServerCert);
				signerNode.item(0).appendChild(signerValueNode);

				// Id de referencia externo (proporcionado por la aplicacion)
				if (referenceId != null) {
					NodeList idReferenceNode = serverSignatureRequest.getElementsByTagName("reference");
					Text idReferenceValueNode = serverSignatureRequest.createTextNode(referenceId);
					idReferenceNode.item(0).appendChild(idReferenceValueNode);
				}
				
				// Algoritmo de hash
				NodeList hashAlgorithmNode = serverSignatureRequest.getElementsByTagName("hashAlgorithm");
				Text hashAlgorithmValueNode = serverSignatureRequest.createTextNode(hashAlgorithm);
				hashAlgorithmNode.item(0).appendChild(hashAlgorithmValueNode);
				
				// Formato de firma
				NodeList signatureFormatNode = serverSignatureRequest.getElementsByTagName("eSignatureFormat");
				Text signatureFormatValueNode = serverSignatureRequest.createTextNode(signatureFormat);
				signatureFormatNode.item(0).appendChild(signatureFormatValueNode);
				
				//nombre documento
				NodeList documentNameNode = serverSignatureRequest.getElementsByTagName("documentName");
				Text documentNameNodeValueNode=serverSignatureRequest.createTextNode(fileName);
				documentNameNode.item(0).appendChild(documentNameNodeValueNode);
				
				//tipo documento
				NodeList documentTypeNode = serverSignatureRequest.getElementsByTagName("documentType");
				Text documentTypeNodeValueNode=serverSignatureRequest.createTextNode(fileType);
				documentTypeNode.item(0).appendChild(documentTypeNodeValueNode);
				return serverSignatureRequest;
			}
			catch (Exception e)
			{
				System.err.println("Se ha producido un error generando la petición de firma servidor.");
				System.out.println(e.getMessage());
				System.exit(-1);
				return null;
			}
		}
		
		
		
		
	}