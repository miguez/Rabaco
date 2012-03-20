/**
 * 
 */
package com.telventi.afirma.wsclient.dss.custody;

import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveSubmitResponse;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.BeanSalidaBrowser;
import com.telventi.afirma.wsclient.utils.BrowserAfirma;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJRL
 *
 */
public class DSSArchiveSubmit extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private String signatureFormat = DEFAULT_SIGNATURE_FORMAT;
	private String signatureFormatForClient = DEFAULT_SIGNATURE_FORMAT;
	private String updateSignatureFormat = DEFAULT_UPDATE_SIGNATURE_FORMAT;
	private String referenceId = null;
	private boolean custodyDoc = false;
	
	private static final String errorMessage =
		"La sintaxis de la aplicaci�n de prueba de Firma 2 Fases mediante interfaz OASIS-DSS es la siguiente:\n" +
		"> Firma2Fases idAplicacion [-r referenciaExterna] [-fe formatoFirmaElectronica] [-a algoritmoHash]"/* [-ef extenderFormatoFirma]*/+"\n\n" +
		"  donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   -r referenciaExterna     	  --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -fe formatoFirmaElectronica  --> Formato de la Firmar Electronica a generar\n" +
		"                                    Opcional. CMS por defecto\n" +
		"                                    Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n" +
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto\n" +
		"                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		/*"   -ef extenderFormatoFirma     --> Formato gen�rico de la firma al cual extender\n" +
		"                                    la firma electr�nica. Opcional\n" +
		"                                    Valores posibles: ES-T\n";*/
		"   -cd custodiarDocumento       --> Indica si se desea custodiar o no el contenido del documento enviado.\n" +
		"									 Valores posibles: true o false";

	public static void main(String[] args) {
		if (!StartingClass.usarFichParams && (args == null || args.length < 1 || args.length > 9)) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSArchiveSubmit().run(args);
	}

	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE FIRMA 2 FASES MEDIANTE INTERFAZ OASIS-DSS]");

		// Obtenci�n de los par�metros de entrada
		fillParameters(args);
		
		String xmlIn = null;
		String response = null;
		///////////////////////////////////////////////////////////////////////////////////
		// FASE 1 //
		// Realizaci�n de la firma electr�nica del Hash.
		System.out.println(".[Realizando la Firma Electr�nica ...]");
		// BeanSalidaBrowser appletOut =
		// BrowserAfirma.navegar(fileContentToBeSigned,0,signatureFormat,hashAlgorithm+"WithRsaEncryption");
		BeanSalidaBrowser appletOut = BrowserAfirma.navegar(signatureFormatForClient, hashAlgorithm + "WithRsaEncryption");
		System.out.println(".[/Firma Electr�nica realizada correctamente]");

		// Obtenci�n de la Firma Electr�nica del hash del Bloque de Firma y del
		// Certificado del firmante
		String signatureValue = appletOut.getFirma();
		String signCertificateValue = appletOut.getCertificado();
		String fileToBeSigned = appletOut.getPathDocumento();
		// /////////////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////////////
		// FASE 2 //
		// Lectura de la informaci�n del fichero a firmar
		System.out.println(".[Obteniendo informaci�n del fichero " + fileToBeSigned + "...]");
		byte[] bfileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned);
		String fileContentToBeSigned = new String(bfileContentToBeSigned);
		String fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned);
		String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned);
		System.out.println(".[/Informaci�n correctamente obtenida]");
		
		//		 Preparaci�n de la petici�n al servicio Web de FirmaUsuario2FasesF2
		System.out.println(".[Preparando la petici�n al servicio Web " + twoPhasesUserSignatureF2WebServiceName + "...]");
		try {
			xmlIn = DSSUtils.getArchiveSubmitRequest(appId, signatureValue, signCertificateValue, fileContentToBeSigned, fileType, fileName, hashAlgorithm, updateSignatureFormat, referenceId,custodyDoc);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Petici�n incorrectamente preparada]");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente preparada]");
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(xmlIn);
		System.out.println("..[/peticion]");
		try {
			response = UtilsWebService.launchRequest(service_DSSArchiveSubmit,operation_DSSArchiveSubmit, DSSUtils.getDocument(xmlIn));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Se ha producido un error en la petici�n]");
			System.exit(-1);
		}
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		//Procesando la respuesta
		try {
			ArchiveSubmitResponse archiveResponse = (ArchiveSubmitResponse) DSSUtils.getResponse(new ArchiveSubmitResponse(),response);
			DSSUtils.printResult(archiveResponse.getResult());
			if(archiveResponse.getResult()== null || archiveResponse.getResult().getResultMajor()==null
					|| !archiveResponse.getResult().getResultMajor().equals(DSSConstants.ResultMajorDes.success)){
				System.err.println();
				System.err.println("La petici�n de Firma en 2 Fases no ha sido satisfactoria. Saliendo ...");
				if(archiveResponse.getResult()!= null || archiveResponse.getResult().getMessage()!=null)
					System.out.println(archiveResponse.getResult().getMessage());
				System.exit(-1);
			}
			System.out.println(".[/Petici�n correctamente realizada]");
			
			//Obtenci�n del detalle de la respuesta
			System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
			if(archiveResponse.getResult()!= null || archiveResponse.getResult().getMessage()!=null)
				System.out.println(archiveResponse.getResult().getMessage());
			//Obtenci�n del identificador de transaccion
			System.out.println(".[Extrayendo el identificador de transaccion de la respuesta...]");
			String transactionId = archiveResponse.getArchiveIdentifier();
			System.out.println(".[/Identificador de transaccion correctamente extra�do de la respuesta]");
			
			//Decodificamos la Firma Electr�nica generada
			byte[] signatureValueBase64Decoded = null;
			try {
				signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());
			} catch (Exception e) {
				System.err.println("Error decodificando la Firma 2 Fases");
				System.exit(-1);
			}
			
			// Generamos el nombre del fichero que contendr� la firma de usuario
			// generada
			String userSignatureName = TEMPORAL_DIR + "/userESignature2Phase_tempfile_" + appId + "_" + transactionId;
			userSignatureName = UtilsSignature.getServerSignatureFileName(userSignatureName, signatureFormat);

			System.out.println(".[Almacenando la Firma Electr�nica en el fichero " + userSignatureName + "...]");
			UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, userSignatureName);
			System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
			//TODO -Validaci�n del justificante
			
			System.out.println("[/PROCESO DE FIRMA 2 FASES MEDIANTE INTERFAZ OASIS_DSS FINALIZADO]");

		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println(".[/Error al procesar la respuesta]");
			System.exit(-1);
		}

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
			boolean existCustodyDoc= false;
			
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
					
				} /*else if (args[i].equals("-ef")) {
					
					if (existExtendSignature)
						throw new Exception();
					
					i++;
					updateSignatureFormat = args[i];
					i++;
					existExtendSignature = true;
					
				} */
				else if (args[i].equals("-cd")) {
					
					if (existCustodyDoc)
						throw new Exception();
					
					i++;
					custodyDoc = (args[i].trim().equalsIgnoreCase("true"));
					i++;
					existCustodyDoc = true;
					
				} 
				else {
					
					throw new Exception();
					
				}
				
			}
			
		} catch (Exception e) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}

}
