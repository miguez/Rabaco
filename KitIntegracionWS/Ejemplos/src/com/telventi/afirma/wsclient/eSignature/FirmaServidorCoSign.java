/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripci�n: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creaci�n: 22-jun-2006</p>
* @author SEJLHA
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
 * @author SERYS
 *
 */
public class FirmaServidorCoSign extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String idTransaction = null;
	private String aliasServerCert = null;
	private String referenceId = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;	
		
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Firma Servidor CoSign es la siguiente:\n" +
		"> FirmaServidorCoSign idAplicacion idTransaccion aliasCertificadoServidor [-r referenciaExterna] [-a algoritmoHash]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   idTransaccion                --> Identificador �nico de la transaccion de firma sobre la que se desea hacer la multifirma CoSign\n" +
		"   aliasCertificadoServidor     --> Alias del certificado servidor a emplear en la Firma Electr�nica Servidor\n" +
		"   -r referenciaExterna         --> Identificador de referencia externa proporcionado por la aplicacion. Opcional.\n" +
		"   -a algoritmoHash             --> Algoritmo de hash a emplear en la firma\n" +
		"                                    Opcional. SHA1 por defecto \n" +
        "                                    Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 3 || args.length > 7))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new FirmaServidorCoSign().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE FIRMA SERVIDOR COSIGN]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		

		// Preparaci�n de la petici�n al servicio Web de FirmaServidorCoSign
		System.out.println(".[Preparando la petici�n al servicio Web " + serverSignatureCoSignWebServiceName + "...]");
		Document serverSignatureCoSignRequest = UtilsWebService.prepareServerSignatureCoSignRequest(appId, idTransaction, aliasServerCert, referenceId, hashAlgorithm);
		System.out.println(".[/Petici�n correctamente preparada]");
		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(serverSignatureCoSignRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(serverSignatureCoSignWebServiceName, serverSignatureCoSignRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petici�n de Firma Servidor CoSign de la transacci�n con el identficador " + idTransaction + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petici�n correctamente realizada]");
		
		//Obtenci�n del formato de la Firma Electr�nica
		System.out.println(".[Extrayendo el Formato de Firma servidor de la respuesta...]");	
		String signatureFormat = UtilsWebService.getInfoFromDocumentNode(response, "formatoFirma");
		System.out.println(".[/Formato de Firma servidor correctamente extra�do de la respuesta]");	
		
		// Obtenci�n de la Firma Electr�nica
		System.out.println(".[Extrayendo la Firma Electr�nica servidor de la respuesta...]");	
		String signatureValue = UtilsWebService.getInfoFromDocumentNode(response, "firmaElectronica");
		System.out.println(".[/Firma Electr�nica servidor correctamente extra�do de la respuesta]");		 

		//Obtenci�n del identificador de Transaccion generado
		System.out.println(".[Extrayendo el identificador de transaccion generado de la respuesta...]");	
		String transactionId = UtilsWebService.getInfoFromDocumentNode(response, "idTransaccion");
		System.out.println(".[/Identificador de transaccion generado correctamente extra�do de la respuesta]");	
		
		//Decodificamos la Firma Electr�nica recibida
		byte[] signatureValueBase64Decoded = null;
		try
		{
			signatureValueBase64Decoded = base64Coder.decodeBase64(signatureValue.getBytes());				
		}
		catch (Exception e)
		{
			System.err.println("Error decodificando la Firma Servidor CoSign");
			System.exit(-1);
		} 		
		
		//Generamos el nombre del fichero que contendr� la firma servidor obtenida
		String serverSignatureName = TEMPORAL_DIR + "/eSignatureCoSign_tempfile_" + appId + "_" + transactionId;
		serverSignatureName = UtilsSignature.getServerSignatureFileName(serverSignatureName, signatureFormat);
		
		System.out.println(".[Almacenando la Firma Electr�nica en el fichero " + serverSignatureName + "...]");
		UtilsFileSystem.writeDataToFileSystem(signatureValueBase64Decoded, serverSignatureName);
		System.out.println(".[/Firma Electr�nica servidor CoSign correctamente almacenada]");	
		
		System.out.println("[/PROCESO DE FIRMA SERVIDOR COSIGN FINALIZADO]");
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
