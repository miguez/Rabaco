package com.telventi.afirma.OCSPTester;
/*
Copyright (c) 2003 
Johannes Nicolai (johannes.nicolai@novosec.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
/*
Modified by Sainathan Sivasubramanian (sainathan@gmail.com) in Nov 2004 to support the following:
1. Add debuglog, writetofile methods
2. Include argument to receive responder cert as this code assumes mastercert(issuer of usercert) and responder are same
3. write request and response data to file in debug mode 
Note: Single line edits are prefixed by //Sai and multiline changes are suffixed with //End
* suggested change: include debug and nonce as command line options.
*/


//Sai
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.CertificateStatus;
import org.bouncycastle.ocsp.RevokedStatus;
import org.bouncycastle.ocsp.UnknownStatus;

import com.telventi.afirma.wsclient.StartingClass;
import com.telventi.afirma.wsclient.WebServicesAvailable;
import com.telventi.afirma.wsclient.utils.UtilsFileSystem;

public class OCSPClient {
    static
    {
        Security.addProvider(new BouncyCastleProvider());
        Properties p=System.getProperties();
        p.setProperty("sun.net.client.defaultConnectTimeout", "15000");
        p.setProperty("sun.net.client.defaultReadTimeout", "15000");
        System.setProperties(p);
    }
    
    private static final String message = 
		"La sintaxis de la aplicación de prueba del Servicio OCSP es:\n" +
		"> testOCSP certificadoAValidar certificadoRaiz\n" +
		"\n" +
		"  donde\n" +
		"   certificadoAValidar    --> Ruta completa al certificado a validar\n" +
		"   certificadoRaiz        --> Ruta completa al certificado raíz de la CA del certificado que deseamos validar\n" +
		"\nSi no se pasa ningún parámetro, estos valores se cogen del fichero OCSPConfiguration.properties"+
		"\n"; 
	//Sai - debug flag - could be made a command line param
	private static boolean debug = false;
    
    private static PrivateKey usercertkey;
    //private static X509Certificate usercert;
    private static X509Certificate[] usercerts;
    private static Collection certs;
    private static X509Certificate[] certificates;
    private static X509Certificate[] mastercerts;
    private static X509Certificate respcert;
    
    private static void loadKeystore(String pkcs12file, String pkcs12passwd, String keyPasswd, String alias) throws OCSPTesterException
    {
        KeyStore ks;        
        
        try
        {
        	Certificate [] certificados;
            ks= KeyStore.getInstance("pkcs12");
            //ks.load(OCSPClient.class.getResourceAsStream(pkcs12file), pkcs12passwd.toCharArray());
            ks.load(new FileInputStream(pkcs12file), pkcs12passwd.toCharArray());
            usercertkey=(PrivateKey)ks.getKey(alias,keyPasswd.toCharArray());            
            //certificado = ks.getCertificate(alias);
            certificados = ks.getCertificateChain(alias);  
                    	
            usercerts= new X509Certificate[certificados.length];
            for (int n = 0; n < certificados.length;n++)
            {	  
            	usercerts[n]= (X509Certificate) certificados[n];            	
            	//System.out.println("SubjectNAme "+n+ ":"+usercerts[n].getSubjectDN().getName());
            }            
            //System.out.println("SubjectNAme: "+usercert.getSubjectDN().getName());
        }
        catch(Exception e)
        {
            System.out.println("Se ha producido un error leyendo el keystore: "+e.getMessage());
            throw new OCSPTesterException(e);
        }
        
        //if (usercert==null || usercertkey==null)
        if (usercerts.length==0 || usercertkey==null){
            String str= "No se ha encontrado el alias "+alias+" en el keystore "+pkcs12file;
            System.out.println(str);
            throw new OCSPTesterException(str);
        }
    }
    
    private static void loadCertFile(String certPath) throws OCSPTesterException
    {
        try
        {
            //InputStream certfile =OCSPClient.class.getResourceAsStream(certPath);
            InputStream certfile = new FileInputStream(certPath);
            CertificateFactory cf=CertificateFactory.getInstance("X509");
            certs=cf.generateCertificates(certfile);
            certificates=(X509Certificate[])certs.toArray(new X509Certificate[certs.size()]);
        }
        catch(Exception e)
        {
            System.out.println("Error cargando "+certPath+": "+e.getMessage());
            throw new OCSPTesterException(e);
        }
        
        if (certs.size()==0) {
            String str= "Error: El fichero "+certPath+" no contiene certificados";   
            System.out.println(str);
            throw new OCSPTesterException(str);
        }
    }
	
    private static final void loadMasterFile(String mFile) throws OCSPTesterException
    {
        try
        {
            //InputStream masterfile =OCSPClient.class.getResourceAsStream(mFile);
            InputStream masterfile = new FileInputStream(mFile);
            CertificateFactory cf=CertificateFactory.getInstance("X509");
            Collection mastersAux = cf.generateCertificates(masterfile);
            mastercerts = (X509Certificate[])mastersAux.toArray(new X509Certificate[mastersAux.size()]);
        }
        catch(Exception e)
        {
            System.out.println("Error cargando master file: "+e.getMessage());
            throw new OCSPTesterException(e);
        }
        
        if (mastercerts==null) {
            String str= "Error: Certificados maestros no encontrados"; 
            System.out.println(str);
            throw new OCSPTesterException(str);
        }
    }
    
    private static final void loadRespFile(String respFile) throws OCSPTesterException
    {
        try
        {
            //InputStream respfile =OCSPClient.class.getResourceAsStream(respFile);
            InputStream respfile = new FileInputStream (respFile);
            CertificateFactory cf=CertificateFactory.getInstance("X509");
            respcert=(X509Certificate)cf.generateCertificate(respfile);
        }
        catch(Exception e)
        {
            System.out.println("Error cargando respFile: "+e.getMessage());
            throw new OCSPTesterException(e);
        }
        if (respcert==null) {
            String str= "No responder certificate found!";  
            System.out.println(str);
            throw new OCSPTesterException(str);
        }   
    }
    
  /*  public static boolean testOCSP() throws OCSPTesterException
    {
        try
        {
            String args[];
            InputStream is= Object.class.getResourceAsStream("/testServices.properties");
            try
            {
                if(is!= null)
                {
                    Properties p= new Properties();
                    p.load(is);
                    args= new String[8];
                    args[0]= p.getProperty("service.ocsp.userkeystore");
                    args[1]= p.getProperty("service.ocsp.userkeystorepass");
                    args[2]= p.getProperty("service.ocsp.userkeypass");
                    args[3]= p.getProperty("service.ocsp.userkeystorealias");
                    args[4]= p.getProperty("service.ocsp.certificatefile");
                    args[5]= p.getProperty("service.ocsp.masterfile");
                    args[6]= p.getProperty("service.ocsp.respfile");
                    args[7]= p.getProperty("service.ocsp.URLofOCSPServer");
                }
                else
                {
                    throw new OCSPTesterException("No se ha encontrado testServices.properties");
                }
            }
            catch(IOException e)
            {
                System.out.println("Error cargando propiedades: "+e.getMessage());
                throw new OCSPTesterException(e);
            }
            finally
            {
                try{if(is!=null)is.close();}catch(IOException e){}
            }
            
            if (args.length<8)
            {
                usage();                
            }
        
            loadKeystore(args[0], args[1], args[2], args[3]);
            
            loadCertFile(args[4]);          
            
            loadMasterFile(args[5]);
            
            loadRespFile(args[6]);
            boolean firmaPeticion = Boolean.valueOf( args[8]).booleanValue();
            ClientOCSPDriver driv =new ClientOCSPDriver(usercert,usercertkey,certificates,mastercert,respcert,firmaPeticion,aplicacion);
            byte[] ocspdata= driv.getRequest();         
            
            URL url= new URL(args[7]);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setAllowUserInteraction(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            HttpURLConnection.setFollowRedirects(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Length",Integer.toString(ocspdata.length));
            con.setRequestProperty("Content-Type","application/ocsp-request");
            con.connect();

            OutputStream os=con.getOutputStream();
            os.write(ocspdata);
            os.close();         
            
            if (con.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                System.out.println("[Servicio OCSP]: [FAIL]: [La petición OCSP-R no ha sido aceptada. Código de respuesta: "+con.getResponseCode()+"]");
                return false;
            }
            if (con.getContentType()==null||!con.getContentType().equals("application/ocsp-response")) {
                System.out.println("[Servicio OCSP]: [FAIL]: [El content-type de la respuesta no es ocsp-response. Content-type: "+con.getContentType()+"]");
                return false;
            }
            
            int len=con.getContentLength();
            if (len<1) {
                System.out.println("[Servicio OCSP]: [FAIL]: [La respuesta OCSP está vacía]");
                return false;
            }
            
            System.out.println("[Servicio OCSP]: [OK]");
            return true;
        }
        catch (Exception e) {
            System.out.println("[Servicio OCSP]: [ERROR]: ["+e.getMessage()+"]");
            return false;
        }
    }
    */
	public static void main (String args[]) throws OCSPTesterException, FileNotFoundException {
		
		String params[]= new String[10];	
		
		if(args.length == 0 || args.length == 2)
        {
        	System.err.println(message);
            InputStream is= new FileInputStream("OCSPConfiguration.properties");//Object.class.getResourceAsStream("/testServices.properties");
            try
            {
                if(is!= null)
                {
                    Properties p= new Properties();
                    p.load(is);                    
                    params[0]= p.getProperty("service.ocsp.userkeystore");
                    params[1]= p.getProperty("service.ocsp.userkeystorepass");
                    params[2]= p.getProperty("service.ocsp.userkeypass");
                    params[3]= p.getProperty("service.ocsp.userkeystorealias");
                    if (args.length == 2)
                    {                    	
                    	params[4]= args[0];
                        params[5]= args[1];
                    }
                    else
                    {
                  
                    	params[4]= p.getProperty("service.ocsp.certificatefile");
                        params[5]= p.getProperty("service.ocsp.masterfile");
                    }                  
                    params[6]= p.getProperty("service.ocsp.respfile");
                    params[7]= p.getProperty("service.ocsp.URLofOCSPServer");
                    params[8]= p.getProperty("service.ocsp.peticionfirmada");
                    params[9]= p.getProperty("service.ocsp.aplicacion");
                }
            }
            catch(IOException e)
            {
                System.out.println("Error cargando el fichero de propiedades: "+e.getMessage());
            }
            catch(Exception e)
            {
                System.out.println("Error cargando los parametros: "+e.getMessage());
            }
            finally
            {
                try{if(is!=null)is.close();}catch(IOException e){}
            }
        }
        
		//Sai
		/*if (args.length<8)
        {
            usage();                
        }
        */
		else if (args.length !=2 )
		{
			System.err.println ("Falta/n parámetro/s");
			System.err.println(message);
			System.exit(-1);
		}
		
		try {
			System.out.println("[COMIENZO DE PROCESO DE PRUEBA DEL SERVICIO OCSP]");
			loadKeystore(params[0], params[1], params[2], params[3]);
			System.out.println("parametros 4="+params[4]);
			System.out.println("parametros 5="+params[5]);
            loadCertFile(params[4]);			
			
			loadMasterFile(params[5]);
			
			if (mastercerts.length != certificates.length) {
				throw new OCSPTesterException("El número de certificados a validar debe coincidir con el número de certificados master pasados.");
			}
            
			loadRespFile(params[6]);
			boolean firmaPeticion = Boolean.valueOf( params[8]).booleanValue();		
			String aplicacion = params [9];
			System.out.println("/***************** PETICION *****************/");
			System.out.println("requestorName: "+aplicacion);
			if (firmaPeticion)
				System.out.println("Se firma la petición");
			else
				System.out.println("No se firma la petición");
            //ClientOCSPDriver driv =new ClientOCSPDriver(usercert,usercertkey,certificates,mastercert,respcert,firmaPeticion,aplicacion);
			ClientOCSPDriver driv =new ClientOCSPDriver(usercerts,usercertkey,certificates,mastercerts,respcert,firmaPeticion,aplicacion);
			byte[] ocspdata= driv.getRequest();			
			
			if (StartingClass.ALMACENAR_PETICION) {
				UtilsFileSystem.writeDataToFileSystem(ocspdata, StartingClass.construyeRutaPetRes(true, WebServicesAvailable.serviceOCSP));
			}
			
            Properties p=System.getProperties();
            p.setProperty("sun.net.client.defaultConnectTimeout", "15000");
            p.setProperty("sun.net.client.defaultReadTimeout", "15000");
            System.setProperties(p);
            
            URL url= new URL(params[7]);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
			con.setAllowUserInteraction(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			HttpURLConnection.setFollowRedirects(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Length",Integer.toString(ocspdata.length));
			con.setRequestProperty("Content-Type","application/ocsp-request");
            con.connect();

            OutputStream os=con.getOutputStream();
			os.write(ocspdata);
			os.close();			
			System.out.println("/***************** RESPUESTA *****************/");
			if (con.getResponseCode()!=HttpURLConnection.HTTP_OK) {
				System.out.println("La petición OCSP-R no ha sido aceptada. Código de respuesta: "+con.getResponseCode());
				
                return;
			}
			if (con.getContentType()==null||!con.getContentType().equals("application/ocsp-response")) {
				System.out.println("El content-type de la respuesta no es ocsp-response. Content-type: "+con.getContentType());
				
                return;
			}
            
			int len=con.getContentLength();
			if (len<1) {
				System.out.println("La respuesta OCSP está vacía.");
				
                return;
			}
			System.out.println("Servicio OCSP: Ok");
			{
            
				InputStream reader=con.getInputStream();
				byte [] ocsprespdata=new byte [len];
				
				int offset=0;
				int bytes_read;
				while ((bytes_read=reader.read(ocsprespdata,offset,len-offset))!=-1) {
					offset+=bytes_read;
					if(offset==len)
						break;
				}
				if (offset!=len) {
					System.out.println("No se puede leer todo el OCSPResponse!");
					
				}
				reader.close();			
				con.disconnect();
				//Sai
				if (debug)
					writeToFile("ocspresp.req", ocsprespdata);
				//End	
				
				if (StartingClass.ALMACENAR_RESPUESTA) {
					UtilsFileSystem.writeDataToFileSystem(ocsprespdata, StartingClass.construyeRutaPetRes(false, WebServicesAvailable.serviceOCSP));
				}
				
				HashMap certstat=driv.processResponse(ocsprespdata);
				System.out.println("Resultados:");
				System.out.println();
				for (int i=0;i<certificates.length;++i) {
					System.out.println("Certificado "+(i+1)+": "+certificates[i].getSubjectX500Principal());
					CertificateStatus stat=(CertificateStatus)certstat.get(certificates[i]);
					if (stat==null)
						System.out.println("Estado: good");
					else if (stat instanceof UnknownStatus)
						System.out.println("Estado: unknown");
					else if (stat instanceof RevokedStatus) {
						RevokedStatus rstat = (RevokedStatus) stat;
						System.out.println("Estado: revoked");
						System.out.println("Time: "+rstat.getRevocationTime());
						try {					
							System.out.println("ReasonCode: "+(rstat.hasRevocationReason()?Integer.toString(rstat.getRevocationReason()):"Unknown"));
						}
						catch (IllegalStateException e){
							System.out.println("ReasonCode: Unknown");
							
						}	
					}
					else 
						System.out.println("Status not recognized!");
					System.out.println("---------------------------------");
				}
			}
			System.out.println("[FIN DE PROCESO DE PRUEBA DEL SERVICIO OCSP]");
			System.exit(0);
			return;
		}
		catch (Exception e) {
			System.out.println("Error	: "+e.getMessage());
			e.printStackTrace();
			
            return;
		}
			
	}
	private static void usage() {
		//Sai
		System.out.println("Sintaxis: OCSPClient userkeystore userkeystorepass userkeypass userkeystorealias certificatefile masterfile respfile URLofOCSPServer");		
		
	}
	//Sai
	static void writeToFile(String fileName, byte[] buffer) throws Exception
    	{	
        	FileOutputStream f = new FileOutputStream(fileName);
        	f.write(buffer);
        	f.close();
    	}    
    	static void debuglog(String msg)
    	{
	    	if (debug)
    			System.out.println(msg);
    	}
    	//End    	    
}
