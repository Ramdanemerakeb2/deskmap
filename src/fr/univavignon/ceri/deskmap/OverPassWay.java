package fr.univavignon.ceri.deskmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.Node;



import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class OverPassWay implements Serializable, Clickable{
	public long id;
	public ArrayList<OverPassRelation> relations;
	public ArrayList<OverPassNode> noeuds;
	public ArrayList<VertexNode> connectedWays;
	public ArrayList<OverPassNode> inter = new ArrayList<OverPassNode>();
	public String name = "";
	public static String highWay = "";
	public String natural = "";
	public String leisure = "";
	public String access="";
	public boolean drawn = false;
	public boolean apied = false;  
	public boolean velo = false;
	public boolean voiture = false; 
	public boolean transportEnCommun = false; 
	public String landuse = "";
	public static double pixelCenterX = 3000;
	public static double pixelCenterY = 3000;
	public Node polyline;
	public double distanceTotal = 0;
	private double distanceTmp;
	public static double distMax = 0;
	public static OverPassNode node1 = null;
	public static OverPassNode node2 = null;
	public OverPassNode[] bbox;
	public OverPassWay(String name) {
		this.name = name;
		connectedWays = new ArrayList<VertexNode>();
	}
	public OverPassWay(Element elem) {
		connectedWays = new ArrayList<VertexNode>();
		relations = new ArrayList<OverPassRelation>();
		this.id = Long.parseLong(elem.getAttribute("ref"));
		this.noeuds = new ArrayList<OverPassNode>();
		NodeList noeudsElm = elem.getElementsByTagName("nd");
		for(int i=0;i<noeudsElm.getLength();i++) {
			Element noeudXml = (Element) noeudsElm.item(i);
			if(noeudXml.hasAttribute("lon")) {
				double lon =  Double.parseDouble(noeudXml.getAttribute("lon"));
				double lat =  Double.parseDouble(noeudXml.getAttribute("lat"));
				noeuds.add(new OverPassNode(lon,lat));
			}
		}	
	}
	public OverPassWay(Element elem, HashMap<String, OverPassNode> nodes) {
		connectedWays = new ArrayList<VertexNode>();
		relations = new ArrayList<OverPassRelation>();
		this.id = Long.parseLong(elem.getAttribute("id"));
		this.noeuds = new ArrayList<OverPassNode>();
		int nodeCount = 0;
		Element bb = (Element) elem.getElementsByTagName("bounds").item(0);
		if(bb != null) {
			double minlat = Double.parseDouble(bb.getAttribute("minlat"));
			double maxlat = Double.parseDouble(bb.getAttribute("maxlat"));
			double minlon = Double.parseDouble(bb.getAttribute("minlon"));
			double maxlon = Double.parseDouble(bb.getAttribute("maxlon"));
			bbox = new OverPassNode[2];
			bbox[0] = new OverPassNode(minlon, minlat);
			bbox[1] = new OverPassNode(maxlon, maxlat);
		}
		NodeList tags = elem.getElementsByTagName("tag");
		for(int i =0; i<tags.getLength();i++ ) {
 			Element membre =(Element) tags.item(i);
			System.out.println(membre.getAttribute("k"));

			String k = membre.getAttribute("k");
			if(k.equals("name")) {
				name = membre.getAttribute("v");
			}
			if(k.equals("highway")) {
				highWay = membre.getAttribute("v");
				if(highWay.equals("primary") || highWay.equals("trunk")) {
					apied = false;
					velo = false;
					voiture = true;
					transportEnCommun = true;
				}
				else if (highWay.equals("secondary") || highWay.equals("tertiary")){
					apied = true;
					velo = true;
					voiture = true;
					transportEnCommun = true;
				}
				else if (highWay.equals("pedestrian") || highWay.equals("footway") || highWay.equals("cycleway") || highWay.equals("residential") || highWay.equals("living_street") || highWay.equals("unclassified")){
					 
					apied = true;
					velo = true;
					voiture = false;
					transportEnCommun = false;
				}
				
				//Ajout des chemin "escape" "bus_ghidway :
				else if (highWay.equals("escape")){
					 
					 apied = false;
					 velo = false;
					 voiture= true;
					 transportEnCommun=true;
				
				}
			      else if (highWay.equals("bus_guideway")){
					 
					 apied = false;
					 velo = false;
					 voiture= false;
					 transportEnCommun=true;
				}

			
			}
			 
			if(k.equals("landuse")) {
				landuse = membre.getAttribute("v");
			}
			if(k.equals("leisure")) {
				leisure = membre.getAttribute("v");
			}
			if(k.equals("natural")) {
				natural = membre.getAttribute("v");
			}
		}
		NodeList noeudsElm = elem.getElementsByTagName("nd");
		OverPassNode oldNode = null;
		for(int i=0;i<noeudsElm.getLength();i++) {
			Element noeudXml = (Element) noeudsElm.item(i);
			if(noeudXml.hasAttribute("lon")) {
				double lon =  Double.parseDouble(noeudXml.getAttribute("lon"));
				double lat =  Double.parseDouble(noeudXml.getAttribute("lat"));
				addNode(0, lon, lat);
			}
			else{
				long nodeId = Long.parseLong(noeudXml.getAttribute("ref"));
				addNode(nodeId, 0, 0);
			}
		}	
		//System.out.println("Added way: "+id+" Nodes count: "+nodeCount);
	}
	
	
	
	

	
	
	
	
	
	
		

	public double distance(OverPassNode node) {
		double distance = 0;
		int i=1;
		for(;i<noeuds.size();i++) {
			distance += noeuds.get(i).distance(noeuds.get(i-1));
			if(noeuds.get(i).lat == node.lat && noeuds.get(i).lon == node.lon) {
				System.out.println("distance "+distance);
				return distance;
			}
		}
		System.out.println("hop "+this.noeuds.contains(node));
		return distance;
	}
	
	public ArrayList<Double> pointInRange(OverPassNode node1, OverPassNode node2){
		if(!noeuds.contains(node1)||!noeuds.contains(node2)) return null;
		boolean debut = false;
		ArrayList<Double> nodes = new ArrayList<Double>();
		for(OverPassNode node : noeuds) {
			if(node==node1) debut = true;
			if(debut) {
				double[] pos = node.getScreenPos(Map.getMapInstance().scale, Map.getMapInstance().center);
				nodes.add(pos[0]);
				nodes.add(pos[1]);
			}
			if(node==node2) break;
		}
		if(nodes.size()==0) {
			debut = false;
			for(OverPassNode node: noeuds) {
				if(node==node2) debut = true;
				if(debut) {
					double[] pos = node.getScreenPos(Map.getMapInstance().scale, Map.getMapInstance().center);
					nodes.add(pos[0]);
					nodes.add(pos[1]);
				}
				if(node==node1) break;
			}	
		}
		return nodes;
	}
	public void addNode(long ref, double lon, double lat) {
		OverPassNode node=null;
		if(ref==0) {
			Map map = Map.getMapInstance();
			String refString = OverPassNode.generateRef(lat, lon);
			if(map.nodes.containsKey(refString)) {
				node = map.nodes.get(refString);
				node.chemins.add(this.id);
				noeuds.add(node);
				if(!highWay.equals("")) {
					if(noeuds.size()>=2) {
						distanceTotal += noeuds.get(noeuds.size()-1).distance(noeuds.get(noeuds.size()-2));
					}
					//attachNode(node);
				}
			}
			else {
				node = new OverPassNode(-1,lon,lat);
				node.chemins.add(this.id);
				noeuds.add(node);
				map.nodes.put(refString, node);
			}
		}
		int size = noeuds.size();
		if(size>1) {
			OverPassNode oldNode = noeuds.get(size-2);
			int distance = (int)node.distance(oldNode);
			node.connectedNode.add(new VertexNode(oldNode, distance, this));
			oldNode.connectedNode.add(new VertexNode(node, distance, this));
			if(!Map.getMapInstance().roadNetword.containsKey(node.ref2))
				Map.getMapInstance().roadNetword.put(node.ref2, node);
		}
		else {
			if(!Map.getMapInstance().roadNetword.containsKey(node.ref2))
				Map.getMapInstance().roadNetword.put(node.ref2, node);
		}
	}
	public OverPassWay(long id, ArrayList<OverPassNode> noeuds) {
		super();
		this.id = id;
		this.noeuds = noeuds;
	}

	public void connectWay(Node nd) {
		
	}
	public static boolean serializeWay(HashMap<Long,OverPassWay> ways, String nom) {
		File fichier =  new File("cache/"+nom+"/ways.ser") ;

		ObjectOutputStream oos;
		try {
			fichier.getParentFile().mkdirs();
			fichier.createNewFile();
			 oos = new ObjectOutputStream(new FileOutputStream(fichier));
			 oos.writeObject(ways) ;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
			
		return true;
	}

	public static boolean track = true;
	public void getTextNom() {
		if(name.equals(""))	return;
		int nameIndex =0;
		OverPassNode oldNode = null;
		for(OverPassNode node: noeuds) {
			if(oldNode==null) {
				oldNode = node;
				continue;
			}
			double scale = Map.getMapInstance().scale;
			double[] center = oldNode.getScreenPos(scale, Map.getMapInstance().limitTop);
			float angle = 360-(float) Math.toDegrees(Math.atan2(oldNode.nord - node.nord, oldNode.est - node.est));
			//float angle = (float) Math.toDegrees(Math.acos((node1.est-node2.est)/node1.distance(node2)));
			  /* if(angle < 0){
			        angle += 360;
			    }*/
			   // System.out.println("Text de "+node1.est+" "+node1.nord+" a "+node2.est+" "+node2.nord);
			
			int pixDistance = (int)oldNode.getScreenDistance(node)/4;
			System.out.println(pixDistance);
			if(pixDistance+nameIndex>name.length()-1) pixDistance = name.length()-1;
			System.out.println("NoM "+pixDistance+" "+nameIndex);
			String cutName = name.substring(nameIndex, pixDistance);
			if(pixDistance>=name.length()-1) nameIndex = 0;
			else nameIndex = pixDistance;
		    Text nom = new Text(center[0],center[1],cutName);
		    nom.getTransforms().add(new Rotate(angle,center[0],center[1]));
		    Launcher.mapPane.getChildren().add(nom);
		}
	}
	ArrayList<Double> getWayPointArrayList(double scale, OverPassNode center){
		
		
        ArrayList<Double> xy = new ArrayList<Double>();
		int i = 0;
		double[] lastPoint = null;
		double[] lastPointOnZone=null;
		for(OverPassNode noeud: noeuds) {
			double xyPos[] = noeud.getScreenPos(scale, Map.getMapInstance().limitTop);
			double x = xyPos[0];
			double y = xyPos[1]; 
			xy.add(x);xy.add(y);
 
			
		}
		return xy;
	}
	static double[] findInter(double x, double y, double x2, double y2) {
		if((x<0 && y<0)) {
			double[] inter1 = lineLineIntersection(x, y, x2, y2, 0, 0, Map.mapDimension, 0);
			double[] inter2 = lineLineIntersection(x, y, x2, y2, 0, 0, 0, Map.mapDimension);
			if(inter1[0] <= Map.mapDimension && inter1[0]>=0 && inter1[1]<=Map.mapDimension && inter1[1]>=0)
				return inter1;
			return inter2;
		}
		
		if(x>Map.mapDimension && y>Map.mapDimension) {
			double[] inter1 = lineLineIntersection(x, y, x2, y2, Map.mapDimension, 0, Map.mapDimension, Map.mapDimension);
			double[] inter2 = lineLineIntersection(x, y, x2, y2, 0, Map.mapDimension, Map.mapDimension, Map.mapDimension);		
			if(inter1[0] <= Map.mapDimension && inter1[0]>=0 && inter1[1]<=Map.mapDimension && inter1[1]>=0)
				return inter1;
			return inter2;
		}
		if(x<0 && y>Map.mapDimension) {
			double[] inter1 = lineLineIntersection(x, y, x2, y2, 0, 0, 0, Map.mapDimension);
			double[] inter2 = lineLineIntersection(x, y, x2, y2, 0, Map.mapDimension, Map.mapDimension, Map.mapDimension);
			if(inter1[0] <= Map.mapDimension && inter1[0]>=0 && inter1[1]<=Map.mapDimension && inter1[1]>=0)
				return inter1;
			return inter2;
		}
		if(x>Map.mapDimension && y<0) {
			double[] inter1 = lineLineIntersection(x, y, x2, y2, 0, Map.mapDimension, Map.mapDimension, Map.mapDimension);
			double[] inter2 = lineLineIntersection(x, y, x2, y2, 0, 0, Map.mapDimension, 0);
			if(inter1[0] <= Map.mapDimension && inter1[0]>=0 && inter1[1]<=Map.mapDimension && inter1[1]>=0)
				return inter1;
			return inter2;
		}
		if(x<0 && y>0 && y<Map.mapDimension) {
			return lineLineIntersection(x, y, x2, y2, 0, 0, 0, Map.mapDimension);
		}
		if(x<Map.mapDimension && x>0 && y>Map.mapDimension) {
			return lineLineIntersection(x, y, x2, y2, 0, Map.mapDimension, Map.mapDimension, Map.mapDimension);			
		}
		if(x>Map.mapDimension && y>0 && y<Map.mapDimension) {
			return lineLineIntersection(x, y, x2, y2, Map.mapDimension, 0, Map.mapDimension, Map.mapDimension);
		}
		if(x<Map.mapDimension && x>0 && y<0) {
			return lineLineIntersection(x, y, x2, y2, 0, 0, Map.mapDimension, 0);
		}
		return null;
	}
	static double[] lineLineIntersection(double x2, double y2, double x22, double y22, double d, double e, double f, double g) 
    { 
		 	double a1 = y22 - y2; 
	        double b1 = x2 - x22; 
	        double c1 = a1*(x2) + b1*(y2); 
	        double a2 = g - e; 
	        double b2 = d - f; 
	        double c2 = a2*(d)+ b2*(e); 
	       
	        double determinant = a1*b2 - a2*b1; 
	       
	        if (determinant == 0) 
	        { 
	            return null; 
	        } 
	        else
	        { 
	            double x = (b2*c1 - b1*c2)/determinant; 
	            double y = (a1*c2 - a2*c1)/determinant; 
	            double[] res = { x,y};
	            return res;
	        } 
    } 
	public void unFocus() {
		
		// colorer les chemins non sélection en blanc : 
		
		
		     if(highWay.equals("primary") || highWay.equals("trunk")) {
			((Polyline)(polyline)).setStroke(Color.WHITE);
		}
		    else if (highWay.equals("secondary")||highWay.equals("tertiary")){
			((Polyline)(polyline)).setStroke(Color.WHITE);
		}
	
		       else if (highWay.equals("pedestrian") || highWay.equals("footway") || highWay.equals("cycleway") || highWay.equals("residential") || highWay.equals("living_street") || highWay.equals("unclassified")){
			((Polyline)(polyline)).setStroke(Color.WHITE);
		}
		    else if (highWay.equals("escape")){
			((Polyline)(polyline)).setStroke(Color.WHITE);
		}
		    else if (highWay.equals("bus_guideway")){
			((Polyline)(polyline)).setStroke(Color.WHITE);
		}
	}
	
	public  void focus() {
		((Polyline)(polyline)).setStroke(Color.PURPLE);
	}
	
	public Node drawArea(double scale, OverPassNode center) {
		Polygon pol = new Polygon();
		
		ArrayList<Double> xy = getWayPointArrayList(scale, center);
		pol.getPoints().addAll(xy);
		if(natural.equals("water") || landuse.equals("basin") || landuse.equals("reservoir"))
			pol.setFill(Color.LIGHTSKYBLUE);
		else
			pol.setFill(Color.LIGHTGREEN);
		return (Node)pol;
	}

	 
	public OverPassWay invert() {
		ArrayList<OverPassNode> invNoeuds = new ArrayList<OverPassNode>(noeuds.size());
		for(int i=noeuds.size()-1; i>=0;i--) {
			invNoeuds.add(noeuds.get(i));
		}
		return new OverPassWay(id, invNoeuds);
	}
	public Node draw( double scale, OverPassNode center) {
		if(relations.size()!=0) return null;
		drawn = true;
		if(!leisure.equals("") || !landuse.equals("") || !natural.equals("")) {
			
			return drawArea(scale,center);
		}
		Polyline poly = new Polyline();
		
		if(highWay.equals("primary") || highWay.equals("trunk")) {
			
			poly.setStrokeWidth(6);
			poly.setStroke(Color.LIGHTPINK);
		}
		else if (highWay.equals("secondary")){
			poly.setStrokeWidth(5);
			poly.setStroke(Color.WHITE);
		}
		else if (highWay.equals("tertiary")){
			poly.setStrokeWidth(3);
			poly.setStroke(Color.WHITE);
		}
		else if (highWay.equals("cycleway") ){
			poly.setStrokeWidth(2);
			poly.setStroke(Color.WHITE);
		}
		/* if (highWay.equals("cycleway")&&(velo= true)) {
				poly.setStrokeWidth(2);
				poly.setStroke(Color.RED);
				}
		 if (highWay.equals("footway")&&(apied= true)) {
				
				poly.setStrokeWidth(2);
				poly.setStroke(Color.BLACK);
				}  */
		   
		
		 
 		else if (highWay.equals("pedestrian") || highWay.equals("residential") || highWay.equals("living_street") || highWay.equals("unclassified") || highWay.equals("motorway")|| highWay.equals("motorway_link")|| highWay.equals("trunk_link") || highWay.equals("primary_link")  || highWay.equals("secondary_link")|| highWay.equals("tertiary_link") || highWay.equals("service")  || highWay.equals("track")|| highWay.equals("bus_guideway") || highWay.equals("escape")  || highWay.equals("road") || highWay.equals("footway")  || highWay.equals("bridleway")|| highWay.equals("steps")|| highWay.equals("path")              ){
			poly.setStrokeWidth(2);
			poly.setStroke(Color.WHITE);
			
		}
		 

        poly.getPoints().addAll(getWayPointArrayList(scale,center));
        polyline = poly;
		return poly;
	}
	

	/*public void draw(GraphicsContext gc, double scale, OverPassNode center) { // CANVAS
		if(relations.size()!=0) return ;
		drawn = true;
		if(!leisure.equals("") || !landuse.equals("") || !natural.equals("")) {
			
			drawArea(gc,scale,center);
		}
		if(highWay.equals("primary") || highWay.equals("trunk")) {
			gc.setStroke(Color.YELLOW);
			gc.setLineWidth(8);
		}
		else if (highWay.equals("secondary")){
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(6);
		}
		else if (highWay.equals("tertiary")){
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(4);
		}
		else if (highWay.equals("pedestrian") || highWay.equals("residential") || highWay.equals("living_street") || highWay.equals("unclassified")){
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(3);
			
		}
		else {
			gc.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
			gc.setLineWidth(1);
		}
		double[][] xy = getWayPointArray(scale, center);
        gc.strokePolyline(xy[0], xy[1], xy[1].length);

	}*/
	public String toString() {
		return getNom();
	}
	@Override
	public String getNom() {
		if(name.equals(""))
			return ""+id;
		return name+" "+id;
	}
	@Override
	public String getType() {
		if(!highWay.equals("")) return highWay;
		if(!natural.equals("")) return natural;
		if(!leisure.equals("")) return leisure;
		if(!landuse.equals("")) return landuse;
		return "";
	}
	
}
