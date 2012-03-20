/** 
 * <p>Fichero: WebServicesAvailable.java</p>
 * <p>Descripción: </p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 22-jun-2006</p>
 * @author SEJLHA
 * @version 1.0
 * 
 */
package com.telventi.afirma.wsclient;

import com.telventi.afirma.wsclient.utils.Base64Coder;

/**
 * @author SEJLHA
 * 
 */
public interface WebServicesAvailable {
	public Base64Coder base64Coder = new Base64Coder();

	// Servicios Web disponibles en el cliente Web Service
	// 1) de Custodia
	public final String custodyDocumentWebServiceName = "AlmacenarDocumento";

	public final String getDocumentContentWebServiceName = "ObtenerContenidoDocumento";

	public final String getDocumentContentByIdWebServiceName = "ObtenerContenidoDocumentoId";

	public final String getDocumentIdWebServiceName = "ObtenerIdDocumento";

	public final String deleteDocumentContentWebServiceName = "EliminarContenidoDocumento";

	public final String setExternalReferenceWebServiceName = "ActualizarReferencia";

	public final String getTransactionsByExternalReferenceWebServiceName = "ObtenerTransaccionesReferencia";

	public final String getTransactionsByDateWebServiceName = "ObtenerTransaccionesPorFecha";

	public final String getTransactionsWebServiceName = "ObtenerTransacciones";

	public final String getESignatureWebServiceName = "ObtenerFirmaTransaccion";

	public final String getSignaturesBlockWebServiceName = "ObtenerBloqueFirmas";

	// 2) de Firma
	public final String signatureValidationWebServiceName = "ValidarFirma";

	public final String serverSignatureWebServiceName = "FirmaServidor";

	public final String serverSignatureCoSignWebServiceName = "FirmaServidorCoSign";

	public final String serverSignatureCounterSignWebServiceName = "FirmaServidorCounterSign";

	public final String threePhasesUserSignatureF1WebServiceName = "FirmaUsuario3FasesF1";

	public final String threePhasesUserSignatureF1CoSignWebServiceName = "FirmaUsuario3FasesF1CoSign";

	public final String threePhasesUserSignatureF1CounterSignWebServiceName = "FirmaUsuario3FasesF1CounterSign";

	public final String threePhasesUserSignatureF3WebServiceName = "FirmaUsuario3FasesF3";

	public final String twoPhasesUserSignatureF2WebServiceName = "FirmaUsuario2FasesF2";

	public final String blockUserSignatureF1WebServiceName = "FirmaUsuarioBloquesF1";

	public final String blockUserSignatureF3WebServiceName = "FirmaUsuarioBloquesF3";

	public final String blockSignatureFullValidacionWebServiceName = "ValidarFirmaBloquesCompleto";

	public final String blockSignatureDocumentValidacionWebServiceName = "ValidarFirmaBloquesDocumento";

	public final String getDocIdSignaturesBlockWebServiceName = "ObtenerIdDocumentosBloqueFirmas";

	public final String getDocIdSignaturesBlockBackwardsWebServiceName = "ObtenerIdDocumentosBloqueFirmasBackwards";

	public final String getInformationSignaturesBlockWebServiceName = "ObtenerInformacionBloqueFirmas";

	public final String getInformationSignaturesBlockBackwardsWebServiceName = "ObtenerInformacionBloqueFirmasBackwards";

	public final String getCompleteInfoSignaturesBlockWebServiceName = "ObtenerInfoCompletaBloqueFirmas";

	// 3) del Modulo de Validacion
	public final String certificateValidationWebServiceName = "ValidarCertificado";

	public final String getCertificateInfoWebServiceName = "ObtenerInfoCertificado";

	// 4) de Custody
	public final String custodyDocumentWebServiceNameEng = "StoreDocument";

	public final String getDocumentContentWebServiceNameEng = "GetDocumentContent";

	public final String getDocumentContentByIdWebServiceNameEng = "GetDocumentContentByDocId";

	public final String getDocumentIdWebServiceNameEng = "GetDocId";

	public final String deleteDocumentContentWebServiceNameEng = "DeleteDocumentContent";

	public final String setExternalReferenceWebServiceNameEng = "SetExternalReference";

	public final String getTransactionsByExternalReferenceWebServiceNameEng = "GetTransactionsByRefId";

	public final String getTransactionsByDateWebServiceNameEng = "GetTransactionsByDates";

	public final String getTransactionsWebServiceNameEng = "GetTransactionsByAppId";

	public final String getESignatureWebServiceNameEng = "GetESignature";

	public final String getSignaturesBlockWebServiceNameEng = "GetBlock";

	// 5) de Signature
	public final String signatureValidationWebServiceNameEng = "SignatureValidation";

	public final String serverSignatureWebServiceNameEng = "ServerSignature";

	public final String serverSignatureCoSignWebServiceNameEng = "ServerSignatureCoSign";

	public final String serverSignatureCounterSignWebServiceNameEng = "ServerSignatureCounterSign";

	public final String threePhasesUserSignatureF1WebServiceNameEng = "ThreePhaseUserSignatureF1";

	public final String threePhasesUserSignatureF1CoSignWebServiceNameEng = "ThreePhaseUserSignatureF1CoSign";

	public final String threePhasesUserSignatureF1CounterSignWebServiceNameEng = "ThreePhaseUserSignatureF1CounterSign";

	public final String threePhasesUserSignatureF3WebServiceNameEng = "ThreePhaseUserSignatureF3";

	public final String twoPhasesUserSignatureF2WebServiceNameEng = "TwoPhaseUserSignatureF2";

	public final String blockUserSignatureF1WebServiceNameEng = "BlockUserSignatureF1";

	public final String blockUserSignatureF3WebServiceNameEng = "BlockUserSignatureF3";

	public final String blockSignatureFullValidacionWebServiceNameEng = "BlockSignatureValidationComplete";

	public final String blockSignatureDocumentValidacionWebServiceNameEng = "BlockSignatureValidationDocument";

	public final String getDocIdSignaturesBlockWebServiceNameEng = "BlockSignatureGetDocIds";

	public final String getDocIdSignaturesBlockBackwardsWebServiceNameEng = "BlockSignatureGetDocIdsBackwards";

	public final String getInformationSignaturesBlockWebServiceNameEng = "BlockSignatureGetInfo";

	public final String getInformationSignaturesBlockBackwardsWebServiceNameEng = "BlockSignatureGetInfoBackwards";

	public final String getCompleteInfoSignaturesBlockWebServiceNameEng = "BlockSignatureGetCompleteInfo";

	// 6) del Modulo de Validation
	public final String certificateValidationWebServiceNameEng = "ValidateCertificate";

	public final String getCertificateInfoWebServiceNameEng = "GetInfoCertificate";
	
	// 7) del Modulo de OASIS-DSS
	public final String service_DSSSign = "DSSAfirmaSign";
	
	public final String operation_DSSSign="sign"; 
	
	public final String service_DSSVerify = "DSSAfirmaVerify";
	
	public final String operation_DSSVerify="verify";
	
	public final String service_DSSArchiveSubmit = "DSSAfirmaArchiveSubmit";
	
	public final String operation_DSSArchiveSubmit="archiveSubmit";
	
	public final String service_DSSArchiveRetrieval = "DSSAfirmaArchiveRetrieval";
	
	public final String operation_DSSArchiveRetrieval="archiveRetrieval";
	
	public final String DEFAULT_SIGNATURE_FORMAT = "CMS";

	public final String DEFAULT_HASH_ALGORITHM = "SHA1";

	public final int DEFAULT_VALIDACION_MODE = 0;

	public final boolean DEFAULT_GET_CERTIFICATE_INFO = false;
	
	public final String DEFAULT_UPDATE_SIGNATURE_FORMAT = null;
	
	// 8) OCSP
	
	public final String serviceOCSP = "serviceOCSP";

	
	public static int DEFAULT_INCLUDE_DOCUMENT = 0;

	// Mensajes SOAP de Petición
	// 1) de Custodia
	public static final String CustodyDocumentRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>AlmacenarDocumento</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<documento/>"
			+ "<nombreDocumento/>"
			+ "<tipoDocumento/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String GetDocumentContentRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerContenidoDocumento</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetDocumentContentIdRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerContenidoDocumentoId</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idDocumento/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetDocumentIdRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerIdDocumento</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String DeleteDocumentContentRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>EliminarContenidoDocumento</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idDocumento/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String SetExternalReferenceRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ActualizarReferencia</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<referencia/>"
			+ "</parametros>" + "</mensajeEntrada>";

	public static final String GetTransactionsByExternalReferenceRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerTransaccionesReferencia</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idReferencia/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetTransactionsByDateRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerTransaccionesPorFecha</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<fechaInicial/>"
			+ "<fechaFinal/>"
			+ "</parametros>" + "</mensajeEntrada>";

	public static final String GetTransactionsRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerTransacciones</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String GetESignatureRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerFirmaTransaccion</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetSignaturesBlockRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/custodia\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/ws.xsd\">"
			+ "<peticion>ObtenerBloqueFirmas</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	// 2) de Firma
	public static final String SignatureValidationRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ValidarFirma</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<firmaElectronica/>"
			+ "<formatoFirma/>"
			+ "<hash/>"
			+ "<algoritmoHash/>"
			+ "<datos/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String ServerSignatureRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaServidor</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idDocumento/>"
			+ "<firmante/>"
			+ "<idReferencia></idReferencia>"
			+ "<algoritmoHash/>"
			+ "<formatoFirma/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";
	
	public static final String ServerSignatureIncDocRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
		+ "<peticion>FirmaServidor</peticion>"
		+ "<versionMsg>1.0</versionMsg>"
		+ "<parametros>"
		+ "<idAplicacion/>"
		+ "<documento/>"
		+"<nombreDocumento/>"
		+"<tipoDocumento/>"
		+ "<firmante/>"
		+ "<idReferencia></idReferencia>"
		+ "<algoritmoHash/>"
		+ "<formatoFirma/>"
		+ "</parametros>"
		+ "</mensajeEntrada>";

	public static final String ServerSignatureCoSignRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaServidorCoSign</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<firmante/>"
			+ "<idReferencia></idReferencia>"
			+ "<algoritmoHash/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String ServerSignatureCounterSignRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaServidorCounterSign</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<firmante/>"
			+ "<idReferencia></idReferencia>"
			+ "<algoritmoHash/>"
			+ "<firmanteObjetivo/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String ThreePhasesUserSignatureF1Request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuario3FasesF1</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idDocumento/>"
			+ "<algoritmoHash/>"
			+ "</parametros>" + "</mensajeEntrada>";

	public static final String ThreePhasesUserSignatureF1CoSignRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuario3FasesF1CoSign</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<algoritmoHash/>"
			+ "</parametros>" + "</mensajeEntrada>";

	public static final String ThreePhasesUserSignatureF1CounterSignRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuario3FasesF1CounterSign</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String ThreePhasesUserSignatureF3Request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuario3FasesF3</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<firmaElectronica/>"
			+ "<certificadoFirmante/>"
			+ "<formatoFirma/>"
			+ "<idReferencia></idReferencia>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String TwoPhasesUserSignatureF2Request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuario2FasesF2</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<firmaElectronica/>"
			+ "<certificadoFirmante/>"
			+ "<idReferencia></idReferencia>"
			+ "<formatoFirma/>"
			+ "<documento/>"
			+ "<nombreDocumento/>"
			+ "<tipoDocumento/>"
			+ "<algoritmoHash/>"
			+ "<custodiarDocumento/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String BlockUserSignatureF1Request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuarioBloquesF1</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<firmante/>"
			+ "<idDocumentos/>"
			+ "<idTransacciones/>"
			+ "<documentosMultifirma/>"
			+ "<algoritmoHash/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String BlockUserSignatureF3Request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>FirmaUsuarioBloquesF3</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "<firmaElectronica/>"
			+ "<certificadoFirmante/>"
			+ "<formatoFirma/>"
			+ "<idReferencia></idReferencia>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String BlockSignatureFullValidacionRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ValidarFirmaBloquesCompleto</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<firmaElectronica/>"
			+ "<bloqueFirmas/>"
			+ "<formatoFirma/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String BlockSignatureDocumentValidacionRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada targetNamespace=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ValidarFirmaBloquesDocumento</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<firmaElectronica/>"
			+ "<documento/>"
			+ "<idDocumento/>"
			+ "<formatoFirma/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetDocIdSignaturesBlockRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ObtenerIdDocumentosBloqueFirmas</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetDocIdSignaturesBlockBackwardsRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ObtenerIdDocumentosBloqueFirmasBackwards</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<bloqueFirmas/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetInformationSignaturesBlockRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ObtenerInformacionBloqueFirmas</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetInformationSignaturesBlockBackwardsRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ObtenerInformacionBloqueFirmasBackwards</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<bloqueFirmas/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	public static final String GetCompleteInfoSignaturesBlockRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/firma\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/ws.xsd\">"
			+ "<peticion>ObtenerInfoCompletaBloqueFirmas</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<idTransaccion/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	// 3) del Modulo de Validacion
	public static final String CertificateValidationRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/validacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/ws.xsd\">"
			+ "<peticion>ValidarCertificado</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<certificado/>"
			+ "<modoValidacion/>"
			+ "<obtenerInfo/>" + "</parametros>" + "</mensajeEntrada>";

	public static final String GetCertificateInfoRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<mensajeEntrada xmlns=\"https://afirmaws/ws/validacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/ws.xsd\">"
			+ "<peticion>ObtenerInfoCertificado</peticion>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parametros>"
			+ "<idAplicacion/>"
			+ "<certificado/>"
			+ "</parametros>"
			+ "</mensajeEntrada>";

	
	
	
//	 Mensajes SOAP de Petición
	// 1) de Custodia
	public static final String CustodyDocumentRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>StoreDocument</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<document/>"
			+ "<documentName/>"
			+ "<documentType/>" + "</parameters>" + "</inputMessage>";

	public static final String GetDocumentContentRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetDocumentContent</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetDocumentContentIdRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetDocumentContentByDocId</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<documentId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetDocumentIdRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetDocId</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String DeleteDocumentContentRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>DeleteDocumentContent</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<documentId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String SetExternalReferenceRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>SetExternalReference</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<reference/>"
			+ "</parameters>" + "</inputMessage>";

	public static final String GetTransactionsByExternalReferenceRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetTransactionsByRefId</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<reference/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetTransactionsByDateRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetTransactionsByDates</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<startDate/>"
			+ "<endDate/>"
			+ "</parameters>" + "</inputMessage>";

	public static final String GetTransactionsRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetTransactionsByAppId</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>" + "</parameters>" + "</inputMessage>";

	public static final String GetESignatureRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetESignature</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetSignaturesBlockRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/custody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mcustodia/wseng.xsd\">"
			+ "<request>GetBlock</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	// 2) de Firma
	public static final String SignatureValidationRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>SignatureValidation</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<eSignature/>"
			+ "<eSignatureFormat/>"
			+ "<hash/>"
			+ "<hashAlgorithm/>"
			+ "<data/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String ServerSignatureRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ServerSignature</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<documentId/>"
			+ "<signer/>"
			+ "<reference></reference>"
			+ "<hashAlgorithm/>"
			+ "<eSignatureFormat/>"
			+ "</parameters>"
			+ "</inputMessage>";
	
	public static final String ServerSignatureIncDocRequestEng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
		+ "<request>ServerSignature</request>"
		+ "<versionMsg>1.0</versionMsg>"
		+ "<parameters>"
		+ "<applicationId/>"
		+ "<document/>"
		+ "<documentName/>"
		+ "<documentType/>"
		+ "<signer/>"
		+ "<reference></reference>"
		+ "<hashAlgorithm/>"
		+ "<eSignatureFormat/>"
		+ "</parameters>"
		+ "</inputMessage>";

	public static final String ServerSignatureCoSignRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ServerSignatureCoSign</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<signer/>"
			+ "<reference></reference>"
			+ "<hashAlgorithm/>" + "</parameters>" + "</inputMessage>";

	public static final String ServerSignatureCounterSignRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ServerSignatureCounterSign</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<signer/>"
			+ "<reference></reference>"
			+ "<hashAlgorithm/>"
			+ "<targetSigner/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String ThreePhasesUserSignatureF1Requesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ThreePhaseUserSignatureF1</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<documentId/>"
			+ "<hashAlgorithm/>"
			+ "</parameters>" + "</inputMessage>";

	public static final String ThreePhasesUserSignatureF1CoSignRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ThreePhaseUserSignatureF1CoSign</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<hashAlgorithm/>"
			+ "</parameters>" + "</inputMessage>";

	public static final String ThreePhasesUserSignatureF1CounterSignRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ThreePhaseUserSignatureF1CounterSign</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String ThreePhasesUserSignatureF3Requesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>ThreePhaseUserSignatureF3</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<eSignature/>"
			+ "<signerCertificate/>"
			+ "<eSignatureFormat/>"
//			+ "<updateSignatureFormat/>"
			+ "<reference></reference>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String TwoPhasesUserSignatureF2Requesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>TwoPhaseUserSignatureF2</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<eSignature/>"
			+ "<signerCertificate/>"
			+ "<reference></reference>"
			+ "<eSignatureFormat/>"
			+ "<document/>"
			+ "<documentName/>"
			+ "<documentType/>"
			+ "<hashAlgorithm/>"
			+ "<storeDocument/>"
//			+ "<updateSignatureFormat/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String BlockUserSignatureF1Requesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockUserSignatureF1</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<signer/>"
			+ "<idDocuments/>"
			+ "<idTransactions/>"
			+ "<multiSignDocuments/>"
			+ "<hashAlgorithm/>" + "</parameters>" + "</inputMessage>";

	public static final String BlockUserSignatureF3Requesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockUserSignatureF3</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "<eSignature/>"
			+ "<signerCertificate/>"
			+ "<eSignatureFormat/>"
			+ "<reference></reference>"
//			+ "<updateSignatureFormat/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String BlockSignatureFullValidacionRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureValidationComplete</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<eSignature/>"
			+ "<block/>"
			+ "<eSignatureFormat/>" + "</parameters>" + "</inputMessage>";

	public static final String BlockSignatureDocumentValidacionRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage targetNamespace=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureValidationDocument</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<eSignature/>"
			+ "<document/>"
			+ "<documentId/>"
			+ "<eSignatureFormat/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetDocIdSignaturesBlockRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureGetDocIds</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetDocIdSignaturesBlockBackwardsRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureGetDocIdsBackwards</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<block/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetInformationSignaturesBlockRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureGetInfo</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetInformationSignaturesBlockBackwardsRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureGetInfoBackwards</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<block/>"
			+ "</parameters>"
			+ "</inputMessage>";

	public static final String GetCompleteInfoSignaturesBlockRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/signature\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mfirma/wseng.xsd\">"
			+ "<request>BlockSignatureGetCompleteInfo</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<transactionId/>"
			+ "</parameters>"
			+ "</inputMessage>";

	// 3) del Modulo de Validacion
	public static final String CertificateValidationRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/validation\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/wseng.xsd\">"
			+ "<request>ValidateCertificate</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<certificate/>"
			+ "<validationMode/>"
			+ "<getInfo/>" + "</parameters>" + "</inputMessage>";

	public static final String GetCertificateInfoRequesteng = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<inputMessage xmlns=\"https://afirmaws/ws/validation\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/wseng.xsd\">"
			+ "<request>GetInfoCertificate</request>"
			+ "<versionMsg>1.0</versionMsg>"
			+ "<parameters>"
			+ "<applicationId/>"
			+ "<certificate/>"
			+ "</parameters>"
			+ "</inputMessage>";

	
	public void run(String[] args);

	/**
	 * Método que obtiene de la entrada la información necesaria para poder
	 * ejecutar la lógica del cliente
	 * 
	 * @param args
	 *            Parámetros de entrada a la aplicación.
	 */
	public void fillParameters(String[] args);
}
