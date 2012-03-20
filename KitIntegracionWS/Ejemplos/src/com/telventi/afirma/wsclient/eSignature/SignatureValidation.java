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
import com.telventi.afirma.wsclient.utils.DigestManager;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */
public class SignatureValidation extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String eSignaturePath = null;
	private String eSignatureFormat =  null;
	private int mode = 0; 
	private String filePath = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Validar Firmas Electrónicas es la siguiente:\n" +
		"SignatureValidation idAplicacion firmaElectrónicaAValidar formatoFirmaElectronica modo [-fh fichero/hash] [-ah algoritmoHash]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   firmaElectrónicaAValidar --> Ruta completa a la Firma Electrónica a validar (debe estar decodificada en Base64)\n" +
		"   formatoFirmaElectronica  --> Formato de la Firma Electronica\n" +
		"                                Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n" +
		"   modo                     --> Modo de validación\n" +
		"                                0: Sólo se enviará la Firma Electrónica\n" +
		"                                1: Se enviará la Firma Electrónica junto con el fichero correspondiente\n" +
		"                                2: Se enviará la Firma Electrónica junto con el hash del fichero correspondiente\n" +
		"                                3: Se enviará la Firma Electrónica junto con el hash y el fichero correspondiente\n" +
		"   -fh fichero/hash         --> Ruta completa al fichero firmado.\n" +
		"                                Se obvia en modo 0. Obligatorio si modo es 1, 2 o 3\n" +
		"								 Si es modo 1, 2 o 3: Indica la ruta del fichero correspondiente.\n" +
		"   -ah algoritmoHash        --> Algoritmo de hash a emplear en la firma\n" +
		"                                Opcional si es modo 0 o 1. Obligatorio si modo es 2 o 3\n" +
		"                                Valores posibles: MD2, MD5, SHA, SHA1, SHA256, SHA384, SHA512\n" +
		"\n " +
		"NOTA: La Firma Electrónica a validar debe estar codificada en binario (NO en Base64)\n";

	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 4 || args.length > 8))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new SignatureValidation().run(args);
	}
	
	public void run(String[] args)
	{
		byte[] hash = null; 

		System.out.println("[COMIENZO DE PROCESO DE VALIDACIÓN DE FIRMA]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		

		// Lectura de la Firma Electrónica a validar
		System.out.println(".[Obteniendo la Firma Electrónica del fichero " + eSignaturePath + "...]");
		byte[] eSignature = null;
		eSignature = UtilsFileSystem.readFileFromFileSystemBase64Encoded(eSignaturePath);		
		System.out.println(".[/Firma Electrónica correctamente obtenida]");
		
		// Lectura del documento original
		byte[] data = null;
		if (mode == 1)
		{
			System.out.println(".[Obteniendo el fichero original codificado en Base64 " + filePath + "...]");
			data = UtilsFileSystem.readFileFromFileSystemBase64Encoded(filePath);		
			System.out.println(".[/Fichero original correctamente obtenido]");
		}
		else if (mode == 2)
		{
			System.out.println(".[Obteniendo el fichero original " + filePath + "...]");
			data = UtilsFileSystem.readFileFromFileSystem(filePath);		
			System.out.println(".[/Fichero original correctamente obtenido]");
			System.out.println(".[Calculando el hash del fichero original con el algoritmo de hash " + hashAlgorithm + "...]");
			hash = calculateHashOfDataBase64Encoded(data);
			System.out.println(".[/Hash correctamente calculado]");
		}
		else if (mode == 3)
		{
			System.out.println(".[Obteniendo el fichero original " + filePath + "...]");
			data = UtilsFileSystem.readFileFromFileSystem(filePath);		
			System.out.println(".[/Fichero original correctamente obtenido]");
			System.out.println(".[Calculando el hash del fichero original con el algoritmo de hash " + hashAlgorithm + "...]");
			hash = calculateHashOfDataBase64Encoded(data);
			System.out.println(".[/Hash correctamente calculado]");
			System.out.println(".[Codificando el fichero original " + filePath + " en Base64...]");
			try
			{
				data = base64Coder.encodeBase64(data);
			}
			catch (Exception e)
			{
				System.err.println(".[/Error codificando el fichero original]");
				System.exit(-1);
			}
			System.out.println(".[/Fichero original correctamente codificado]");
		}
		
		// Preparación de la petición al servicio Web de ValidarFirma
		System.out.println(".[Preparando la petición al servicio Web " + signatureValidationWebServiceNameEng + "...]");
		Document eSignatureValidationRequest = null;
		try
		{
			if (mode == 0)
				eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(eSignature), eSignatureFormat, null, null, null);
			else if (mode == 1)
				eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(eSignature), eSignatureFormat, null, null, data);
			else if (mode == 2)
				eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(eSignature), eSignatureFormat, hash, hashAlgorithm, null);
			else
				eSignatureValidationRequest = UtilsWebService.prepareValidateSignatureRequestEng(appId, new String(eSignature), eSignatureFormat, hash, hashAlgorithm, data);
		}
		catch (Exception e)
		{
			System.err.println(".[/Petición incorrectamente preparada]");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(eSignatureValidationRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(signatureValidationWebServiceNameEng, eSignatureValidationRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de verificación de la Firma Electrónica " + eSignaturePath + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
							
		// Obtención del detalle de la respuesta
		System.out.println(".[Extrayendo la información detallada de la respuesta...]");	
		String descriptionValue = UtilsWebService.getXMLChildsFromDocumentNode(response, "description");
		System.out.println(descriptionValue);
		System.out.println(".[/Información detallada correctamente extraída de la respuesta]");		 
		
		System.out.println("[/PROCESO DE VALIDACIÓN DE FIRMA FINALIZADO]");
	}
	
	private byte[] calculateHashOfDataBase64Encoded(byte[] data)
	{
		DigestManager digestManager = new DigestManager(hashAlgorithm);
		try
		{
			return base64Coder.encodeBase64(digestManager.computeHash(data));
		}
		catch (Exception e)
		{
			System.err.println("Error calculando el hash de los datos");
			System.exit(-1);
			return null;
		}
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			eSignaturePath = obtenerPropiedadArg(StartingClass.PROP_FIRMA);
			eSignatureFormat = obtenerPropiedadArg(StartingClass.PROP_FORMATOFIRMA);
			mode = Integer.parseInt(obtenerPropiedadArg(StartingClass.PROP_MODOVALFIRMA));
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_FICHEROFIRMADO);
			if (param != null) {
				filePath = param;
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
			eSignaturePath = args[1];
			eSignatureFormat = args[2];
			mode = Integer.parseInt(args[3]);
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}

		if (mode != 0 && mode != 1 && mode != 2 && mode != 3)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		boolean existHashAlgorithm = false;
		boolean existFileHash = false;
		int i = 4;
		
		try {
			while (i < args.length) {
				
				if (args[i].equals("-ah")) {
					
					if (existHashAlgorithm)
						throw new Exception();
					i++;
					hashAlgorithm = args[i];
					i++;
					existHashAlgorithm = true;
					
				} else if (args[i].equals("-fh")) {
					
					if (existFileHash)
						throw new Exception();
					
					i++;				
					if (mode > 0) {
						filePath = args[i];
					}
					i++;
					existFileHash = true;
					
				} else {
					
					throw new Exception();
					
				}
				
			}
			
			if ((mode>0 && !existFileHash) || (mode>1 && !existHashAlgorithm))
				throw new Exception();
			
		} catch (Exception e) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
