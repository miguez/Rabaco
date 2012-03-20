package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class AdditionalDetails extends AElement{
	
	private Vector additionalDetailsItems = new Vector();

	public Vector getAdditionalDetailsItems() {
		return additionalDetailsItems;
	}

	public void setAdditionalDetailsItems(Vector additionalDetailsItems) {
		this.additionalDetailsItems = additionalDetailsItems;
	}
	
	public void addAdditionalDetails(AElement item){
		if(item !=null)
			additionalDetailsItems.add(item);
	}

	
}
