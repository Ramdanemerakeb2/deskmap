package fr.univavignon.ceri.deskmap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import org.json.*;
public class OverPassLignesBuTramIrigo extends OverPassNode{
	
	public OverPassLignesBuTramIrigo(double lon, double lat) {
		super(lon, lat);
	}

	private static final int CONNECTION_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);
	private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);

		public static String get() {
		    String result = null;
		    HttpURLConnection conn = null;
		    InputStream in = null;
		    try {
		        String address = "https://data.angers.fr/api/records/1.0/search/?dataset=bus-tram-topologie-dessertes&rows=1000&facet=codeparcours&facet=mnemoligne&facet=nomligne&facet=dest&facet=mnemoarret&facet=nomarret&facet=numarret&facet=coordonnees&refine.codeparcours=TRELAZE+%3E+ST+SYLVAIN+9.9";

		        // building api url
		        URL url = new URL(address);
		        System.out.println("GET URL " + url.toString());
		        // establishing connection with server
		        conn = (HttpURLConnection) url.openConnection();
		        // building headers
		        conn.setReadTimeout(READ_TIMEOUT);
		        conn.setConnectTimeout(CONNECTION_TIMEOUT);
		        conn.setRequestMethod("GET");
		        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		            in = new BufferedInputStream(conn.getInputStream());
		            // building output string from stream
		            StringBuilder sb = new StringBuilder();
		            int b;
		            while ((b = in.read()) != -1) {
		                sb.append((char) b);
		            }
		            String output = sb.toString().replace("\n", "");
		   
		            result = output;
		        }
		    } catch (MalformedURLException ex) {
		        System.err.println("malformed url");
		        ex.printStackTrace();
		    } catch (IOException ex) {
		        System.err.println("I/O exception");
		        ex.printStackTrace();
		    } finally {
		        if (in != null) {
		            try {
		                in.close();
		            } catch (IOException ex) {
		            }
		        }
		        if (conn != null) {
		            conn.disconnect();
		        }
		    }
		    return result;

 		}
		
 
	
}
