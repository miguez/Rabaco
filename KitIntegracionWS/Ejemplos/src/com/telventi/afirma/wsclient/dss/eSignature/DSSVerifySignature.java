/**
 * 
 */
package com.telventi.afirma.wsclient.dss.eSignature;

import com.telventi.afirma.mschema.dss.core.v10.VerifyResponse;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.AFirmaXSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJRL
 *
 */
public class DSSVerifySignature  extends StartingClass implements WebServicesAvailable{

	private String appId = null;
	private String eSignaturePath = null;
	private int reportNivel =  0;
	private int mode = 0; 
	private String filePath = null;
	private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
	private boolean obtenerInfo = false;
	private boolean includeCert = false;
	private boolean includeTST = false; 
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Validar Firmas Electrónicas mediante interfaz OASIS-DSS es la siguiente:\n" +
		"ValidarFirma idAplicacion firmaElectrónicaAValidar nivel modo [-fh fichero/hash] [-ah algoritmoHash] [-i obtenerInfo] [-c includeCert] [-t includeTST]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   firmaElectrónicaAValidar --> Ruta completa a la Firma Electrónica a validar (debe estar decodificada en Base64)\n" +
		//"   formatoFirmaElectronica  --> Formato de la Firma Electronica\n" +
		//"                                Valores posibles: PKCS7, CMS, CMS-T, CADES, CADES-BES, CADES-T, XMLDSIG, XADES, XADES-BES, XADES-T, PDF, ODF, ODF-T\n" +
		"   nivel                    --> Determina la información adicional que se desea obtener del proceso\n"+
		"                                0: Para cada firma solo se devolverá el resultado final de la verificación\n"+
		"                                1: nivel 0 + resultado final del proceso de validación de los certificados incluidos\n"+
		"                                2: nivel 1 + información adicional sobre la verificación de los certificados incluidos\n"+
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
		"                                Valores posibles: SHA1, SHA256, SHA512\n" +
		"   -i obtenerInfo           --> Booleano que indica si se desea obtener información del certificado a validar.\n" +
		"                                Valor por defecto:false\n"+
		"   -c includeCert			 --> Booleano que indica si la respuesta incluirá el certificado firmante y de la TSA\n" +
		"                                (en caso de solicitar información del TST)codificado en BASE64\n"+
		"                                Solo aplica para peticiones de nivel 2. Valor por defecto: false\n"+
		"   -t includeTST			 --> Booleano que indica si la respuesta incluirá información sobre el Sello de Tiempo\n"+
		"                                Solo aplica para peticiones de nivel 1 y 2. Valor por defecto: false\n"+
		" \n"+
		"\n " +
		"NOTA: La Firma Electrónica a validar debe estar codificada en binario (NO en Base64)\n";

	public static void main(String[] args) {
		if (!StartingClass.usarFichParams && (args == null || args.length < 4 || args.length > 14))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSVerifySignature().run(args);
	}

	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE VALIDACIÓN DE FIRMA MEDIANTE INTERFAZ OASIS-DSS]");
		String xmlIn = null;
		String response = null;
		// Obtención de los parámetros de entrada
		fillParameters(args);
		//Preparación de la petición al servicio Web de ValidarFirma
		System.out.println(".[Preparando la petición al servicio Web " + service_DSSVerify + "...]");
		try {
			xmlIn = DSSUtils.getVerifySignatureRequest(appId,eSignaturePath,mode,filePath,hashAlgorithm,reportNivel,obtenerInfo,includeCert,includeTST);
		
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Petición incorrectamente preparada]");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente preparada]");
		//Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(xmlIn);
		System.out.println("..[/peticion]");
		try {
			response = UtilsWebService.launchRequest(service_DSSVerify,operation_DSSVerify, DSSUtils.getDocument(xmlIn));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Error al realizar la petición]");
			System.exit(-1);
		}
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		//Procesando la respuesta
		try {
			VerifyResponse verifyResponse = (VerifyResponse) DSSUtils.getResponse(new VerifyResponse(),response);
			DSSUtils.printResult(verifyResponse.getResult());
			if(verifyResponse.getResult()== null || verifyResponse.getResult().getResultMajor()==null
					|| !verifyResponse.getResult().getResultMajor().equals(AFirmaXSSConstants.ResultMajorDes.valid_signature)){
				System.err.println();
				System.err.println("La petición de verificación de la Firma Electrónica " + eSignaturePath + " no ha sido satisfactoria. Saliendo ...");
				if(verifyResponse.getResult()!= null || verifyResponse.getResult().getMessage()!=null)
					System.out.println(verifyResponse.getResult().getMessage());
				System.exit(-1);
			}
			System.out.println(".[/Petición correctamente realizada]");
			//Obtención del detalle de la respuesta
			System.out.println(".[Extrayendo la información detallada de la respuesta...]");	
			if(verifyResponse.getResult()!= null || verifyResponse.getResult().getMessage()!=null)
				System.out.println(verifyResponse.getResult().getMessage());
			System.out.println(".[/Información detallada correctamente extraída de la respuesta]");		 
			
			System.out.println("[/PROCESO DE VALIDACIÓN DE FIRMA MEDIANTE INTERFAZ OASIS-DSS FINALIZADO]");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Error al procesar la respuesta]");
			System.exit(-1);
		}
		

	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			eSignaturePath = obtenerPropiedadArg(StartingClass.PROP_FIRMA);
			mode = Integer.valueOf(obtenerPropiedadArg(StartingClass.PROP_MODOVALFIRMA)).intValue();			
			reportNivel = Integer.valueOf(obtenerPropiedadArg(StartingClass.PROP_NIVEL)).intValue();		
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_HASHALGORITHM);
			if (param != null) {
				hashAlgorithm = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_FICHEROFIRMADO);
			if (param != null) {
				filePath = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_OBTENERINFO);
			if (param != null) {
				obtenerInfo = Boolean.valueOf(param).booleanValue();
			}
			param = obtenerPropiedadArg(StartingClass.PROP_INCLUDE_CERT);
			if (param != null) {
				includeCert = Boolean.valueOf(param).booleanValue();
			}
			param = obtenerPropiedadArg(StartingClass.PROP_INCLUDE_TST);
			if (param != null) {
				includeTST = Boolean.valueOf(param).booleanValue();
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
			reportNivel = Integer.parseInt(args[2]);
			mode = Integer.parseInt(args[3]);
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}

		if ((mode != 0 && mode != 1 && mode != 2 && mode != 3)
				||(reportNivel != 0 && reportNivel != 1 && reportNivel != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		
		boolean existHashAlgorithm = false;
		boolean existFileHash = false;
		boolean existInfoCert = false;
		boolean existIncludeCert = false;
		boolean existIncludeTST = false;
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
					
				} else if (args[i].equals("-i")) {
					
					if (existInfoCert)
						throw new Exception();
					
					i++;				
					if (args[i].equalsIgnoreCase("true")) 
						obtenerInfo = true;
					else if(args[i].equalsIgnoreCase("false"))
						obtenerInfo = false;
					else
						throw new Exception();
					i++;
					existInfoCert = true;
					
				}else if (args[i].equals("-c")) {
					
					if (existIncludeCert)
						throw new Exception();
					
					i++;				
					if (args[i].equalsIgnoreCase("true")) 
						includeCert = true;
					else if(args[i].equalsIgnoreCase("false"))
						includeCert = false;
					else
						throw new Exception();
					i++;
					existIncludeCert = true;
					
				}else if (args[i].equals("-t")) {
					
					if (existIncludeTST)
						throw new Exception();
					
					i++;				
					if (args[i].equalsIgnoreCase("true")) 
						includeTST = true;
					else if(args[i].equalsIgnoreCase("false"))
						includeTST = false;
					else
						throw new Exception();
					i++;
					existIncludeTST = true;
					
				}else {
					
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
