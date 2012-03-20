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
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJLHA
 *
 */
public class AlmacenarDocumento extends StartingClass implements WebServicesAvailable 
{
	private String appId = null;
	private String fileToRegister = null;
		
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Alta de Documento en Custodia es la siguiente:\n" +
		"> AlmacenarDocumento idAplicacion ficheroACustodiar\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   ficheroACustodiar        --> Ruta completa al fichero a custodiar";

	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new AlmacenarDocumento().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE CUSTODIA DE DOCUMENTO]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo información del fichero " + fileToRegister + "...]");
		byte[] fileContentToRegister = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToRegister);		
		String fileName = UtilsFileSystem.getNameFromFilePath(fileToRegister);
		String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToRegister);
		System.out.println(".[/Información correctamente obtenida]");
		
		// Preparación de la petición al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petición al servicio Web " + custodyDocumentWebServiceName + "...]");
		Document custodyDocumentRequest = UtilsWebService.prepareCustodyDocumentRequest(appId, fileName, fileType, new String(fileContentToRegister));
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(custodyDocumentRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(custodyDocumentWebServiceName, custodyDocumentRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Almacenamiento del documento " + fileToRegister + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
					
		// Obtención del identificador del documento custodiado
		System.out.println(".[Extrayendo el identificador del documento de la respuesta...]");
		String docId = UtilsWebService.getInfoFromDocumentNode(response, "idDocumento");
		if (docId == null)
			System.err.println("No se ha podido obtener el identificador del documento");
		else
			System.out.println(".[/Identificador del documento correctamente extraído de la respuesta = " + docId + "]");

		System.out.println("[/PROCESO DE CUSTODIA DE DOCUMENTO FINALIZADO]");
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			fileToRegister = obtenerPropiedadArg(StartingClass.PROP_FICHEROACUSTODIAR);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			fileToRegister = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
