package com.telventi.afirma.wsclient.dss.eSignature;

import java.io.ByteArrayInputStream;

import com.telventi.afirma.mschema.dss.core.v10.SignResponse;
import com.telventi.afirma.mschema.dss.core.v10.SignatureType;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.ArchiveInfo;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

public class DSSCoServerSignature extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String idTransaction = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;	
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Firma Servidor CoSign madiante DSS es la siguiente:\n" +
		"> FirmaServidorCoSign idAplicacion idTransaccion aliasCertificadoServidor firmaAMultifirmar [-r referenciaExterna] [-a algoritmoHash]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   idTransaccion                --> Identificador único de la transaccion de firma sobre la que se desea hacer la multifirma CoSign\n" +
		"   aliasCertificadoServidor     --> Alias del certificado servidor a emplear en la Firma Electrónica Servidor\n" +
		"   -r referenciaExterna         --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto \n" +
        "                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n";
	
	public static void main(String[] args){
		if (!StartingClass.usarFichParams && (args == null || args.length < 3 || args.length > 7))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSCoServerSignature().run(args);
	}
	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR COSIGN MEDIANTE INTERFAZ DSS]");
		String xmlIn = null;
		String response = null;
		fillParameters(args);
		try{
			//Preparación de la petición al servicio Web DSS
			System.out.println(".[Preparando la petición al servicio Web " + service_DSSSign + "...]");
			xmlIn = DSSUtils.getCoServerSignatureRequest(appId,idTransaction, aliasServerCert, referenceId,hashAlgorithm);
			System.out.println(".[/Petición correctamente preparada]");
		}catch(Exception e){
			System.err.println("Error al obtener la petición");
			e.printStackTrace();
			System.exit(-1);
		}
		//Lanzamiento de la petición WS
		try{
			System.out.println(".[Lanzando la petición...]");
			System.out.println("..[peticion]");
			System.out.println(xmlIn);
			System.out.println("..[/peticion]");
			response = UtilsWebService.launchRequest(service_DSSSign,operation_DSSSign, DSSUtils.getDocument(xmlIn));
			System.out.println("..[respuesta]");
			System.out.println(response);
			System.out.println("..[/respuesta]");
		}catch(Exception e){
			System.err.println("Error al realizar la petición");
			e.printStackTrace();
			System.exit(-1);
		}
		//Procesando la respuesta
		try {
			SignResponse signResponse = (SignResponse) DSSUtils.getResponse(new SignResponse(),response);
			
			
			if(signResponse.getResult()!=null){
				DSSUtils.printResult(signResponse.getResult());
				if(signResponse.getResult().getResultMajor()!=null && signResponse.getResult().getResultMajor().equals(DSSConstants.ResultMajorDes.success)){
					System.out.println(".[/Petición correctamente realizada]");
					//Obtención de la Firma Electrónica
					System.out.println(".[Extrayendo la Firma Electrónica servidor CoSign de la respuesta...]");	
					byte[] signature = DSSUtils.getSignature(signResponse);
					System.out.println(".[/Firma Electrónica servidor CoSign correctamente extraído de la respuesta]");		 
					//Obtención del identificador de la transacción
					System.out.println(".[Extrayendo el identificador de transacción generada de la respuesta...]");	
					ArchiveInfo id = (ArchiveInfo) DSSUtils.getOptionalOutput(signResponse.getOptionalOutputs(),ArchiveInfo.class.getName());
					SignatureType signatureType = (SignatureType) DSSUtils.getOptionalOutput(signResponse.getOptionalOutputs(),SignatureType.class.getName());
					if(id == null || id.getArchiveIdentifier()==null 
							|| id.getArchiveIdentifier().getIdentifier() == null || id.getArchiveIdentifier().getIdentifier().trim().equals(""))
						throw new Exception("Valor invalido del elemento <xss:ArchiveInfo>");
					String transactionId = id.getArchiveIdentifier().getIdentifier();
					System.out.println(".[/Identificador de transacción generada correctamente extraído de la respuesta]");	
					//Generamos el nombre del fichero que contendrá la firma servidor obtenida
					String serverSignatureName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
					if(signatureType ==null)
						serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, getSignatureFormat(signature));
					else
						serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, getSignatureFormat(signatureType));
					System.out.println(".[Almacenando la Firma Electrónica CoSign en el fichero " + serverSignatureName + "...]");
					UtilsFileSystem.writeDataToFileSystem(signature, serverSignatureName);
					System.out.println(".[/Firma Electrónica servidor CoSign correctamente almacenada]");	
					
					System.out.println("[/PROCESO DE FIRMA EN PARALELO DE SERVIDOR MEDIANTE INTERFACES OASIS-DSS FINALIZADO]");
				}else{
					if(signResponse.getResult().getMessage()!=null)
						System.out.println(".[/Operación no valida: "+signResponse.getResult().getMessage()+"]");
				}
			}else{
				System.err.println();
				System.err.println("La petición de Firma Servidor CoSign mediante interfaz DSS no ha sido satisfactoria. Saliendo ...");
				System.exit(-1);
			}
		} catch (Exception e) {
			System.err.println("Error al procesar la respuesta");
			e.printStackTrace();
			System.exit(-1);
		}

	}
	
	private String getSignatureFormat(byte[] signature){
		try{
			javax.xml.parsers.DocumentBuilderFactory dbf =javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);
			javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
			db.parse(new ByteArrayInputStream(signature));
			return "XADES";
		}catch(Exception e){
			return "CMS";
		}
	}
	
	private String getSignatureFormat(SignatureType signatureType){
		if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.pkcs7)){
			return "PKCS7";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.cms)){
			return "CMS";
		}else if(signatureType.getType().equals(DSSConstants.SignatureFormDes.CMS_With_TST)){
			return "CMS-T";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.xml_dsig)){
			return "XMLDSIG";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.cades)){
			return "CADES";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.xades_v_1_3_2)){
			return "XADES";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.ODF)||signatureType.getType().equals(DSSConstants.SignatureFormDes.ODF_With_TST) ){
			return "ODF";
		}else if(signatureType.getType().equals(DSSConstants.SignatureTypeDes.PDF)){
			return "PDF";
		}
		return null;
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			idTransaction = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			aliasServerCert = obtenerPropiedadArg(StartingClass.PROP_ALIASCERTSERV);
			
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			if (param != null) {
				referenceId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}
	
	public void fillParametersFromArgs(String[] args) {
		
		try {
			
			appId = args[0];
			idTransaction = args[1];
			aliasServerCert = args[2];
			
			int i = 3;
			boolean existExternReference = false;
			boolean existHashAlgorithm = false;
			
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
