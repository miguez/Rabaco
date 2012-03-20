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
public class ServerSignatureCounterSign extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private String objetiveSignerFile=null;
	
	private String signatureFormat=null;
		
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Firma Servidor CoSign es la siguiente:\n" +
		"> ServerSignatureCounterSign idAplicacion idTransaccion aliasCertificadoServidor [-r referenciaExterna] [-f firmanteObjetivo] [-a algoritmoHash]\n\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   idTransaccion                --> Identificador único de la transaccion de firma sobre la que se desea hacer la multifirma CoSign\n" +
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
		
		new ServerSignatureCounterSign().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR COUNTERSIGN]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		
		
		byte[] objetiveSigner=null;
		//Lectura del firmanteObjetivo
		if (objetiveSignerFile!=null)
		{
			System.out.println(".[Obteniendo el certificado del firmante objetivo " + objetiveSignerFile + "...]");
			objetiveSigner = UtilsFileSystem.readFileFromFileSystem(objetiveSignerFile);		
			System.out.println(".[/Certificado correctamente obtenida]");
		}

		// Preparación de la petición al servicio Web de FirmaServidorCounterSign
		System.out.println(".[Preparando la petición al servicio Web " + serverSignatureCounterSignWebServiceNameEng + "...]");
		Document serverSignatureCounterSignRequest = UtilsWebService.prepareServerSignatureCounterSignRequestEng(appId, idTransaction, aliasServerCert, referenceId, hashAlgorithm, objetiveSigner);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(serverSignatureCounterSignRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(serverSignatureCounterSignWebServiceNameEng, serverSignatureCounterSignRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Firma Servidor CounterSign de la transacción con el identficador " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
		
		//Obtención de la Firma Electrónica
		System.out.println(".[Extrayendo el Formato de Firma servidor counterSign de la respuesta...]");	
		signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "eSignatureFormat");
		System.out.println(".[/Formato de Firma servidor correctamente extraído de la respuesta]");	
		
		// Obtención de la Firma Electrónica
		System.out.println(".[Extrayendo la Firma Electrónica servidor counterSign de la respuesta...]");	
		String signatureValue = UtilsWebService.getInfoFromDocumentNode(response, "eSignature");
		System.out.println(".[/Firma Electrónica servidor correctamente extraído de la respuesta]");	
		
		//Obtención del identificador de Transaccion generado
		System.out.println(".[Extrayendo el identificador de transaccion generado de la respuesta...]");	
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "transactionId");
		System.out.println(".[/Identificador de transaccion generado correctamente extraído de la respuesta]");	
		
		//Decodificamos la Firma Electrónica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma Servidor CounterSign");
			System.exit(-1);
		} 
			
		//Generamos el nombre del fichero que contendrá la firma servidor obtenida
		String serverSignatureName = TEMPORAL_DIR + "/eSignatureCounterSign_tempfile_" + appId + "_" + transactionId;
		serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electrónica en el fichero " + serverSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, serverSignatureName);
		System.out.println(".[/Firma Electrónica servidor CounterSign correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE FIRMA SERVIDOR COUNTERSIGN FINALIZADO]");
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
					signatureFormat = args[i];
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
