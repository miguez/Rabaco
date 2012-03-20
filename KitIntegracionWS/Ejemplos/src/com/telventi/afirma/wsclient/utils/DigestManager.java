/** 
* <p>Fichero: UtilsHash.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 12-enero-2006</p>
* @author Jorge
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Clase con utilidades para cálculo y gestión de hashes
 * 
 * @author Jorge
 */
public class DigestManager
{
	private MessageDigest md = null;
	
	// Tamaños para el cálculo de hashes por trozos
	// 1 KB
	public static final int SIZE_1KB 	= 1000;
	// 10 KB
	public static final int SIZE_10KB 	= 10000;
	// 100 KB
	public static final int SIZE_100KB 	= 100000;
	// 1 MB
	public static final int SIZE_1MB 	= 1000000;
	
	private DigestManager(){}

	// Registramos BouncyCastle como proveedor criptográfico de último nivel
	// para poder usar algoritmos de hash superiores a MD5 y SHA1
	static
	{
		Security.insertProviderAt(new BouncyCastleProvider(), Security.getProviders().length+1);		
	}
	
	/**
	 * Constructor. Crea una instancia MessageDigest para el procesamiento
	 * de hashes
	 * 
	 * @param algorithm Algoritmo Hash con el cual realizar las operaciones
	 * @throws NoSuchAlgorithmException En caso que el algoritmo no esté soportado
	 */
	public DigestManager (String algorithm)
	{
		try
		{
			md = MessageDigest.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME); 
		}
		catch (NoSuchAlgorithmException e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		catch (NoSuchProviderException nsp)
		{
			System.err.println(nsp.getMessage());
			System.exit(-1);
		}
	}
	
	/**
	 * Método que actualiza el digest a calcular
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 */
	public void addDataToCompute (byte data)
	{
		md.update(data);
	}
	
	/**
	 * Método que actualiza el digest a calcular
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 */
	public void addDataToCompute (byte[] data)
	{
		md.update(data);
	}

	/**
	 * Método que actualiza el digest a calcular, sobre una parte de los datos
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 * @param offset byte a partir del cual leer la info de los datos
	 * @param len bytes a leer 
	 */
	public void addDataToCompute (byte[] data, int offset, int len)
	{
		md.update(data, offset, len);
	}
	
	/**
	 * Método que actualiza el digest a calcular
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 */
	public void addDataToCompute (String data)
	{
		md.update(data.getBytes());
	}
	
	/**
	 * Método que actualiza el digest con los datos proporcionados y realiza
	 * los cálculos finales del digest.
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 * @return El digest resultante
	 */
	public byte[] computeHash (byte[] data)
	{
		// Update the digest with the supplied data and performs the final computation
		return md.digest(data);
	}
	
	/**
	 * Método que actualiza el digest con los datos proporcionados y realiza
	 * los cálculos finales del digest.
	 * 
	 * @param data Datos a añadir al cálculo del digest
	 * @return El digest resultante
	 */
	public byte[] computeHash (String data)
	{
		// Update the digest with the supplied data and performs the final computation
		return md.digest(data.getBytes());
	}

	/**
	 * Método que realiza los cálculos finales del digest.

	 * @return El digest resultante
	 */
	public byte[] computeHash ()
	{
		// Performs the hash computation
		return md.digest();		
	}
	
	/**
	 * Método para el cálculo de hashes por trozos
	 * 
	 * @param is InputStream a los datos sobre los que calcular el hash
	 * @param size Tamaño del trozo parcial sobre el que se va calculando el hash
	 * @return hash de los datos
	 * @throws IOException 
	 */
	public byte[] computeHashOptimized(InputStream is, int size) throws IOException
	{
		byte[] buff= new byte[size];
		int r;
		while((r=is.read(buff))>0)
			addDataToCompute(buff, 0, r);

		return computeHash();
	}
	
	/** 
	 * Método que compara los dos digests recibidos
	 * 
	 * @param hash1 Primer digest a ser comparado
	 * @param hash2 Segundo digest a ser comparado
	 * @return true en caso de ser iguales, false en caso contrario
	 */
	synchronized public static boolean equalHashes(byte[] hash1, byte[] hash2)
	{
		return MessageDigest.isEqual(hash1, hash2);
	}
	
	/** 
	 * Método que imprime la representación binaria del digest pasado como
	 * argumento. La representación muestra el bit más significativo a la
	 * izquierda y el menos significativo a la derecha.
	 * 
	 * @param digest Código Hash a imprimir
	 */
	synchronized public static void printDigest(byte[] digest)
	{
		byte aux1, aux2;
		
		// Get each byte
		for (int i=digest.length-1; i>=0; i--)
		{
			aux1 = digest[i];
			
			// Get each bit of this byte
			for(int j=0; j<8; j++)
			{
				aux2 = 0;

				// Get the most significant bit
				aux2 = (byte)(((aux1 & 0x80)>>7) & 0x01);
				
				// Shift 1 bit to the left
				aux1 = (byte)(aux1 << 1);
				
				System.out.print(aux2);
			}
		}
		
		return; 
	}
}
