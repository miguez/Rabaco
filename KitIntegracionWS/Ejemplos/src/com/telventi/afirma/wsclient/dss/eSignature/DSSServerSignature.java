
package com.telventi.afirma.wsclient.dss.eSignature;

import com.telventi.afirma.mschema.dss.core.v10.SignResponse;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.ArchiveInfo;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJRL
 *
 */
public class DSSServerSignature extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String fileToBeSigned = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String signatureFormat = DEFAULT_SIGNATURE_FORMAT;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private String tstFormat = DSSConstants.SignatureTypeDes.RFC3161_TST;
	private int modo = 0;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Firma Servidor DSS es la siguiente:\n" +
		"> FirmaServidor idAplicacion ficheroAFirmar aliasCertificadoServidor [-r referenciaExterna] [-f formatoFirmaElectronica] [-a algoritmoHash]"/* [-ft formatoTST]*/+"\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   ficheroAFirmar           --> Ruta completa al fichero a firmar o Identificador de documento \n" +
		"   aliasCertificadoServidor --> Alias del certificado servidor a emplear en la Firma Electrónica Servidor\n" +
		"   -r referenciaExterna     --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -f formatoFirma          --> Formato de la Firma Electronica a generar\n" +
 		"                                Opcional. CMS por defecto\n" +
		"                                Valores posibles: CMS, CMS-T, CADES, CADES-BES, CADES-T, XMLDSIG, XADES, XADES-BES, XADES-T, PDF, ODF, ODF-T\n" +
		"   -a algoritmoHash         --> Algoritmo de hash a emplear en la firma\n" +
		"                                Opcional. SHA1 por defecto\n" +
		"                                Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"   -m modo                  --> Modo de firma. Opcional\n" +
		"                                Valores posibles: \n" +
		"                                0: Modo detached (Valor por defecto)\n" +
		"                                   La Firma no contiene los datos firmados.\n " +
		"                                   (EXPLICIT, en ASN.1)\n"+
		"                                1: Modo enveloping.\n " +
		"                                   La Firma contiene (IMPLICIT, en ASN.1)\n" +
		"                                   /envuelve(en XML)los datos firmados.\n"+
		"                                2: Modo enveloped\n" +
		"                                   La Firma (sólo para firmas XML) se\n" +
		"                                   incorpora dentro de los datos firmados\n"+
		
		/*"   -ft formatoTST           --> Formato del TimeStampToken a generar\n" +
		"                                Opcional, valores posibles:\n" +
		"                                0: Binary TST según RFC3161 (Valor por defecto)\n" +
		"                                1: XMLTimeStampToken\n"+ */
		"\n " +
		"NOTA: La Firma Electrónica se almacena en binario (NO en Base64)\n";

	public static void main (String[] args){
		if (!StartingClass.usarFichParams && (args == null || args.length < 3 || args.length > 11))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSServerSignature().run(args);
	}
	
	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR MEDIANTE INTERFAZ DSS]");
		fillParameters(args);
		String response = null;
		try {
			String xmlIn = null;
			try{
				new Long(fileToBeSigned);
				xmlIn = DSSUtils.getStoreServerSignatureRequest(appId,fileToBeSigned, aliasServerCert, referenceId,signatureFormat,hashAlgorithm,tstFormat,modo);
			}catch(NumberFormatException ne){
				//No es un identificador de documento
				xmlIn = DSSUtils.getServerSignatureRequest(appId,fileToBeSigned, aliasServerCert, referenceId,signatureFormat,hashAlgorithm,tstFormat,modo);
			}
			System.out.println(".[Lanzando la petición...]");
			System.out.println("..[peticion]");
			System.out.println(xmlIn);
			System.out.println("..[/peticion]");
			response = UtilsWebService.launchRequest(service_DSSSign,operation_DSSSign, DSSUtils.getDocument(xmlIn));
			System.out.println("..[respuesta]");
			System.out.println(response);
			System.out.println("..[/respuesta]");		
		} catch (Exception e) {
			System.err.println();
			System.err.println("Error al obtener el xml de entrada: "+e.getMessage());
			System.exit(-1);
		}
		try {
			SignResponse signResponse = (SignResponse) DSSUtils.getResponse(new SignResponse(),response);
			if(signResponse.getResult()!=null){
				DSSUtils.printResult(signResponse.getResult());
				if(signResponse.getResult().getResultMajor()!=null && signResponse.getResult().getResultMajor().equals(DSSConstants.ResultMajorDes.success)){
					System.out.println(".[/Petición correctamente realizada]");
					//Obtención de la Firma Electrónica
					System.out.println(".[Extrayendo la Firma Electrónica servidor de la respuesta...]");	
					byte[] signature = DSSUtils.getSignature(signResponse);
					System.out.println(".[/Firma Electrónica servidor correctamente extraído de la respuesta]");		 
					//Obtención del identificador de la transacción
					System.out.println(".[Extrayendo el identificador de transacción generada de la respuesta...]");	
					ArchiveInfo id = (ArchiveInfo) DSSUtils.getOptionalOutput(signResponse.getOptionalOutputs(),ArchiveInfo.class.getName());
					if(id == null || id.getArchiveIdentifier()==null 
							|| id.getArchiveIdentifier().getIdentifier() == null || id.getArchiveIdentifier().getIdentifier().trim().equals(""))
						throw new Exception("Valor invalido del elemento <xss:ArchiveInfo>");
					String transactionId = id.getArchiveIdentifier().getIdentifier();
					System.out.println(".[/Identificador de transacción generada correctamente extraído de la respuesta]");	
					//Generamos el nombre del fichero que contendrá la firma servidor obtenida
					String serverSignatureName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
					serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
					
					System.out.println(".[Almacenando la Firma Electrónica en el fichero " + serverSignatureName + "...]");
					UtilsFileSystem.writeDataToFileSystem(signature, serverSignatureName);
					System.out.println(".[/Firma Electrónica servidor correctamente almacenada]");	
					
					System.out.println("[/PROCESO DE FIRMA SERVIDOR MEDIANTE INTERFACES OASIS-DSS FINALIZADO]");
				}else{
					if(signResponse.getResult().getMessage()!=null)
						System.out.println(".[/Operación no valida: "+signResponse.getResult().getMessage()+"]");
				}
			}else{
				System.out.println(".[/La respuesta no es correcta]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println();
			System.err.println("Error al procesar la respuesta: "+e.getMessage());
			System.exit(-1);
		}
		
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
			
			param = obtenerPropiedadArg(StartingClass.PROP_MODOFIRMA);
			if (param != null) {
				modo = Integer.valueOf(param).intValue();
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
			boolean existXMLMode = false;
			
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
					
				} else if (args[i].equals("-m")) {
					
					if (existXMLMode)
						throw new Exception();
					
					i++;
					modo = Integer.parseInt(args[i]);
					if(modo>2 || modo<0){
						throw new Exception();
					}

					i++;
					existXMLMode = true;
					
				} else {
					
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
