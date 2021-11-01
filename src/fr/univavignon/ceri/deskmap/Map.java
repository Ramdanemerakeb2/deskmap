package fr.univavignon.ceri.deskmap;
	
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import javafx.concurrent.Task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.json.*;
 
import java.io.FileReader;
import java.util.Iterator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.net.URL;

/* TODO CORRECTION DES ROUTES 
 * TODO COLORATION DES RELATION
 * TODO	CORRECTION DES OCEAN
 * TODO	PROBLEME MER ALBORAN
 * TODO ENLEVER LE CANVAS 
 */
public class Map extends Graph implements Serializable {
	public OverPassRelation ville;
	public ArrayList<OverPassWay> highWays = new ArrayList<OverPassWay>(); 
	
	public static enum Deplacement { HAUT, BAS, GAUCHE, DROIT, ZOOM_IN, ZOOM_OUT, RESET };
	public double centerLat= 43.9492493, centerLon=4.8059012, scale = 4, relScale = 0.0002; // Avignon , 
//	public static double centerLat= 43.3327, centerLon=5.3577, scale = 0.0001; // Marseille ,, 
	//public static double centerLat= 43.2946, centerLon=5.3677, scale = 0.000009; , 
	//public static double centerLat=  43.4238, centerLon=4.5066, scale = 0.0007; 36.6258, -4.433
//	public static double centerLat= 35.4311, centerLon=-2.5792, scale = 0.005;
	public OverPassNode center;
	public OverPassNode limitTop;
	public OverPassNode limitBottom;
	public HashMap<String,OverPassNode> nodes;
	public HashMap<Long,OverPassWay> ways;
	public List<OverPassNode> lastPath;
	//public  HashMap<Long,OverPassWay> roadNetword;
	public HashMap<Long,OverPassRelation> relations;
	public static double mapDimension = 3000;
	//private Canvas map;
	
	
	public ArrayList<VertexNode> vertex;
	static Map mapInstance;
	
	private boolean mapLoaded = false;
	private double[] deltaDep = new double[2];
	
	public Map(OverPassRelation ville) {
		super();
		this.ville = ville;
		ville.getBBOX();
		Launcher.mapPane = new Pane();
		mapInstance = this;

		this.ways = new HashMap<Long,OverPassWay>();
		this.nodes = new HashMap<String,OverPassNode>();
		this.relations = new HashMap<Long,OverPassRelation>();
		this.limitTop = ville.bbox[0];
		this.limitBottom = ville.bbox[1];
		this.center = ville.center;
		System.out.println(center);
		this.scale = (limitTop.est-limitBottom.est)/mapDimension;
		System.out.println("SCALE "+scale);
	}

	public static Map getMapInstance() {
		return mapInstance;
	}
	public String path(OverPassWay depart, OverPassWay arrivee, String rechercheType, String calculeMode) {
		System.out.println(roadNetword.size());
		System.out.println(depart.noeuds.get(0).connectedNode.size());
		System.out.println(arrivee.noeuds.get(0).connectedNode.size());
		
		this.lastPath = getShortestPath(depart.noeuds.get(0), arrivee.noeuds.get(0), rechercheType,calculeMode);
		
		if(this.lastPath==null)
			return new String("Aucun Chemin Trouv�e");
		String result = "";
		OverPassWay oldWay = null;
		OverPassNode oldNode = null;
		ArrayList<Double> nodes = new ArrayList<Double>();
		Launcher.points = new ArrayList<Circle>();
		int distance=0;
		
		for(int i=lastPath.size()-1;i>=0;i--) {
			OverPassNode node = lastPath.get(i);
			if(oldNode!=null) {
				VertexNode vertex = oldNode.getConnectionVertex(node);
				if(oldWay==null|| (!vertex.getWay().name.equals(oldWay.name)&&!vertex.getWay().name.equals(""))) {
					result += vertex.getWay().name+"\n";		
				}
				distance += vertex.getDistance();
				nodes.addAll(vertex.getWay().pointInRange(oldNode, node));
				oldWay = vertex.getWay();
			}
			Circle circle = new Circle(3);
			double[] pos = node.getScreenPos(this.scale, this.center);
			circle.setLayoutX(pos[0]);
			circle.setLayoutY(pos[1]);
			Launcher.mapPane.getChildren().add(circle);
			Launcher.points.add(circle);
			oldNode = node;
			
		}
		if (Launcher.calculeMode=="Temps") {
			if(Launcher.rechercheType=="A pied") {
				float d = (float) distance/1000  ;
				float vitesse = 5	;
				float temps = d / vitesse ;
				int heure = (int) temps ; 
				int mins= (int)(60 * (temps - heure));
				result+="temps totale : "+heure+" heures et "+mins+"minutes";
			} 
			
			if(Launcher.rechercheType=="Voiture") {
				float d = (float) distance/1000  ;
				float vitesse = 60	;
				float temps = d / vitesse ;
				int heure = (int) temps ; 
				int mins= (int)(60 * (temps - heure));
				result+="temps totale : "+heure+" heures et "+mins+"minutes";
			}
			if(Launcher.rechercheType=="Velo") {
				float d = (float) distance/1000  ;
				float vitesse = 20	;
				float temps = d / vitesse ;
				int heure = (int) temps ; 
				int mins= (int)(60 * (temps - heure));
				result+="temps totale : "+heure+" heures et "+mins+"minutes";
			}
		}if (Launcher.calculeMode=="Distance") {
			result+="Distance Totale: "+distance+"m";	
		}
		
		Launcher.pathPolyline = new Polyline();
		Launcher.pathPolyline.getPoints().addAll(nodes);
		Launcher.pathPolyline.setStrokeWidth(2);
		Launcher.pathPolyline.setStroke(Color.BLUE);
		Launcher.mapPane.getChildren().add(Launcher.pathPolyline);
		return result;
	}
	public void stopRecherche() {
		if(Launcher.pathPolyline!=null) {
			Launcher.mapPane.getChildren().remove(Launcher.pathPolyline);
			Launcher.mapPane.getChildren().removeAll(Launcher.points);}
		if(Launcher.pathPolylineLoad!=null) {
			Launcher.mapPane.getChildren().remove(Launcher.pathPolylineLoad);
		}
	} 
	public Pane getMapPane() {
		if(mapLoaded) {

			
			Launcher.mapPane = new Pane();
			Launcher.mapPane.setPrefSize(mapDimension, mapDimension);
			Platform.runLater(new Runnable() {	
				@Override
				public void run() {
					drawMap();
				}
			});
			
			return Launcher.mapPane;
		}
		try {
			if(this.limitTop == null) {
				this.limitTop = OverPassNode.xyToNode(this.mapDimension+1000, -1000, this.centerLon, this.centerLat, this.scale);
				this.limitBottom = OverPassNode.xyToNode(-1000, this.mapDimension+1000, this.centerLon , this.centerLat, this.scale);
			}
			System.out.println("TOP "+this.limitTop);
			System.out.println("BOTTOM "+this.limitBottom);

			Polyline pol = new Polyline(0,500,1000,500);
			pol.setStrokeWidth(5);
		//	map.getChildren().add(pol);
			Launcher.mapPane.setPrefSize(mapDimension, mapDimension);
			getMap();
			Platform.runLater(new Runnable() {	
				@Override
				public void run() {
				drawMap();
				}
			}); 
			return Launcher.mapPane;

		} catch(Exception e) {
			e.printStackTrace();
			return null;

		}
	}


	public void drawNatural(OverPassNode limitTop,OverPassNode limitBottom) {
		String query = "";
		query += OverPassQuery.createQuery("natural","scrub");
		System.out.println(query);
		query += OverPassQuery.createQuery("natural","grassland");
		query += OverPassQuery.createQuery("natural","wood");
		query += OverPassQuery.createQuery("natural","heath");
		query += OverPassQuery.createQuery("natural","bay");
		query += OverPassQuery.createQuery("natural","water");
		OverPassQuery.execQuery(query, 25, "geom");
	}
	public void drawLanduse(OverPassNode limitTop,OverPassNode limitBottom) {
		String query = "";
		query += OverPassQuery.createQuery("landuse","forest");
		System.out.println(query);
		query += OverPassQuery.createQuery("landuse","grass");
		query += OverPassQuery.createQuery("landuse","reservoir");
		query += OverPassQuery.createQuery("landuse","basin");
		OverPassQuery.execQuery(query, 25, "geom");
	}
	public void drawRoad(OverPassNode limitTop,OverPassNode limitBottom) {
		String query = "";
		
		query += OverPassQuery.createQuery("highway", "footway");
		query += OverPassQuery.createQuery("highway", "cycleway");
		query += OverPassQuery.createQuery("highway", "residential");
		System.out.println(query);
		query += OverPassQuery.createQuery("highway", "pedestrian");
		query += OverPassQuery.createQuery("highway", "living_street");
		query += OverPassQuery.createQuery("highway", "unclassified");
		query += OverPassQuery.createQuery("highway", "tertiary");
		query += OverPassQuery.createQuery("highway", "secondary");
		query += OverPassQuery.createQuery("highway", "primary");
		query += OverPassQuery.createQuery("highway", "trunk");
		query += OverPassQuery.createQuery("highway", "motorway");
		query += OverPassQuery.createQuery("highway", "motorway_link");
		query += OverPassQuery.createQuery("highway", "trunk_link");
		query += OverPassQuery.createQuery("highway", "primary_link");
		query += OverPassQuery.createQuery("highway", "secondary_link");
		query += OverPassQuery.createQuery("highway", "tertiary_link");
		query += OverPassQuery.createQuery("highway", "service");
		query += OverPassQuery.createQuery("highway", "track");
		query += OverPassQuery.createQuery("highway", "bus_guideway");
		query += OverPassQuery.createQuery("highway", "escape");
		query += OverPassQuery.createQuery("highway", "road");
		
		query += OverPassQuery.createQuery("highway", "bridleway");
		query += OverPassQuery.createQuery("highway", "steps");

		query += OverPassQuery.createQuery("highway", "path");
		
		 

		 
		 
	 	 
		 
		OverPassQuery.execQuery(query, 25, "geom");
//		System.out.println("Fin");
	}
	public void getMap() {
		if(mapLoaded) return;
		drawRoad(this.limitTop,this.limitBottom);
		drawLanduse(this.limitTop, this.limitBottom);
		drawNatural(this.limitTop, this.limitBottom);
		mapLoaded = true;
	//	OverPassQuery.getPlace("sea",limitTop,limitBottom,nodes,ways,relations);
		serializeMap();
	}
	public void drawMap() {
		for(Entry<Long, OverPassRelation> relation: this.relations.entrySet()) {
			ArrayList<Polyline> waters = relation.getValue().drawBorder(Launcher.mapPane, this.scale, this.center);
			if(waters!=null) {
				Launcher.mapPane.getChildren().addAll(waters);
				//System.out.println("Water added");
			}
		}
		for(Entry<Long, OverPassWay> way: this.ways.entrySet()) {
			
			Node nd = way.getValue().draw(this.scale, this.center);
			if(nd != null) {
			//	way.getValue().getTextNom();
				Launcher.mapPane.getChildren().add(nd);
				if(!way.getValue().highWay.equals("")) {
					Tooltip.install(nd, new Tooltip(way.getValue().getNom()));
					nd.setOnContextMenuRequested(event -> {
						new ElementMenu(way.getValue()).show(nd, event.getSceneX(),event.getSceneY());
						
					});
				}
			}
		}
		drawBus();
		drawBusLive();

  		System.out.println("Scale "+this.relScale);
		System.out.println(this.ways.size());
	}
	
public void drawBusLive() {
	
	
	 Thread thread = new Thread(new Runnable() {
    	 Rectangle busLiveR;
    	  ArrayList<Rectangle> listeBus = new ArrayList<Rectangle>();
          @Override
         public void run() {
             Runnable updater = new Runnable() {
            	 
                 @Override
                 public void run() {
                	 if(!listeBus.isEmpty()) {
                		  for(int i=0;i<listeBus.size();i++) {
              	    	 Launcher.mapPane.getChildren().remove(listeBus.get(i));
                      }
                	 }
                 	 // build a JSON object
                	    JSONObject obj = new JSONObject(busLive.get());
                		JSONArray parameters = obj.getJSONArray("records");
                	    for(int i=0; i<parameters.length();i++) { 
                	    	JSONObject fields = parameters.getJSONObject(i).getJSONObject("fields");
                	    	JSONArray coordonnees = fields.getJSONArray("coordonnees");
                	    	String nom =fields.getString("nomligne");
                	     
                	    	double lon = coordonnees.getDouble(0);
                	    	double lat = coordonnees.getDouble(1);
                	    	System.out.println("NOM ARRET :"+nom);

                	    	System.out.println(lon+" "+lat);
                	 
                	    	busLive bus = new busLive(lat, lon);
                	    	double[] xy = bus.getScreenPos(Map.getMapInstance().scale, Map.getMapInstance().limitTop);
                 	    	 
                	    	ClassLoader loader = getClass().getClassLoader();
                	    	URL url = loader.getResource("./buslive.png");
                	    	
                			Image image = new Image(url.toString());
                			busLiveR = new Rectangle(xy[0], xy[1],25,25);
                			ImagePattern imagePattern = new ImagePattern(image);
                			busLiveR.setFill(imagePattern);
                	       
                			Text nombuslive = new Text(xy[0], xy[1], nom );
                			nombuslive.setFont(Font.font(5));
                			listeBus.add(busLiveR);
                	    	Launcher.mapPane.getChildren().addAll(busLiveR);
                	     }
                 }
             };

             while (true) {
                 try {
                     
                     Thread.sleep(10000);
                 
                     
                     } catch (InterruptedException ex) {
                 }
                
                 // UI update is run on the Application thread
                 Platform.runLater(updater);
             }
         }

     });
     // don't let thread prevent JVM shutdown
     thread.setDaemon(true);
     thread.start();
	 
 
}
public void drawBus() {
	
	
	 
	
	
    // build a JSON object
    JSONObject obj = new JSONObject(OverPassLignesBuTramIrigo.get());
	JSONArray parameters = obj.getJSONArray("records");
    for(int i=0; i<parameters.length();i++) { 
    	JSONObject fields = parameters.getJSONObject(i).getJSONObject("fields");
    	JSONArray coordonnees = fields.getJSONArray("coordonnees");
    	String nom =fields.getString("nomarret");
    	int numArret = fields.getInt("numarret");
    	double lon = coordonnees.getDouble(0);
    	double lat = coordonnees.getDouble(1);
    	System.out.println("NOM ARRET :"+nom+" Numéro : "+numArret);

    	System.out.println(lon+" "+lat);
 
    	OverPassLignesBuTramIrigo arret = new OverPassLignesBuTramIrigo(lat, lon);
    	double[] xy = arret.getScreenPos(Map.getMapInstance().scale, Map.getMapInstance().limitTop);
 
    	ClassLoader loader = getClass().getClassLoader();
    	URL url = loader.getResource("./icon.png");
    	
		Image image = new Image(url.toString());
		Rectangle arretBus = new Rectangle(xy[0], xy[1],15,15);  
		ImagePattern imagePattern = new ImagePattern(image);
		arretBus.setFill(imagePattern);
       
		Text nombus = new Text(xy[0], xy[1], nom );
		nombus.setFont(Font.font(5));
    	Launcher.mapPane.getChildren().addAll(arretBus,nombus);
     }
 
 
}
	public boolean serializeMap() {
		if(ville==null) return false;
		File fichier =  new File(".cache/"+ville.id+"/data.ser") ;
		ObjectOutputStream oos;
		try {
			fichier.getParentFile().mkdirs();
			fichier.createNewFile();
			 oos = new ObjectOutputStream(new FileOutputStream(fichier));
			 oos.writeObject(this);
			 System.out.println("Serialized");
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
			
		return true;
	}
	public void filtrerChemin(String type) {
		System.out.println("Filtrer "+type);
		if(type.equals("A pied")) {
		
			//Ajout du try catch pour apied
			for(OverPassWay way: this.highWays) {
				try{
					way.unFocus();
					if(way.apied) {
					
						way.focus();
						
					}
				}catch(NullPointerException e){
					System.out.println(e);
					
				}
			}
				
			
		}else if (type.equals("Velo")) {
			
			//Ajout du try catch pour velo
			
			 for(OverPassWay way: this.highWays) {
				 try{
					 way.unFocus();
					 if(way.velo) {
					
						 way.focus();
						
					}
				} catch(NullPointerException e){
					 System.out.println(e);
					
				}
			}
			
		}else if (type.equals("Voiture")) {
			
			
			//Ajout du try catch pour voiture
			 for(OverPassWay way: this.highWays) {
				 try{
				 	 way.unFocus();
					 if(way.voiture) {
						
						 way.focus();
						
					}
				} catch(NullPointerException e){
					 System.out.println(e);
					
				}
			}
			
		} else if (type.equals("Transport en commun")) {
			//Ajout du try catch pour Transport en commun
			 for(OverPassWay way: this.highWays) {
				 try{
					 way.unFocus();  
					 if(way.transportEnCommun) {
						
						 way.focus();
					}
				} catch(NullPointerException e){
					 System.out.println(e);
					
				}
			}
			
		}
	}


}
	
