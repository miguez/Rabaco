/** 
* <p>Fichero: Base64Coder.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 17-may-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.utils;


/**
 * Clase de utilidad de emplea el codificador en Base64 de la plataforma,
 * pero gestionando el error tanto en codificación como decodificación.
 * 
 * @author SEJLHA
 */
public class Base64Coder
{	
	// Tamaños para el cálculo de codificaciones y decodificaciones en Base64 por trozos
	// 1 KB
	public static final int SIZE_1KB = 1000;
	// 10 KB
	public static final int SIZE_10KB = 10000;
	// 100 KB
	public static final int SIZE_100KB = 100000;
	// 1 MB
	public static final int SIZE_1MB = 10000000;
	
	/**
	 * Método que codifica un array de bytes en base 64.
	 * Empleado por las clases de firmado para codificar los datos, firmas
	 * o hashes antes de enviarlos por la red. En caso de no poder codificar los
	 * datos recibidos, se supondrán ya codificados en Base64.
	 * 
	 * @param data Datos a codificar en Base64
	 * @return Datos codificados.
	 * @throws SignatureModuleException 
	 */
	public byte[] encodeBase64 (byte[] data) throws Exception
	{
		return encodeBase64(data, 0, data.length);
	}
	
	public byte[] encodeBase64 (byte[] data, int offset, int len) throws Exception
	{
		UtilsBase64 encoder = null;
		byte result [] = null;
		
		if (data == null)
			throw new NullPointerException("Error en el parámetro de entrada");

		try
		{
			encoder = new UtilsBase64();
			
			result = encoder.encodeBytes(data, offset, len).getBytes();
			
			return (result == null) ? data : result;
		}
		catch (Exception e)
		{
			throw new Exception("Error codificando los datos en Base64");
		}
	}
	
	/**
	 * Método que decodifica un array de bytes en base 64.
	 * Empleado por las clases de firmado para decodificar los datos, firmas
	 * o hashes recibidos. En caso de no poder decodificar los
	 * datos recibidos, se supondrán ya decodificados en Base64.
	 * 
	 * @param data Datos codificados en Base64
	 * @return Datos decodificados.
	 * @throws Exception 
	 */
	public byte[] decodeBase64 (byte[] data) throws Exception
	{
		return decodeBase64(data, 0, data.length);
	}
	
	public byte[] decodeBase64 (byte[] data, int offset, int len) throws Exception
	{
		UtilsBase64 decoder = null;
		byte result [] = null;
		
		if (data == null)
			throw new NullPointerException("Error en el parámetro de entrada");

		try
		{
			decoder = new UtilsBase64();
			
			result = decoder.decode(data, offset, len);
			
			return (result == null) ? data : result;
		}
		catch (Exception e)
		{
			throw new Exception("Error decodificando los datos en Base64");
		}
	}
	
	public boolean isBase64Encoded(byte[] data) throws Exception
	{
		
		UtilsBase64 decoder = null;
		byte result[] = null;
		
		if (data==null)
			throw new NullPointerException("Error en el parametro de entrada");
		
		try{
			decoder = new UtilsBase64();
			
			result = decoder.decode(data, 0, data.length);
			
			if (result==null)
				return false;
			else
				return true;
		}catch(Exception e)
		{
			throw new Exception("Error comprobando si los datos están codificados en Base64");
		}
		

	}
}
