package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.wsclient.dss.DSSConstants;

public class HashAlgorithm extends AElement {
	
	
	private String  algorithm = null;

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setAfirmaAlgorithm(String hashAlgorithm) {
		if(hashAlgorithm != null){
			if(hashAlgorithm.equals("MD2")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_md2;
			}else if(hashAlgorithm.equals("MD5")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_md5;
			}else if(hashAlgorithm.equals("SHA1")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_sha1;
			}else if(hashAlgorithm.equals("SHA256")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_sha256;
			}else if(hashAlgorithm.equals("SHA384")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_sha384;
			}else if(hashAlgorithm.equals("SHA512")){
				this.algorithm = DSSConstants.DigestMethodDes.digest_method_sha512;
			}else{
				System.err.println("Incorrect hash algorithm");
				System.exit(-1);
			}
		}
		
	}
	
}
