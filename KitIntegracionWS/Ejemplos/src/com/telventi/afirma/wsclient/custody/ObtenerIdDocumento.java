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
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJLHA
 *
 */
public class ObtenerIdDocumento extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String transactionId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener el Identificador del Documento firmado es la siguiente:\n" +
		"> ObtenerIdDocumento idAplicacion idTransaccion\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idTransaccion            --> Identificador de la transacci�n correspondiente al proceso de firma sobre el documento del cual se desea obtener el identificador\n"; 
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ObtenerIdDocumento().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DEL IDENTIFICADOR DE UN DOCUMENTO FIRMADO]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + getDocumentIdWebServiceName + "...]");
		Document getDocumentIdRequest = UtilsWebService.prepareGetDocumentIdRequest(appId, transactionId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getDocumentIdRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getDocumentIdWebServiceName, getDocumentIdRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de obtenci�n del identificador del documento firmado no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
		// Obtenci�n del detalle de la respuesta
		System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
		String documentIdValue = UtilsWebService.getInfoFromDocumentNode(response, "idDocumento");
		if (documentIdValue == null)
			System.err.println("No se ha podido obtener el identificador del documento");
		else
			System.out.println("Identificador del documento firmado en la transaccion " + transactionId + " = " + documentIdValue);
		System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
		
		System.out.println("[/PROCESO DE OBTENCI�N DEL IDENTIFICADOR DE UN DOCUMENTO FIRMADO FINALIZADO]");
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
