package com.telventi.afirma.wsclient.dss.eSignature;

import java.io.ByteArrayInputStream;

import com.telventi.afirma.mschema.dss.core.v10.SignResponse;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.ArchiveInfo;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

public class DSSCounterServerSignature extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String idTransaction = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private String objetiveSignerFile=null;
	
		
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Firma Servidor CounterSign es la siguiente:\n" +
		"> FirmaServidorCounterSign idAplicacion idTransaccion aliasCertificadoServidor firmaAMultifirmar [-r referenciaExterna] [-f firmanteObjetivo] [-a algoritmoHash]\n\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   idTransaccion                --> Identificador único de la transaccion de firma sobre la que se desea hacer la multifirma CounterSign\n" +
		"   aliasCertificadoServidor     --> Alias del certificado servidor a emplear en la Firma Electrónica Servidor\n" +
		"   -r referenciaExterna         --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -f firmanteObjetivo          --> Ruta completa al Certificado X509 del firmante sobre el que se desea realizar la firma CounterSign.\n" +
		"                                    Opcional. En caso de no indicarlo se realizará una firma CounterSign sobre todos los firmantes \n" +
		"                                    localizados en las hojas de arbol de firmantes.\n" +
		"                                    En caso de indicarlo, éste deberá estar codificado en Base64\n" + 
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto \n" +
        "                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 3 || args.length > 9))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new DSSCounterServerSignature().run(args);
	}
	
	public void run(String[] args) {
		
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR COUNTERSIGN MEDIANTE INTERFAZ OASIS-DSS]");
		String xmlIn = null;
		String response = null;
		fillParameters(args);
		try{
			//Preparación de la petición al servicio Web DSS
			System.out.println(".[Preparando la petición al servicio Web " + service_DSSSign + "...]");
			xmlIn = DSSUtils.getCounterServerSignatureRequest(appId,idTransaction,aliasServerCert, referenceId,hashAlgorithm, objetiveSignerFile);
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
					System.out.println(".[Extrayendo la Firma Electrónica servidor CounterSign de la respuesta...]");	
					byte[] signature = DSSUtils.getCounterSignature(signResponse);
					System.out.println(".[/Firma Electrónica servidor CounterSign correctamente extraído de la respuesta]");		 
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
					serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, getSignatureFormat(signature));
					
					System.out.println(".[Almacenando la Firma Electrónica CounterSign en el fichero " + serverSignatureName + "...]");
					UtilsFileSystem.writeDataToFileSystem(signature, serverSignatureName);
					System.out.println(".[/Firma Electrónica servidor CounterSign correctamente almacenada]");	
					
					System.out.println("[/PROCESO DE FIRMA EN CASCADA DE SERVIDOR MEDIANTE INTERFACES OASIS-DSS FINALIZADO]");
				}else{
					if(signResponse.getResult().getMessage()!=null)
						System.out.println(".[/Operación no valida: "+signResponse.getResult().getMessage()+"]");
				}
			}else{
				System.err.println();
				System.err.println("La petición de Firma Servidor CounterSign mediante interfaz DSS no ha sido satisfactoria. Saliendo ...");
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
			
			param = obtenerPropiedadArg(StartingClass.PROP_CERTFIRMOBJ);
			if (param != null) {
				objetiveSignerFile = param;
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
		
		try
		{
			
			appId = args[0];
			idTransaction = args[1];
			aliasServerCert = args[2];
			
			int i = 3;
			boolean existExternReference = false;
			boolean existObjetive = false;
			boolean existHashAlgorithm = false;
			
			while (i < args.length) {
				
				if (args[i].equals("-r")) {
					
					if (existExternReference)
						throw new Exception();
					i++;
					referenceId = args[i];
					i++;
					existExternReference = true;
					
				} else if (args[i].equals("-f")) {
					
					if (existObjetive)
						throw new Exception();
					
					i++;				
					objetiveSignerFile = args[i];
					i++;
					existObjetive = true;
					
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
		}
		catch(Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}

}
