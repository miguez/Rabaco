package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SERYS
 *
 */
public class ValidarFirmaBloquesDocumento extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String document = null;
	private String idDocument = null;
	private String signature = null;
	private String signatureFormat = DEFAULT_SIGNATURE_FORMAT;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Validar Firma por Bloques Documento es la siguiente:\n" +
		"> ValidarFirmaBloquesDocumento idAplicacion firmaElectronicaAValidar documento idDocumento [-f formatoFirmaElectronica]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion                 --> Identificador de la aplicacion\n" +
		"   firmaElectrónicaAValidar     --> Ruta completa a la Firma Electrónica del bloque a validar\n" +
		"   documento                    --> Ruta completa del documento original sobre el que se calculo la firma servidor.\n" +
		"   idDocumento                  --> Identificador del documento sobre el que se desea validar la Firma Electronica servidor contenida en el bloque de firmas.\n" +
		"   -f formatoFirmaElectronica   --> Formato de la Firma Electronica. Opcional.\n" +
		"                                    Valores posibles: PKCS7, CMS, XMLDSIG, XADES, XADES-BES, XADES-T\n";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length < 4 || args.length > 6))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new ValidarFirmaBloquesDocumento().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE VALIDACIÓN DE FIRMA POR BLOQUES DOCUMENTO]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		
		
		// Lectura de la información del fichero que contiene la firma a validar
		System.out.println(".[Obteniendo información de la firma electronica del bloque a validar " + signature + "...]");
		byte[] signatureBase64 = UtilsFileSystem.readFileFromFileSystemBase64Encoded(signature);	
		System.out.println(".[/Información correctamente obtenida]");

		//Lectura de la información del fichero que contiene el documento original sobre el que se calculo la firma servidor
		System.out.println(".[Obteniendo información del contenido del documento " + document + "...]");
		byte[] documentBase64 = UtilsFileSystem.readFileFromFileSystemBase64Encoded(document);	
		System.out.println(".[/Información correctamente obtenida]");
		
		// Preparación de la petición al servicio Web de ValidarFirmaBloquesDocumento
		System.out.println(".[Preparando la petición al servicio Web " + blockSignatureDocumentValidacionWebServiceName + "...]");
		Document blockSignatureDocumentValidationRequest = UtilsWebService.prepareBlockSignatureDocumentValidationRequest(appId,new String(signatureBase64),new String(documentBase64),idDocument, signatureFormat);
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(blockSignatureDocumentValidationRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(blockSignatureDocumentValidacionWebServiceName, blockSignatureDocumentValidationRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrect(response))
		{
			System.err.println();
			System.err.println("La petición de Validacion de Bloque de Firmas Documento no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");		
		
		System.out.println("[FIN DE PROCESO DE VALIDACION DE FIRMA POR BLOQUES DOCUMENTO]");		
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			signature = obtenerPropiedadArg(StartingClass.PROP_FIRMA);
			document = obtenerPropiedadArg(StartingClass.PROP_DOCUMENTO);
			idDocument = obtenerPropiedadArg(StartingClass.PROP_IDDOCUMENTO);
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_FORMATOFIRMA);
			if (param != null) {
				signatureFormat = param;
			}		
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			
			appId = args[0];
			signature= args[1];
			document = args[2];
			idDocument = args[3];
			
			if (args.length > 4 && args[4].equals("-f"))
				signatureFormat=args[5];
			else
				throw new Exception();
		
		} catch (Exception e) {
			
			System.err.println(errorMessage);
			System.exit(-1);
			
		}
		
	}	
}
