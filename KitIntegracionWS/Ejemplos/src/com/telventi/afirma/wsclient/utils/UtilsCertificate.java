package com.telventi.afirma.wsclient.utils;

public class UtilsCertificate
{
	public static final String CERTIFICATE_HEAD_BASE64= "-----BEGIN CERTIFICATE-----";
	
	public static final String CERTIFICATE_END_BASE64= "-----END CERTIFICATE-----";
	
	/**
	 * Método que elimina el patrón de comienzo y fin en un certificado codificado en Base64
	 * 
	 * @param data Certificado codificado en Base64
	 * @return Certificado con los patrones del comienzo y fin eliminados
	 * @throws NullPointerException 
	 */
	public static byte[] deletePatternCertificateBase64Encoded(byte[] certificate) throws Exception
	{
		String sCertificate = null;
		boolean flag = false;
		
		if (certificate == null)
			throw new NullPointerException("Error en el parámetro de entrada");
		
		
		sCertificate = new String(certificate).trim();
		
		if (sCertificate.startsWith(CERTIFICATE_HEAD_BASE64))
		{
			sCertificate=sCertificate.replaceAll(CERTIFICATE_HEAD_BASE64," ");
			System.out.println("  > Borrado el patron del certificado " + CERTIFICATE_HEAD_BASE64);
			flag = true;
		}
		if (sCertificate.endsWith(CERTIFICATE_END_BASE64)){
			sCertificate=sCertificate.replaceAll(CERTIFICATE_END_BASE64," ");
			System.out.println("  > Borrado el patron del certificado " + CERTIFICATE_END_BASE64);
			flag = true;
		}
				
		if (flag)
			return sCertificate.trim().getBytes();
		else
			return certificate;
	}
	
}
