/** 
* <p>Fichero: UtilsFileSystem.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 23-jun-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author SEJLHA
 *
 */
public class UtilsFileSystem
{
	/**
	 * Método que devuelve el contenido del fichero indicado.
	 * 
	 * @param filePathToRead Ruta completa al fichero
	 * @return Contenido del fichero
	 */
	public static synchronized byte[] readFileFromFileSystem(String filePathToRead)
	{
		FileInputStream fileReader = null;
		ByteArrayOutputStream baos = null;
		
		try
		{
			fileReader = new FileInputStream(new File(filePathToRead));
			
			baos = new ByteArrayOutputStream();
			byte[] buff = new byte[100000];
			int r = -1;
			while ((r = fileReader.read(buff)) > 0)
				baos.write(buff, 0, r);
			
			fileReader.close();
			baos.close();

			return baos.toByteArray();
		}
		catch (IOException ioe)
		{
			System.err.println("El fichero indicado " + filePathToRead + " no existe.");
			System.err.println("Es necesario indicarlo junto con la ruta completa al mismo");
			System.out.println(ioe.getMessage());
			System.exit(-1);
			return null;
		}
		catch (Exception e)
		{
			System.err.println("Ha ocurrido un error leyendo el fichero " + filePathToRead);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
		finally
		{
			try
			{
				baos.close();
			}
			catch (Exception e){}
			
			try
			{
				fileReader.close();
			}
			catch (Exception e){}
		}		
	}
	
	/**
	 * Método que devuelve el contenido del fichero indicado codificado en Base64.
	 * En caso que el fichero ya se encuentre codificado en Base64, lo devuelve tal cual.
	 * 
	 * @param filePathToRead Ruta completa al fichero
	 * @return Contenido del fichero codificado en Base64
	 */
	public static synchronized byte[] readFileFromFileSystemBase64Encoded(String filePathToRead)
	{
		Base64Coder base64Coder = new Base64Coder();
		
		try
		{			
			byte[] content = readFileFromFileSystem(filePathToRead);
			
			if (!base64Coder.isBase64Encoded(content))
				return base64Coder.encodeBase64(content);	
			else
				return content;
		}
		catch (Exception e)
		{
			System.err.println("Ha ocurrido un error codificando en Base64 el contenido del fichero " + filePathToRead);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}	
	}
	
	/**
	 * Método que devuelve el contenido del fichero indicado decodificado en Base64.
	 * 
	 * @param filePathToRead Ruta completa al fichero
	 * @return Contenido del fichero codificado en Base64
	 */
	public static synchronized byte[] readFileFromFileSystemBase64Decoded(String filePathToRead)
	{
		try
		{			
			return new Base64Coder().decodeBase64(readFileFromFileSystem(filePathToRead));
		}
		catch (Exception e)
		{
			System.err.println("Ha ocurrido un error decodificando en Base64 el contenido del fichero " + filePathToRead);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}	
	}
	
	/**
	 * Método que almacena la información indicado en el fichero indicado.
	 * 
	 * @param dataToStore Datos a guardar
	 * @param filePathToCreate Ruta completa al fichero a crear
	 */
	public static synchronized void writeDataToFileSystem(byte[] dataToStore, String filePathToCreate)
	{
		FileOutputStream fileWriter = null;
				
		try
		{
			fileWriter = new FileOutputStream(new File(filePathToCreate));
			
			fileWriter.write(dataToStore);
		}
		catch (IOException ioe)
		{
			System.err.println("Error guardando los datos en el fichero " + filePathToCreate);
			System.out.println(ioe.getMessage());
			System.exit(-1);
		}
		catch (Exception e)
		{
			System.err.println("Error guardando los datos en el fichero " + filePathToCreate);
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		finally
		{
			try
			{
				fileWriter.flush();
				fileWriter.close();
			}
			catch (Exception e){}
		}	
	}
	
	/**
	 * Devuelve el nombre del fichero indicado.
	 * 
	 * @param filePath Ruta completa del fichero
	 * @return Nombre del fichero
	 */
	public static synchronized String getNameFromFilePath(String filePath)
	{
		try
		{
			return new File(filePath).getName();
		}
		catch (Exception e)
		{
			System.err.println("Error obteniendo el nombre del fichero de " + filePath);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * Devuelve la extensión del fichero indicado.
	 * 
	 * @param filePath Ruta completa del fichero
	 * @return Extensión del fichero
	 */
	public static synchronized String getExtensionFromFilePath(String filePath)
	{
		try
		{
			String name = getNameFromFilePath(filePath);
			
			if ( (name.indexOf('.') == -1) || (name.lastIndexOf('.') == (name.length() - 1)))
				return "undefined";
			else
				return name.substring(name.lastIndexOf('.') + 1);
		}
		catch (Exception e)
		{
			System.err.println("Error obteniendo el nombre del fichero de " + filePath);
			System.out.println(e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	public static synchronized void deleteFile(String filePath){
	
		try{
			File fileToBeDeleted= new File(filePath);
			
			if (!fileToBeDeleted.exists())
				throw new Exception("	>El fichero o directorio no existe.");
			
			if (!fileToBeDeleted.canWrite())
				throw new Exception("	>El fichero o directorio se encuentra protegido." );
		
			fileToBeDeleted.deleteOnExit();
		}
		catch(Exception e)
		{
			System.err.println("Error borrando el fichero o directorio " + filePath);
			System.err.println(e.getMessage());
		}
		
	}
}
