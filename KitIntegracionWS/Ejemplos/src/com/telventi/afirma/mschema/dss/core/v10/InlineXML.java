package com.telventi.afirma.mschema.dss.core.v10;

import com.telventi.afirma.mschema.AElement;

public class InlineXML extends AElement {
	
	private String content = null;
	
	private boolean ignorePIs =false;
	
	private boolean ignoreComments = false;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isIgnoreComments() {
		return ignoreComments;
	}

	public void setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
	}

	public boolean isIgnorePIs() {
		return ignorePIs;
	}

	public void setIgnorePIs(boolean ignorePIs) {
		this.ignorePIs = ignorePIs;
	}
	
}
