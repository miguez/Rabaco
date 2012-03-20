/** 
* <p>Fichero: StartingClass.java</p>
* <p>Descripción: </p>
* <p>Empresa: Telvent Interactiva </p>
* <p>Fecha creación: 20-jul-2006</p>
* @author SEJLHA
* @version 1.0
* 
*/
package com.telventi.afirma.wsclient;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author SEJLHA
 *
 */
public abstract class StartingClass
{
	protected static String TEMPORAL_DIR = null;
	
	protected static final String NOMBRE_PETICION = "peticion";
	public static boolean ALMACENAR_PETICION = false;
	protected static final String PROP_ALMACENAR_PETICION = "almacenar.peticion";
	
	protected static final String NOMBRE_RESPUESTA = "respuesta";
	public static boolean ALMACENAR_RESPUESTA = false;
	protected static final String PROP_ALMACENAR_RESPUESTA = "almacenar.respuesta";
	
	protected static String DATE_PATTERN = "yyyy-MM-dd_HH-mm-ss-SSS_";
	protected static final String PROP_DATE_PATTERN = "formato.fecha";
	
	
	protected static Properties propsParams = null;
	
	protected static boolean usarFichParams = false;
	
	protected static final String PROP_USARFICHPARAMS = "usarParametros";
	
	protected static final String errorMessagePropsArgs =	"LOS PARÁMETROS INDICADOS EN EL FICHERO DE PROPIEDADES SON INCORRECTOS O INCOMPLETOS.\n";
	
	protected static final String PROP_APLICACION = "aplicacion";
	protected static final String PROP_CERTIFICADO = "certificado";
	protected static final String PROP_MODOVALIDACION = "modoValidacion";
	protected static final String PROP_OBTENERINFO = "obtenerInfoCertificado";
	protected static final String PROP_FIRMA = "firma";
	protected static final String PROP_FORMATOFIRMA = "formatoFirma";
	protected static final String PROP_MODOVALFIRMA = "modoValidFirma";

	protected static final String PROP_HASHALGORITHM = "algoritmoHash";
	protected static final String PROP_FICHEROFIRMADO = "ficheroFirmado";

	protected static final String PROP_FICHEROAFIRMAR = "ficheroAfirmar";
	protected static final String PROP_ALIASCERTSERV = "aliasCertServidor";
	protected static final String PROP_REFERENCIAEXT = "referenciaExterna";
	protected static final String PROP_INCLUIRDOCUMENTO = "incluirDocumento";
	
	protected static final String PROP_IDTRANSACCION = "idTransaccion";
	protected static final String PROP_CERTFIRMOBJ = "certFirmanteObjetivo";

	//protected static final String PROP_EXTENDERFORMATOFIRMA = "extenderFormatoFirma";
	
	protected static final String PROP_DOCUMENTO = "documento";
	protected static final String PROP_IDDOCUMENTO = "idDocumento";
	
	protected static final String PROP_IDTRANSACCIONMULTIBLOQUE = "idTransaccionMultiBloque";
	protected static final String PROP_IDTRANSACCIONMULTIDOCS = "idTransaccionMultiDocs";
	
	protected static final String PROP_CUSTODIARDOC = "custodiarDocumento";
	
	protected static final String PROP_BLOQUEFIRMAS = "bloqueFirmas";
	
	protected static final String PROP_FICHEROACUSTODIAR = "ficheroAcustodiar";
	
	protected static final String PROP_FECHAINICIO = "fechaInicio";
	protected static final String PROP_FECHAFIN = "fechaFin";

	protected static final String PROP_MODOFIRMA = "modoFirma";

	protected static final String PROP_UPGRADEFORMAT = "formatoUpgrade";
	public static final String PROP_MODOACTUALIZACION = "modoActualizacion";
	protected static final String PROP_XADESVERSION = "xadesVersion";
	
	protected static final String PROP_NIVEL = "nivel";
	protected static final String PROP_INCLUDE_CERT = "includeCert";
	protected static final String PROP_INCLUDE_TST = "includeTST";
	
	
	// Cargamos el fichero de configuración
	static 
	{
		try
		{
			Properties prop = new Properties();
			URL url =  ClassLoader.getSystemResource("configuration.properties");
			prop.load(new FileInputStream(new File(url.getFile())));
			
			// Directorio temporal donde almacenar los ficheros generados en las pruebas
			TEMPORAL_DIR = prop.getProperty("dir.temporal.path");
			
			ALMACENAR_PETICION = Boolean.valueOf(prop.getProperty(PROP_ALMACENAR_PETICION)).booleanValue();
			
			ALMACENAR_RESPUESTA = Boolean.valueOf(prop.getProperty(PROP_ALMACENAR_RESPUESTA)).booleanValue();
			
			DATE_PATTERN = prop.getProperty(PROP_DATE_PATTERN);
			
		}
		catch (Exception e)
		{
			System.err.println("Error cargando el fichero de properties configuration.properties");
			System.exit(-1);
		}
		
		
		try
		{
			propsParams = new Properties();
			URL url =  ClassLoader.getSystemResource("parametros.properties");
			propsParams.load(new FileInputStream(new File(url.getFile())));
			
			String aux = propsParams.getProperty(StartingClass.PROP_USARFICHPARAMS);			
			// Directorio temporal donde almacenar los ficheros generados en las pruebas
			usarFichParams = Boolean.valueOf(aux).booleanValue();
			
		}
		catch (Exception e)
		{
			System.err.println("Error cargando el fichero de properties parametros.properties");
			System.exit(-1);
		}
		
	}
	
	
	public void fillParameters (String [] args) {
		
		if (StartingClass.usarFichParams) {
			
			fillParametersFromFile();
			
		} else {
			
			fillParametersFromArgs(args);
			
		}
		
	}
	
	public abstract void fillParametersFromFile();
	
	public abstract void fillParametersFromArgs(String [] args);
	
	protected String obtenerPropiedadArg (String clave) {
		
		String result = null;
		
		if (clave != null) {
			
			result = testIfIsNullOrEmpty(propsParams.getProperty(clave));
			
		}
		
		return result;
		
	}
	
	private String testIfIsNullOrEmpty (String str) {
		
		String result = null;
		
		if (str != null) {
			
			result = str.trim();
			
			if (result.equals("")) {
				
				result = null;
				
			}
			
		}
		
		return result;
		
	}
	
	public static String construyeRutaPetRes (boolean esPeticion, String nombreServicio) {
		
		return TEMPORAL_DIR + construyeNombrePetRes(esPeticion, nombreServicio);
		
	}
	
	protected static String construyeNombrePetRes (boolean esPeticion, String nombreServicio) {
		
		String separador = "_";
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		String result = sdf.format(new Date());
		
		if (esPeticion) {
			result += NOMBRE_PETICION + separador; 
		} else {
			result += NOMBRE_RESPUESTA + separador;
		}
		
		result += nombreServicio;
		
		result += ".txt";
		
		return result;
		
	}
	
}
