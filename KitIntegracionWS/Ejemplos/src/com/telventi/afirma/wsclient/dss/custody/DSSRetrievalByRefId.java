package com.telventi.afirma.wsclient.dss.custody;

import java.util.Vector;

import com.telventi.afirma.mschema.dss.core.v10.SignatureObject;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.SignatureArchiveId;
import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveRetrievalResponse;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

public class DSSRetrievalByRefId extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String externalReference = null;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicación de prueba de Obtener las transacciones por referencia externa es la siguiente:\n" +
		"> ObtenerTransaccionesReferencia idAplicacion referenciaExterna\n" +
		"\n" +
		"  donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   referenciaExterna        --> Referencia Externa";
	
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new DSSRetrievalByRefId().run(args);
	}
	
	public void fillParametersFromFile() {
		try {
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			externalReference = obtenerPropiedadArg(StartingClass.PROP_REFERENCIAEXT);
			
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}
	
	public void fillParametersFromArgs(String[] args) {
		try
		{
			appId = args[0];
			externalReference = args[1];
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}

	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE OBTENCIÓN DE TRANSACCIONES POR REFERENCIA EXTERNA MEDIANTE INTERFAZ DSS]");
		System.out.println(".[Preparando la petición al servicio Web " + getTransactionsByExternalReferenceWebServiceName + "...]");
		String xmlIn = null;
		String response = null;
		System.out.println(".[Preparando la petición al servicio Web " + service_DSSArchiveRetrieval + "...]");
		try {
			xmlIn = DSSUtils.getArchiveRetrivalRequestByRefId(appId, externalReference);
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
			response = UtilsWebService.launchRequest(service_DSSArchiveRetrieval,operation_DSSArchiveRetrieval, DSSUtils.getDocument(xmlIn));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Se ha producido un error en la petición]");
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
				System.err.println("La petición obtencion de firmas mediante identificador de referencia no ha sido satisfactoria. Saliendo ...");
				if(archiveResponse.getResult()!= null || archiveResponse.getResult().getMessage()!=null)
					System.out.println(archiveResponse.getResult().getMessage());
				System.exit(-1);
			}
			System.out.println(".[/Petición correctamente realizada]");
			//Obtención del detalle de la respuesta
			System.out.println(".[Extrayendo los identificadores de transacción obtenidos...]");
			SignatureObject sigObj = archiveResponse.getSignatureObject();
			if(sigObj == null || sigObj.getOther()==null || sigObj.getOther().getOtherItems()==null){
				System.err.println();
				System.err.println("El mensaje de respuesta no es válido. Saliendo ...");
				System.exit(-1);
			}
			Vector items = sigObj.getOther().getOtherItems();
			for(int i = 0;i<items.size();i++){
				if(!(items.get(i) instanceof SignatureArchiveId)){
					System.err.println();
					System.err.println("El mensaje de respuesta no es válido. Saliendo ...");
					System.exit(-1);
				}else{
					System.out.println("Transacción: "+((SignatureArchiveId)items.get(i)).getArchiveId());
				}
			}
			System.out.println("[/PROCESO DE OBTENCIÓN DE TRANSACCIONES POR REFERENCIA EXTERNA MEDIANTE INTERFAZ DSS FINALIZADO]");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Error al procesar la respuesta]");
			System.exit(-1);
		}
		
	}

}
