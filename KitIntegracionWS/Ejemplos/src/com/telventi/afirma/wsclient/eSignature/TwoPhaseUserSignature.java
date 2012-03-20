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


public class TwoPhaseUserSignature extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String hashAlgorithm=DEFAULT_HASH_ALGORITHM;
	private String signatureFormat=DEFAULT_SIGNATURE_FORMAT;
	private String signatureFormatForClient=DEFAULT_SIGNATURE_FORMAT;	
	private String updateSignatureFormat=DEFAULT_UPDATE_SIGNATURE_FORMAT;
	private String referenceId = null;
	private boolean custodyDoc = false;

	private static final String errorMessage =
		"La sintaxis de la aplicación de prueba de Firma 2 Fases es la siguiente:\n" +
		"> TwoPhaseUserSignature idAplicacion [-r referenciaExterna] [-fe formatoFirmaElectronica] [-a algoritmoHash] [-ef extenderFormatoFirma]\n\n" +
		"  donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   -r referenciaExterna     	  --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -fe formatoFirmaElectronica  --> Formato de la Firmar Electronica a generar\n" +
		"                                    Opcional. CMS por defecto\n" +
		"                                    Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n" +
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto\n" +
		"                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -ef extenderFormatoFirma     --> Formato genérico de la firma al cual extender\n" +
		"                                    la firma electrónica. Opcional\n" +
		"                                    Valores posibles: ES-T\n";
		
	public static void main(String[] args) {
		
		if (!StartingClass.usarFichParams && (args == null || args.length < 1 || args.length > 9)) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new TwoPhaseUserSignature().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA 2 FASES]");		

		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		
		
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 1											 //	
		//Realización de la firma electrónica del Hash.
		System.out.println(".[Realizando la Firma Electrónica ...]");
		//BeanSalidaBrowser appletOut =  BrowserAfirma.navegar(fileContentToBeSigned,0,signatureFormat,hashAlgorithm+"WithRsaEncryption");
		BeanSalidaBrowser appletOut =  BrowserAfirma.navegar(signatureFormatForClient,hashAlgorithm+"WithRsaEncryption");
		System.out.println(".[/Firma Electrónica realizada correctamente]");
		
		//Obtención de la Firma Electrónica del hash del Bloque de Firma y del Certificado del firmante
		String signatureValue= appletOut.getFirma();
		String signCertificateValue= appletOut.getCertificado();
		String fileToBeSigned= appletOut.getPathDocumento();		
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 2											 //
		// Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo información del fichero " + fileToBeSigned + "...]");
		byte[] bfileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned);
		String fileContentToBeSigned= new String(bfileContentToBeSigned);
		String fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned);
		String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned);
		System.out.println(".[/Información correctamente obtenida]");		
		
		//Preparación de la petición al servicio Web de FirmaUsuario2FasesF2
		System.out.println(".[Preparando la petición al servicio Web " + twoPhasesUserSignatureF2WebServiceNameEng + "...]");
		Document twoPhasesUserSignatureF2Request = UtilsWebService.prepareTwoPhasesUserSignatureF2RequestEng(appId, signatureValue, signCertificateValue,signatureFormat,fileContentToBeSigned, fileType,fileName,hashAlgorithm, updateSignatureFormat, referenceId, custodyDoc);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(twoPhasesUserSignatureF2Request));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(twoPhasesUserSignatureF2WebServiceNameEng,twoPhasesUserSignatureF2Request);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Usuario 2 Fases F2 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		
		//Obtención del identificador de transaccion 
		System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transaccion correctamente extraído de la respuesta]");	
		
		//Decodificamos la Firma Electrónica generada
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma 2 Fases");
			System.exit(-1);
		} 
		
		//Generamos el nombre del fichero que contendrá la firma de usuario generada
		String userSignatureName = TEMPORAL_DIR + "/userESignature2Phase_tempfile_" + appId + "_" + transactionId;
		userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName,signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electrónica en el fichero " + userSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
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

		System.out.println(".[/Firma Usuario 2 Fases correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE FIRMA 2 FASES FINALIZADO]");
		
		System.exit(0);
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
						
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			if (param != null) {
				referenceId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_FORMATOFIRMA);
			if (param != null) {
				signatureFormat = param;
				if (signatureFormat.equalsIgnoreCase("XMLDSIG") || signatureFormat.equals("XMLDSIGN")) {
					signatureFormat = "XMLDSIG";
					signatureFormatForClient = "XMLDSIGN"; 
				} else {
					signatureFormatForClient = signatureFormat;
				}
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}

//			param = obtenerPropiedadArg(StartingClass.PROP_EXTENDERFORMATOFIRMA);
//			if (param != null) {
//				updateSignatureFormat = param;
//			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_CUSTODIARDOC);
			if (param != null) {
				custodyDoc = Boolean.valueOf(param).booleanValue();
			}
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			
			appId = args[0];
						
			int i = 1;
			boolean existExternReference = false;
			boolean existFormatSignature = false;
			boolean existHashAlgorithm = false;
			boolean existExtendSignature = false;
			
			while (i < args.length) {
				
				if (args[i].equals("-r")) {
					
					if (existExternReference)
						throw new Exception();
					i++;
					referenceId = args[i];
					i++;
					existExternReference = true;
					
				} else if (args[i].equals("-fe")) {
					
					if (existFormatSignature)
						throw new Exception();
					
					i++;
					if (args[i].equalsIgnoreCase("XMLDSIG") || args[i].equalsIgnoreCase("XMLDSIGN")) 
					{
						signatureFormat = "XMLDSIG";
						signatureFormatForClient = "XMLDSIGN";
					} 
					else 
					{
						signatureFormat = args[i];
						signatureFormatForClient = args[i];
					}
					i++;
					existFormatSignature = true;
					
				} else if (args[i].equals("-a")) {
					
					if (existHashAlgorithm)
						throw new Exception();
					
					i++;
					hashAlgorithm = args[i];
					i++;
					existHashAlgorithm = true;
					
				} else if (args[i].equals("-ef")) {
					
					if (existExtendSignature)
						throw new Exception();
					
					i++;
					updateSignatureFormat = args[i];
					i++;
					existExtendSignature = true;
					
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
