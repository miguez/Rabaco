package com.telventi.afirma.mschema.dss.core.v10;

import java.util.Vector;

import com.telventi.afirma.mschema.AElement;

public class Other extends AElement{
	
	private Vector otherItems = new Vector();
	
	public Vector getOtherItems() {
		return otherItems;
	}

	public void setOtherItems(Vector other) {
		this.otherItems = other;
	}

	public void addOtherItems(AElement other){
		if(other !=null){
			otherItems.add(other);
		}
	}
}
