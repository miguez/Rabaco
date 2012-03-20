/** 
* <p>Fichero: ObtenerInfoCertificado.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 20-jul-2006</p>
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
public class ObtenerInfoCertificado extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String certificateToGetInfo = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Obtención de Información de Certificado es la siguiente:\n" +
		"> ObtenerInfoCertificado idAplicacion certificadoAValidar\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion           --> Identificador de la aplicacion\n" +
		"   certificadoAValidar    --> Ruta completa al certificado a validar\n" +
		"\n"; 
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length !=2 ))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ObtenerInfoCertificado().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DE INFORMACIÓN DE CERTIFICADO]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);

		// Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo el certificado " + certificateToGetInfo + "...]");
		byte[] certificateContentToGetInfo = UtilsFileSystem.readFileFromFileSystem(certificateToGetInfo);
		
		System.out.println("..[Comprobando la codificación del certificado...]");
		try{
			
			certificateContentToGetInfo = UtilsCertificate.deletePatternCertificateBase64Encoded(certificateContentToGetInfo);
			if (!base64Coder.isBase64Encoded(certificateContentToGetInfo))
			{
				System.out.println(" > El certificado no se encuentra en Base 64. Codificando...");
				certificateContentToGetInfo = base64Coder.encodeBase64(certificateContentToGetInfo);		
			}
		}
		catch(Exception e)
		{
			System.err.println();
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		System.out.println("..[/Comprobación de la codificación del certificado correcta...]");
		System.out.println(".[/Información correctamente obtenida]");


		// Preparación de la petición al servicio Web de AlmacenarDocumento
		System.out.println(".[Preparando la petición al servicio Web " + getCertificateInfoWebServiceName + "...]");
		Document getCertificateInfoRequest = UtilsWebService.prepareGetCertificateInfoRequest(appId, new String(certificateContentToGetInfo));
		System.out.println(".[/Petición correctamente preparada]");

		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getCertificateInfoRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getCertificateInfoWebServiceName, getCertificateInfoRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectGetCertificateInfoRequest(response))
		{
			System.err.println();
			System.err.println("La petición de Obtención de Información del certificado " + certificateToGetInfo + " no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
					
		
		System.out.println("[/PROCESO DE OBTENCIÓN DE INFORMACION DE CERTIFICADO FINALIZADO]");
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			certificateToGetInfo = obtenerPropiedadArg(StartingClass.PROP_CERTIFICADO);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}	
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			certificateToGetInfo = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
