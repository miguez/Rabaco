/**
 * <p>Fichero: AFirmaXSSConstants.java</p>
 * <p>Descripción:  Constantes definidas en el perfil XSS de @FIRMA </p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 13-mar-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.wsclient.dss;


/**
 * Constantes definidas en el perfil XSS de @FIRMA
 * @author SEJRL
 *
 */
public class AFirmaXSSConstants {
	
	/** Identificador del perfil XSS de AFirma*/
	public static final transient String profile_id = "urn:afirma:dss:1.0:profile:XSS";
	
	public class UpgradeMode {
		
		/** Identificador que determina que la operación de upgrade no se verifica el certificado*/
		public static final transient String no_cert_validation = profile_id+":upgrade:NoCertificateValidation";
	
	}
	
	
	public class ResultMajorDes{
		
		/**La firma es valida */
		public static final transient String valid_signature = profile_id+":resultmajor:ValidSignature"; 
		
		/** La firma no es valida*/
		public static final transient String invalid_signature= profile_id+":resultmajor:InvalidSignature";
		
		/** El estado de la firma es ideterminado */
		
		public static final transient String unknown_signature_status = profile_id+":resultmajor:UnknownSignatureStatus";
	}
	
	public class ResultMinorDes {
		/** No se ha suministrado  formato de actualización */
		public static final transient String updated_format_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:UpdateSignatureTypeNotProvided";
		
		/** El elemento <dss:SignatureType> no se ha incluido en la peticion */
		public static final transient String signature_type_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:SignatureTypeNotProvided";
		
		/** El valor establecido al elemento <dss:SignatureType> no es valido o no es soportado*/
		public static final transient String signature_type_not_supported = "urn:afirma:dss:1.0:profile:XSS:resultminor:SignatureTypeNotSupported";
		
		/** El valor establecido al elemento <dss:SignatureForm> no es valido o no es soportado*/
		public static final transient String signature_form_not_supported = "urn:afirma:dss:1.0:profile:XSS:resultminor:SignatureFormNotSupported";
	
		/** El valor establecido al elemento <afxp:HashAlgorithm> no es valido o no es soportado */
		public static final transient String hash_algorithm_not_supported = "urn:afirma:dss:1.0:profile:XSS:resultminor:HashAlgorithmNotSupported";
		
		/** No se ha incluido en la petición el elemento <dss:ClaimedIdentity>*/
		public static final transient String identity_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:ClaimedIdentityNotProvided";
		
		/** El valor del elemento <dss:ClaimedIdentity> no es valido, la aplicación no esta dada de alta en la plataforma*/
		public static final transient String invalid_identity = "urn:afirma:dss:1.0:profile:XSS:resultminor:UnauthorizedClaimedIdentity";
	
		/** No se ha incluido en la petición el elemento <afxp:TransactionId>*/
		public static final transient String transaction_id_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:TransactionIdNotProvided";
		
		/** Alguno de los elementos contenidos en el <dss:InputDocument> no es valido o no está soportado*/
		public static final transient String inputdocument_not_supported = "urn:afirma:dss:1.0:profile:XSS:resultminor:InputDocumentNotSupported";
		
		/** No es soportado el elemento <arch:ArchiveIdentifier> dentro del elemento <dss:SignatureOther> */
		public static final transient String sig_id_not_supported = "urn:afirma:dss:1.0:profile:XSS:resultminor:SignatureArchiveIdentifierNotSupported";
		
		/** El atributo "type" del elemento <dss:ReturnUpdatedSignature> no es valido*/
		public static final transient String invalid_update_type = "urn:afirma:dss:1.0:profile:XSS:resultminor:IncorrectUpdateSignatureType";
		
		/** El contenido del elemento <afxp:UpdatedSignatureMode> no es valido*/
		public static final transient String invalid_updated_mode = "urn:afirma:dss:1.0:profile:XSS:resultminor:UpdatedModeNotSupported";
		
		/** El contenido del elemento <afxp:XMLSignatureMode> no es valido*/
		public static final transient String invalid_XMLSignature_mode = "urn:afirma:dss:1.0:profile:XSS:resultminor:XMLSignatureModeNotSupported";
		
		/** El contenido del elemento <afxp:XMLSignatureMode> no es valido*/
		public static final transient String signature_not_provided = "urn:afirma:dss:1.0:profile:XSS:resultminor:SignatureNotProvided";
		
		/** Errores del certificado firmante */
		public class SignerCertificate{
			
			public static final transient String invalid_issuer_certificate = profile_id +":resultminor:SignerCertificate:IncorrectIssuer";
			
			public static final transient String invalid_signature = profile_id +":resultminor:SignerCertificate:InvalidSignature";

			public static final transient String certificate_not_valid_yet = profile_id +":resultminor:SignerCertificate:NotValidYet";

			public static final transient String expired_certificate = profile_id +":resultminor:SignerCertificate:Expired";

			public static final transient String revoked_certificate = profile_id +":resultminor:SignerCertificate:Revoked";

			public static final transient String unknown_certificate_status = profile_id +":resultminor:SignerCertificate:UnknownStatus";

			public static final transient String incompleted_process = profile_id +":resultminor:SignerCertificate:IncompletedProcess";

		}
		
		public class SignatureTimeStamp{
			
			public static final transient String invalid_signature = profile_id +":resultminor:SignatureTimeStamp:InvalidSignature";
			
			public static final transient String mismatched_signed_data = profile_id +":resultminor:SignatureTimeStamp:MismatchedSignedData";

			public static final transient String invalid_format = profile_id +":resultminor:SignatureTimeStamp:IncorrectFormat";

			public static final transient String invalid_cert_signature = profile_id +":resultminor:SignatureTimeStamp:Certificate:InvalidSignature";

			public static final transient String invalid_issuer_certificate = profile_id +":resultminor:SignatureTimeStamp:Certificate:IncorrectIssuer";

			public static final transient String certificate_not_valid_yet = profile_id +":resultminor:SignatureTimeStamp:Certificate:NotValidYet";

			public static final transient String expired_certificate = profile_id +":resultminor:SignatureTimeStamp:Certificate:Expired";

			public static final transient String revoked_certificate = profile_id +":resultminor:SignatureTimeStamp:Certificate:Revoked";

			public static final transient String unknown_certificate_status = profile_id +":resultminor:SignatureTimeStamp:Certificate:UnknownStatus";

			public static final transient String incompleted_cert_process = profile_id +":resultminor:SignatureTimeStamp:Certificate:IncompletedProcess";
		
		
		}
		public static final transient String unknown_key_info = profile_id+":resultminor:KeyInfo:SerialNumberNotIncluded";

		public static final transient String invalid_signature_core = profile_id+":resultminor:SignatureCore:InvalidSignature";

		public static final transient String invalid_signature_reference = profile_id+":resultminor:SignatureCore:InvalidReference";

		public static final transient String mismatched_key_info =profile_id+":resultminor:KeyInfo:InvalidReference";

		public static final transient String  signing_certificate_not_incluided = profile_id+":resultminor:KeyInfo:SigningCertificateNotIncluded";

		public static final transient String invalid_period = profile_id+":resultminor:SigningTime:InvalidPeriod";

		public static final transient String invalid_not_signer_certificate = profile_id+":resultminor:InvalidNotSignerCertificate";
	
	}
	
	
	public class SignatureTypeDes{
		public static final transient String pkcs7 = "urn:ietf:rfc:2315";
	
		public static final transient String ODF ="urn:afirma:dss:1.0:profile:XSS:forms:ODF";
		
		public static final transient String PDF ="urn:afirma:dss:1.0:profile:XSS:forms:PDF";
	}
	
	public class SignatureFormDes{
		
		/** Formato de firma ODF con sello de tiempo, expuesto en el perfil XSS de AFirma */
		public static final transient String ODF_With_TST = "urn:afirma:dss:1.0:profile:XSS:forms:ODFWithTST";
		
		/** Formato de firma CMS con sello de tiempo, expuesto en el perfil XSS de AFirma */
		public static final transient String CMS_With_TST = "urn:afirma:dss:1.0:profile:XSS:forms:CMSWithTST";
		
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
	
	public class XMLSignatureMode{
		
		/** Modo de firma XML enveloping */
		public static final transient String enveloping_mode = "urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:EnvelopingMode";
		
		/** Modo de firma XML enveloped */
		public static final transient String enveloped_mode = "urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:EnvelopedMode";
		
		/** Modo de firma XML detached */
		public static final transient String detached_mode ="urn:afirma:dss:1.0:profile:XSS:XMLSignatureMode:DetachedMode";
		
	}
	
	public class DetailTypes{
		/** Validacion del Core*/
		public static final transient String signature_core_validation = "urn:afirma:dss:1.0:profile:XSS:detail:SignatureCore";
		
		/** Validación del formato de la firma*/
		public static final transient String signature_format = "urn:afirma:dss:1.0:profile:XSS:detail:SignatureFormat";
		
		
		/** Algoritmo de resumen utilizado para realizar el SignatureTimeStamp */
		public static final transient String message_hash_alg = "urn:afirma:dss:1.0:profile:XSS:detail:MessageHashAlg";
		
		/** Validación de un certificado */
		public static final transient String certificate_validation = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate";
		
		/** Validatión del certificado firmante */
		public static final transient String signer_cert_validation = "urn:afirma:dss:1.0:profile:XSS:detail:type:SignerCertificateValidation";
		
		public static final transient String key_info_validation = profile_id +":detail:type:KeyInfoValidation";

		public static final transient String signing_time_validation = profile_id+":detail:type:KeyInfoValidation";

		public static final transient String signature_timestamp_validation = profile_id+":detail:type:SignatureTimeStampValidation";

	}
	
	public class DetailCodes{
		
		public static final transient String valid_signature_core = "urn:afirma:dss:1.0:profile:XSS:detail:SignatureCore:code:ValidSignature";
		
		public static final transient String invalid_signature_core = "urn:afirma:dss:1.0:profile:XSS:detail:SignatureCore:code:InvalidSignature";
		
		public static final transient String valid_signature_format	="urn:afirma:dss:1.0:profile:XSS:detail:SignatureFormat:code:ValidFormat";
		
		public static final transient String invalid_signature_format="urn:afirma:dss:1.0:profile:XSS:detail:SignatureFormat:code:IncorrectFormat";
		
		public static final transient String unknown_message_hash_alg = "urn:afirma:dss:1.0:profile:XSS:detail:MessageHashAlg:code:Unknown";
	
		public static final transient String invalid_signature_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:InvalidSignature";
		
		public static final transient String valid_signature_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:ValidSignature";
		
		public static final transient String invalid_issuer_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:IncorrectIssuer";
		
		public static final transient String cert_not_valid_yet ="urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:NotValidYet";
		
		public static final transient String invalid_extension_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:InvalidExtension";
		
		public static final transient String expired_cert ="urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:Expired";
		
		public static final transient String revoked_cert ="urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:Revoked";
		
		public static final transient String unknown_cert_status = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:UnknownStatu";
		
		public static final transient String valid_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:Valid";
		
		public static final transient String valid_period_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:ValidPeriod";
		
		public static final transient String valid_extension_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:ValidExtension";

		public static final transient String incompleted_process_cert = "urn:afirma:dss:1.0:profile:XSS:detail:Certificate:code:IncompletedProcess";
		
	}
	
	public static final transient String signaturetimestamp_property_id="urn:afirma:dss:1.0:profile:XSS:SignatureProperty:SignatureTimeStamp";
}
