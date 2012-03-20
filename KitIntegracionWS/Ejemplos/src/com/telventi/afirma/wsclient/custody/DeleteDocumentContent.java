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
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */
public class DeleteDocumentContent extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String docId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Eliminar el Contenido de un Documento es la siguiente:\n" +
		"> DeleteDocumentContent applicationId documentId \n" +
		"\n" +
		"  donde\n" +
		"   applicationId             --> Identificador de la aplicacion\n" +
		"   documentId                --> Identificador del documento del cual se desea borrar el contenido\n";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new DeleteDocumentContent().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE BORRADO DEL CONTENIDO DE UN DOCUMENTO]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		// Preparaci�n de la petici�n al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + deleteDocumentContentWebServiceNameEng + "...]");
		Document deleteDocumentContentRequest = UtilsWebService.prepareDeleteDocumentContentRequestEng(appId, docId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(deleteDocumentContentRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(deleteDocumentContentWebServiceNameEng, deleteDocumentContentRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petici�n de borrado del contenido del documento no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
							
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
