/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripci�n: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creaci�n: 22-jun-2006</p>
* @author MAMFN
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.custody;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */
public class GetDocumentContentByDocId extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String docId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener el Contenido de un Documento por id es la siguiente:\n" +
		"> GetDocumentContentByDocId applicationId documentId\n" +
		"\n" +
		"  donde\n" +
		"   applicationId            --> Identificador de la aplicacion\n" +
		"   documentId               --> Identificador del documento del cual se desea obtener el contenido\n";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new GetDocumentContentByDocId().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DEL CONTENIDO DE UN DOCUMENTO POR ID]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + getDocumentContentByIdWebServiceNameEng + "...]");
		Document getDocumentContentByIdRequest = UtilsWebService.prepareGetDocumentContentIdRequestEng(appId, docId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getDocumentContentByIdRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getDocumentContentByIdWebServiceNameEng, getDocumentContentByIdRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de obtenci�n del contenido del documento por id no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
		// Obtenci�n del detalle de la respuesta
		System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
		String documentValue = UtilsWebService.getInfoFromDocumentNode(response, "document");
		if (documentValue == null)
		{
			System.err.println("No se ha podido obtener la informaci�n de la respuesta. Saliendo...");
			System.exit(-1);
		}
		System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
		
		// Decodificamos el contenido del documento
		System.out.println(".[Decodificando el contenido del documento codificado en Base64...]");
		byte[] documentContentBase64Decoded = null;
		try
		{
			documentContentBase64Decoded = base64Coder.decodeBase64(documentValue.getBytes());
			System.out.println(".[/Decodificaci�n del contenido del documento codificado en Base64 correctamente realizada.]");
		}
		catch (Exception e)
		{
			System.out.println(".[/Error decodificando el contenido del documento codificado en Base64.]");
			System.exit(-1);
		}
		
		String docDestFileName = TEMPORAL_DIR + "/document_tempfile_" + appId + "_" + docId;
		System.out.println(".[Almacenando el documento recibido en el fichero " + docDestFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(documentContentBase64Decoded, docDestFileName);
		System.out.println(".[/Documento recibido correctamente almacenado]");	
		
		System.out.println("[/PROCESO DE OBTENCI�N DEL CONTENIDO DE UN DOCUMENTO POR ID FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			docId = obtenerPropiedadArg(StartingClass.PROP_IDDOCUMENTO);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			docId = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
