/** 
* <p>Fichero: ValidarCertificado.java</p>
* <p>Descripci�n: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creaci�n: 20-jul-2006</p>
* @author SERYS
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.certificate;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsCertificate;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SERYS
 *
 */
public class ValidarCertificado extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String certificateToValidate = null;
	private int validationMode = DEFAULT_VALIDACION_MODE;
	private boolean getCertificateInfo = DEFAULT_GET_CERTIFICATE_INFO;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Validar Certificado es la siguiente:\n" +
		"> ValidarCertificado idAplicacion certificadoAValidar [-m modoValidacion] [-i obtenerInfo]\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   certificadoAValidar      --> Ruta completa al certificado a validar\n" +
		"   -m modoValidacion        --> Modo de Validaci�n\n" +
 		"                                Opcional. 0 por defecto\n" +
		"                                Valores posibles:\n" +
		"                                0 --> Integridad y el Periodo de Validez del certificado\n" +
		"                                1 --> Modo 0 + Estado de Revocaci�n del certificado\n" +
		"                                2 --> Modo 0 + Modo 1 + Verificaci�n de la cadena de certificaci�n completa\n" +
		"   -i obtenerInfo           --> Booleano que indica si se desea obtener informaci�n del certificado a validar.\n" +
		"                                Opcional. false por defecto\n" + 
		"\n";
	
	public static void main (String[] args)
	{	
		if (!StartingClass.usarFichParams && (args == null || args.length < 2 || args.length > 6))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ValidarCertificado().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE VALIDACI�N DE CERTIFICADO]");
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		// Lectura de la informaci�n del certificado a validar
		System.out.println(".[Obteniendo el certificado " + certificateToValidate + "...]");
		byte[] certificateContentToValidate = UtilsFileSystem.readFileFromFileSystem(certificateToValidate);
		System.out.println("..[Comprobando la codificaci�n del certificado...]");
		try{
			certificateContentToValidate = UtilsCertificate.deletePatternCertificateBase64Encoded(certificateContentToValidate);
			if (!base64Coder.isBase64Encoded(certificateContentToValidate))
			{
				System.out.println(" > El certificado no se encuentra en Base 64. Codificando...");
				certificateContentToValidate = base64Coder.encodeBase64(certificateContentToValidate);		
			}
		}
		catch(Exception e)
		{
			System.err.println();
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		System.out.println("..[/Comprobaci�n de la codificaci�n del certificado correcta...]");
		System.out.println(".[/Informaci�n correctamente obtenida]");

		

		// Preparaci�n de la petici�n al servicio Web de ValidarCertificado
		System.out.println(".[Preparando la petici�n al servicio Web " + certificateValidationWebServiceName + "...]");
		Document certificateValidationRequest = UtilsWebService.prepareCertificateValidationRequest(appId, new String(certificateContentToValidate), validationMode, getCertificateInfo);
		System.out.println(".[/Petici�n correctamente preparada]");

		
		// Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(certificateValidationRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(certificateValidationWebServiceName, certificateValidationRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");	
		
		//Comprobamos el resultado de la validacion
		System.out.println("     >>>>> " + UtilsWebService.getResultCertificateValidationRequest(response));
		System.out.println(".[/Petici�n correctamente realizada]");
					
		
		System.out.println("[/PROCESO DE VALIDACION DE CERTIFICADO FINALIZADO]");
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			certificateToValidate = obtenerPropiedadArg(StartingClass.PROP_CERTIFICADO);
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_MODOVALIDACION);
			if (param != null) {
				validationMode = new Integer(param).intValue();
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_OBTENERINFO);
			if (param != null) {
				getCertificateInfo = new Boolean(param).booleanValue();
			}
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}		
		
	}
	
	public void fillParametersFromArgs(String [] args) {
		
		try
		{
			appId = args[0];
			certificateToValidate = args[1];
			
			if (args.length > 2) {
				
				int i = 2;
				String opcion = null;
				
				while (i < args.length) {
					opcion = args[i];
					if (opcion.startsWith("-")) {

						if (opcion.equals("-m")) {
							i++;
							validationMode = new Integer(args[i]).intValue();
						} else if (opcion.equals("-i")) {
							i++;
							getCertificateInfo = new Boolean(args[i]).booleanValue();
						} else {
							throw new Exception();
						}
					} else {
						throw new Exception();
					}
					i++;
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
