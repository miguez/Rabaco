package com.telventi.afirma.wsclient.utils;

public class UtilsSignature
{
	public static String getServerSignatureFileName(String name, String signatureFormat)
	{
		
		if (isASN1TypeOfSignature(signatureFormat))
			name += ".p7s";
		else if(signatureFormat.equals("PDF"))
			name += ".pdf";
		else if(signatureFormat.equals("ODF"))
			name += ".odt";
		else
			name += ".xml";
		
		return name;
	}
	
	public static boolean isCustomType(String signatureForm){
		return (signatureForm.equals("PDF") || signatureForm.equals("ODF"));
	}
	public static boolean isASN1TypeOfSignature(String signatureFormat)
	{
		if (signatureFormat.equalsIgnoreCase("PKCS7") ||
			signatureFormat.equalsIgnoreCase("CMS") 
			|| signatureFormat.equalsIgnoreCase("CADES")
			||signatureFormat.equalsIgnoreCase("CADES-BES")
			||signatureFormat.equalsIgnoreCase("CADES-T")
			||signatureFormat.equalsIgnoreCase("CMS-T"))
			return true;
		else
			return false;
	}
}
