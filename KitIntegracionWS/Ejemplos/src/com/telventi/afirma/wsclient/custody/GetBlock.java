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
public class GetBlock extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String transactionId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener el Bloque de Firmas Electr�nicas de una Transacci�n es la siguiente:\n" +
		"> GetBlock applicationId transactionId\n" +
		"\n" +
		"  donde\n" +
		"   applicationId             --> Identificador de la aplicacion\n" +
		"   transactionId             --> Identificador de la transacci�n correspondiente al proceso de firma de la cual se desea obtener el Bloque de Firmas Electr�nicas\n"; 
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new GetBlock().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DEL BLOQUE DE FIRMAS ELECTR�NICAS DE UNA TRANSACCI�N]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + getSignaturesBlockWebServiceNameEng + "...]");
		Document getSignaturesBlockRequest = UtilsWebService.prepareGetSignaturesBlockRequestEng(appId, transactionId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getSignaturesBlockRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getSignaturesBlockWebServiceNameEng, getSignaturesBlockRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de obtenci�n del Bloque de Firmas Electr�nicas de una transacci�n no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
		// Obtenci�n del detalle de la respuesta
		System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
		String block = UtilsWebService.getInfoFromDocumentNode(response, "block");
		if (block == null)
		{
			System.err.println("No se ha podido obtener la informaci�n de la respuesta. Saliendo...");
			System.exit(-1);
		}
		System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
		
		// Decodificamos el contenido del documento
		System.out.println(".[Decodificando el Bloque de Firmas Electr�nicas codificado en Base64...]");
		byte[] blockBase64Decoded = null;
		try
		{
			blockBase64Decoded = base64Coder.decodeBase64(block.getBytes());
			System.out.println(".[/Decodificaci�n del Bloque de Firmas Electr�nicas codificado en Base64 correctamente realizada.]");
		}
		catch (Exception e)
		{
			System.out.println(".[/Error decodificando el Bloque de Firmas Electr�nicas codificado en Base64.]");
			System.exit(-1);
		}
		
		String blockDestFileName = TEMPORAL_DIR + "/block_tempfile_" + appId + "_" + transactionId;
		System.out.println(".[Almacenando el Bloque de Firmas Electr�nicas recibido en el fichero " + blockDestFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(blockBase64Decoded, blockDestFileName);
		System.out.println(".[/Bloque de Firmas Electr�nicas recibido correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE OBTENCI�N DEL BLOQUE DE FIRMAS ELECTR�NICAS DE UNA TRANSACCI�N FINALIZADO]");
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
