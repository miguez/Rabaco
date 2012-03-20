package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.xmldsig.core.KeyInfo;

public class KeySelector extends AElement {
	
	private KeyInfo keyInfo = null;

	public KeyInfo getKeyInfo() {
		return keyInfo;
	}

	public void setKeyInfo(KeyInfo keyInfo) {
		this.keyInfo = keyInfo;
	}
}
