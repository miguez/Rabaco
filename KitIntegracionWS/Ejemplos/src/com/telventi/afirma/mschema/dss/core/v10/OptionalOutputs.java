package com.telventi.afirma.mschema.dss.core.v10;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class OptionalOutputs extends AElement {
	
	private Vector optionalOutputsItems = new Vector();
	
	public Vector getOptionalOutputsItems() {
		return optionalOutputsItems;
	}
	
	public void setOptionalOutputsItems(Vector optionalOutputsItems) {
		this.optionalOutputsItems = optionalOutputsItems;
	}

	public void addItem(AElement item){
		if(item !=null){
			optionalOutputsItems.add(item);
		}
	}
}
