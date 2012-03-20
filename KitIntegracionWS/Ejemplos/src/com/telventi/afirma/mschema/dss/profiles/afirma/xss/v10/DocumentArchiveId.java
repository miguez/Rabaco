/**
 * <p>Fichero: DocumentArchiveId.java</p>
 * <p>Descripción: Clase que implementa el componente "DocumentArchiveId" del perfil XSS de @Firma</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 30-oct-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import com.telventi.afirma.mschema.AElement;

/**
 * @author SEJRL
 *
 */
public class DocumentArchiveId extends AElement {
	
	private String archiveId = null;
	
	private String id = null;

	public String getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(String archiveId) {
		this.archiveId = archiveId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
