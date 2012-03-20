package com.telventi.afirma.wsclient.eSignature;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author MAMFN
 *
 */

public class BlockSignatureGetDocIdsBackwards extends StartingClass implements WebServicesAvailable
{
	private String appId = null;
	private String signaturesBlock = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Obtener Identificadores de Documentos de un Bloque de Firmas generado por @firma 4.0 es la siguiente:\n" +
		"> BlockSignatureGetDocIdsBackwards applicationId block\n" +
		"\n" +
		"donde\n" +
		"   applicationId           --> Identificador de la aplicacion\n" +
		"   block                   --> Ruta completa al Bloque de firmas en ASN1 generado por @firma 4.0.\n";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new BlockSignatureGetDocIdsBackwards().run(args);
	}
	
	public void run(String[] args)
	{
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DE IDS DE DOCUMENTOS DE BLOQUE DE FIRMAS GENERADO POR @FIRMA 4.0]");
		
		// Obtención de los parámetros de entrada
		fillParameters(args);
		
		
		
		// Lectura de la información del fichero a firmar
		System.out.println(".[Obteniendo información del bloque de Firmas " + signaturesBlock + "...]");
		byte[] signaturesBlockBase64 = UtilsFileSystem.readFileFromFileSystemBase64Encoded(signaturesBlock);	
		System.out.println(".[/Información correctamente obtenida]");

		
		// Preparación de la petición al servicio Web de ObtenerIdDocumentoBloqueFirmasBackwards
		System.out.println(".[Preparando la petición al servicio Web " + getDocIdSignaturesBlockBackwardsWebServiceNameEng + "...]");
		Document getDocIdSignaturesBlockBackwardsRequest = UtilsWebService.prepareGetDocIdSignaturesBlockBackwardsRequestEng(appId,new String(signaturesBlockBase64));
		System.out.println(".[/Petición correctamente preparada]");
		
		// Lanzamiento de la petición WS
		System.out.println(".[Lanzando la petición...]");
		System.out.println("..[peticion]");
		System.out.println(XMLUtils.DocumentToString(getDocIdSignaturesBlockBackwardsRequest));
		System.out.println("..[/peticion]");
		String response = UtilsWebService.launchRequest(getDocIdSignaturesBlockBackwardsWebServiceNameEng, getDocIdSignaturesBlockBackwardsRequest);
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");		
		if (!UtilsWebService.isCorrectEng(response))
		{
			System.err.println();
			System.err.println("La petición de Obtención de Ids de Documentos del bloque de Firmas " + signaturesBlock + " generado por @firma 4.0 no ha sido satisfactoria. Saliendo ...");
			System.exit(-1);
		}
		System.out.println(".[/Petición correctamente realizada]");
				
		System.out.println("[FIN DE PROCESO DE OBTENCIÓN DE IDS DE DOCUMENTOS DE BLOQUE DE FIRMAS GENERADO POR @FIRMA 4.0]");		
	}
	
	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			signaturesBlock = obtenerPropiedadArg(StartingClass.PROP_BLOQUEFIRMAS);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try {
			appId = args[0];
			signaturesBlock = args[1];
		} catch (Exception e) {
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}	
}
