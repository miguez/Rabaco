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

public class ThreePhaseUserSignature extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String fileToBeSigned = null;
	private String referenceId = null;
	private String hashAlgorithm=DEFAULT_HASH_ALGORITHM;
	private String signatureFormat=DEFAULT_SIGNATURE_FORMAT;
	private String signatureFormatForClient=DEFAULT_SIGNATURE_FORMAT;
	private String updateSignatureFormat=DEFAULT_UPDATE_SIGNATURE_FORMAT;
	
	private static final String errorMessage =
		"La sintaxis de la aplicación de prueba de Firma 3 Fases es la siguiente:\n" +
		"> ThreePhaseUserSignature idAplicacion ficheroAFirmar [-fe formatoFirmaElectronica] [-a algoritmoHash] [-r referenciaExterna] [-ef extenderFormatoFirma]\n\n" +
		"  donde\n" +
		"   idAplicacion                  --> Identificador de la aplicacion\n" +
		"   ficheroAFirmar                --> Ruta completa al fichero a firmar\n" +
		"   -fe formatoFirmaElectronica   --> Formato de la Firmar Electronica a generar\n" +
		"                                     Opcional. CMS por defecto\n" +
		"                                     Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n" +
		"   -a algoritmoHash              --> Algoritmo de hash a emplear en la firma\n" +
		"                                     Opcional. SHA1 por defecto\n" +
		"                                     Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -r referenciaExterna     	  --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n";
//		"   -ef extenderFormatoFirma      --> Formato genérico de la firma al cual extender\n" +
//		"                                     la firma electrónica. Opcional\n" +
//		"                                     Valores posibles: ES-T\n";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 2 || args.length > 10))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ThreePhaseUserSignature().run(args);
	}
	
	public void run(String[] args)
	{
		byte[] fileContentToBeSigned;
		String fileName;
		String fileType;
		Document custodyDocumentRequest; 
		String response;
		String docId=null;
		
		System.out.println("[COMIENZO DE PROCESO DE FIRMA 3 FASES]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 1											 //
		// Lectura de la información de los ficheros a firmar
		System.out.println(".[Obteniendo información del fichero " + fileToBeSigned + "...]");
		fileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned);		
		fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned);
		fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned);
		System.out.println(".[/Información correctamente obtenida]");
	
		//Preparación de la petición al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petición al servicio Web " + custodyDocumentWebServiceNameEng + "...]");
		custodyDocumentRequest = UtilsWebService.prepareCustodyDocumentRequestEng(appId,fileName,fileType, new String(fileContentToBeSigned));
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(custodyDocumentRequest));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(custodyDocumentWebServiceNameEng,custodyDocumentRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");	
		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Almacenamiento del documento " + fileToBeSigned + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención del identificador del documento custodiado
		System.out.println(".[Extrayendo el identificador del documento de la respuesta...]");
		docId = UtilsWebService.getInfoFromDocumentNode(response, "documentId");
		System.out.println(".[/Identificador del documento correctamente extraído de la respuesta]");		
		
		//Preparación de la petición al servicio Web de FirmaUsuario3FasesF1
		System.out.println(".[Preparando la petición al servicio Web " + threePhasesUserSignatureF1WebServiceNameEng + "...]");
		Document threePhasesUserSignatureF1Request = UtilsWebService.prepareThreePhasesUserSignatureF1RequestEng(appId, docId,  hashAlgorithm);
		System.out.println(".[/Petición correctamente preparada]");		
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(threePhasesUserSignatureF1Request));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(threePhasesUserSignatureF1WebServiceNameEng,threePhasesUserSignatureF1Request);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Usuario 3 Fases F1 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención del identificador de transaccion 
		System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transaccion correctamente extraído de la respuesta]");
		
		//Obtención del hash 
		System.out.println(".[Extrayendo el hash de los datos a firmar de la respuesta...]");
		String hashSignature = UtilsWebService.getInfoFromDocumentNode(response, "hash");
		System.out.println(".[/Hash de los datos a firmar correctamente extraído de la respuesta]");		
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 2											 //	
		//Realización de la firma electrónica del Hash.
		System.out.println(".[Realizando la Firma Electrónica del Hash ...]");
		BeanSalidaBrowser appletOut =  BrowserAfirma.navegar(hashSignature,1,signatureFormatForClient,hashAlgorithm+"WITHRSAENCRYPTION");
		System.out.println(".[/Firma Electrónica realizada correctamente]");

		//Obtención de la Firma Electrónica del hash del Bloque de Firma y del Certificado del firmante
		String signatureValue= appletOut.getFirma();
		String signCertificateValue= appletOut.getCertificado();		
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
		
		// Decodificamos la Firma Electrónica generada
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma 3 Fases");
			System.exit(-1);
		} 
		
		//Generamos el nombre del fichero que contendrá la firma de usuario generada
		String userSignatureName = TEMPORAL_DIR + "/userESignature3Phase_tempfile_" + appId + "_" + transactionId;
		userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName,signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electrónica en el fichero " + userSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
		System.out.println(".[/Firma Usuario 3 Fases correctamente almacenada]");	
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

		System.out.println("[/PROCESO DE FIRMA 3 FASES FINALIZADO]");	
		
		System.exit(0);
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			fileToBeSigned = obtenerPropiedadArg(StartingClass.PROP_FICHEROAFIRMAR);			
			
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
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			
			appId = args[0];
			fileToBeSigned = args[1];
			
			int i = 2;
			boolean existExternReference = false;
			boolean existFormatSignature = false;
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
