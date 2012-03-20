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
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJLHA
 *
 */
public class EliminarContenidoDocumento extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String docId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Eliminar el Contenido de un Documento es la siguiente:\n" +
		"> EliminarContenidoDocumento idAplicacion idDocumento \n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idDocumento              --> Identificador del documento del cual se desea borrar el contenido\n";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new EliminarContenidoDocumento().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE BORRADO DEL CONTENIDO DE UN DOCUMENTO]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Preparación de la petición al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petición al servicio Web " + deleteDocumentContentWebServiceName + "...]");
		Document deleteDocumentContentRequest = UtilsWebService.prepareDeleteDocumentContentRequest(appId, docId);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(deleteDocumentContentRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(deleteDocumentContentWebServiceName, deleteDocumentContentRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petición de borrado del contenido del documento no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		System.out.println("[/PROCESO DE BORRADO DEL CONTENIDO DE UN DOCUMENTO FINALIZADO]");
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
