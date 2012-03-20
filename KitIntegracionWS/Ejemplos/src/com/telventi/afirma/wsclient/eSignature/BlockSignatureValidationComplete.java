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

public class BlockSignatureValidationComplete extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Validar Firma por Bloques Completo es la siguiente:\n" +
		"> BlockSignatureValidationComplete applicationId transactionId\n" +
		"\n" +
		"donde\n" +
		"   applicationId                 --> Identificador de la aplicacion\n" +
		"   transactionId                 --> Identificador de transaccion\n";

	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new BlockSignatureValidationComplete().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE VALIDACI�N DE FIRMA POR BLOQUES COMPLETO]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);
		
		
		
		//Preparaci�n de la petici�n al servicio Web de ObtenerFirmaTransaccion
		System.out.println(".[Preparando la petici�n al servicio Web " + getESignatureWebServiceNameEng + "...]");
		Document getESignatureRequest = UtilsWebService.prepareGetESignatureRequestEng(appId,idTransaction);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getESignatureRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getESignatureWebServiceNameEng, getESignatureRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Obtenci�n de Firma Electronica correspondiente a la transacci�n " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
		
		//Obtenci�n de la Firma electronica 
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signature = UtilsWebService.getInfoFromDocumentNode(response, "eSignature");
		System.out.println(".[/Firma electronica correctamente extra�do de la respuesta]");
		
		//Obtenci�n del formato de firma
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "eSignatureFormat");
		System.out.println(".[/Firma electronica correctamente extra�do de la respuesta]");		
		
		//Preparaci�n de la petici�n al servicio Web de ObtenerBloqueFirma
		System.out.println(".[Preparando la petici�n al servicio Web " + getSignaturesBlockWebServiceNameEng + "...]");
		Document getSignaturesBlockRequest = UtilsWebService.prepareGetSignaturesBlockRequestEng(appId,idTransaction);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getSignaturesBlockRequest));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(getSignaturesBlockWebServiceNameEng, getSignaturesBlockRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Obtenci�n del Bloque de Firmas correspondiente a la transacci�n " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
		
		//Obtenci�n del Bloque de firmas 
		System.out.println(".[Extrayendo el Bloque de firmas de la respuesta...]");
		String signaturesBlock = UtilsWebService.getInfoFromDocumentNode(response, "block");
		System.out.println(".[/Bloque de firmas correctamente extra�do de la respuesta]");		
		
		// Preparaci�n de la petici�n al servicio Web de ValidarFirmaBloquesCompleto
		System.out.println(".[Preparando la petici�n al servicio Web " + blockSignatureFullValidacionWebServiceNameEng + "...]");
		Document blockSignatureFullValidationRequest = UtilsWebService.prepareBlockSignatureFullValidationRequestEng(appId,signature,signaturesBlock, signatureFormat);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(blockSignatureFullValidationRequest));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(blockSignatureFullValidacionWebServiceNameEng, blockSignatureFullValidationRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Validacion de Bloque de Firmas Completo no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");		
		
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
