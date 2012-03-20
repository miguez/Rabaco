/**
 * 
 */
package com.telventi.afirma.wsclient.dss.eSignature;

import com.telventi.afirma.mschema.dss.core.v10.VerifyResponse;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.ArchiveInfo;
import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.dss.DSSConstants;
import com.telventi.afirma.wsclient.dss.DSSUtils;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;
import com.telventi.afirma.wsclient.utils.UtilsWebService;

/**
 * @author SEJRL
 *
 */
public class DSSUpgrade  extends StartingClass implements WebServicesAvailable{
	
	private String appId = null;
	private String eSignaturePath = null;
	private String upgradeFormat = null;
	private String transactionId = null;
	private String certificatePath = null;
	private int modo = 0;
	
	private static final String errorMessage = 
		"La sintaxis de la aplicaci�n de prueba de Actualizaci�n de Firmas Electr�nicas mediante interfaz OASIS-DSS es la siguiente:\n" +
		"ActualizarFirma idAplicacion formatoActualizado [-f firmaElectronica] [-t transaccion] [-fo firmanteObjetivo] [-m modo]\n" +
		"\n" +
		"donde\n" +
		"   idAplicacion             --> Identificador de la aplicacion\n" +
		"   formatoActualizado       --> Formato de la firma electr�nica a la que se pretende extender\n" +
		"                                Valores posibles: CMS-T (CMS con sello de tiempo),XADES-T,CADES-T\n" +
		"   -f firmaElectronica      --> Ruta completa a la Firma Electr�nica a actualizar (debe estar decodificada en Base64)\n" +
		"                                (Obligatorio si no se indica el identificador de transacci�n)\n"+
		"   -t transaccion           --> Identificador de transacci�n de la firma origen.\n" +
		"                                (Obligatorio si no se indica la firma origen)\n"+
		"   -fo firmanteObjetivo     --> Ruta completa al Certificado X509 del firmante sobre el que se desea realizar la actualizaci�n\n" +
		"                                Opcional. En el caso de no indicarse se actualizar� todos los firmantes\n" +
		"   -m  modo                 --> Modo de actualizaci�n. 0pcional.\n"+
		"                                0 --> Se verifica previamente la firma y los certificados contenidos. (Opci�n por defecto)\n"+
		"                                1 --> Solo se verifica la firma.\n"+
		"\n " +
		"NOTA: La Firma Electr�nica a actualizar debe estar codificada en binario (NO en Base64)\n";
	
	public static void main(String[] args) {
		if (!StartingClass.usarFichParams && (args == null || args.length < 4 || args.length > 10))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		new DSSUpgrade().run(args);
	}

	public void run(String[] args) {
		System.out.println("[COMIENZO DE PROCESO DE ACTUALIZACI�N DE FIRMA MEDIANTE INTERFAZ OASIS-DSS]");
		fillParameters(args);
		String xmlIn = null;
		String response = null;
		System.out.println(".[Preparando la petici�n al servicio Web " + service_DSSVerify + "...]");
		try {
			xmlIn = DSSUtils.getUpdateVerifyRequest(appId, upgradeFormat, eSignaturePath, transactionId,certificatePath, modo);
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
			response = UtilsWebService.launchRequest(service_DSSVerify,operation_DSSVerify, DSSUtils.getDocument(xmlIn));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Error al realizar la petici�n]");
			System.exit(-1);
		}
		System.out.println("..[respuesta]");
		System.out.println(response);
		System.out.println("..[/respuesta]");
		//		Procesando la respuesta
		try {
			VerifyResponse verifyResponse = (VerifyResponse) DSSUtils.getResponse(new VerifyResponse(),response);
			DSSUtils.printResult(verifyResponse.getResult());
			if(verifyResponse.getResult()== null || verifyResponse.getResult().getResultMajor()==null
					|| !verifyResponse.getResult().getResultMajor().equals(DSSConstants.ResultMajorDes.success)){
				System.err.println();
				System.err.println("La petici�n de actualizaci�n de la Firma Electr�nica " + (transactionId == null?eSignaturePath:transactionId) + " no ha sido satisfactoria. Saliendo ...");
				if(verifyResponse.getResult()!= null || verifyResponse.getResult().getMessage()!=null)
					System.out.println(verifyResponse.getResult().getMessage());
				System.exit(-1);
			}
			System.out.println(".[/Petici�n correctamente realizada]");
			//Obtenci�n del detalle de la respuesta
			System.out.println(".[Extrayendo la informaci�n detallada de la respuesta...]");	
			if(verifyResponse.getResult()!= null || verifyResponse.getResult().getMessage()!=null)
				System.out.println(verifyResponse.getResult().getMessage());
			System.out.println(".[Extrayendo la Firma Electr�nica actualizada de la respuesta...]");	
			byte[] signature = DSSUtils.getSignature(verifyResponse);
			System.out.println(".[/Firma Electr�nica actualizada correctamente extra�da de la respuesta]");		 
			System.out.println(".[Extrayendo el identificador de transacci�n generada de la respuesta...]");	
			Object obj = DSSUtils.getOptionalOutput(verifyResponse.getOptionalOutputs(),ArchiveInfo.class.getName());
			String transactionId = "no_registrada_"+System.currentTimeMillis();
			if(obj != null){
				ArchiveInfo  archiveInfo = (ArchiveInfo) obj;
				if(archiveInfo.getArchiveIdentifier()!=null && archiveInfo.getArchiveIdentifier().getIdentifier()!=null)
					transactionId = archiveInfo.getArchiveIdentifier().getIdentifier();
				else
					throw new Exception("Valor invalido del elemento <xss:ArchiveInfo>");
			}
			//Generamos el nombre del fichero que contendr� la firma servidor obtenida
			String upgradeSignatureName = TEMPORAL_DIR + "/eSignature_tempfile_" + appId + "_" + transactionId;
			upgradeSignatureName = UtilsSignature.getServerSignatureFileName(upgradeSignatureName, upgradeFormat);
			
			System.out.println(".[Almacenando la Firma Electr�nica actualizada en el fichero " + upgradeSignatureName + "...]");
			UtilsFileSystem.writeDataToFileSystem(signature, upgradeSignatureName);
			System.out.println(".[/Firma Electr�nica actualizada correctamente almacenada]");	

			System.out.println(".[/Identificador de transacci�n generada correctamente extra�do de la respuesta]");	
			
			System.out.println(".[/Informaci�n detallada correctamente extra�da de la respuesta]");		 
			
			System.out.println("[/PROCESO DE ACTUALIZACI�N DE FIRMA MEDIANTE INTERFAZ OASIS-DSS FINALIZADO]");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(".[/Error al procesar la respuesta]");
			System.exit(-1);
		}
		
	}

	public void fillParametersFromFile() {
		
		try {
			
			appId = obtenerPropiedadArg(StartingClass.PROP_APLICACION);
			upgradeFormat = obtenerPropiedadArg(StartingClass.PROP_UPGRADEFORMAT);			
			
			String param = null;
			
			param = obtenerPropiedadArg(StartingClass.PROP_FIRMA);
			if (param != null) {
				eSignaturePath = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_IDTRANSACCION);
			if (param != null) {
				transactionId = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_CERTFIRMOBJ);
			if (param != null) {
				certificatePath = param;
			}
			
			param = obtenerPropiedadArg(StartingClass.PROP_MODOACTUALIZACION);
			if (param != null) {
				modo = Integer.valueOf(param).intValue();
			}
						
		} catch (Exception e) {
			
			System.err.println(errorMessagePropsArgs + errorMessage);
			System.exit(-1);
			
		}
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try{
			appId = args[0];
			upgradeFormat = args[1];
			
			boolean existeFirma = false;
			boolean existeTransaccion = false;
			boolean existeFirmante = false;
			boolean existeModo = false;
			int i=2;
			while(i<args.length){
				if(args[i].equals("-f")){
					if(existeFirma)
						throw new Exception();
					i++;
					eSignaturePath = args[i];
					i++;
					existeFirma = true;
				}else if(args[i].equals("-t")){
					if(existeTransaccion)
						throw new Exception();
					i++;
					transactionId = args[i];
					i++;
					existeTransaccion = true;
				}else if(args[i].equals("-fo")){
					if(existeFirmante)
						throw new Exception();
					i++;
					certificatePath = args[i];
					i++;
					existeFirmante = true;
				}else if(args[i].equals("-m")){
					if(existeModo)
						throw new Exception();
					i++;
					modo = Integer.parseInt(args[i]);
					if(modo !=0 && modo !=1)
						throw new Exception();
					i++;
					existeModo = true;
				}else
					throw new Exception();
			}
		}catch(Exception e){
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}

}
