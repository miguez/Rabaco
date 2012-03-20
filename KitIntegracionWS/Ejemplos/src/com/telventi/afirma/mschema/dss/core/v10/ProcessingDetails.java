package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class ProcessingDetails extends AElement {
	
	private ValidDetail validDetail = null;
	
	private IndeterminateDetail indeterminateDetail = null;
	
	private InvalidDetail invalidDetail = null;

	public IndeterminateDetail getIndeterminateDetail() {
		return indeterminateDetail;
	}

	public void setIndeterminateDetail(IndeterminateDetail indeterminateDetail) {
		this.indeterminateDetail = indeterminateDetail;
	}

	public InvalidDetail getInvalidDetail() {
		return invalidDetail;
	}

	public void setInvalidDetail(InvalidDetail invalidDetail) {
		this.invalidDetail = invalidDetail;
	}

	public ValidDetail getValidDetail() {
		return validDetail;
	}

	public void setValidDetail(ValidDetail validDetail) {
		this.validDetail = validDetail;
	}

	
}
