/**
 * <p>Fichero: AdditionalReportOption.java</p>
 * <p>Descripción: Clase que implementa el componente AdditionalReportOption del perfil XSS de @Firma</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 20-nov-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.xss.draft;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;
import com.telventi.afirma.wsclient.dss.AFirmaXSSConstants;

/**
 * @author SEJRL
 *
 */
public class AdditionalReportOption extends AElement {
	
	private boolean includeTST = false;

	public void marshal(StringWriter writer) throws Exception {
		if(includeTST){
			writer.write("<afxp:AdditionalReportOption xmlns:afxp=\"urn:oasis:names:tc:dss:1.0:profiles:XSS\">");
				writer.write("<afxp:IncludeProperties>");
					writer.write("<afxp:IncludeProperty Type=\""+AFirmaXSSConstants.signaturetimestamp_property_id+"\"/>");
				writer.write("</afxp:IncludeProperties>");
			writer.write("</afxp:AdditionalReportOption>");
		}
	}

	/**
	 * @return Returns the includeTST.
	 */
	public boolean isIncludeTST() {
		return includeTST;
	}

	/**
	 * @param includeTST The includeTST to set.
	 */
	public void setIncludeTST(boolean includeTST) {
		this.includeTST = includeTST;
	}

		
}
