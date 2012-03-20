package com.telventi.afirma.wsclient.dss;




public class DSSConstants {
	
	
	public static final transient String prefix = "dss";
	
	public static final transient String OASIS_CORE_1_0_NS = "urn:oasis:names:tc:dss:1.0:core:schema";
	
	public static final transient String default_doc_name = "Document name DSS request";
	
	public static final transient String default_doc_type = "text/plain";
	
	public static final transient String namespace_dsig="http://www.w3.org/2000/09/xmldsig#";
	
	/** Identificador que determina que la operación de upgrade no se verifica el certificado*/
	public static final transient String no_cert_validation = "urn:afirma:dss:1.0:profile:XSS:upgrade:NoCertificateValidation";

	public class ELEMENT_CORE_1_0 {
		/** Nombre del nodo de Timestamp según las especificaciones OASIS-DSS para XMLTimeStamp*/
		public static final transient String TIMESTAMP= "Timestamp";
		
		/** Nombre del nodo de TSTInfo */
		public static final transient String TSTINFO = "TstInfo";
		
		/** Nombre del nodo de CreationTime */
		public static final transient String CREATIONTIME = "CreationTime";
	}
	
	public class ResultMajorDes {
		
		public static final transient String success = "urn:oasis:names:tc:dss:1.0:resultmajor:Success";
		
		public static final transient String requester_error = "urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError";

		public static final transient String responder_error = "urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError";
		
		public static final transient String insufficient_information = "urn:oasis:names:tc:dss:1.0:resultmajor:InsufficientInformation";
	
	}
	
	public class ResultMinorDes {
		
		public static final transient String on_all_documents = "urn:oasis:names:tc:dss:1.0:resultminor:valid:signature:OnAllDocuments";
		
		public static final transient String not_all_referenced = "urn:oasis:names:tc:dss:1.0:resultminor:valid:signature:NotAllDocumentsReferenced";

		public static final transient String incorrect_signature = "urn:oasis:names:tc:dss:1.0:resultminor:invalid:IncorrectSignature";
		
		public static final transient String has_manifest_results = "urn:oasis:names:tc:dss:1.0:resultminor:valid:signature:HasManifestResults";
		
		public static final transient String invalid_timestamp = "urn:oasis:names:tc:dss:1.0:resultminor:valid:signature:InvalidSignatureTimestamp";
		
		public static final transient String referenced_not_present = "urn:oasis:names:tc:dss:1.0:resultminor:ReferencedDocumentNotPresent";
		
		public static final transient String key_info_not_provided = "urn:oasis:names:tc:dss:1.0:resultminor:KeyInfoNotProvided";
		
		public static final transient String ref_uri_omitted = "urn:oasis:names:tc:dss:1.0:resultminor:MoreThanOneRefUriOmitted";
		
		public static final transient String invalid_ref_uri = "urn:oasis:names:tc:dss:1.0:resultminor:InvalidRefURI";
		
		public static final transient String not_parseable_xml = "urn:oasis:names:tc:dss:1.0:resultminor:NotParseableXMLDocument";

		public static final transient String not_supported = "urn:oasis:names:tc:dss:1.0:resultminor:NotSupported";
		
		public static final transient String inappropriate_signature = "urn:oasis:names:tc:dss:1.0:resultminor:Inappropriate:signature";
	
		public static final transient String general_error ="urn:oasis:names:tc:dss:1.0:resultminor:GeneralError";
	
		public static final transient String key_lookup_failed ="urn:oasis:names:tc:dss:1.0:resultminor:invalid:KeyLookupFailed";
		
		public static final transient String crl_not_availiable ="urn:oasis:names:tc:dss:1.0:resultminor:CrlNotAvailiable";
		
		public static final transient String ocsp_not_availiable ="urn:oasis:names:tc:dss:1.0:resultminor:OcspNotAvailiable";
	
		public static final transient String Cert_chain_not_complete ="urn:oasis:names:tc:dss:1.0:resultminor:CertificateChainNotComplete";
		
		public static final transient String valid_multi_signatures  = "urn:oasis:names:tc:dss:1.0:resultminor:ValidMultiSignatures";
	
		public static final transient String arch_identifier_not_found  = "urn:oasis:names:tc:dss:1.0:profile:archive:resultminor:ArchiveIdentifierNotFound";
		
		/** No se ha suministrado  formato de actualización */
		public static final transient String updated_format_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:UpdateSignatureTypeNotProvided";
	}
	
	public class SignatureTypeDes{
		
		/** */
		public static final transient String xades_v_1_3_2 = "http://uri.etsi.org/01903/v1.3.2#";
		
		public static final transient String xades_v_1_2_2 = "http://uri.etsi.org/01903/v1.2.2#";
		
		public static final transient String xades_v_1_1_1 = "http://uri.etsi.org/01903/v1.1.1#";
		
		public static final transient String cades = "http://uri.etsi.org/01733/v1.7.3#";
		
		public static final transient String xml_dsig = "urn:ietf:rfc:3275";
		
		public static final transient String cms = "urn:ietf:rfc:3369";
		
		public static final transient String pkcs7 = "urn:ietf:rfc:2315";
		
		public static final transient String RFC3161_TST = "urn:ietf:rfc:3161";
		
		public static final transient String xml_TST= "urn:oasis:names:tc:dss:1.0:core:schema:XMLTimeStampToken";
	
		public static final transient String ODF ="urn:afirma:dss:1.0:profile:XSS:forms:ODF";
		
		public static final transient String PDF ="urn:afirma:dss:1.0:profile:XSS:forms:PDF";
	}
	
	public class SignatureFormDes{
		/** Formato de firma ODF con sello de tiempo, expuesto en el perfil XSS de AFirma */
		public static final transient String ODF_With_TST = "urn:afirma:dss:1.0:profile:XSS:forms:ODFWithTST";
		
		/** Formato de firma CMS con sello de tiempo, expuesto en el perfil XSS de AFirma */
		public static final transient String CMS_With_TST = "urn:afirma:dss:1.0:profile:XSS:forms:CMSWithTST";
		
		/** Formato avanzado BES según las especificaciones del perfil AdES de OASIS */
		public static final transient String BES = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:BES";
		
		/** Formato avanzado EPES según las especificaciones del perfil AdES de OASIS */
		public static final transient String EPES = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:EPES";
		
		/** Formato avanzado T según las especificaciones del perfil AdES de OASIS */
		public static final transient String ES_T = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-T";
		
		/** Formato avanzado C según las especificaciones del perfil AdES de OASIS */
		public static final transient String ES_C = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-C";
	
		/** Formato avanzado X según las especificaciones del perfil AdES de OASIS */
		public static final transient String ES_X = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X";
		
		/** Formato avanzado X-L según las especificaciones del perfil AdES de OASIS */
		public static final transient String ES_X_L = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-X-L";
		
		/** Formato avanzado A según las especificaciones del perfil AdES de OASIS */		
		public static final transient String ES_A = "urn:oasis:names:tc:dss:1.0:profiles:AdES:forms:ES-A";
		
		/** Formato XADES-BES v.1.1.1 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_1_1_BES = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.1.1:forms:BES";
		
		/** Formato XADES-BES v.1.2.2 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_2_2_BES = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.2.2:forms:BES";
		
		/** Formato XADES-BES V.1.3.2 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_3_2_BES = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.3.2:forms:BES";
	
		/** Formato XADES-T v.1.1.1 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_1_1_T = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.1.1:forms:ES-T";
		
		/** Formato XADES-T v.1.2.2 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_2_2_T = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.2.2:forms:ES-T";
		
		/** Formato XADES-T V.1.3.2 segun las especificaciones XSS de AFirma*/
		public static final transient String XAdES_1_3_2_T = "urn:afirma:dss:1.0:profile:XSS:XAdES:1.3.2:forms:ES-T";

	}
	
	public class ProfileDes{
		/** Identificador del perfil XAdES de OASIS*/
		public static final transient String oasis_xades = "urn:oasis:names:tc:dss:1.0:profiles:XAdES";
		
		/** Identificador del perfil CAdES de OASIS*/
		public static final transient String oasis_cades = "urn:oasis:names:tc:dss:1.0:profiles:CAdES";
		
		/** Identificador del perfil XSS de OASIS*/
		public static final transient String oasis_xss = "urn:oasis:names:tc:dss:1.0:profiles:XSS";
		
		/** Identificador del perfil TimeStamp de OASIS*/
		public static final transient String oasis_timestamping = "urn:oasis:names:tc:dss:1.0:profiles:timestamping";
		
		/** Identificador del perfil XSS de AFirma*/
		public static final transient String afirma_xss = "urn:afirma:dss:1.0:profile:XSS";
		
		/** Identificador del perfil Archive de AFirma*/
		public static final transient String afirma_archive ="urn:afirma:dss:1.0:profile:archive";
	}
	
	public class DigestMethodDes{
		
		public static final transient String digest_method_sha1 = "http://www.w3.org/2000/09/xmldsig#sha1";
		
		public static final transient String digest_method_sha256 = "http://www.w3.org/2001/04/xmlenc#sha256";

		public static final transient String digest_method_sha512 = "http://www.w3.org/2001/04/xmlenc#sha512";

		public static final transient String digest_method_sha384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";

		public static final transient String digest_method_md5 = "http://www.w3.org/2001/04/xmldsig-more#md5";

		public static final transient String digest_method_md2 = "urn:ietf:rfc:1319";
	}
	
	public class XMLSignatureMode{
		
		/** Modo de firma XML enveloping */
		public static final transient String enveloping_mode = "urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:EnvelopingMode";
		
		/** Modo de firma XML enveloped */
		public static final transient String enveloped_mode = "urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:EnvelopedMode";
		
		/** Modo de firma XML detached */
		public static final transient String detached_mode ="urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:DetachedMode";
		
	}
}
