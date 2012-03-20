package com.telventi.afirma.OCSPTester;
/*
Copyright (c) 2003 
Johannes Nicolai (johannes.nicolai@novosec.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
/*
Modified by Sainathan Sivasubramanian (sainathan@gmail.com) in Nov 2004 to support the following:
1. Use a different constructor of X509Name to reverse order of requestor subject DN
2. ASN1Encode requestorName to encode requestor subject DN in appropriate ASN1 format
3. Inlude nonce extension - A command line param can be added to enable/disable nonce in OCSP request
*/

import iaik.x509.ocsp.OCSPException;

import java.io.IOException;

import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;



import org.bouncycastle.asn1.ASN1EncodableVector;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateID;
import org.bouncycastle.ocsp.OCSPReqGenerator;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.ocsp.OCSPRespStatus;
import org.bouncycastle.ocsp.RespData;
import org.bouncycastle.ocsp.RespID;
import org.bouncycastle.ocsp.SingleResp;


//End

/**
This class is responsable for generating OCSP client responses and processing OCSP server responses. All OCSP information is processed and generated as raw
DER encoded data.
*/
public class ClientOCSPDriver {
	//private X509Certificate usercert;
	private X509Certificate[] usercerts;
	private PrivateKey userkey;
	private X509Certificate[] certificates;
	private X509Certificate[] mastercerts;	
	private HashMap map;
	private boolean calledgenerate=false;
	private boolean calledprocess=false;
	//Sai
	private X509Certificate respcert;
	private static SecureRandom random = new SecureRandom();
	private boolean firmaPeticion = false;
	private String aplicacion="";
	//End
/**
Constructs a ClientOCSPDriver object. Objects of this class can only be used for one OCSP client request and the processing of the resulting server OCSP response.
@param usercert Certificate that should be used to authenticate the OCSP client 
@param userkey  Private key that belongs to the certificate
@param certificates Certificates whose status should be requested from the OCSP server
@param mastercert Certificate that is aspected to authenticate the OCSP server response
*/
	public ClientOCSPDriver(X509Certificate[] usercerts, PrivateKey userkey, X509Certificate [] certificates, X509Certificate [] mastercerts, X509Certificate respcert,
			boolean firmaPeticion,String aplicacion) throws OCSPException {
		if (usercerts.length==0||userkey==null||certificates==null||mastercerts==null||respcert==null)
			throw new OCSPException("At least one parameter of ClientOCSPDriver was uninitialised");
		this.usercerts=usercerts;
		this.userkey=userkey;
		this.certificates=certificates;
		this.mastercerts=mastercerts;
		//Sai
		this.respcert=respcert;
		map=new HashMap(certificates.length);		
		this.firmaPeticion = firmaPeticion;
		this.aplicacion = aplicacion;
		
	}
/**
Generates a signed OCSP client request with the parameters specified in the constructor.
This method can only be called once.
@return The raw DER encoded client OCSP request. This data has to be transported over a specific protocol (such as HTTP) to the OCSP server in order to get
an OCSP server response.
 * @throws org.bouncycastle.ocsp.OCSPException 
 * @throws IllegalArgumentException 
*/
	public byte [] getRequest() throws OCSPException, NoSuchProviderException,IOException, IllegalArgumentException, org.bouncycastle.ocsp.OCSPException {
		//return getRequest("MD5WITHRSA","BC");
		//Sai - Choose the signature algorithm based on what your responder expects/supports
		return getRequest("SHA1WITHRSA","BC");		
	}


//Sai
/**
     * Apply ASN1 coversion for the given value depending on the oid
     * and the character range of the value.
     *
     * This code was taken and modified from X509DefaultEntryConverter.java file 
     * of BouncyCastle. Modify this code to match the ASN1 type for your requestor subject DN
     * Refer Bouncycastle X509DefaultEntryConverter.java source for implementation of methods
     * such as convertHexEncoded, canBePrintable and canBeUTF8 
     * 
     * @param oid the object identifier for the DN entry
     * @param value the value associated with it
     * @return the ASN.1 equivalent for the string value.
     *      
     */
    public DERObject getConvertedValue(
        DERObjectIdentifier  oid,
        String               value)
    {
        /*if (value.length() != 0 && value.charAt(0) == '#')
        {
            try
            {
                return convertHexEncoded(value, 1);
            }
            catch (IOException e)
            {
                throw new RuntimeException("can't recode value for oid " + oid.getId());
            }
        }
        else if (oid.equals(X509Name.EmailAddress))
        {
            return new DERIA5String(value);
        }
        else */if (oid.equals(X509Name.O) || oid.equals(X509Name.OU))
        {
            return new DERT61String(value);
        }
        else /*if (canBePrintable(value))  */
        {
            return new DERPrintableString(value);
        }
        /*else if (canBeUTF8(value))
        {
            return new DERUTF8String(value);
        }*/

        //return new DERBMPString(value);
    }	
	
/**
 Method added to generate ASNSequence object of subjectDN consturcted in appropriate ASN1 type
 X509Name constructs all DN components as printablestring by default
 
 This code was taken and modified from X509Name.java file of BouncyCastle
 **/	
	public /*DERObject*/ASN1Sequence getASNSequence(Vector oids, Vector values)
    {
        ASN1Sequence seq;
        
        ASN1EncodableVector  vec = new ASN1EncodableVector();
        ASN1EncodableVector  sVec = new ASN1EncodableVector();
        DERObjectIdentifier  lstOid = null;
        
        for (int i = 0; i != oids.size(); i++)
        {
            ASN1EncodableVector     v = new ASN1EncodableVector();
            DERObjectIdentifier     oid = (DERObjectIdentifier)oids.elementAt(i);

            v.add(oid);

            String  str = (String)values.elementAt(i);

            v.add(getConvertedValue(oid, str));

            if (lstOid == null)
            {
                sVec.add(new DERSequence(v));
            }
            else
            {
                vec.add(new DERSet(sVec));
                sVec = new ASN1EncodableVector();                
                sVec.add(new DERSequence(v));
            }
            
            lstOid = oid;
        }
        
        vec.add(new DERSet(sVec));
        
        seq = new DERSequence(vec);        

        return seq;
    }
//End	
/**
Generates a signed OCSP client request with the parameters specified in the constructor.
This method can only be called once.
@param signingalgorithm The algorithm, that should be used to sign the OCSP client request, default is "MD5WITHRSA".
@param provider The provider used to compute the hashes and sign the request, default is "BC" (Bouncy Castle).
@return The raw DER encoded client OCSP request. This data has to be transported over a specific protocol (such as HTTP) to the OCSP server in order to get
an OCSP server response.
 * @throws org.bouncycastle.ocsp.OCSPException 
 * @throws IllegalArgumentException 
*/
	public byte [] getRequest(String signingalgorithm,String provider) throws OCSPException,NoSuchProviderException,IOException, IllegalArgumentException, org.bouncycastle.ocsp.OCSPException {
		if (calledgenerate)
			throw new OCSPException("Request was already generated!");
		map.clear();	
		OCSPReqGenerator gen= new OCSPReqGenerator();
		for (int i=0;i<certificates.length;++i) {
			CertificateID certid=new CertificateID(CertificateID.HASH_SHA1,mastercerts[i],certificates[i].getSerialNumber());
			map.put(certid,certificates[i]);
			gen.addRequest(certid);
		}                
        
        //Sai
        	//gen.setRequestorName(new GeneralName(new X509Name(usercert.getSubjectX500Principal().getName())));
        	//Call this constructor to parse and reverse order the DN		
		X509Name subjectName = new X509Name(true,usercerts[0].getSubjectX500Principal().getName());
		Vector oids = subjectName.getOIDs();  
		Vector values = subjectName.getValues();		
		
		//Create a ASNSequence object for the subject DN
		ASN1Sequence seq = getASNSequence(oids,values);    						
    		//gen.setRequestorName(new GeneralName(new X509Name(seq)));
		if (aplicacion!="" && !aplicacion.equals(null))
			gen.setRequestorName(new GeneralName(GeneralName.rfc822Name,aplicacion));
			//"cjap.au tenticacionCJAP"		
		else
			gen.setRequestorName(new GeneralName(new X509Name(seq)));
    	
    		//Include nonce extension 1.3.6.1.5.5.7.48.1.2    	    	    	
  		byte[] Nonce = new byte[16];
  		random.nextBytes(Nonce);     		    	
            	ASN1EncodableVector  v = new ASN1EncodableVector();    	
                ASN1EncodableVector  sVec = new ASN1EncodableVector();        
            	DERObjectIdentifier  oid = new DERObjectIdentifier("1.3.6.1.5.5.7.48.1.2");
                v.add(oid);        
                v.add(new DEROctetString(Nonce));
                sVec.add(new DERSequence(v));        
                seq = new DERSequence(sVec);		
            	gen.setRequestExtensions(new X509Extensions(seq));      	  	
          //End   
        byte [] ocspdata;
		if (firmaPeticion){
			//ocspdata= gen.generate(signingalgorithm,userkey,new X509Certificate[] {usercert},provider).getEncoded();
			/*PARA FIRMA INVALIDA
			 PrivateKeyFactory pkf=new PrivateKeyFactory();
			KeyPairGenerator kpg=null;
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
			
			
			KeyPair kp=kpg.generateKeyPair();
			PrivateKey pk=kp.getPrivate();
			ocspdata= gen.generate(signingalgorithm,pk,usercerts,provider).getEncoded();
			OCSPReq req=new OCSPReq(ocspdata);
			System.out.println("REQUEST "+req.toString());*/
			
			ocspdata= gen.generate(signingalgorithm,userkey,usercerts,provider).getEncoded();
			
			
			}
		else
			ocspdata= gen.generate().getEncoded();
		calledgenerate=true;
		return ocspdata;
	}
	
/**
Processes the resulting OCSP server response. This method checks, whether the server can authenticate itself and then finds out the status of the
requested certificates. This method can be called only once after generating the OCSP client response via {@link #getRequest() getRequest}.
@param data The raw DER encoded OCSP server response
@return A hashmap is returned that contains the status of each requested certificate. The key for the hashmap is the certificate itself. The status (the associated value) is either null (certificate is good), an {@link org.bouncycastle.ocsp.UnknownStatus UnkownStatus} object or a {@link org.bouncycastle.ocsp.RevokedStatus RevokedStatus} object.  
 * @throws org.bouncycastle.ocsp.OCSPException 
*/
	public HashMap processResponse(byte [] data) throws OCSPException,IOException,NoSuchProviderException, org.bouncycastle.ocsp.OCSPException{
		return processResponse(data,"BC");
	}
	
/**
Processes the resulting OCSP server response. This method checks, whether the server can authenticate itself and then finds out the status of the
requested certificates. This method can be called only once after generating the OCSP client response via {@link #getRequest(String,String) getRequest}.
@param data The raw DER encoded OCSP server response
@param provider The provider used to authenticate the server, default is "BC" (Bouncy Castle).
@return A hashmap is returned that contains the status of each requested certificate. The key for the hashmap is the certificate itself. The status (the associated value) is either null (certificate is good), an {@link org.bouncycastle.ocsp.UnknownStatus UnkownStatus} object or a {@link org.bouncycastle.ocsp.RevokedStatus RevokedStatus} object.  
 * @throws org.bouncycastle.ocsp.OCSPException 
*/
	public HashMap processResponse(byte [] data,String provider) throws IOException,OCSPException,NoSuchProviderException, org.bouncycastle.ocsp.OCSPException {
		if (!calledgenerate)
			throw new OCSPException("No request was generated!");
		if(calledprocess)
			throw new OCSPException("Request was already processed!");
			
			
		Date current=new Date();
		HashMap responsemap=new HashMap(certificates.length);
		OCSPResp resp=null;
		try {
			resp = new OCSPResp(data);
		}
		catch (Exception e) {
			throw new OCSPException("No valid OCSPResponse - object obtained! Exception: "+e);
		}
		int status = resp.getStatus();
		switch (status) {
			case OCSPRespStatus.INTERNAL_ERROR: throw new OCSPException ("El servidor ha respondido [internalError]. Ha occurrido un error interno en el Servidor OCSP!");
			case OCSPRespStatus.MALFORMED_REQUEST: throw new OCSPException ("El servidor ha respondido [malformedRequest]. La petición no cumple el formato especificado en la RFC 2560!");
			case OCSPRespStatus.SIGREQUIRED: throw new OCSPException ("El servidor ha respondido [sigrequired]. La petición no está firmada!");
			case OCSPRespStatus.TRY_LATER: throw new OCSPException ("El servidor ha respondido [tryLater]. El servidor está demasiado ocupado como para responder a la petición!");
			case OCSPRespStatus.UNAUTHORIZED: throw new OCSPException ("El servidor ha respondido [unauthorized]. El servidor no puede autenticar la petición enviada!");
			case OCSPRespStatus.SUCCESSFUL: break;
			default: throw new OCSPException ("Codigo de estado de OCSPResponse desconocido!");
		}
		BasicOCSPResp bresp=null;
		try {
			bresp= (BasicOCSPResp) resp.getResponseObject();
		}
		catch (Exception e) {
			throw new OCSPException("No valid BasicOCSPResponse object obtained! Exception: "+e);
		}
		if (bresp==null)
			throw new OCSPException("No BasicOCSPResponse found!");
			
		RespData respdata=null;
		try {
			respdata=bresp.getResponseData();
		}
		catch (Exception e) {
			throw new OCSPException("No valid OCSP - ResponseData object obtained! Exception: "+e);
		}
		
		if (respdata==null)
			throw new OCSPException("No response data found!");		
		
		if (current.before(respdata.getProducedAt())) 
			throw new OCSPException("Response was generated in the future at "+respdata.getProducedAt() + ". Check your system time");
		
		X509Certificate [] servercerts = (X509Certificate[])bresp.getCerts(provider);
		
		//Sai		
		if (servercerts!=null&&servercerts.length!=0) 
			//Sai
			//replace mastercert with respcert and use serial# to compare the certs instead of cert itself
			//if (!servercerts[0].equals(mastercert))
			if (!servercerts[0].getSerialNumber().equals(respcert.getSerialNumber()))
			//End
				throw new OCSPException("Certificate seems to be signed with unknown certificate!");
				
		RespID respid=respdata.getResponderId();
		
		X500Principal x500 =respcert.getSubjectX500Principal();
		X509Principal x509 = new X509Principal(x500.getEncoded());
		
		//Sai
		//replace mastercert with respcert		
		//if (!respid.equals(new RespID(mastercert.getPublicKey()))&&!respid.equals(new RespID(mastercert.getSubjectX500Principal())))
		//if (!respid.equals(new RespID(respcert.getPublicKey()))&&!respid.equals(new RespID(respcert.getSubjectX500Principal())))
		if (!respid.equals(new RespID(respcert.getPublicKey()))&&!respid.equals(new RespID(x509)))
		//End
			throw new OCSPException("The Responder ID does not match your responder certificate!");
		//Sai
		//replace mastercert with respcert
		if (!bresp.verify(respcert.getPublicKey(),provider))
			throw new OCSPException("Could not verify OCSP server response");
		
		SingleResp [] responses = respdata.getResponses();
		if (responses==null)
			throw new OCSPException("No client certificates inside OCSP response");
		
		//Sai
		//This validation is commented as the OCSP responder I was using does not validate
		//multiple certs coming in a single request. 
		/*		
		if (responses.length!=certificates.length)
			throw new OCSPException("Number of certificates inside OCSP response does not fit to request");
		*/
		//End
		calledprocess=true;
		for (int i=0;i<responses.length;++i) {
			SingleResp sresp=responses[i];
			
			if (current.before(sresp.getThisUpdate()))
				throw new OCSPException("Certificate was revoked in the future!");
			Date nextu=sresp.getNextUpdate();
			if (nextu!=null)
				if (current.after(nextu))
					throw new OCSPException("OCSP response is too old (New version already available)!");
			X509Certificate cert=(X509Certificate)map.remove(sresp.getCertID());
			if (cert==null)
				throw new OCSPException("Received certificate twice or one, that was not requested!");
			responsemap.put(cert,sresp.getCertStatus());
		}
		return responsemap;
		
	}
}
