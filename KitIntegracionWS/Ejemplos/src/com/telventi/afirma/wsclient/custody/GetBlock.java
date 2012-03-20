/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 22-jun-2006</p>
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
		"La sintaxis de la aplicación de prueba de Obtener el Bloque de Firmas Electrónicas de una Transacción es la siguiente:\n" +
		"> GetBlock applicationId transactionId\n" +
		"\n" +
		"  donde\n" +
		"   applicationId             --> Identificador de la aplicacion\n" +
		"   transactionId             --> Identificador de la transacción correspondiente al proceso de firma de la cual se desea obtener el Bloque de Firmas Electrónicas\n"; 
		
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
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DEL BLOQUE DE FIRMAS ELECTRÓNICAS DE UNA TRANSACCIÓN]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Preparación de la petición al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petición al servicio Web " + getSignaturesBlockWebServiceNameEng + "...]");
		Document getSignaturesBlockRequest = UtilsWebService.prepareGetSignaturesBlockRequestEng(appId, transactionId);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
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
			System.err.println("La petición de obtención del Bloque de Firmas Electrónicas de una transacción no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		// Obtención del detalle de la respuesta
		System.out.println(".[Extrayendo la información detallada de la respuesta...]");	
		String block = UtilsWebService.getInfoFromDocumentNode(response, "block");
		if (block == null)
		{
			System.err.println("No se ha podido obtener la información de la respuesta. Saliendo...");
			System.exit(-1);
		}
		System.out.println(".[/Información detallada correctamente extraída de la respuesta]");		 
		
		// Decodificamos el contenido del documento
		System.out.println(".[Decodificando el Bloque de Firmas Electrónicas codificado en Base64...]");
		byte[] blockBase64Decoded = null;
		try
		{
			blockBase64Decoded = base64Coder.decodeBase64(block.getBytes());
			System.out.println(".[/Decodificación del Bloque de Firmas Electrónicas codificado en Base64 correctamente realizada.]");
		}
		catch (Exception e)
		{
			System.out.println(".[/Error decodificando el Bloque de Firmas Electrónicas codificado en Base64.]");
			System.exit(-1);
		}
		
		String blockDestFileName = TEMPORAL_DIR + "/block_tempfile_" + appId + "_" + transactionId;
		System.out.println(".[Almacenando el Bloque de Firmas Electrónicas recibido en el fichero " + blockDestFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(blockBase64Decoded, blockDestFileName);
		System.out.println(".[/Bloque de Firmas Electrónicas recibido correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE OBTENCIÓN DEL BLOQUE DE FIRMAS ELECTRÓNICAS DE UNA TRANSACCIÓN FINALIZADO]");
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
