/**
 * <p>Fichero: ArchiveIdentifierType.java</p>
 * <p>Descripción: Componente que implementa el componente afap:ArchiveIdentifierType del perfil Archive de @Firma</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 08-ene-2009</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.afirma.arch.v10;

import com.telventi.afirma.mschema.AElement;

public class ArchiveIdentifierType extends AElement {
	
	private String type = null;

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
}
