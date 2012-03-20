package com.telventi.afirma.mschema.xmldsig.core;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class Transforms extends AElement {
	
	private Vector transforms = new Vector();

	public Vector getTransforms() {
		return transforms;
	}

	public void setTransforms(Vector transforms) {
		this.transforms = transforms;
	}
	
}
