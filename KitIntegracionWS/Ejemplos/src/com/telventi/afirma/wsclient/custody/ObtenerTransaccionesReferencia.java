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
public class ObtenerTransaccionesReferencia extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String externalReference = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener las transacciones por referencia externa es la siguiente:\n" +
		"> ObtenerTransaccionesReferencia idAplicacion referenciaExterna\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   referenciaExterna        --> Referencia Externa";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ObtenerTransaccionesReferencia().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DE TRANSACCIONES POR REFERENCIA EXTERNA]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + getTransactionsByExternalReferenceWebServiceName + "...]");
		Document getTransactionsByExternalReferenceRequest = UtilsWebService.prepareGetTransactionsByExternalReference(appId, externalReference);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getTransactionsByExternalReferenceRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getTransactionsByExternalReferenceWebServiceName, getTransactionsByExternalReferenceRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de obtenci�n de transacciones por referencia externa no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
		// Obtenci�n del detalle de la respuesta
		System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
		String transactionsValue = UtilsWebService.getInfoFromDocumentNode(response, "idTransacciones");
		if (transactionsValue == null || transactionsValue.length() == 0)
			System.err.println("No existen transacciones realizadas por la aplicaci�n " + appId + " con referencia externa = " + externalReference);
		else
			System.out.println(transactionsValue);
		System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
		
		System.out.println("[/PROCESO DE OBTENCI�N DE TRANSACCIONES POR REFERENCIA EXTERNA FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			externalReference = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			externalReference = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
