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

public class ThreePhaseUserSignatureCounterSign extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	private String referenceId = null;
	private String updateSignatureFormat=DEFAULT_UPDATE_SIGNATURE_FORMAT;
	
	private static final String errorMessage =
		"La sintaxis de la aplicación de prueba de Firma 3 Fases CounterSign es la siguiente:\n" +
		"> ThreePhaseUserSignatureCounterSign idAplicacion idTransaccion [-r referenciaExterna] [-e extenderFormatoFirma]\n\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   idTransaccion            --> Identificador de la transacción de firma sore la que se desea hacer la multifirma CounterSign\n" +
		"   -r referenciaExterna     --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n";
//		"   -e extenderFormatoFirma  --> Formato genérico de la firma al cual extender\n" +
//		"                                la firma electrónica. Opcional\n" +
//		"                                Valores posibles: ES-T\n";
	
	public static void main (String[] args)
	{		
		if (!StartingClass.usarFichParams && (args == null || args.length < 2 || args.length > 6))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ThreePhaseUserSignatureCounterSign().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA 3 FASES COUNTERSIGN]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		
	
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 1											 //
		//Preparación de la petición al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petición al servicio Web " + threePhasesUserSignatureF1CounterSignWebServiceNameEng + "...]");
		Document threePhasesUserSignaturesF1CounterSignRequest = UtilsWebService.prepareThreePhasesUserSignaturesF1CounterSignRequestEng(appId,idTransaction);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(threePhasesUserSignaturesF1CounterSignRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(threePhasesUserSignatureF1CounterSignWebServiceNameEng,threePhasesUserSignaturesF1CounterSignRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");	
		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Usuario 3 Fases F1 CounterSign no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención del identificador de transaccion 
		System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transaccion correctamente extraído de la respuesta]");
		
		//Obtención de la Firma electronica 
		System.out.println(".[Extrayendo la firma electronica de la respuesta...]");
		String signature = UtilsWebService.getInfoFromDocumentNode(response, "eSignature");
		System.out.println(".[/Firma electronica correctamente extraído de la respuesta]");		
		
		//Obtención del formato de la firma
		System.out.println(".[Extrayendo el formato de la firma de la respuesta...]");
		String signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "eSignatureFormat");
		System.out.println(".[/Formato de la firma correctamente extraído de la respuesta]");
		
		//Decodificamos la Firma Electrónica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signature.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma 3 Fases CounterSign");
			System.exit(-1);
		} 

		//Generamos el nombre del fichero que contendrá la firma obtenida
		String serverSignatureName = TEMPORAL_DIR + "/eSignatureReceived_tempfile_" + appId + "_" + transactionId;
		serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
		
		System.out.println(".[Almacenando Firma Electrónica recibida en el fichero temporal " + serverSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, serverSignatureName);
		System.out.println(".[/Firma Electrónica recibida correctamente almacenada]");
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 2											 //
		//Realización de la firma electrónica del Hash y de la Firma electronica.
		System.out.println(".[Realizando la Firma Electrónica countersign...]");
		BeanSalidaBrowser appletOut =  BrowserAfirma.navegarCounterSign(serverSignatureName,signatureFormat);
		System.out.println(".[/Firma Electrónica realizada correctamente]");
		
		//Obtención de la Firma Electrónica del hash del Bloque de Firma y del Certificado del firmante
		String signatureValue= appletOut.getFirma();
		String signCertificateValue= appletOut.getCertificado();
		
		//Borramos el fichero temporal generado
		System.out.println(".[Borrando la Firma Electrónica temporal " + serverSignatureName + "...]");
		UtilsFileSystem.deleteFile(serverSignatureName);
		System.out.println(".[/Firma Electronica temporal correctamente borrada.]");
		///////////////////////////////////////////////////////////////////////////////////
	
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 3											 //
		//Preparación de la petición al servicio Web de FirmaUsuario3FasesF3
		System.out.println(".[Preparando la petición al servicio Web " + threePhasesUserSignatureF3WebServiceNameEng + "...]");
		Document threePhasesUserSignatureF3Request = UtilsWebService.prepareThreePhasesUserSignatureF3RequestEng(appId, transactionId, signatureValue, signCertificateValue,signatureFormat, updateSignatureFormat, referenceId);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
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
			System.err.println("La petición de Firma Usuario 3 Fases F3 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");

		//Decodificamos la Firma Electrónica realizada
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma ");
			System.exit(-1);
		} 
		
		//Generamos el nombre del fichero que contendrá la firma de usuario generada
		String userSignatureName = TEMPORAL_DIR + "/userESignature3PhaseCounterSign_tempfile_" + appId + "_" + transactionId;
		userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName,signatureFormat);

		System.out.println(".[Almacenando la Firma Electrónica del usuario countersign en el fichero " + userSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
		System.out.println(".[/Firma Usuario 3 Fases CounterSign correctamente almacenada]");	
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 							VALIDACIÓN JUSTIFICANTE DE FIRMA 					 //
		// Preparación de la petición al servicio Web de ValidarFirma
		System.out.println(".[Validando JUSTIFICANTE de FIRMA ELECTRÓNICA de la Plataforma...]");
		System.out.println(".[Extrayendo el Justificante de Firma Electrónica de la respuesta...]");
		String evidence = UtilsWebService.getInfoFromDocumentNode(response, "evidenceOfESignature");
		System.out.println(".[/Justificante de Firma Electrónica correctamente extraído de la respuesta]");
		System.out.println("..[Preparando la petición al servicio Web " + signatureValidationWebServiceNameEng + "...]");
		Document eSignatureValidationRequest = null;
		try
		{
			eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(evidence), DEFAULT_SIGNATURE_FORMAT, null, null, null);
		}
		catch (Exception e)
		{
			System.err.println("..[/Petición incorrectamente preparada]");
			System.exit(-1);
		}
		System.out.println("..[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println("..[Lanzando la petición...]");
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
			System.err.println("La petición de verificación del Justificante de Firma Electrónica de la Plataforma no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println("..[/Petición correctamente realizada]");
		System.out.println(".[/JUSTIFICANTE de FIRMA ELECTRÓNICA de la Plataforma correctamente validado]");
		///////////////////////////////////////////////////////////////////////////////////

		System.out.println("[/PROCESO DE FIRMA 3 FASES COUNTERSIGN FINALIZADO]");
		
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
//			boolean existExtendSignature = false;
			
			while (i < args.length) {
				
				if (args[i].equals("-r")) {
					
					if (existExternReference)
						throw new Exception();
					i++;
					referenceId = args[i];
					i++;
					existExternReference = true;
					
//				} else if (args[i].equals("-e")) {
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
