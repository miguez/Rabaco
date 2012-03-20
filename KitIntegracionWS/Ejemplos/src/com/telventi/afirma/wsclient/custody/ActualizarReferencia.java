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
public class ActualizarReferencia extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String transactionId = null;
	private String externalReference = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Actualizar Referencia Externa de una Transacción es la siguiente:\n" +
		"> ActualizarReferencia idAplicacion idTransaccion referenciaExterna\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idTransaccion            --> Identificador de la transacción correspondiente al proceso de firma sobre la cual se desea actualizar la referencia externa\n" + 
		"   referenciaExterna        --> Referencia Externa";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 3))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ActualizarReferencia().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE ACTUALIZACIÓN DE LA REFERENCIA EXTERNA DE UNA TRANSACCIÓN]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Preparación de la petición al servicio Web de ObtenerContenidoDocumento
		System.out.println(".[Preparando la petición al servicio Web " + setExternalReferenceWebServiceName + "...]");
		Document setExternalReferenceRequest = UtilsWebService.prepareSetExternalReferenceRequest(appId, transactionId, externalReference);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(setExternalReferenceRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(setExternalReferenceWebServiceName, setExternalReferenceRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println();
			System.err.println("La petición de actualización de referencia externa de la transacción " + transactionId + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		System.out.println("[/PROCESO DE ACTUALIZACIÓN DE LA REFERENCIA EXTERNA DE UNA TRANSACCIÓN FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			transactionId = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
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
			transactionId = args[1];
			externalReference = args[2];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
