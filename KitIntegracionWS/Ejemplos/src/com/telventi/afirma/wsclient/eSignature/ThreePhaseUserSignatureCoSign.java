package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.BeanSalidaBrowser;
import com.telventi.afirma.wsclient.utils.BrowserAfirma;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */

public class ThreePhaseUserSignatureCoSign extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	private String referenceId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private String updateSignatureFormat = DEFAULT_UPDATE_SIGNATURE_FORMAT;
	
	private static final String errorMessage =
		"La sintaxis de la aplicaci�n de prueba de Firma 3 Fases CoSign es la siguiente:\n" +
		"> ThreePhaseUserSignatureCoSign idAplicacion idTransaccion [-a algoritmoHash] [-r referenciaExterna] [-ef extenderFormatoFirma]\n\n" +
		"  donde\n" +
		"   idAplicacion                --> Identificador de la aplicacion\n" +
		"   idTransaccion               --> Identificador de la transacci�n de firma sore la que se desea hacer la multifirma CoSign\n" +
		"   -a algoritmoHash            --> Algoritmo de hash a emplear en la firma\n" +
		"                                Opcional. SHA1 por defecto\n" +
		"                                Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -r referenciaExterna     	--> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n";
//		"   -ef extenderFormatoFirma    --> Formato gen�rico de la firma al cual extender\n" +
//		"                                la firma electr�nica. Opcional\n" +
//		"                                Valores posibles: ES-T\n";	
	
	public static void main (String[] args)
	{		
		if (!StartingClass.usarFichParams && (args == null || args.length < 2 || args.length > 8))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new ThreePhaseUserSignatureCoSign().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA 3 FASES COSIGN]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);
		
		
	
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 1											 //
		//Preparaci�n de la petici�n al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petici�n al servicio Web " + threePhasesUserSignatureF1CoSignWebServiceNameEng + "...]");
		Document threePhasesUserSignaturesF1CoSignRequest = UtilsWebService.prepareThreePhasesUserSignaturesF1CoSignRequestEng(appId,idTransaction,hashAlgorithm);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(threePhasesUserSignaturesF1CoSignRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(threePhasesUserSignatureF1CoSignWebServiceNameEng,threePhasesUserSignaturesF1CoSignRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");	
		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Firma Usuario 3 Fases F1 CoSign no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
		
		//Obtenci�n del identificador de transaccion 
		System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transaccion correctamente extra�do de la respuesta]");
		
		//Obtenci�n del hash 
		System.out.println(".[Extrayendo el hash de los datos a firmar de la respuesta...]");
		String hashSignature = UtilsWebService.getInfoFromDocumentNode(response, "hash");
		System.out.println(".[/Hash de los datos a firmar correctamente extra�do de la respuesta]");
		
		//Obtenci�n de la Firma electronica 
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signature = UtilsWebService.getInfoFromDocumentNode(response, "eSignature");
		System.out.println(".[/Firma electronica correctamente extra�do de la respuesta]");
	
		//Obtenci�n del formato de la firma
		System.out.println(".[Extrayendo el formato de la firma de la respuesta...]");
		String signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "eSignatureFormat");
		System.out.println(".[/Formato de la firma correctamente extra�do de la respuesta]");
		
		// Decodificamos la Firma Electr�nica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signature.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma 3 Fases CoSign");
			System.exit(-1);
		} 

		//Generamos el nombre del fichero que contendr� la firma obtenida
		String serverSignatureName = TEMPORAL_DIR + "/eSignatureReceived_tempfile_" + appId + "_" + transactionId;
		serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electr�nica en el fichero temporal " + serverSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, serverSignatureName);
		System.out.println(".[/Firma Usuario 3 Fases correctamente almacenada]");
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 2											 //		
		//Realizaci�n de la firma electr�nica del Hash y de la Firma electronica.
		System.out.println(".[Realizando la Firma CoSign de la Firma Electronica y del Hash ...]");
		//Se firma con el cliente la Firma electronica y el hash
		BeanSalidaBrowser appletOut =  BrowserAfirma.navegarCoSign(serverSignatureName,hashSignature,signatureFormat,hashAlgorithm+"WITHRSAENCRYPTION");
		System.out.println(".[/Firma Electr�nica realizada correctamente]");
		
		//Obtenci�n de la Firma Electr�nica del hash del Bloque de Firma y del Certificado del firmante
		String signatureValue= appletOut.getFirma();
		String signCertificateValue= appletOut.getCertificado();

		//Borramos el fichero temporal generado
		System.out.println(".[Borrando la Firma Electr�nica temporal " + serverSignatureName + "...]");
		UtilsFileSystem.deleteFile(serverSignatureName);
		System.out.println(".[/Firma Electronica temporal correctamente borrada.]");
		///////////////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 3											 //		
		//Preparaci�n de la petici�n al servicio Web de FirmaUsuario3FasesF3
		System.out.println(".[Preparando la petici�n al servicio Web " + threePhasesUserSignatureF3WebServiceNameEng + "...]");
		Document threePhasesUserSignatureF3Request = UtilsWebService.prepareThreePhasesUserSignatureF3RequestEng(appId, transactionId, signatureValue, signCertificateValue,signatureFormat, updateSignatureFormat, referenceId);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(threePhasesUserSignatureF3Request));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(threePhasesUserSignatureF3WebServiceNameEng,threePhasesUserSignatureF3Request);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de Firma Usuario 3 Fases F3 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}	
		System.out.println(".[/Petici�n correctamente realizada]");

		// Decodificamos la Firma Electr�nica recibida
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma ");
			System.exit(-1);
		} 
		
		// Generamos el nombre del fichero que contendr� la firma de usuario generada
		String userSignatureName = TEMPORAL_DIR + "/userESignature3PhaseCoSign_tempfile_" + appId + "_" + transactionId;
		userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName,signatureFormat);
	
		System.out.println(".[Almacenando la Firma Electr�nica en el fichero " + userSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
		System.out.println(".[/Firma Usuario 3 Fases CoSign correctamente almacenada]");
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 							VALIDACI�N JUSTIFICANTE DE FIRMA 					 //
		// Preparaci�n de la petici�n al servicio Web de ValidarFirma
		System.out.println(".[Validando JUSTIFICANTE de FIRMA ELECTR�NICA de la Plataforma...]");
		System.out.println(".[Extrayendo el Justificante de Firma Electr�nica de la respuesta...]");
		String evidence = UtilsWebService.getInfoFromDocumentNode(response, "evidenceOfESignature");
		System.out.println(".[/Justificante de Firma Electr�nica correctamente extra�do de la respuesta]");
		System.out.println("..[Preparando la petici�n al servicio Web " + signatureValidationWebServiceNameEng + "...]");
		Document eSignatureValidationRequest = null;
		try
		{
			eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(evidence), DEFAULT_SIGNATURE_FORMAT, null, null, null);
		}
		catch (Exception e)
		{
			System.err.println("..[/Petici�n incorrectamente preparada]");
			System.exit(-1);
		}
		System.out.println("..[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println("..[Lanzando la petici�n...]");
		System.out.println("...[peticion]");
		System.out.println(XMLUtils.DocumentToString(eSignatureValidationRequest));
		System.out.println("...[/peticion]");
		response = UtilsWebService.launchRequest(signatureValidationWebServiceNameEng, eSignatureValidationRequest);
		System.out.println("...[respuesta]");
		System.out.println(response);
		System.out.println("...[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petici�n de verificaci�n del Justificante de Firma Electr�nica de la Plataforma no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println("..[/Petici�n correctamente realizada]");
		System.out.println(".[/JUSTIFICANTE de FIRMA ELECTR�NICA de la Plataforma correctamente validado]");
		///////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("[/PROCESO DE FIRMA 3 FASES COSIGN FINALIZADO]");
		
		System.exit(0);
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			idTransaction = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);			
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			if (param != null) {
				referenceId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}
			
//			param = obtenerPropiedadArg(StartingClass.PROP_EXTENDERFORMATOFIRMA);
//			if (param != null) {
//				updateSignatureFormat = param;
//			}			
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			
			appId = args[0];
			idTransaction = args[1];
			
			int i = 2;
			boolean existExternReference = false;
			boolean existHashAlgorithm = false;
//			boolean existExtendSignature = false;
			
			while (i < args.length) {
				
				if (args[i].equals("-r")) {
					
					if (existExternReference)
						throw new Exception();
					i++;
					referenceId = args[i];
					i++;
					existExternReference = true;
					
				} else if (args[i].equals("-a")) {
					
					if (existHashAlgorithm)
						throw new Exception();
					
					i++;
					hashAlgorithm = args[i];
					i++;
					existHashAlgorithm = true;
					
//				} else if (args[i].equals("-ef")) {
//					
//					if (existExtendSignature)
//						throw new Exception();
//					
//					i++;
//					updateSignatureFormat = args[i];
//					i++;
//					existExtendSignature = true;
					
				} else {
					
					throw new Exception();
					
				}
				
			}
			
		} catch (Exception e) {
			System.err.println(errorMessage);
			System.exit(-1);
		}	
		
	}
}
