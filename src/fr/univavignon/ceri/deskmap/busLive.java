package fr.univavignon.ceri.deskmap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class busLive extends OverPassNode{
	public busLive(double lon, double lat) {
		super(lon, lat);
	}

	private static final int CONNECTION_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);
	private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(15);

		public static String get() {
		    String result = null;
		    HttpURLConnection conn = null;
		    InputStream in = null;
		    try {
		    	String address="https://data.angers.fr/api/records/1.0/search/?dataset=bus-tram-position-tr&rows=1000&facet=novh&facet=mnemoligne&facet=nomligne&facet=dest&facet=coordonnees&refine.nomligne=ST+SYLVAIN+BANCHAIS+%3C%3ETRELAZE";
		        //String address = "https://data.angers.fr/api/records/1.0/search/?dataset=bus-tram-position-tr&rows=20&facet=novh&facet=mnemoligne&facet=nomligne&facet=dest&facet=coordonnees&refine.nomligne=ST+SYLVAIN+BANCHAIS+%3C%3ETRELAZE";
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
