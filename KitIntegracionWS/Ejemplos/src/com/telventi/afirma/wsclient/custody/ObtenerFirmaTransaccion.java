/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripci�n: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creaci�n: 22-jun-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.custody;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJLHA
 *
 */
public class ObtenerFirmaTransaccion extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String transactionId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener la Firma Electr�nica de una Transacci�n es la siguiente:\n" +
		"> ObtenerFirmaTransaccion idAplicacion idTransaccion\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idTransaccion            --> Identificador de la transacci�n correspondiente al proceso de firma de la cual se desea obtener la Firma Electr�nica\n"; 
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ObtenerFirmaTransaccion().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DE LA FIRMA ELECTR�NICA DE UNA TRANSACCI�N]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + getESignatureWebServiceName + "...]");
		Document getESignatureRequest = UtilsWebService.prepareGetESignatureRequest(appId, transactionId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getESignatureRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getESignatureWebServiceName, getESignatureRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de obtenci�n de la Firma Electr�nica de una transacci�n no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
		// Obtenci�n del detalle de la respuesta
		System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
		String eSignature = UtilsWebService.getInfoFromDocumentNode(response, "firmaElectronica");
		String eSignatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "formatoFirma");
		if (eSignature == null || eSignatureFormat == null)
		{
			System.err.println("No se ha podido obtener la informaci�n de la respuesta. Saliendo...");
			System.exit(-1);
		}
		System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
		
		// Decodificamos el contenido del documento
		System.out.println(".[Decodificando la Firma Electr�nica codificada en Base64...]");
		byte[] eSignatureBase64Decoded = null;
		try
		{
			eSignatureBase64Decoded = base64Coder.decodeBase64(eSignature.getBytes());
			System.out.println(".[/Decodificaci�n de la Firma Electr�nica codificada en Base64 correctamente realizada.]");
		}
		catch (Exception e)
		{
			System.out.println(".[/Error decodificando la Firma Electr�nica codificada en Base64.]");
			System.exit(-1);
		}
		
		String eSignatureDestFileName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
		if (UtilsSignature.isASN1TypeOfSignature(eSignatureFormat))
			eSignatureDestFileName += ".p7s";
		else if(eSignatureFormat.equals("PDF"))
			eSignatureDestFileName += ".pdf";
		else if(eSignatureFormat.equals("ODF"))
			eSignatureDestFileName += ".odt";
		else
			eSignatureDestFileName += ".xml";
		System.out.println(".[Almacenando la Firma Electr�nica recibida en el fichero " + eSignatureDestFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(eSignatureBase64Decoded, eSignatureDestFileName);
		System.out.println(".[/Firma Electr�nica recibida correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE OBTENCI�N DE LA FIRMA ELECTR�NICA DE UNA TRANSACCI�N FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			transactionId = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			transactionId = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
