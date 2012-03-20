/**
 * <p>Fichero: OASISVRConstants.java</p>
 * <p>Descripción: Constantes para el perfil VR de OASIS DSS</p>
 * <p>Empresa: Telvent Interactiva </p>
 * <p>Fecha creación: 12-nov-2008</p>
 * @author SEJRL
 * @version 1.0
 */
package com.telventi.afirma.wsclient.dss;

/**
 * @author SEJRL
 *
 */
public class OASISVRConstants {
	
	public static final transient String prefix ="vr";
	
	public static final transient String profile_ns = "urn:oasis:names:tc:dss:1.0:profiles:verificationreport:schema#";
	
	public static final transient String trustorigin_data_base = "urn:oasis:names:tc:dss:1.0:trustorigin:certDataBase";

	public static final transient String no_report_detail = "urn:oasis:names:tc:dss:1.0:reportdetail:noDetails";
	
	public static final transient String no_report_path_detail = "urn:oasis:names:tc:dss:1.0:reportdetail:noPathDetails";

	public static final transient String report_all_detail = "urn:oasis:names:tc:dss:1.0:reportdetail:allDetails";
}
