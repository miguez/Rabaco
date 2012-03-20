/**
 * <p>Fichero: ReturnReadableCertificateInfo.java</p>
 * <p>Descripción: Componente del perfil XSS de @Firma que indica que se desea que se debe parsear el certificado firmante</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 18-nov-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.afirma.xss.v10;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

/**
 * @author SEJRL
 *
 */
public class ReturnReadableCertificateInfo extends AElement {

	public void marshal(StringWriter writer) throws Exception {
		writer.write("<afxp:ReturnReadableCertificateInfo xmlns:afxp=\"urn:afirma:dss:1.0:profile:XSS:schema\"/>");
	}
	
}
