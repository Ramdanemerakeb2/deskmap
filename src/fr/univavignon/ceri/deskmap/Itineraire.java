package fr.univavignon.ceri.deskmap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

public class Itineraire {

	
	
	 
	public static void saveTextToFile(Document content, File file) throws TransformerException {
		//for output to file, console
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //for pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(content);

        //write to console or file
        StreamResult console = new StreamResult(System.out);
        StreamResult file1 = new StreamResult(file);

        //write data
        transformer.transform(source, console);
        transformer.transform(source, file1);
        System.out.println("DONE");
        System.out.println("Done creating XML File");
    } 
	
	
	public static String LoadFile(File file) throws SAXException, IOException, ParserConfigurationException{
		
		//an instance of factory that gives a document builder  
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
		//an instance of builder to parse the specified xml file  
		DocumentBuilder db = dbf.newDocumentBuilder();  
		Document document= db.parse(file);  
		document.getDocumentElement().normalize(); 
		
		//recuperation de chemin a partir du fichier 
		String chemin = document.getElementsByTagName("chemin").item(0).getTextContent();
		
		//recuperation de la ville de depart et arrivée a partir du fichier 
		String depart = document.getElementsByTagName("ville_depart").item(0).getTextContent();
		String arivee = document.getElementsByTagName("ville_arrivee").item(0).getTextContent();
		
		//recuperation du mode de calcule et moyen de transport a partir du fichier 
		String calculMode = document.getElementsByTagName("calculMode").item(0).getTextContent();
		String moyTransport = document.getElementsByTagName("moyTransport").item(0).getTextContent();
		
		//Get all employees
		NodeList nList = document.getElementsByTagName("c");
		System.out.println("======================================================");
		
		ArrayList<Double> nodes = new ArrayList<Double>();
		String c ;
		
		
		for (int temp = 0; temp < nList.getLength(); temp++)
		{
		 Node node = nList.item(temp);
		 System.out.println("\nNode Name :" + node.getNodeName());   
		 if (node.getNodeType() == Node.ELEMENT_NODE)
		 {
		    //Print each employee's detail
		    Element eElement = (Element) node;
		    System.out.println("cordonées id : "    + eElement.getAttribute("id"));
		    c = eElement.getTextContent() ;
		    System.out.println("cordonées : "  + c);
		    nodes.add(Double.valueOf(c));
		    
		 }
		}
		//pour dessiner le chemin 
		Launcher.pathPolylineLoad = new Polyline();
		Launcher.pathPolylineLoad.getPoints().addAll(nodes);
		Launcher.pathPolylineLoad.setStrokeWidth(2);
		Launcher.pathPolylineLoad.setStroke(Color.RED);
		Launcher.mapPane.getChildren().add(Launcher.pathPolylineLoad);
		
		//insitialiser les combobox avec la ville de depart et arrivee corespondant au chemin importé 
		Launcher.nomVoieD.getSelectionModel().select(new OverPassWay(depart));
		Launcher.nomVoieA.getSelectionModel().select(new OverPassWay(arivee));
		
		//insitialiser les combobox avec le mode de calcul et le moyen de transport au chemin importé 
		Launcher.modecalcule.getSelectionModel().select(calculMode);
		Launcher.moyendetransport.getSelectionModel().select(moyTransport);

		
        System.out.println("Done extracting XML File");
        
        return chemin ; 
    }
	
	
	
	 public static Document convertStringToXMLDocument(String villeD ,String villeA,String cheminI,ObservableList<Double> nodes, String calculMode,String moyTransport) throws ParserConfigurationException 
	    {
		
		 DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		 
         DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

         Document document = documentBuilder.newDocument();

         
         // Itineraire element
         Element Itineraire = document.createElement("Itineraire");
         //Itineraire.setIdAttribute("i", true);

         document.appendChild(Itineraire);
         
         
       

         // ville_depart element
         Element ville_depart = document.createElement("ville_depart");
         ville_depart.appendChild(document.createTextNode(villeD));
         Itineraire.appendChild(ville_depart);

         // ville_arrivee element
         Element ville_arrivee = document.createElement("ville_arrivee");
         ville_arrivee.appendChild(document.createTextNode(villeA));
         Itineraire.appendChild(ville_arrivee);

         // chemin element
         Element chemin = document.createElement("chemin");
         chemin.appendChild(document.createTextNode(cheminI));
         Itineraire.appendChild(chemin);
         
	      // calculMode element
	         Element calculMode1 = document.createElement("calculMode");
	         calculMode1.appendChild(document.createTextNode(calculMode));
	         Itineraire.appendChild(calculMode1);
	      
	         // moyTransport element
	         Element moyTransport1 = document.createElement("moyTransport");
	         moyTransport1.appendChild(document.createTextNode(moyTransport));
	         Itineraire.appendChild(moyTransport1);
         

      // cordonnees elements
         Element cordonnees = document.createElement("cordonnees");
         
         Itineraire.appendChild(cordonnees);
        
		 int i = 0;
         for (Double elem: nodes) {
        	 
        		 Element c = document.createElement("c");
                 
                 c.appendChild(document.createTextNode(Double.toString(elem)));
                 cordonnees.appendChild(c);
                 //c.setIdAttribute(Double.toString(i), true);
                 c.setAttribute("id", Integer.toString(i));
                 
                 
                 i++;
		
             

         }
		
         return document;
	
       }	
	 
	
	
}	
	








/* public static Document addPathToXMLDocument(Document document ,ObservableList<Double> nodes) throws ParserConfigurationException 
{
 
// cordonnees elements
 Element cordonnees = document.createElement("cordonnees");
 
 document.getElementById("i").appendChild(cordonnees);

 int i = 0;
 for (Double elem: nodes) {
	 
     Element c = document.createElement("c");
     c.appendChild(document.createTextNode(Double.toString(elem)));
     cordonnees.appendChild(c);

  
     
     i++;

 }
return document;
 
 
}*/








/*// create the xml file
    //transform the DOM Object to an XML File
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource domSource = new DOMSource(document);
    File f = new File ();
    StreamResult streamResult = new StreamResult(new File());
    transformer.transform(domSource, streamResult);*/
	
	/*public static void stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException, TransformerException{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(new InputSource(new StringReader(xmlSource)));
	    // Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();

	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(new File("/home/ramdane/eclipse-workspace/deskmap-s6/cache/test.xml"));
	    transformer.transform(source, result);
	}
	
	
	///deuxieme methode 
	public String format(String input) {
		return prettyFormat(input, "2");
	}

	public static String prettyFormat(String input, String indent) {
		Source xmlInput = new StreamSource(new StringReader(input));
		StringWriter stringWriter = new StringWriter();
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			transformer.setOutputProperty("{https://xml.apache.org/xslt}indent-amount", indent);
			transformer.transform(xmlInput, new StreamResult(stringWriter));

			return stringWriter.toString().trim();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	*/