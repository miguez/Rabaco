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
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */
public class GetTransactionsByDates extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String startDate = null;
	private String endDate = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Obtener las transacciones por fecha es la siguiente:\n" +
		"> GetTransactionsByDates applicationId startDate endDate\n" +
		"\n" +
		"  donde\n" +
		"   applicationId             --> Identificador de la aplicacion\n" +
		"   startDate                 --> Fecha de filtro de inicio\n" +
		"                                 formato: dd/mm/yyyy\n" +
		"   endDate                   --> Fecha de filtro de fin\n" +
		"                                 formato: dd/mm/yyyy";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 3))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new GetTransactionsByDates().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DE TRANSACCIONES POR FECHA]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Preparación de la petición al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petición al servicio Web " + getTransactionsByDateWebServiceNameEng + "...]");
		Document getTransactionsByDateRequest = UtilsWebService.prepareGetTransactionsByDateRequestEng(appId, startDate, endDate);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getTransactionsByDateRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getTransactionsByDateWebServiceNameEng, getTransactionsByDateRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petición de obtención de transacciones por fecha no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		// Obtención del detalle de la respuesta
		System.out.println(".[Extrayendo la información detallada de la respuesta...]");	
		String transactionsValue = UtilsWebService.getInfoFromDocumentNode(response, "idTransactions");
		if (transactionsValue == null || transactionsValue.length() == 0)
			System.err.println("No existen transacciones realizadas por la aplicación " + appId + " entre las fechas " + startDate + " - " + endDate);
		else
			System.out.println(transactionsValue);
		System.out.println(".[/Información detallada correctamente extraída de la respuesta]");		 
		
		System.out.println("[/PROCESO DE OBTENCIÓN DE TRANSACCIONES POR FECHA FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			startDate = obtenerPropiedadArg(StartingClass.PROP_FECHAINICIO);
			endDate = obtenerPropiedadArg(StartingClass.PROP_FECHAFIN);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			startDate = args[1];
			endDate = args[2];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
