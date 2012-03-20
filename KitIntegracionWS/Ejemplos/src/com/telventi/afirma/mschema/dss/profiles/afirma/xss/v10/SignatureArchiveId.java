/**
 * <p>Fichero: SignatureArchiveId.java</p>
 * <p>Descripción: Objeto que representa el componente "SignatureArchiveId" del perfil XSS de @Firma</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 30-oct-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;


public class SignatureArchiveId extends AElement {
	
	public String archiveId = null;

	public String getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}
	
	public void marshal(StringWriter writer) throws Exception {
		if(this.archiveId!=null)
			writer.write("<afxp:SignatureArchiveId xmlns:afxp=\"urn:afirma:dss:1.0:profile:XSS:schema\">"+this.archiveId+"</afxp:SignatureArchiveId>");
	}
}
