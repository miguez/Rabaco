package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SERYS
 *
 */

public class ValidarFirmaBloquesCompleto extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Validar Firma por Bloques Completo es la siguiente:\n" +
		"> ValidarFirmaBloquesCompleto idAplicacion idTransaccion\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   idTransaccion                --> Identificador de transaccion\n";

	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ValidarFirmaBloquesCompleto().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE VALIDACIÓN DE FIRMA POR BLOQUES COMPLETO]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		
		
		//Preparación de la petición al servicio Web de ObtenerFirmaTransaccion
		System.out.println(".[Preparando la petición al servicio Web " + getESignatureWebServiceName + "...]");
		Document getESignatureRequest = UtilsWebService.prepareGetESignatureRequest(appId,idTransaction);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getESignatureRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getESignatureWebServiceName, getESignatureRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Obtención de Firma Electronica correspondiente a la transacción " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención de la Firma electronica 
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signature = UtilsWebService.getInfoFromDocumentNode(response, "firmaElectronica");
		System.out.println(".[/Firma electronica correctamente extraído de la respuesta]");
		
		//Obtención del formato de firma
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "formatoFirma");
		System.out.println(".[/Firma electronica correctamente extraído de la respuesta]");		
		
		//Preparación de la petición al servicio Web de ObtenerBloqueFirma
		System.out.println(".[Preparando la petición al servicio Web " + getSignaturesBlockWebServiceName + "...]");
		Document getSignaturesBlockRequest = UtilsWebService.prepareGetSignaturesBlockRequest(appId,idTransaction);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getSignaturesBlockRequest));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(getSignaturesBlockWebServiceName, getSignaturesBlockRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Obtención del Bloque de Firmas correspondiente a la transacción " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención del Bloque de firmas 
		System.out.println(".[Extrayendo el Bloque de firmas de la respuesta...]");
		String signaturesBlock = UtilsWebService.getInfoFromDocumentNode(response, "bloqueFirmas");
		System.out.println(".[/Bloque de firmas correctamente extraído de la respuesta]");		
		
		// Preparación de la petición al servicio Web de ValidarFirmaBloquesCompleto
		System.out.println(".[Preparando la petición al servicio Web " + blockSignatureFullValidacionWebServiceName + "...]");
		Document blockSignatureFullValidationRequest = UtilsWebService.prepareBlockSignatureFullValidationRequest(appId,signature,signaturesBlock, signatureFormat);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(blockSignatureFullValidationRequest));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(blockSignatureFullValidacionWebServiceName, blockSignatureFullValidationRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Validacion de Bloque de Firmas Completo no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");		
		
		System.out.println("[FIN DE PROCESO DE VALIDACION DE FIRMA POR BLOQUES COMPLETO]");		
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
