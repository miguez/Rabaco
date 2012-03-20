/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 22-jun-2006</p>
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
		"La sintaxis de la aplicación de prueba de Obtener la Firma Electrónica de una Transacción es la siguiente:\n" +
		"> ObtenerFirmaTransaccion idAplicacion idTransaccion\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idTransaccion            --> Identificador de la transacción correspondiente al proceso de firma de la cual se desea obtener la Firma Electrónica\n"; 
		
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
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DE LA FIRMA ELECTRÓNICA DE UNA TRANSACCIÓN]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		

		// Preparación de la petición al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petición al servicio Web " + getESignatureWebServiceName + "...]");
		Document getESignatureRequest = UtilsWebService.prepareGetESignatureRequest(appId, transactionId);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
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
			System.err.println("La petición de obtención de la Firma Electrónica de una transacción no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		// Obtención del detalle de la respuesta
		System.out.println(".[Extrayendo la información detallada de la respuesta...]");	
		String eSignature = UtilsWebService.getInfoFromDocumentNode(response, "firmaElectronica");
		String eSignatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "formatoFirma");
		if (eSignature == null || eSignatureFormat == null)
		{
			System.err.println("No se ha podido obtener la información de la respuesta. Saliendo...");
			System.exit(-1);
		}
		System.out.println(".[/Información detallada correctamente extraída de la respuesta]");		 
		
		// Decodificamos el contenido del documento
		System.out.println(".[Decodificando la Firma Electrónica codificada en Base64...]");
		byte[] eSignatureBase64Decoded = null;
		try
		{
			eSignatureBase64Decoded = base64Coder.decodeBase64(eSignature.getBytes());
			System.out.println(".[/Decodificación de la Firma Electrónica codificada en Base64 correctamente realizada.]");
		}
		catch (Exception e)
		{
			System.out.println(".[/Error decodificando la Firma Electrónica codificada en Base64.]");
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
		System.out.println(".[Almacenando la Firma Electrónica recibida en el fichero " + eSignatureDestFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(eSignatureBase64Decoded, eSignatureDestFileName);
		System.out.println(".[/Firma Electrónica recibida correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE OBTENCIÓN DE LA FIRMA ELECTRÓNICA DE UNA TRANSACCIÓN FINALIZADO]");
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
