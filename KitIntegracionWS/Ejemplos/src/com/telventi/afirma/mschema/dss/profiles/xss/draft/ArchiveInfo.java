package com.telventi.afirma.mschema.dss.profiles.xss.draft;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.mschema.dss.profiles.arch.draf.ArchiveIdentifier;

public class ArchiveInfo extends AElement{
	
	private ArchiveIdentifier archiveIdentifier = null;

	public ArchiveIdentifier getArchiveIdentifier() {
		return archiveIdentifier;
	}

	public void setArchiveIdentifier(ArchiveIdentifier archiveIdentifier) {
		this.archiveIdentifier = archiveIdentifier;
	}
	
	
}

