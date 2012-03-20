package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;


public class TargetSigner extends AElement {
	
	private byte[] targetSigner = null;

	/**
	 * @return Returns the targetSigner.
	 */
	public byte[] getTargetSigner() {
		return targetSigner;
	}

	/**
	 * @param targetSigner The targetSigner to set.
	 */
	public void setTargetSigner(byte[] targetSigner) {
		this.targetSigner = targetSigner;
	}
}
