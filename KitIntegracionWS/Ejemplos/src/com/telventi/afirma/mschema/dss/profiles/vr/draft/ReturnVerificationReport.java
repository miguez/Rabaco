/**
 * <p>Fichero: ReturnVerificationReport.java</p>
 * <p>Descripción: Clase que implementa el objeto "ReturnVerificationReport" de perfil VR de OASIS</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 03-nov-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.mschema.dss.profiles.vr.draft;

import java.io.StringWriter;

import com.telventi.afirma.mschema.AElement;

/**
 * @author SEJRL
 *
 */
public class ReturnVerificationReport extends AElement {
	
	private boolean includeCertValue = false;
	
	private String reportLevel = null;

		public void marshal(StringWriter writer) throws Exception {
		writer.write("<vr:ReturnVerificationReport xmlns:vr=\"urn:oasis:names:tc:dss:1.0:profiles:verificationreport:schema#\">");
			writer.write("<vr:ReportOptions>");
				writer.write("<vr:IncludeCertificateValues>"+this.includeCertValue+"</vr:IncludeCertificateValues>");
				if(this.reportLevel!=null)
					writer.write("<vr:ReportDetailLevel>"+this.reportLevel+"</vr:ReportDetailLevel>");
			writer.write("</vr:ReportOptions>");
		writer.write("</vr:ReturnVerificationReport>");
	}

	/**
	 * @return Returns the includeCertValue.
	 */
	public boolean isIncludeCertValue() {
		return includeCertValue;
	}

	/**
	 * @param includeCertValue The includeCertValue to set.
	 */
	public void setIncludeCertValue(boolean includeCertValue) {
		this.includeCertValue = includeCertValue;
	}

	/**
	 * @return Returns the reportLevel.
	 */
	public String getReportLevel() {
		return reportLevel;
	}

	/**
	 * @param reportLevel The reportLevel to set.
	 */
	public void setReportLevel(String reportLevel) {
		this.reportLevel = reportLevel;
	}
}
