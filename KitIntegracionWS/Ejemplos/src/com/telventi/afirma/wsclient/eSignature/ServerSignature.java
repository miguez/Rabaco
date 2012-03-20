/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 22-jun-2006</p>
* @author MAMFN
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */
public class ServerSignature extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String fileToBeSigned = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String signatureFormat = DEFAULT_SIGNATURE_FORMAT;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private int includeDocument = DEFAULT_INCLUDE_DOCUMENT;
	
	private static final String errorMessage =  
		"La sintaxis de la aplicación de prueba de Firma Servidor es la siguiente:\n" +
		"> ServerSignature idAplicacion ficheroAFirmar aliasCertificadoServidor [-r referenciaExterna] [-f formatoFirmaElectronica] [-a algoritmoHash] [-i incluirDocumento]\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   ficheroAFirmar           --> Ruta completa al fichero a firmar\n" +
		"   aliasCertificadoServidor --> Alias del certificado servidor a emplear en la Firma Electrónica Servidor\n" +
		"   -r referenciaExterna     --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -f formatoFirma          --> Formato de la Firma Electronica a generar\n" +
 		"                                Opcional. CMS por defecto\n" +
		"                                Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T, PDF, ODF\n" +
		"   -a algoritmoHash         --> Algoritmo de hash a emplear en la firma\n" +
		"                                Opcional. SHA1 por defecto\n" +
		"                                Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -i incluirDocumento      --> Incluir Documento. Determina si se desea incluir el documento en\n"+
		"                                la petición de firma de servidor. Opcional. Valores posibles:\n"+
		"                                0: Se incluye el documento (Valor por defecto)\n"+
		"                                1: No se incluye el documento.\n"+
		"\n " +
		"NOTA: La Firma Electrónica se almacena en binario (NO en Base64)\n";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 3 || args.length > 11))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ServerSignature().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		if(includeDocument ==0)
			processIncludeDocument();
		else
			processNoIncludeDocument();
		
	}
	
	private void processIncludeDocument(){
		//Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo información del fichero " + fileToBeSigned + "...]");
		byte[] fileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned);		
		String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned);
		String fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned);
		System.out.println(".[/Información correctamente obtenida]");
		
		//Preparación de la petición al servicio Web de FirmaServidor
		System.out.println(".[Preparando la petición al servicio Web " + serverSignatureWebServiceNameEng + "...]");
		Document serverSignatureRequest = UtilsWebService.prepareServerSignatureIncDocRequestEng(appId, new String(fileContentToBeSigned), aliasServerCert, referenceId, hashAlgorithm, signatureFormat,fileName,fileType);
		System.out.println(".[/Petición correctamente preparada]");
		
		launchServerSignatureRequest(serverSignatureRequest, null,fileType);
	}
	
	private void processNoIncludeDocument(){
		//Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo información del fichero " + fileToBeSigned + "...]");
		byte[] fileContentToBeSigned = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToBeSigned);		
		String fileName = UtilsFileSystem.getNameFromFilePath(fileToBeSigned);
		String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToBeSigned);
		System.out.println(".[/Información correctamente obtenida]");
		
		// Preparación de la petición al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petición al servicio Web " + custodyDocumentWebServiceNameEng + "...]");
		Document custodyDocumentRequest = UtilsWebService.prepareCustodyDocumentRequestEng(appId, fileName, fileType, new String(fileContentToBeSigned));
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(custodyDocumentRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(custodyDocumentWebServiceNameEng, custodyDocumentRequest);
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
					
		// Obtención del identificador del documento custodiado
		System.out.println(".[Extrayendo el identificador del documento de la respuesta...]");
		String docId = UtilsWebService.getInfoFromDocumentNode(response, "documentId");
		System.out.println(".[/Identificador del documento correctamente extraído de la respuesta]");

		// Preparación de la petición al servicio Web de FirmaServidor
		System.out.println(".[Preparando la petición al servicio Web " + serverSignatureWebServiceNameEng + "...]");
		Document serverSignatureRequest = UtilsWebService.prepareServerSignatureRequestEng(appId, docId, aliasServerCert, referenceId, hashAlgorithm, signatureFormat);
		System.out.println(".[/Petición correctamente preparada]");
		
		launchServerSignatureRequest(serverSignatureRequest,docId,fileType);
		
	}
	
	private void launchServerSignatureRequest(Document serverSignatureRequest, String docId,String fileType){
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(serverSignatureRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(serverSignatureWebServiceNameEng, serverSignatureRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			if(docId != null)
				System.err.println("La petición de Firma Servidor del documento con el identificador " + docId + " no ha sido satisfactoria. Saliendo ...");
			else
				System.err.println("La petición de Firma Servidor no ha sido satisfactoria. Saliendo ...");
			
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		// Obtención de la Firma Electrónica
		System.out.println(".[Extrayendo la Firma Electrónica servidor de la respuesta...]");	
		String signatureValue = UtilsWebService.getInfoFromDocumentNode(response, "eSignature");
		System.out.println(".[/Firma Electrónica servidor correctamente extraído de la respuesta]");		 

		// Obtención del identificador de la transacción
		System.out.println(".[Extrayendo el identificador de transacción generada de la respuesta...]");	
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transacción generada correctamente extraído de la respuesta]");	
		
		// Decodificamos la Firma Electrónica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma Servidor");
			System.exit(-1);
		} 
			
		// Generamos el nombre del fichero que contendrá la firma servidor obtenida
		String serverSignatureName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
		if(signatureFormat.equals("PDF")||signatureFormat.equals("ODF"))
			serverSignatureName = serverSignatureName +"."+fileType.toLowerCase();
		else
			serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
		System.out.println(".[Almacenando la Firma Electrónica en el fichero " + serverSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, serverSignatureName);
		System.out.println(".[/Firma Electrónica servidor correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE FIRMA SERVIDOR FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			fileToBeSigned = obtenerPropiedadArg(StartingClass.PROP_FICHEROAFIRMAR);
			aliasServerCert = obtenerPropiedadArg(StartingClass.PROP_ALIASCERTSERV);
			
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			if (param != null) {
				referenceId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_FORMATOFIRMA);
			if (param != null) {
				signatureFormat = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_INCLUIRDOCUMENTO);
			if (param != null) {
				includeDocument = Integer.parseInt(param);
			}
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			fileToBeSigned = args[1];
			aliasServerCert = args[2];
			
			int i = 3;
			boolean existExternReference = false;
			boolean existFormatSignature = false;
			boolean existHashAlgorithm = false;
			boolean existInclDoc = false;
			while (i < args.length) {
				
				if (args[i].equals("-r")) {
					
					if (existExternReference)
						throw new Exception();
					i++;
					referenceId = args[i];
					i++;
					existExternReference = true;
					
				} else if (args[i].equals("-f")) {
					
					if (existFormatSignature)
						throw new Exception();
					
					i++;				
					signatureFormat = args[i];
					i++;
					existFormatSignature = true;
					
				} else if (args[i].equals("-a")) {
					
					if (existHashAlgorithm)
						throw new Exception();
					
					i++;
					hashAlgorithm = args[i];
					i++;
					existHashAlgorithm = true;
					
				}  else if (args[i].equals("-i")) {
					
					if (existInclDoc)
						throw new Exception();
					
					i++;
					includeDocument = Integer.parseInt(args[i]);
					if(includeDocument!=1 && includeDocument!=0)
						throw new Exception();
					i++;
					existInclDoc = true;
					
				}else {
					
					throw new Exception();
					
				}
				
			}

		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
