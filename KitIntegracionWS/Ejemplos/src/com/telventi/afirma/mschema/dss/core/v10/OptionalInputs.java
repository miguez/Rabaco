package com.telventi.afirma.mschema.dss.core.v10;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class OptionalInputs extends AElement {
	
	private Vector optionalInputsItems = new Vector();
	
	public Vector getOptionalInputsItems() {
		return optionalInputsItems;
	}

	public void setOptionalInputsItems(Vector inputs) {
		this.optionalInputsItems = inputs;
	}

	public void addOptionalInputsItems(AElement input){
		if(input !=null){
			optionalInputsItems.add(input);
		}
	}
	
}
