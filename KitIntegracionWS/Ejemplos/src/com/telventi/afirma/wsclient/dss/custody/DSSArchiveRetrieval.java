/**
 * 
 */
package com.telventi.afirma.wsclient.dss.custody;

import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveRetrievalResponse;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJRL
 *
 */
public class DSSArchiveRetrieval extends StartingClass implements WebServicesAvailable{

	private String appId = null;
	private String transactionId = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Obtener la Firma Electr�nica de una Transacci�n mediante interfaz OASIS - DSS es la siguiente:\n" +
		"> ObtenerFirmaTransaccion applicationId transactionId\n" +
		"\n" +
		"  donde\n" +
		"   applicationId             --> Identificador de la aplicacion\n" +
		"   transactionId             --> Identificador de la transacci�n correspondiente al proceso de firma de la cual se desea obtener la Firma Electr�nica\n"; 

	public static void main(String[] args) {
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSArchiveRetrieval().run(args);

	}

	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE OBTENCI�N DE LA FIRMA ELECTR�NICA DE UNA TRANSACCI�N]");
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);
		String xmlIn = null;
		String response = null;
		System.out.println(".[Preparando la petici�n al servicio Web " + service_DSSArchiveRetrieval + "...]");
		try {
			xmlIn = DSSUtils.getArchiveRetrivalRequest(appId, transactionId);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Petici�n incorrectamente preparada]");
			System.exit(-1);
		}		
		System.out.println(".[/Petici�n correctamente preparada]");
		
		//Lanzamiento de la petici�n WS
		System.out.println(".[Lanzando la petici�n...]");
		System.out.println("..[peticion]");
		System.out.println(xmlIn);
		System.out.println("..[/peticion]");
		try {
			response = UtilsWebService.launchRequest(service_DSSArchiveRetrieval,operation_DSSArchiveRetrieval, DSSUtils.getDocument(xmlIn));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Se ha producido un error en la petici�n]");
			System.exit(-1);
		}
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		
		//Procesando la respuesta
		try {
			ArchiveRetrievalResponse archiveResponse = (ArchiveRetrievalResponse) DSSUtils.getResponse(new ArchiveRetrievalResponse(),response);
			DSSUtils.printResult(archiveResponse.getResult());
			if(archiveResponse.getResult()== null || archiveResponse.getResult().getResultMajor()==null
					|| !archiveResponse.getResult().getResultMajor().equals(DSSConstants.ResultMajorDes.success)){
				System.err.println();
				System.err.println("La petici�n obtencion de firma no ha sido satisfactoria. Saliendo ...");
				if(archiveResponse.getResult()!= null || archiveResponse.getResult().getMessage()!=null)
					System.out.println(archiveResponse.getResult().getMessage());
				System.exit(-1);
			}
			System.out.println(".[/Petici�n correctamente realizada]");
			//Obtenci�n del detalle de la respuesta
			System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");
			if(archiveResponse.getResult()!= null || archiveResponse.getResult().getMessage()!=null)
				System.out.println(archiveResponse.getResult().getMessage());
			//Obtenemos la firma 
			byte[] signature = DSSUtils.getSignature(archiveResponse);
			String eSignatureDestFileName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
			if(DSSUtils.isXML(signature))
				eSignatureDestFileName += ".xml";
			else
				eSignatureDestFileName += ".p7s";
			
			System.out.println(".[Almacenando la Firma Electr�nica recibida en el fichero " + eSignatureDestFileName + "...]");
			UtilsFileSystem.writeDataToFileSystem(signature, eSignatureDestFileName);
			System.out.println(".[/Firma Electr�nica recibida correctamente almacenada]");	
			
			System.out.println("[/PROCESO DE OBTENCI�N DE LA FIRMA ELECTR�NICA DE UNA TRANSACCI�N MEDIANTE INTERFAZ OASIS_DSS FINALIZADO]");

		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println(".[/Error al procesar la respuesta]");
			System.exit(-1);
		}
		
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			transactionId = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			appId = args[0];
			transactionId = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
	
}
