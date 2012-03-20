package com.telventi.afirma.mschema.dss.core.v10;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class InputDocuments extends AElement {
	
	private Vector inputDocumentsItems = new Vector();
	
	public void addItem(AElement item){
		if(item !=null){
			inputDocumentsItems.add(item);
		}
	}

	public Vector getInputDocumentsItems() {
		return inputDocumentsItems;
	}

	public void setInputDocumentsItems(Vector inputDocumentsItems) {
		this.inputDocumentsItems = inputDocumentsItems;
	}

}
