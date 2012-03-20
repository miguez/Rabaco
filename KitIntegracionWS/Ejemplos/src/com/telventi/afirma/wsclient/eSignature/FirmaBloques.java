package com.telventi.afirma.wsclient.eSignature;

import java.util.HashMap;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.BeanSalidaBrowser;
import com.telventi.afirma.wsclient.utils.BrowserAfirma;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;


public class FirmaBloques extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String aliasServerCert = null;
	private String[] fileToBeSigned = null;
	private long[] idTransactions = null;
	private HashMap selectiveBlocks = null;
	private String referenceId = null;
	private String hashAlgorithm=DEFAULT_HASH_ALGORITHM;
	private String signatureFormat=DEFAULT_SIGNATURE_FORMAT;
	private String signatureFormatForClient=DEFAULT_SIGNATURE_FORMAT;
	private String updateSignatureFormat=DEFAULT_UPDATE_SIGNATURE_FORMAT;
	
	
//	private boolean modoHash = true;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Firma Bloques es la siguiente:\n" +
		"> FirmaBloques idAplicacion aliasCertificadoServidor [-f ficherosAFirmar] [-t transaccionesFirma] [-m transaccionBloque_idTransaccionesDocumentos] [-r referenciaExterna] [-a algoritmoHash] [-fe formatoFirmaElectronica] [-ef etenderFormatoFirma]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   aliasCertificadoServidor     --> Alias del certificado servidor a emplear en las Firmas Electrónicas Servidor del Bloque de Firmas\n" +
		"   -f ficherosAFirmar           --> Ruta completa de ficheros a incluir en el Bloque de firmas.\n" +
		"								     Opcional. Obligatorio si transaccionesFirma no se especifica.\n" +
		"                                    Se deben indicar seguidos separados por el caracter *\n" +
		"                                    P.ej: FirmaBloques idAplicacion -f C:\fichero1*C:\fichero2*C:\fichero3\n" +		
		"   -t transaccionesFirma        --> Identificadores de Transacciones de Firma por Bloques\n" +
		"                                    Opcional. Obligatorio si ficherosAFirmar no se especifica.\n" +
		"                                    Se deben indicar seguidos separados por el caracter *\n" +
		"                                    P.ej: FirmaBloques idAplicacion -t 123454526*65343521*231342343\n" +	
		"   -m transaccionBloque         --> Identificador de Transacción de Firma por Bloques que contiene los documentos a Multifirmar\n" +
		"      idTransaccionesDocumentos --> Identificadores de las Transacciones de Firma Servidor realizadas sobre los documentos a Multifirmar\n" +
		"                                    Se deben indicar seguidos separados por el caracter *\n" +
		"                                    P.ej: FirmaBloques idAplicacion -m 123456_432432*5434543*423565*757675\n" +
		"   -r referenciaExterna     	 --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto \n" +
        "                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -fe formatoFirmaElectronica  --> Formato de la Firmar Electronica a generar\n" +
		"                                    Opcional. CMS por defecto\n" +
		"                                    Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n" +
//		"   -ef extenderFormatoFirma     --> Formato genérico de la firma al cual extender" +
//		"                                    la firma electrónica. Opcional" +
//		"                                    Valores posibles: ES-T" +
		"   NOTA: Es posible indicar todas las duplas -m ... -d ... deseadas, pero siempre en el orden -m .. -d ...\n";
	
	public static void main (String[] args)
	{			 
		if (!StartingClass.usarFichParams && (args == null || args.length < 4))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		 
		new FirmaBloques().run(args);
	}
	
	public void run(String[] args)
	{
		byte[] fileContentToBeSigned;
		String fileName;
		String fileType;
		Document custodyDocumentRequest; 
		String response;
		
		System.out.println("[COMIENZO DE PROCESO DE FIRMA BLOQUES]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		

		// Lectura de la información de los ficheros a firmar		
		String[] docIds=null;
		
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 1											 //
		if (fileToBeSigned!=null)
		{
			docIds=new String[fileToBeSigned.length];
		
			for (int i=0; i<fileToBeSigned.length; i++)
			{
				System.out.println(".[Obteniendo información del fichero " + fileToBeSigned[i] + "...]");
				fileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned[i]);		
				fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned[i]);
				fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned[i]);
				System.out.println(".[/Información correctamente obtenida]");		
			
				//Preparación de la petición al servicio Web de AlmacenarDocumento
				System.out.println(".[Preparando la petición al servicio Web " + custodyDocumentWebServiceName + "...]");
				custodyDocumentRequest = UtilsWebService.prepareCustodyDocumentRequest(appId,fileName,fileType, new String(fileContentToBeSigned));
				System.out.println(".[/Petición correctamente preparada]");
				
				//Lanzamiento de la petición WS
				System.out.println(".[Lanzando la petición...]");
				System.out.println("..[peticion]");
				System.out.println(XMLUtils.DocumentToString(custodyDocumentRequest));
				System.out.println("..[/peticion]");
				response = UtilsWebService.launchRequest(custodyDocumentWebServiceName,custodyDocumentRequest);
				System.out.println("..[respuesta]");
				System.out.println(response);
				System.out.println("..[/respuesta]");	
				
				if (!UtilsWebService.isCorrect(response))
				{
					System.err.println();
					System.err.println("La petición de Almacenamiento del documento " + fileToBeSigned[i] + " no ha sido satisfactoria. Saliendo ...");
					System.exit(-1);
				}
				System.out.println(".[/Petición correctamente realizada]");
				
				//Obtención del identificador del documento custodiado
				System.out.println(".[Extrayendo el identificador del documento de la respuesta...]");
				docIds[i] = UtilsWebService.getInfoFromDocumentNode(response, "idDocumento");
				System.out.println(".[/Identificador del documento correctamente extraído de la respuesta]");
			}
		}
		
		//Preparación de la petición al servicio Web de FirmaUsuarioBloquesF1
		System.out.println(".[Preparando la petición al servicio Web " + blockUserSignatureF1WebServiceName + "...]");
		Document blockUserSignatureF1Request = UtilsWebService.prepareBlockUserSignatureF1Request(appId, docIds, idTransactions, aliasServerCert, selectiveBlocks, hashAlgorithm);
		System.out.println(".[/Petición correctamente preparada]");		
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(blockUserSignatureF1Request));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(blockUserSignatureF1WebServiceName,blockUserSignatureF1Request);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Usuario por Bloques F1 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención del identificador de transaccion 
		System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "idTransaccion");
		System.out.println(".[/Identificador de transaccion correctamente extraído de la respuesta]");
		
		//Obtención del hash del bloque de firma 
		System.out.println(".[Extrayendo el hash del bloque de firma  de la respuesta...]");
		String hashBlockSignature = UtilsWebService.getInfoFromDocumentNode(response, "hash");
		System.out.println(".[/Identificador del hash del bloque de firma correctamente extraído de la respuesta]");
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 2											 //	
		//Realización de la firma electrónica del Hash.
		System.out.println(".[Realizando la Firma Electrónica del Hash del Bloque de Firma...]");
		
				
//		BeanSalidaBrowser appletOut =  null;
//		if (modoHash)
//			appletOut = BrowserAfirma.navegar(hashBlockSignature,1,signatureFormatForClient,hashAlgorithm+"WITHRSAENCRYPTION");
//		else
//			appletOut = BrowserAfirma.navegar(hashBlockSignature, 0, signatureFormatForClient, hashAlgorithm+"WITHRSAENCRYPTION");
				
		BeanSalidaBrowser appletOut =  BrowserAfirma.navegar(hashBlockSignature,1,signatureFormatForClient,hashAlgorithm+"WITHRSAENCRYPTION");
		
		System.out.println(".[/Firma Electrónica realizada correctamente]");
		
		//Obtención de la Firma Electrónica del hash del Bloque de Firma y del Certificado del firmante
		String signatureValue= appletOut.getFirma();
		String signCertificateValue= appletOut.getCertificado();
		///////////////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////////////////
		// 								FASE 3											 //	
		//Preparación de la petición al servicio Web de FirmaUsuarioBloquesF3
		System.out.println(".[Preparando la petición al servicio Web " + blockUserSignatureF3WebServiceName + "...]");
		Document blockUserSignatureF3Request = UtilsWebService.prepareBlockUserSignatureF3Request(appId, transactionId, signatureValue, signCertificateValue,signatureFormat, updateSignatureFormat, referenceId);
		System.out.println(".[/Petición correctamente preparada]");
		
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(blockUserSignatureF3Request));
		System.out.println("..[/peticion]");
		response = UtilsWebService.launchRequest(blockUserSignatureF3WebServiceName,blockUserSignatureF3Request);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Usuario Bloques F3 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");

		
		//Decodificamos la Firma Electrónica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma por Bloques");
			System.exit(-1);
		} 
		
		//Generamos el nombre del fichero que contendrá la firma de usuario generada
		String userSignatureName = TEMPORAL_DIR + "/userESignatureBlock_tempfile_" + appId + "_" + transactionId;
		userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName,signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electrónica en el fichero " + userSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
		System.out.println(".[/Firma Usuario por Bloques correctamente almacenada]");	
		///////////////////////////////////////////////////////////////////////////////////

		///////////////////////////////////////////////////////////////////////////////////
		// 							VALIDACIÓN JUSTIFICANTE DE FIRMA 					 //
		// Preparación de la petición al servicio Web de ValidarFirma
		System.out.println(".[Validando JUSTIFICANTE de FIRMA ELECTRÓNICA de la Plataforma...]");
		System.out.println(".[Extrayendo el Justificante de Firma Electrónica de la respuesta...]");
		String evidence = UtilsWebService.getInfoFromDocumentNode(response, "justificanteFirmaElectronica");
		System.out.println(".[/Justificante de Firma Electrónica correctamente extraído de la respuesta]");
		System.out.println("..[Preparando la petición al servicio Web " + signatureValidationWebServiceName + "...]");
		Document eSignatureValidationRequest = null;
		try
		{
			eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequest(appId, new String(evidence), DEFAULT_SIGNATURE_FORMAT, null, null, null);
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
		response = UtilsWebService.launchRequest(signatureValidationWebServiceName, eSignatureValidationRequest);
		System.out.println("...[respuesta]");
		System.out.println(response);
		System.out.println("...[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de verificación del Justificante de Firma Electrónica de la Plataforma no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println("..[/Petición correctamente realizada]");
		System.out.println(".[/JUSTIFICANTE de FIRMA ELECTRÓNICA de la Plataforma correctamente validado]");
		///////////////////////////////////////////////////////////////////////////////////

		System.out.println("[/PROCESO DE FIRMA BLOQUES FINALIZADO]");
		
		System.exit(0);
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			aliasServerCert = obtenerPropiedadArg(StartingClass.PROP_ALIASCERTSERV);
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_FICHEROAFIRMAR);
			if (param != null) {
				fileToBeSigned = param.split("\\*");
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			if (param != null) {
				String[] idTrans = param.split("\\*");
				idTransactions = new long[idTrans.length];
				for (int j=0; j<idTrans.length; j++) {
					idTransactions[j]= new Long(idTrans[j]).longValue();
				}
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCIONMULTIBLOQUE);
			if (param != null) {
				
				Long idTransOfMultiBlock = Long.valueOf(param);				
				param = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCIONMULTIDOCS);
				
				String[] aux = param.split("\\*");
				long idTransOfMultiDocs[] = new long[aux.length];
				
				for (int j=0; j<aux.length; j++) {
					idTransOfMultiDocs[j]= new Long(aux[j]).longValue();
				}
				
				if (this.selectiveBlocks == null) {
					selectiveBlocks = new HashMap();
				}
				
				selectiveBlocks.put(idTransOfMultiBlock, idTransOfMultiDocs);
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			if (param != null) {
				referenceId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
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
		
		int i=2;
		String opcion=null;
		
		try
		{
			
			appId = args[0];	
			aliasServerCert = args[1];

			boolean opcion_f = false;
			boolean opcion_t = false;
			boolean opcion_r = false;
			boolean opcion_a = false;
			boolean opcion_fe = false;
//			boolean opcion_ef = false;
			 
			while (i<args.length)
			{
				opcion= args[i];

				if (opcion.startsWith("-"))
				{
			
					if (opcion.equals("-f") )
					{
						i++;
						if (args[i].startsWith("-") || opcion_f)
						{
							throw new Exception();
						}						
											
						fileToBeSigned=args[i].split("\\*");
						opcion_f = true;
					}
					else if (opcion.equals("-t"))
					{
						i++;
						if (args[i].startsWith("-") || opcion_t)
						{
							throw new Exception();
						}						
						
						String[] idTrans= args[i].split("\\*");
						idTransactions = new long[idTrans.length];
						try
						{
							for (int j=0; j<idTrans.length; j++)
								idTransactions[j]= new Long(idTrans[j]).longValue();							
						}
						catch(NumberFormatException e)
						{
							System.err.println("ERROR en los Parametros de entrada:");
							System.err.println("	--> Las transacciones especificadas tienen que ser numéricas.");
							System.exit(-1);
						}
						opcion_t = true;
					}
					else if (opcion.equals("-m"))
					{

						i++;
						if (args[i].startsWith("-") )
						{
							throw new Exception();
						}						
						
						// Tomamos tanto el identificador del bloque de firmas con los documentos a multifirmar como
						// los identificadores de las transacciones servidor de dichos documentos
						String infoOfTrans[] = args[i].split("\\_");
						
						// Tomamos el identificador del bloque de firmas con los documentos a multifirmar
						Long idTransOfMultiBlock = null;
						try
						{
							idTransOfMultiBlock = new Long(infoOfTrans[0]);
						}
						catch(NumberFormatException e)
						{
							System.err.println("ERROR en los Parametros de entrada:");
							System.err.println("	--> Las transacciones especificadas tienen que ser numéricas.");
							System.exit(-1);
						}	
						
						String[] aux = infoOfTrans[1].split("\\*");
						long idTransOfMultiDocs[] = new long[aux.length];
						try
						{
							for (int j=0; j<aux.length; j++) {
								idTransOfMultiDocs[j]= new Long(aux[j]).longValue();
							}
						}
						catch(NumberFormatException e)
						{
							System.err.println("ERROR en los Parametros de entrada:");
							System.err.println("	--> Las transacciones especificadas tienen que ser numéricas.");
							System.exit(-1);
						}	
						
						if (this.selectiveBlocks == null)
							selectiveBlocks = new HashMap();
						
						selectiveBlocks.put(idTransOfMultiBlock, idTransOfMultiDocs);
					}
					else if (opcion.equals("-r"))
					{
						i++;
						if (args[i].startsWith("-") || opcion_r)
						{
							throw new Exception();
						}
						referenceId = args[i];
						opcion_r = true;
					}
					else if (opcion.equals("-a"))
					{
						i++;
						if (args[i].startsWith("-") || opcion_a)
						{
							throw new Exception();
						}
						hashAlgorithm = args[i];
						opcion_a = true;
					}
					else if (opcion.equals("-fe"))
					{
						i++;
						if (args[i].startsWith("-") || opcion_fe)
						{
							throw new Exception();
						}
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
						
						//signatureFormat = args[i];
						opcion_fe = true;
					}
//					else if (opcion.equals("-ef"))
//					{
//						i++;
//						if (args[i].startsWith("-") || opcion_ef)
//						{
//							throw new Exception();
//						}
//						updateSignatureFormat = args[i];
//						opcion_ef = true;
//					}
//					else if (opcion.equals("-modo"))
//					{
//						i++;
//						
//						if (args[i].equals("hash")) 
//						{
//							modoHash = true;
//						}
//						else if (args[i].equals("data"))
//						{
//							modoHash = false;
//						}
//						else 
//						{
//							throw new Exception();
//						}
//					}
					else 
					{
						throw new Exception();
					}
				}
				i++;
			 }
			 
			if (idTransactions==null && fileToBeSigned==null && selectiveBlocks == null)
			{
				System.err.println(errorMessage);
				System.exit(-1);
			}
			
//			//A modo de Debug
//			System.out.println("Parametros de entrada:");
//			System.out.println("	appId	 ---> " + appId);
//			System.out.println("	aliasServerCert	 ---> " + aliasServerCert);
//				
//			if (fileToBeSigned!=null)
//			{
//				System.out.println("	filesToBeSigned	 ---> " );
//				for (i=0; i<fileToBeSigned.length; i++)
//					System.out.println("	           " + fileToBeSigned[i]);
//			}
//			if (idTransactions!=null)
//			{
//				System.out.println("	idTransactions	 ---> " );
//				for (int z=0; z<idTransactions.length; z++)
//					System.out.println("	           " + idTransactions[z]);
//			}
//			System.out.println("	hashAlgorithm	 ---> " + hashAlgorithm);
//			System.out.println("	signatureFormat	 ---> " + signatureFormat);
 
		}
		catch(Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
