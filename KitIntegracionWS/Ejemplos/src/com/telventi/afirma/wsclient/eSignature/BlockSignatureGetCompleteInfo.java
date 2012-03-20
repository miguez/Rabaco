package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */

public class BlockSignatureGetCompleteInfo extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener Informaci�n Completa de un Bloque de Firmas generado por @firma 5.0 es la siguiente:\n" +
		"> BlockSignatureGetCompleteInfo applicationId transactionId\n" +
		"\n" +
		"donde\n" +
		"   applicationId                 --> Identificador de la aplicacion\n" +
		"   transactionId                 --> Identificador de transaccion asociada al bloque de firmas del cual se desean extraer\n" +
		"                                     toda la informaci�n.\n";

	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new BlockSignatureGetCompleteInfo().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DE LA INFORMACI�N COMPLETA DEL BLOQUE DE FIRMAS]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);
		
				
		
		// Preparaci�n de la petici�n al servicio Web de ObtenerIdDocumentoBloqueFirmasBackwards
		System.out.println(".[Preparando la petici�n al servicio Web " + getCompleteInfoSignaturesBlockWebServiceNameEng + "...]");
		Document getCompleteInfoSignatureBlockRequest = UtilsWebService.prepareGetCompleteInfoSignaturesBlockRequestEng(appId,idTransaction);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getCompleteInfoSignatureBlockRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getCompleteInfoSignaturesBlockWebServiceNameEng, getCompleteInfoSignatureBlockRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Obtenci�n de Informaci�n Completa del bloque de Firmas no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");		
		
		System.out.println("[FIN DE PROCESO DE OBTENCI�N DE IDS DE DOCUMENTOS DE BLOQUE DE FIRMAS]");		
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			idTransaction = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			appId = args[0];
			idTransaction = args[1];
		} catch (Exception e) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}	
}
