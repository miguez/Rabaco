package com.telventi.afirma.wsclient.dss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.core.v10.Base64Data;
import com.telventi.afirma.mschema.dss.core.v10.Base64Signature;
import com.telventi.afirma.mschema.dss.core.v10.ClaimedIdentity;
import com.telventi.afirma.mschema.dss.core.v10.Document;
import com.telventi.afirma.mschema.dss.core.v10.DocumentHash;
import com.telventi.afirma.mschema.dss.core.v10.DocumentWithSignature;
import com.telventi.afirma.mschema.dss.core.v10.IncludeEContent;
import com.telventi.afirma.mschema.dss.core.v10.InputDocuments;
import com.telventi.afirma.mschema.dss.core.v10.KeySelector;
import com.telventi.afirma.mschema.dss.core.v10.Name;
import com.telventi.afirma.mschema.dss.core.v10.OptionalInputs;
import com.telventi.afirma.mschema.dss.core.v10.OptionalOutputs;
import com.telventi.afirma.mschema.dss.core.v10.Other;
import com.telventi.afirma.mschema.dss.core.v10.Result;
import com.telventi.afirma.mschema.dss.core.v10.ReturnUpdatedSignature;
import com.telventi.afirma.mschema.dss.core.v10.SignRequest;
import com.telventi.afirma.mschema.dss.core.v10.SignResponse;
import com.telventi.afirma.mschema.dss.core.v10.SignatureObject;
import com.telventi.afirma.mschema.dss.core.v10.SignaturePtr;
import com.telventi.afirma.mschema.dss.core.v10.SignatureType;
import com.telventi.afirma.mschema.dss.core.v10.UpdatedSignature;
import com.telventi.afirma.mschema.dss.core.v10.VerifyRequest;
import com.telventi.afirma.mschema.dss.core.v10.VerifyResponse;
import com.telventi.afirma.mschema.dss.profiles.ades.v10.SignatureForm;
import com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10.AdditionalSignatureInfo;
import com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10.ArchiveIdentifierType;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.AdditionalDocumentInfo;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.DocumentArchiveId;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.HashAlgorithm;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.ReferenceId;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.ReturnReadableCertificateInfo;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.SignatureArchiveId;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.TargetSigner;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.UpdatedSignatureMode;
import com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10.XMLSignatureMode;
import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveRetrievalRequest;
import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveRetrievalResponse;
import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveSubmitRequest;
import com.telventi.afirma.mschema.dss.profiles.vr.draft.ReturnVerificationReport;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.AdditionalReportOption;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.CounterSignature;
import com.telventi.afirma.mschema.dss.profiles.xss.draft.ParallelSignature;
import com.telventi.afirma.mschema.xmldsig.core.DigestMethod;
import com.telventi.afirma.mschema.xmldsig.core.DigestValue;
import com.telventi.afirma.mschema.xmldsig.core.KeyInfo;
import com.telventi.afirma.mschema.xmldsig.core.X509Data;
import com.telventi.afirma.wsclient.utils.Base64Coder;
import com.telventi.afirma.wsclient.utils.DigestManager;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;
import com.telventi.afirma.wsclient.utils.UtilsSignature;

public class DSSUtils {
	
	public static boolean isXML(byte[] file){
		String fileStr = new String(file);
		return (fileStr.startsWith("<") && fileStr.endsWith(">"));
	}
	
	public static String getUpdateVerifyRequest(String aplicationId, String upgradedFormat, String signaturePath, String transactionId,String certificatePath, int modo) throws Exception{
		VerifyRequest request = new VerifyRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		//recogemos la firma
		SignatureObject signatureObj = new SignatureObject();
		if(transactionId != null){
			Other other = new Other();
			SignatureArchiveId identifier = new SignatureArchiveId();
			identifier.setArchiveId(transactionId);
			other.addOtherItems(identifier);
			signatureObj.setOther(other);
		}else{
			byte[] signature = UtilsFileSystem.readFileFromFileSystem(signaturePath);
			boolean xml = false;
			try{
				DSSUtils.getDocument(new String(signature));
				xml=true;
			}catch(Exception e){}
			boolean enveloping = false;
			if(xml)
				enveloping = isXMLEnvelopingSignature(signature);
			if(!xml){
				Base64Signature b64Signature = new Base64Signature();
				b64Signature.setSignature(signature);
				signatureObj.setB64Signature(b64Signature);
			}else{
				if(enveloping){
					org.w3c.dom.Document docSignature = getDocument(new String(signature));
					signatureObj.setXMLDSignature(getStringXml(docSignature.getDocumentElement()));
				}else{
					//firma enveloped o detached
					SignaturePtr signaturePtr = new SignaturePtr();
					String docId = ""+System.currentTimeMillis();
					signaturePtr.setWhichDocument(docId);
					signatureObj.setSignaturePtr(signaturePtr);
					InputDocuments inputDoc = new InputDocuments();
					Document docWithSign = new Document();
					docWithSign.setId(docId);
					docWithSign.setBase64XML(signature);
					inputDoc.addItem(docWithSign);
					request.setInputDocuments(inputDoc);
				}
			}
		}
		
		if(certificatePath != null){
			byte[] certificate = UtilsFileSystem.readFileFromFileSystem(certificatePath);
			TargetSigner signer = new TargetSigner();
			signer.setTargetSigner(certificate);
			optional.addOptionalInputsItems(signer);
		}
		
		optional.addOptionalInputsItems(getReturnUpdatedSignature(upgradedFormat));
		
		if(modo==1){
			UpdatedSignatureMode updatedMode = new UpdatedSignatureMode();
			updatedMode.setMode(DSSConstants.no_cert_validation);
			optional.addOptionalInputsItems(updatedMode);
		}
		request.setSignatureObject(signatureObj);
		request.setOptionalInputs(optional);
		return getXML(request);
	}
	public static String getArchiveRetrivalRequest(String aplicationId, String transactionId) throws Exception{
		ArchiveRetrievalRequest request = new ArchiveRetrievalRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_archive);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de transacción
		request.setArchiveIdentifier(transactionId);
		//
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		request.setOptionalInputs(optional);
		return getXML(request);
	}
	public static String getArchiveRetrivalRequestByRefId(String appId, String externalReference) throws Exception {
		ArchiveRetrievalRequest request = new ArchiveRetrievalRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_archive);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de transacción
		request.setArchiveIdentifier(externalReference);
		//
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(appId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		
		//Tipo de identificador 
		ArchiveIdentifierType type = new ArchiveIdentifierType();
		type.setType("urn:afirma:dss:1.0:profile:archive:identifier:type:reference");
		optional.addOptionalInputsItems(type);
		//
		
		request.setOptionalInputs(optional);
		return getXML(request);
	}
	public static String getArchiveSubmitRequest(String aplicationId, String b64Signature, String b64Certificate,String b64File, String fileType, String fileName, String hashAlgorithm, String updateSignatureFormat, String referenceId, boolean custodiar) throws Exception{
		ArchiveSubmitRequest request = new ArchiveSubmitRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_archive);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		InputDocuments inputDoc = new InputDocuments();
		//Firma electrónica
		SignatureObject signatureObject = new SignatureObject();
		Base64Coder b64Coder = new Base64Coder();
		byte[] signature = b64Coder.decodeBase64(b64Signature.getBytes());
		boolean xml = false;
		try{
			DSSUtils.getDocument(new String(signature));
			xml=true;
		}catch(Exception e){}
		boolean enveloping = false;
		if(xml)
			enveloping = isXMLEnvelopingSignature(signature);
		if(!xml){
			Base64Signature b64Sign = new Base64Signature();
			b64Sign.setSignature(signature);
			signatureObject.setB64Signature(b64Sign);
		}else{
			if(enveloping){
				signatureObject.setXMLDSignature(new String(signature));
			}else{
				//firma enveloped o detached
				SignaturePtr signaturePtr = new SignaturePtr();
				String docId = ""+System.currentTimeMillis();
				signaturePtr.setWhichDocument(docId);
				signatureObject.setSignaturePtr(signaturePtr);
				Document doc = new Document();
				doc.setId(docId);
				doc.setBase64XML(signature);
				inputDoc.addItem(doc);
			}
		}
		//Información adicional de firma.
		AdditionalSignatureInfo signatureInfo = new AdditionalSignatureInfo();
		//---Documento
		Document doc = new Document();
		if(fileType.equalsIgnoreCase("xml")){
			doc.setBase64XML(b64Coder.decodeBase64(b64File.getBytes()));
		}else{
			Base64Data b64Data = new Base64Data();
			b64Data.setData(b64Coder.decodeBase64(b64File.getBytes()));
			doc.setBase64Data(b64Data);
		}
		inputDoc.addItem(doc);
		signatureInfo.setInputDocuments(inputDoc);
		//---Certificado
		X509Data x509Data = new X509Data();
		x509Data.setX509Certificate(b64Coder.decodeBase64(b64Certificate.getBytes()));
		signatureInfo.setCertificate(x509Data);
		//---IdReferencia
		if(referenceId != null){
			ReferenceId refId = new ReferenceId();
			refId.setReference(referenceId);
			signatureInfo.setReference(refId);
		}
		//--Algoritmo de firma
		HashAlgorithm hash = new HashAlgorithm();
		hash.setAfirmaAlgorithm(hashAlgorithm);
		signatureInfo.setHashAlgorithm(hash);
		//---opción custodia
		signatureInfo.setStoreDocument(custodiar);
		optional.addOptionalInputsItems(signatureInfo);
		//
		//Información adicional del documento
		AdditionalDocumentInfo documentInfo = new AdditionalDocumentInfo();
		documentInfo.setName(fileName);
		documentInfo.setType(fileType);
		optional.addOptionalInputsItems(documentInfo);
		//
		request.setOptionalInputs(optional);
		request.setSignatureObject(signatureObject);
		return getXML(request);
	}
	
	public static String getVerifySignatureRequest(String aplicationId,String signaturePath,int mode,String filePath,String algorithm, int reportNivel, boolean obtenerInfo, boolean includeCert, boolean includeTST) throws Exception{
		VerifyRequest request = new VerifyRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		
		//Incluimos la firma se incluya en la petición, los datos firmados, hash
		InputDocuments inputDoc = null;
		SignatureObject signatureObject = new SignatureObject();
		byte[] signature = UtilsFileSystem.readFileFromFileSystem(signaturePath);
		boolean xml = false;
		try{
			DSSUtils.getDocument(new String(signature));
			xml=true;
		}catch(Exception e){}
		
		boolean enveloping = false;
		if(xml)
			enveloping = isXMLEnvelopingSignature(signature);
		if(!xml){
			Base64Signature b64Signature = new Base64Signature();
			b64Signature.setSignature(signature);
			//b64Signature.setType(type.getType());
			signatureObject.setB64Signature(b64Signature);
		}else{
			if(enveloping){
				signatureObject.setXMLDSignature(new String(signature));
			}else{
				//firma enveloped o detached
				SignaturePtr signaturePtr = new SignaturePtr();
				String docId = ""+System.currentTimeMillis();
				signaturePtr.setWhichDocument(docId);
				signatureObject.setSignaturePtr(signaturePtr);
				if(inputDoc == null)
					inputDoc = new InputDocuments();
				Document docWithSign = new Document();
				docWithSign.setId(docId);
				docWithSign.setBase64XML(signature);
				inputDoc.addItem(docWithSign);
			}
		}
		if(mode==2 || mode ==3){
			if(inputDoc == null)
				inputDoc = new InputDocuments();
			inputDoc.addItem(getDocumentHash(filePath,algorithm));
		}
		if(!(xml && !enveloping)){
			if(mode>0){
				if(inputDoc == null)
					inputDoc = new InputDocuments();
				if(mode ==1 || mode ==3)
					inputDoc.addItem(getDocumentFile(filePath));
			}
		}
		//Comprobamos si se desea obtener el mapeo del certificado firmarmante
		if(obtenerInfo){
			ReturnReadableCertificateInfo rrci = new ReturnReadableCertificateInfo();
			optional.addOptionalInputsItems(rrci);
		}
		//
		//Comprobamos si se incluye la solicitud de información del sello de tiempo
		if(includeTST){
			AdditionalReportOption ad = new AdditionalReportOption();
			ad.setIncludeTST(true);
			optional.addOptionalInputsItems(ad);
		}
		ReturnVerificationReport rvr = new ReturnVerificationReport();
		rvr.setIncludeCertValue(includeCert);
		if(reportNivel==0){
			rvr.setReportLevel(OASISVRConstants.no_report_detail);
		}else if(reportNivel==1){
			rvr.setReportLevel(OASISVRConstants.no_report_path_detail);
		}else if(reportNivel==2){
			rvr.setReportLevel(OASISVRConstants.report_all_detail);
		}else
			throw new Exception("Invalid Report Level");
		
		optional.addOptionalInputsItems(rvr);
		//
		
		request.setInputDocuments(inputDoc);
		request.setSignatureObject(signatureObject);
		request.setOptionalInputs(optional);
		//
		return getXML(request);
	}
	
	public static String getCoServerSignatureRequest(String aplicationId,String transactionId, String signer, String referenceId,String algorithmHash) throws Exception{
		SignRequest request = new SignRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		//Firmante
		KeySelector keySelector = new KeySelector();
		KeyInfo keyInfo = new KeyInfo();
		keyInfo.setKeyName(signer);
		keySelector.setKeyInfo(keyInfo);
		optional.addOptionalInputsItems(keySelector);
		//
		//Id de referencia
		if(referenceId != null){
			ReferenceId refId = new ReferenceId();
			refId.setReference(referenceId);
			optional.addOptionalInputsItems(refId);
		}
		//
		//Algoritmo de hash
		if(algorithmHash !=null){
			HashAlgorithm algorithm = new HashAlgorithm();
			algorithm.setAfirmaAlgorithm(algorithmHash);
			optional.addOptionalInputsItems(algorithm);
		}
		//
		//Entrada correspondiente a multifirma en paralelo
		ParallelSignature parallel = new ParallelSignature();
		optional.addOptionalInputsItems(parallel);
		//
		//Incluimos la firma
		InputDocuments inputDoc = new InputDocuments();
		Other other = new Other();
		DocumentArchiveId id = new DocumentArchiveId();
		id.setArchiveId(transactionId);
		other.addOtherItems(id);
		inputDoc.addItem(other);
		//
		request.setInputDocuments(inputDoc);
		request.setOptionalInputs(optional);
		//
		return getXML(request);
	}
	public static String getCounterServerSignatureRequest(String aplicationId,String transactionId, String signer, String referenceId,String algorithmHash, String objetive) throws Exception{
		SignRequest request = new SignRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		//Firmante
		KeySelector keySelector = new KeySelector();
		KeyInfo keyInfo = new KeyInfo();
		keyInfo.setKeyName(signer);
		keySelector.setKeyInfo(keyInfo);
		optional.addOptionalInputsItems(keySelector);
		//
		//Id de referencia
		if(referenceId != null){
			ReferenceId refId = new ReferenceId();
			refId.setReference(referenceId);
			optional.addOptionalInputsItems(refId);
		}
		//
		//Algoritmo de hash
		if(algorithmHash !=null){
			HashAlgorithm algorithm = new HashAlgorithm();
			algorithm.setAfirmaAlgorithm(algorithmHash);
			optional.addOptionalInputsItems(algorithm);
		}
		//
		//Entrada correspondiente a multifirma en cascada
		CounterSignature counter = new CounterSignature();
		String docId = "CounterSignature-"+System.currentTimeMillis();
		counter.setWhichDocument(docId);
		optional.addOptionalInputsItems(counter);
		//
		
		//Incluimos la firma
		InputDocuments inputDoc = new InputDocuments();
		Other other = new Other();
		DocumentArchiveId archiveID = new DocumentArchiveId();
		archiveID.setArchiveId(transactionId);
		archiveID.setId(docId);
		other.addOtherItems(archiveID);
		inputDoc.addItem(other);
		//
		//Certificado del Firmante Objetivo
		if(objetive !=null){
			byte[] cert = UtilsFileSystem.readFileFromFileSystem(objetive);
			TargetSigner targetSigner = new TargetSigner();
			targetSigner.setTargetSigner(cert);
			optional.addOptionalInputsItems(targetSigner);
		}
		//
		request.setInputDocuments(inputDoc);
		request.setOptionalInputs(optional);
		//
		return getXML(request);
	}
	public static String getServerSignatureRequest(String aplicationId,String filePath, String signer, String referenceId,String signatureForm,String algorithmHash, String tstFormat,int modo) throws Exception{
		SignRequest request = new SignRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		//Firmante
		KeySelector keySelector = new KeySelector();
		KeyInfo keyInfo = new KeyInfo();
		keyInfo.setKeyName(signer);
		keySelector.setKeyInfo(keyInfo);
		optional.addOptionalInputsItems(keySelector);
		//
		//Id de referencia
		if(referenceId != null){
			ReferenceId refId = new ReferenceId();
			refId.setReference(referenceId);
			optional.addOptionalInputsItems(refId);
		}
		//
		//Formato de firma
		SignatureType type = getSignatureType(signatureForm,null);
		optional.addOptionalInputsItems(type);
		SignatureForm form = getSignatureForm(signatureForm,null);
		if(form !=null)
			optional.addOptionalInputsItems(form);
		//
		//Formato del TST
//		if(signatureForm.equals("XADES-T")){
//			AddTimestamp addTST = new AddTimestamp();
//			addTST.setType(tstFormat);
//			optional.addOptionalInputsItems(addTST);
//		}
		//Algoritmo de hash
		if(algorithmHash !=null){
			HashAlgorithm algorithm = new HashAlgorithm();
			algorithm.setAfirmaAlgorithm(algorithmHash);
			optional.addOptionalInputsItems(algorithm);
		}
		//
		//Información adicional del documento
		AdditionalDocumentInfo documentInfo = new AdditionalDocumentInfo();
		documentInfo.setName(UtilsFileSystem.getNameFromFilePath(filePath));
		documentInfo.setType(UtilsFileSystem.getExtensionFromFilePath(filePath));
		optional.addOptionalInputsItems(documentInfo);
		//
		//Modo de firma en XML
		if(!UtilsSignature.isASN1TypeOfSignature(signatureForm) && !UtilsSignature.isCustomType(signatureForm)){
			XMLSignatureMode xmlSignMode = new XMLSignatureMode();
			xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.detached_mode);
			if(modo == 1){
				xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.enveloping_mode);
			}else if(modo == 2){
				xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.enveloped_mode);
			} 
			optional.addOptionalInputsItems(xmlSignMode);
		}else{
			if(modo == 1){
				optional.addOptionalInputsItems(new IncludeEContent());
			}else if(modo == 2){
				throw new Exception("El modo enveloped no es valido para firma de tipo ASN1");
			}
		}
		//
		//Documento a firmar
		InputDocuments inputDoc = new InputDocuments();
		inputDoc.addItem(getDocument(filePath,documentInfo.getType()));
		//
		request.setInputDocuments(inputDoc);
		request.setOptionalInputs(optional);
		//
		return getXML(request);
	}
	
	public static String getStoreServerSignatureRequest(String aplicationId,String docId, String signer, String referenceId,String signatureForm,String algorithmHash, String tstFormat,int modo) throws Exception{
		SignRequest request = new SignRequest();
		//Perfil
		request.setProfile(DSSConstants.ProfileDes.afirma_xss);
		//OptionalInputs
		OptionalInputs optional = new OptionalInputs();
		
		//Identificador de aplicación
		ClaimedIdentity identity = new ClaimedIdentity();
		Name name = new Name();
		name.setNameIdentifier(aplicationId);
		identity.setName(name);
		optional.addOptionalInputsItems(identity);
		//
		//Firmante
		KeySelector keySelector = new KeySelector();
		KeyInfo keyInfo = new KeyInfo();
		keyInfo.setKeyName(signer);
		keySelector.setKeyInfo(keyInfo);
		optional.addOptionalInputsItems(keySelector);
		//
		//Id de referencia
		if(referenceId != null){
			ReferenceId refId = new ReferenceId();
			refId.setReference(referenceId);
			optional.addOptionalInputsItems(refId);
		}
		//
		//Formato de firma
		SignatureType type = getSignatureType(signatureForm,null);
		optional.addOptionalInputsItems(type);
		SignatureForm form = getSignatureForm(signatureForm,null);
		if(form !=null)
			optional.addOptionalInputsItems(form);
		//
		//Formato del TST
//		if(signatureForm.equals("XADES-T")){
//			AddTimestamp addTST = new AddTimestamp();
//			addTST.setType(tstFormat);
//			optional.addOptionalInputsItems(addTST);
//		}
		//Algoritmo de hash
		if(algorithmHash !=null){
			HashAlgorithm algorithm = new HashAlgorithm();
			algorithm.setAfirmaAlgorithm(algorithmHash);
			optional.addOptionalInputsItems(algorithm);
		}

		//Modo de firma en XML
		if(!UtilsSignature.isASN1TypeOfSignature(signatureForm) && !UtilsSignature.isCustomType(signatureForm)){
			XMLSignatureMode xmlSignMode = new XMLSignatureMode();
			xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.detached_mode);
			if(modo == 1){
				xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.enveloping_mode);
			}else if(modo == 2){
				xmlSignMode.setSignatureMode(DSSConstants.XMLSignatureMode.enveloped_mode);
			} 
			optional.addOptionalInputsItems(xmlSignMode);
		}else{
			if(modo == 1){
				optional.addOptionalInputsItems(new IncludeEContent());
			}else if(modo == 2){
				throw new Exception("El modo enveloped no es valido para firma de tipo ASN1");
			}
		}
		//
		//Documento a firmar
		InputDocuments inputDoc = new InputDocuments();
		Other other = new Other();
		DocumentArchiveId archiveId = new DocumentArchiveId();
		archiveId.setArchiveId(docId);
		other.addOtherItems(archiveId);
		inputDoc.addItem(other);
		//
		request.setInputDocuments(inputDoc);
		request.setOptionalInputs(optional);
		//
		return getXML(request);
	}
	public static AElement getResponse(AElement response, String xml) throws Exception{
		org.w3c.dom.Document doc = getDocument(xml);
		response.unmarshal(doc.getDocumentElement());
		return response;
	}
	public static org.w3c.dom.Document getDocument(String strXML) throws Exception{
		javax.xml.parsers.DocumentBuilderFactory dbf =javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(new ByteArrayInputStream(strXML.getBytes("UTF-8")));
	}
	
	public static byte[] getSignature(AElement response) throws Exception{
		SignatureObject signObject = null;
		OptionalOutputs optional = null;
		if(response instanceof SignResponse){
			SignResponse signResponse = (SignResponse)response;
			signObject = signResponse.getSignatureObject();
			optional = signResponse.getOptionalOutputs();
		}else if(response instanceof VerifyResponse){
			VerifyResponse verifyResponse = (VerifyResponse) response;
			optional = verifyResponse.getOptionalOutputs();
			Vector outputs = optional.getOptionalOutputsItems();
			int i=0;
			while(i<outputs.size() && signObject == null){
				if(outputs.get(i) instanceof UpdatedSignature)
					signObject = ((UpdatedSignature) outputs.get(i)).getSignatureObject();
				i++;
			}
			if(signObject == null)
				throw new Exception("No se ha encontrado elemento <ds:SignatureObject>");
		}else
			throw new Exception("No se ha encontrado elemento <ds:SignatureObject>");
		return getSignature(optional,signObject);
	}
	
	public static byte[] getSignature(ArchiveRetrievalResponse response) throws Exception{
		SignatureObject signObject = response.getSignatureObject();
		return getSignature(response.getOptionalOutputs(),signObject);
	}
	
	public static byte[] getCounterSignature(SignResponse response) throws Exception{
		UpdatedSignature updatedSignature = (UpdatedSignature) getOptionalOutput(response.getOptionalOutputs(), UpdatedSignature.class.getName());
		return getSignature(response.getOptionalOutputs(),updatedSignature.getSignatureObject());
	}
	
	public static AElement getOptionalOutput(OptionalOutputs optional, String className){
		AElement element = null;
		if(optional !=null){
			Vector v = optional.getOptionalOutputsItems();
			int i = 0;
			while (i<v.size() && element == null){
				if(v.get(i).getClass().getName().equals(className))
					element = (AElement)v.get(i);
				i++;
			}
		}
		return element;
	}
	public static String getStringXml(Node child) throws Exception{
		 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		TransformerFactory tf = TransformerFactory.newInstance();
		    
		Transformer trans = tf.newTransformer();

		trans.transform(new DOMSource(child), new StreamResult(baos));
		    
		return new String (baos.toByteArray(),"UTF-8");
		    
	}
	
	public static void printResult(Result result){
		if(result != null){
			System.out.println("Resultado del proceso:");
			if (result.getResultMajor()!=null)
				System.out.println("ResultMajor: "+result.getResultMajor());
			if (result.getResultMinor()!=null)
				System.out.println("ResultMinor: "+result.getResultMinor());
			if (result.getMessage()!=null)
				System.out.println("Message: "+result.getMessage());
		}
	}
	//-------------- METODOS PROTEGIDOS ---------------------------------------------
	
	protected static byte[] getSignature(OptionalOutputs optional,SignatureObject signObject) throws Exception{
		if(signObject !=null){
			if(signObject.getB64Signature()!=null && signObject.getB64Signature().getSignature() !=null){
				return signObject.getB64Signature().getSignature();
			}else if(signObject.getXMLDSignature()!=null){
				return signObject.getXMLDSignature().getBytes("UTF-8");
			}else if(signObject.getSignaturePtr()!=null){
				//String docId = signObject.getSignaturePtr().getWhichDocument();
				DocumentWithSignature docAndSign = (DocumentWithSignature)getOptionalOutput(optional, DocumentWithSignature.class.getName());
				if(docAndSign != null && docAndSign.getDocument()!=null){
					if(docAndSign.getDocument().getBase64Data()!= null && docAndSign.getDocument().getBase64Data().getData()!=null){
						return docAndSign.getDocument().getBase64Data().getData();
					}else if(docAndSign.getDocument().getBase64XML()!= null ){
						return docAndSign.getDocument().getBase64XML();
					}else if(docAndSign.getDocument().getEscapedXML()!= null ){
						return docAndSign.getDocument().getEscapedXML().getBytes("UTF-8");
					}else if(docAndSign.getDocument().getInlineXML()!= null && docAndSign.getDocument().getInlineXML().getContent()!=null ){
						return docAndSign.getDocument().getInlineXML().getContent().getBytes("UTF-8");
					}else{
						throw new Exception("No se encuentra la firma dentro del objeto <dss:DocumentWithSignature>");
					}
				}else{
					throw new Exception("No se encuentra el objeto <dss:DocumentWithSignature> en la respuesta");
				}
			}else{
				throw new Exception("No se encuentra la firma dentro del objeto <dss:SignatureObject>");
			}
		}else{
			throw new Exception("No se encuentra el objeto <dss:SignatureObject> en la respuesta");
		}
	}
	protected static String getXML(AElement element) throws Exception{
		StringWriter w = new StringWriter();
		element.marshal(w);
		return w.toString();
	}
	
	protected static SignatureType getSignatureType(String signatureForm, String xadesVersion) throws Exception{
		SignatureType type = new SignatureType();
		if(signatureForm ==null){
			type.setType(DSSConstants.SignatureTypeDes.cms);
		}else if(signatureForm.equalsIgnoreCase("CMS"))
				type.setType(DSSConstants.SignatureTypeDes.cms);
		else if(signatureForm.equalsIgnoreCase("CMS-T")){
			type.setType(DSSConstants.SignatureFormDes.CMS_With_TST);
		}else if(signatureForm.equalsIgnoreCase("PKCS7")){
			type.setType(DSSConstants.SignatureTypeDes.pkcs7);
		}else if(signatureForm.equalsIgnoreCase("CADES")||signatureForm.equalsIgnoreCase("CADES-BES")||
				signatureForm.equalsIgnoreCase("CADES-T")){
			type.setType(DSSConstants.SignatureTypeDes.cades);
		}else if(signatureForm.equalsIgnoreCase("XADES")||signatureForm.equalsIgnoreCase("XADES-BES")||
				signatureForm.equalsIgnoreCase("XADES-T")){
			if(xadesVersion !=null){
				if(xadesVersion.equals("1.1.1"))
					type.setType(DSSConstants.SignatureTypeDes.xades_v_1_1_1);
				else if(xadesVersion.equals("1.2.2"))
					type.setType(DSSConstants.SignatureTypeDes.xades_v_1_2_2);
				else 
					type.setType(DSSConstants.SignatureTypeDes.xades_v_1_3_2);
			}else
				type.setType(DSSConstants.SignatureTypeDes.xades_v_1_3_2);
		}else if(signatureForm.equalsIgnoreCase("XMLDSIG")){
			type.setType(DSSConstants.SignatureTypeDes.xml_dsig);
		}else if(signatureForm.equals("PDF")){
			type.setType(DSSConstants.SignatureTypeDes.PDF);
		}else if(signatureForm.equals("ODF")){
			type.setType(DSSConstants.SignatureTypeDes.ODF);
		}else if(signatureForm.equals("ODF-T")){
			type.setType(DSSConstants.SignatureFormDes.ODF_With_TST);
		}else{
			//No debería entrar
			throw new Exception("Formato de firma no soportado: "+signatureForm);
		}
		return type;
	}
	
	protected static SignatureForm getSignatureForm(String signatureForm, String xadesVersion) throws Exception{
		SignatureForm form = null;
		if(signatureForm !=null){
			if(signatureForm.equalsIgnoreCase("CADES-BES")||signatureForm.equalsIgnoreCase("XADES-BES")){
				form = new SignatureForm();
				if(xadesVersion != null){
                	if(xadesVersion.equals("1.1.1")&&signatureForm.equalsIgnoreCase("XADES-BES"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_1_1_BES);
                	else if(xadesVersion.equals("1.2.2")&&signatureForm.equalsIgnoreCase("XADES-BES"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_2_2_BES);
                	else if(xadesVersion.equals("1.3.2")&&signatureForm.equalsIgnoreCase("XADES-BES"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_3_2_BES);
                }else
                	form.setType(DSSConstants.SignatureFormDes.BES);
			}else if(signatureForm.equalsIgnoreCase("CADES-T")||signatureForm.equalsIgnoreCase("XADES-T")){
				form = new SignatureForm();
                if(xadesVersion != null){
                	if(xadesVersion.equals("1.1.1")&&signatureForm.equalsIgnoreCase("XADES-T"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_1_1_T);
                	else if(xadesVersion.equals("1.2.2")&&signatureForm.equalsIgnoreCase("XADES-T"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_2_2_T);
                	else if(xadesVersion.equals("1.3.2")&&signatureForm.equalsIgnoreCase("XADES-T"))
                		form.setType(DSSConstants.SignatureFormDes.XAdES_1_3_2_T);
                }else
                	form.setType(DSSConstants.SignatureFormDes.ES_T);
			}/*else if(signatureForm.equals("CMS-T")){
				form = new SignatureForm();
				form.setType(DSSConstants.SignatureFormDes.CMS_With_TST);
			}*/
		}
		return form;
	}
	
	protected static Document getDocument(String filePath, String extension){
		Document doc = new Document();
		byte[] content = UtilsFileSystem.readFileFromFileSystem(filePath);
		if(extension != null && extension.equalsIgnoreCase("xml")){
			doc.setBase64XML(content);
		}else{
			Base64Data b64Data = new Base64Data();
			b64Data.setData(content);
			doc.setBase64Data(b64Data);
		}
		return doc;
	}
	
	protected static Document getDocumentFile(String filePath){
		String extension = UtilsFileSystem.getExtensionFromFilePath(filePath);
		return getDocument(filePath,extension);
	}
	
	protected static boolean isXMLEnvelopingSignature(byte[] signature) throws Exception{
		javax.xml.parsers.DocumentBuilderFactory dbf =javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document xmlSignature = db.parse(new ByteArrayInputStream(signature));
		return (DSSConstants.namespace_dsig.equals(xmlSignature.getDocumentElement().getNamespaceURI())&& xmlSignature.getDocumentElement().getNodeName().substring(xmlSignature.getDocumentElement().getNodeName().indexOf(":")+1).equals("Signature"));
	}
	
	protected static DocumentHash getDocumentHash(String filePath,String algorithm) throws Exception{
		DocumentHash docHash = null;
		//System.out.println("Obteniendo document hash para:"+algorithm);
		if(algorithm.equals("SHA1") ||algorithm.equals("SHA256")||algorithm.equals("SHA512")
				||algorithm.equals("SHA384")||algorithm.equals("MD2")||algorithm.equals("MD5")){
			byte[] content = UtilsFileSystem.readFileFromFileSystem(filePath);
			DigestManager digestManager = new DigestManager(algorithm);
			byte[] hash = digestManager.computeHash(content); 
			docHash = new DocumentHash();
			DigestValue digestValue = new DigestValue();
			digestValue.setDigest(hash);
			docHash.setDigestValue(digestValue);
			DigestMethod digestMethod = new DigestMethod();
			if(algorithm.equals("SHA1"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_sha1);
			else if(algorithm.equals("SHA256"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_sha256);
			else if(algorithm.equals("SHA512"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_sha512);
			else if(algorithm.equals("SHA384"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_sha384);
			else if(algorithm.equals("MD5"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_md5);
			else if(algorithm.equals("MD2"))
				digestMethod.setAlgorithm(DSSConstants.DigestMethodDes.digest_method_md2);
			
			docHash.setDigestMethod(digestMethod);
		}else{
			throw new Exception("Invalid Hash Algorithm:"+algorithm);
		}
		return docHash;
	}
	
	protected static ReturnUpdatedSignature getReturnUpdatedSignature(String updatedFormat) throws Exception{
		ReturnUpdatedSignature updated = new ReturnUpdatedSignature();
		if(updatedFormat.equals("CMS-T")){
			updated.setType(DSSConstants.SignatureFormDes.CMS_With_TST);
		}else if(updatedFormat.equals("CADES-T")){
			updated.setType(DSSConstants.SignatureFormDes.ES_T);
		}else if(updatedFormat.equals("XADES-T")){
			updated.setType(DSSConstants.SignatureFormDes.ES_T);
				
		}
		else
			throw new Exception("Formato de actualización no valida");
		return updated;
	}

	
	
}
