/** 
* <p>Fichero: FirmaServidor.java</p>
* <p>Descripci�n: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creaci�n: 22-jun-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.coder;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;

/**
 * @author SEJLHA
 *
 */
public class Base64CoderUtility extends StartingClass implements WebServicesAvailable
{
	private String fileToProcess = null;
	private String option = null;
	
	private static String CODER_OPTION = "CODER";
	private static String DECODER_OPTION = "DECODER";
	
	private static final String errorMessage = 
		"La sintaxis de la utilidad de codificaci�n/decodificaci�n en Base64 es la siguiente:\n" +
		"> UtilidadBase64 ficheroAProcesar opcion\n" +
		"\n" +
		"  donde\n" +
		"   ficheroAProcesar         --> Ruta completa al fichero a codificar/decodificar en Base64\n" +
		"   opcion                   --> Operaci�n a realizar. 2 posibles valores:\n" +
		"                                coder   = Se codifica el fichero en Base64\n" +
		"                                decoder = Se decodifica el fichero en Base64\n";
		
	public static void main (String[] args)
	{
		if (!StartingClass.usarFichParams && (args == null || args.length != 2))
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
		new Base64CoderUtility().run(args);
	}
	
	public void run(String[] args)
	{
		String destFileName = null;
		byte[] destFileContent = null;
		
		// Obtenci�n de los par�metros de entrada
		fillParameters(args);

		System.out.println("[Utilidad de CODIFICACI�N/DECODIFICACI�N en Base64]");

		// Option de codificar en Base64
		if (option.equals(CODER_OPTION))
		{
			// Lectura de la informaci�n del fichero codific�ndolo
			System.out.println(".[Obteniendo informaci�n del fichero " + fileToProcess + " y realizando codificaci�n en Base64...]");
			destFileContent = UtilsFileSystem.readFileFromFileSystemBase64Encoded(fileToProcess);		
			String fileName = UtilsFileSystem.getNameFromFilePath(fileToProcess);
			String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToProcess);
			System.out.println(".[/Informaci�n correctamente procesada]");

			destFileName = TEMPORAL_DIR + "/" + fileName + "_BASE64Coded." + fileType;
		}
		else
		{
			// Lectura de la informaci�n del fichero codific�ndolo
			System.out.println(".[Obteniendo informaci�n del fichero " + fileToProcess + " y realizando decodificaci�n en Base64...]");
			destFileContent = UtilsFileSystem.readFileFromFileSystemBase64Decoded(fileToProcess);		
			String fileName = UtilsFileSystem.getNameFromFilePath(fileToProcess);
			String fileType = UtilsFileSystem.getExtensionFromFilePath(fileToProcess);
			System.out.println(".[/Informaci�n correctamente procesada]");

			destFileName = TEMPORAL_DIR + "/" + fileName + "_BASE64Decoded." + fileType;
		}

		System.out.println(".[Almacenando la informaci�n codificada en Base64 en el fichero " + destFileName + "...]");
		UtilsFileSystem.writeDataToFileSystem(destFileContent, destFileName);
		System.out.println(".[/Informaci�n correctamente almacenada]");
		
		System.out.println("[/Utilidad de CODIFICACI�N/DECODIFICACI�N en Base64]");
	}

	public void fillParametersFromFile() {
		// TODO Auto-generated method stub
		
	}

	public void fillParametersFromArgs(String[] args) {
		
		try
		{
			fileToProcess = args[0];
			option = args[1].toUpperCase();
			if (!option.equals(CODER_OPTION) & !option.equals(DECODER_OPTION))
			{
				System.err.println(errorMessage);
				System.exit(-1);
			}
		}
		catch (Exception e)
		{
			System.err.println(errorMessage);
			System.exit(-1);
		}
		
	}
}
