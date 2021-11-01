package fr.univavignon.ceri.deskmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.javafx.css.converters.StringConverter;
import com.sun.javafx.tk.Toolkit.Task;

import fr.univavignon.ceri.deskmap.Map.Deplacement;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * DESK Map v1.0
 * 
 * @author 
 *
 * 
 * 
 */
public class Launcher extends Application implements  EventHandler<MouseEvent> 
{	
	public static String calculeMode ;
	public static String rechercheType ;
	public static OverPassWay departWay;
	public static OverPassWay arriveeWay;
	public static OverPassRelation selectedRelation;
	public static Circle departPoint;
	public static Circle arriveePoint;
	public static boolean rechercheActive = false;
	public static TextField numVoieAText;
	public static TextField numVoieDText;
	public static String moytransport;
	public static ComboBox<OverPassWay> nomVoieA;
	public static Pane mapPane;
	public static Pane mapZone;
	public static ComboBox<OverPassWay> nomVoieD; 
	private static Button depDroite;
	private static Button depGauche;
	private static Button depBas;
	private static Button depHaut;
	private static Button zoomOut;
	private static Button zoomIn;
	public static ArrayList<Circle> points;
	public static ComboBox<String> modecalcule;
	public static ComboBox<String> moyendetransport ;
 
	public static Polyline pathPolyline;
	public static Polyline pathPolylineLoad;
	private static double[] deltaDep = new double[2];
	static OverPassRelation ville ;
	public static void addMap(OverPassRelation v) {
			Launcher.ville = v;
			if(mapPane!=null) {
				mapZone.getChildren().remove(mapPane);
			}
			new Thread( ()->{
			
			if(ville==null) ville = new OverPassRelation("",null,null,178351);
			Map carte = MapCreator.createMap(ville);
//				Map carte = null;
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					if(carte!=null) {
						mapPane = carte.getMapPane();
						mapPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
						mapZone.getChildren().add(mapPane);
						nomVoieD.getItems().setAll(Map.getMapInstance().highWays);
						nomVoieA.getItems().setAll(Map.getMapInstance().highWays);
						
						mapPane.setOnScroll(event-> {
							if(event.getDeltaY()<0)
								deplacer(Deplacement.ZOOM_OUT,1.1);
							else if(event.getDeltaY()>0)
								deplacer(Deplacement.ZOOM_IN,1.1);
						});
						mapPane.setOnMousePressed(new EventHandler<MouseEvent>() {
						  @Override public void handle(MouseEvent mouseEvent) {
						    // record a delta distance for the drag and drop operation.
							 deltaDep[0] = mapPane.getLayoutX() - mouseEvent.getSceneX();
							 deltaDep[1] = mapPane.getLayoutY() - mouseEvent.getSceneY();
							 mapPane.setCursor(Cursor.MOVE);
						  }
						});
						mapPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
						  @Override public void handle(MouseEvent mouseEvent) {
							  mapPane.setCursor(Cursor.HAND);
						  }
						});
						mapPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
						  @Override public void handle(MouseEvent mouseEvent) {
							  double newPosX = mouseEvent.getSceneX() + deltaDep[0];
							  double newPosY = (mouseEvent.getSceneY() + deltaDep[1]);
							  double scaleX = mapZone.getScaleX();
							  double scaleY = mapZone.getScaleY();
							  System.out.println(newPosX*mapPane.getScaleX());
								  mapPane.setLayoutX(newPosX);
							  
								  mapPane.setLayoutY(newPosY);
							 
							  
						  }
						});
						mapPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
						  @Override public void handle(MouseEvent mouseEvent) {
							  mapPane.setCursor(Cursor.HAND);
						  }
						});
					}
					else {
						textAreaEtat.setText("Erreur lors de la recherche de la ville");
					}
					
				}});
			
			}).start();
	}//
	public static void main(String[] args){
		try {

			launch(args);
		} catch (Exception e) {

		    // Answer:
		    e.getCause().printStackTrace();
		}
		
	}
	private static Button reset;
	private static TextArea textAreaEtat;


	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("DeskMap_V1.0");
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 1200, 900, Color.CADETBLUE);
			scene.getStylesheets().add(getClass().getResource("launcher.css").toExternalForm());
			primaryStage.setScene(scene);
			
			
			// Interface graphique
			VBox vBox1 = new VBox();
	        vBox1.setPadding(new Insets(0));
	        vBox1.setId("vBox1");
  
 	        VBox ville = new VBox();
 	        ville.setPadding(new Insets(0));
	        ville.setSpacing(3);
	        Label labelX = new Label("Ville");
	        labelX.setPadding(new Insets(3));
	        ComboBox<OverPassRelation> champTexteVille = new ComboBox<OverPassRelation>();
	        champTexteVille.setEditable(true);
	        AutoComplete<OverPassRelation> villeAutoComplete = new AutoComplete<OverPassRelation>(champTexteVille, true);
	        Button rechercher = new Button ("Go");
	        ville.getChildren().addAll(labelX,champTexteVille,rechercher);
	       
	        HBox depart = new HBox();
	        depart.setPadding(new Insets(3));
	        depart.setSpacing(3);
	        Label labelD = new Label("Départ");
	        labelD.setPadding(new Insets(3));
	        numVoieDText = new TextField();
	        numVoieDText.setMaxWidth(70);   
	        nomVoieD = new ComboBox<OverPassWay>();
	        nomVoieD.setEditable(true);
	        new AutoComplete<OverPassWay>(nomVoieD,false);
	        depart.getChildren().addAll(numVoieDText, nomVoieD);
	        //Ajout d'une HBOX
	        
	        HBox arrivee = new HBox();
	        arrivee.setPadding(new Insets(3));
	        arrivee.setSpacing(3);
	        Label labelA = new Label("Arrivée");
	        labelA.setPadding(new Insets(3));
	        numVoieAText = new TextField();
	        numVoieAText.setMaxWidth(70);
	        nomVoieA = new ComboBox<OverPassWay>();
	        nomVoieA.setEditable(true);
	        new AutoComplete<OverPassWay>(nomVoieA,false);
	        arrivee.getChildren().addAll(numVoieAText, nomVoieA);
	        
 	      
	        Label labelmodedecalcule = new Label("Mode de calcule");
	        labelmodedecalcule.setPadding(new Insets(3));
	        String modeCalculeTableau[] = 
                { "Temps", "Distance"}; 
	           modecalcule = 
                    new ComboBox<String>(FXCollections 
                                .observableArrayList(modeCalculeTableau)); 
 

	        Label labelmoyendetransport = new Label("Moyen de Transport Recherche");
	        labelmoyendetransport.setPadding(new Insets(3));
	        String moyendetransportTableau[] = 
                { "A pied", "Velo","Voiture","Transport en commun"}; 
	        		moyendetransport = 
                    new ComboBox<String>(FXCollections 
                                .observableArrayList(moyendetransportTableau)); 
	        
	       
	        Label labelmoyendetransport1 = new Label("Filtre Moyen de Transport");
	        labelmoyendetransport1.setPadding(new Insets(3));
	        String moyendetransportTableau1[] = 
                { "A pied", "Velo","Voiture","Transport en commun"}; 
	        ComboBox<String> filtremoyendetransport = 
                    new ComboBox<String>(FXCollections 
                                .observableArrayList(moyendetransportTableau1)); 
	        
	    
	        
	        
	        CheckBox checkBox = new CheckBox();
	        checkBox.setText("Correspandance");
	        
	        CheckBox checkBox1 = new CheckBox();
	        checkBox1.setText("Sans Correspandance");
	        HBox lancerR = new HBox();
	        lancerR.setPadding(new Insets(3));
	        lancerR.setSpacing(3);
	        Button recherche = new Button ("Rechercher");
	        recherche.setStyle("-fx-background-color: #fb1313");
 	        Button arret = new Button ("Arreter");
	        arret.setStyle("-fx-background-color: #fb1313");
	        arret.setDisable(true);

 	        lancerR.getChildren().addAll(recherche, arret);
	        
	        
	        VBox itineraire = new VBox();
	        itineraire.setPadding(new Insets(3));
	        Label labelI = new Label("Itinéraire");
	        labelI.setPadding(new Insets(3));
	        TextArea textAreaIti = new TextArea();
	        textAreaIti.setEditable(false);
	        textAreaIti.setMaxWidth(300);
	        textAreaIti.setMaxHeight(300);
	        itineraire.getChildren().add(textAreaIti);
	        
	        //savegarde et importation 
	        HBox XML = new HBox();
	        XML.setPadding(new Insets(3));
	        XML.setSpacing(3);
	        Button sauvegarder = new Button ("sauvegarder itineraire XML");
	        sauvegarder.setStyle("-fx-background-color: #fb1313");
	        sauvegarder.setDisable(true);
 	        Button importer = new Button ("importer itineraire XML");
 	        importer.setStyle("-fx-background-color: #fb1313");

	        XML.getChildren().addAll(sauvegarder, importer);

	        
	        
	        HBox barre_etat = new HBox();
	        textAreaEtat = new TextArea();
	        textAreaEtat.setEditable(false);
	        textAreaEtat.setMinSize(1200, 20);
	        barre_etat.getChildren().add(textAreaEtat);
	        
	        
	        // Zone carte
	        Pane map = new Pane();
	        map.setMinSize(850, 845);
	        vBox1.getChildren().addAll(ville,labelD,depart,labelA,arrivee,labelmodedecalcule,modecalcule,labelmoyendetransport,moyendetransport,labelmoyendetransport1,filtremoyendetransport,checkBox,checkBox1,lancerR,labelI,XML);
	        
	     
	        
	        vBox1.setPrefSize(200, 845);
	        SplitPane split = new SplitPane(vBox1, map);
	        split.setPrefHeight(845);
	        
	        
	        // Barre de s�paration
	        Platform.runLater(() -> {
	        	StackPane pane = (StackPane) split.lookup(".split-pane-divider");
		        
		        DoubleExpression scale = pane.widthProperty().multiply(0.50);
		        
		        Polygon leftArrow = new Polygon(0, 1, 1, 0, 1, 2);
		        leftArrow.setCursor(Cursor.DEFAULT);
		        leftArrow.scaleXProperty().bind(scale);
		        leftArrow.scaleYProperty().bind(scale);
		        leftArrow.translateYProperty().bind(scale.multiply(2));
		        leftArrow.translateXProperty().bind(scale);
		        
		        leftArrow.setOnMouseClicked(e -> {
		    		split.setDividerPosition(0, -1);
		        });
		        
		        Polygon rightArrow = new Polygon(1, 1, 0, 0, 0, 2);
		        rightArrow.setCursor(Cursor.DEFAULT);
		        rightArrow.scaleXProperty().bind(scale);
		        rightArrow.scaleYProperty().bind(scale);
		        rightArrow.translateYProperty().bind(scale.multiply(5));
		        rightArrow.translateXProperty().bind(scale);
		        
		        rightArrow.setOnMouseClicked(e -> {
		        	map.setMinSize(0, 845);
		    		split.setDividerPosition(0, 1);
		        });
		        
		        pane.getChildren().add(leftArrow);
		        pane.getChildren().add(rightArrow);
	        });
	        
	        
	        // Bouton d�placement
	        VBox deplacement = new VBox();
	        HBox haut = new HBox(200);
	        HBox bas = new HBox(20);
	        HBox centre = new HBox(20);
	        haut.setAlignment(Pos.CENTER);
	        bas.setAlignment(Pos.CENTER);
	        
	        
	        depHaut = new Button("H");
	        depBas = new Button("B");
	        depGauche = new Button("<");
	        depDroite = new Button(">");
	        reset = new Button("reset");
	        
	        haut.getChildren().add(depHaut);
	        bas.getChildren().add(depBas);
	        centre.getChildren().addAll(depGauche,reset, depDroite);
	        deplacement.getChildren().addAll(haut,centre,bas);
	        deplacement.setLayoutX(scene.getWidth()-530);
	        deplacement.setLayoutY(10);
	        deplacement.setSpacing(5);
	        
	        
	        // Bouton Zoom
	        VBox zoom = new VBox(10);
	        zoomIn = new Button("+");
	        zoomOut = new Button("-");
	        zoomIn.getStyleClass().add("zoom-button");
	        zoomOut.getStyleClass().add("zoom-button");
	        zoom.getChildren().add(zoomIn);
	        zoom.getChildren().add(zoomOut);
	        zoom.setLayoutX(scene.getWidth()-450);
	        zoom.setLayoutY(scene.getHeight()-400);
	        
	        	
			mapZone = new Pane();
			 
			map.getChildren().add(mapZone);
			
			mapZone.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
//			mapPane.setLayoutX(-3000);
//			mapPane.setLayoutY(-3000);
			addMap(null);
			map.getChildren().add(zoom);
			map.getChildren().add(deplacement);
			
			
			VBox resultat = new VBox();
			resultat.setPadding(new Insets(10));
			resultat.setSpacing(10);
			TextField resultatsInfo = new TextField("");
			resultatsInfo.setMinWidth(textAreaIti.getWidth());
			resultat.getChildren().add(resultatsInfo);
			vBox1.getChildren().add(resultat);
			champTexteVille.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					if(champTexteVille.getSelectionModel().getSelectedItem()!=null) {
						selectedRelation = champTexteVille.getSelectionModel().getSelectedItem();
					}
					else
						recherche.setDisable(true);
				}
			});
			nomVoieA.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					if(nomVoieA.getSelectionModel().getSelectedItem()!=null) {
						arriveeWay = nomVoieA.getSelectionModel().getSelectedItem();
						if(nomVoieD.getSelectionModel().getSelectedItem()!=null)
							recherche.setDisable(false);
					}
					else
						recherche.setDisable(true);
				}
			});
			nomVoieD.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					if(nomVoieD.getSelectionModel().getSelectedItem()!=null) {
						departWay = nomVoieD.getSelectionModel().getSelectedItem();
						if(nomVoieA.getSelectionModel().getSelectedItem()!=null)
							recherche.setDisable(false);
					}
					else
						recherche.setDisable(true);
				}
			});
			
			
		moyendetransport.getSelectionModel().selectedItemProperty()
		    .addListener(new ChangeListener<String>() {
		        public void changed(ObservableValue<? extends String> observable,
		                            String oldValue, String newValue) {
		            System.out.println("Value is: "+newValue);
		           rechercheType = newValue;
		           System.out.println("Value : "+rechercheType);
		            
		            
		           
		        }   
		});
			
			filtremoyendetransport.getSelectionModel().selectedItemProperty()
		    .addListener(new ChangeListener<String>() {
		        public void changed(ObservableValue<? extends String> observable,
		                            String oldValue, String newValue) {
		            System.out.println("Value is: "+newValue);
		            Map.getMapInstance().filtrerChemin(newValue);
		            
		            
		           
		        }   
		});
			
			modecalcule.getSelectionModel().selectedItemProperty()
		    .addListener(new ChangeListener<String>() {
		        public void changed(ObservableValue<? extends String> observable,
		                            String oldValue, String newValue) {
		            System.out.println("Value is: "+newValue);
		           calculeMode = newValue;
		           System.out.println("Value : "+calculeMode);
		            
		           
		        }   
		});
			
			
			
			
			
			
			
			

			
			
			//Recherche Click
			recherche.setOnAction((ActionEvent e) -> {
				System.out.println("Depart "+departWay.getNom());
				System.out.println("Arrivee "+arriveeWay.getNom());
			    String chemin = Map.getMapInstance().path(departWay, arriveeWay, rechercheType,calculeMode);
			    System.out.println("distance TOTALE :"+departWay.distanceTotal);
			        arret.setDisable(false);
			        recherche.setDisable(true);
			        sauvegarder.setDisable(false);
			        nomVoieA.setDisable(true);
			        nomVoieD.setDisable(true);
			        numVoieAText.setDisable(true);
			        numVoieDText.setDisable(true);
			        textAreaIti.setText(chemin);
			        rechercheActive = true;
			}
			);
			//Annuler Click
			arret.setOnAction((ActionEvent e) -> {
				
			        arret.setDisable(true);
			        recherche.setDisable(false);
			        nomVoieA.setDisable(false);
			        nomVoieD.setDisable(false);
			        numVoieAText.setDisable(false);
			        numVoieDText.setDisable(false);
			        rechercheActive = false;
			        Map.getMapInstance().stopRecherche();
			    }
			);
			
			rechercher.setOnAction((ActionEvent e) -> {    
				
		        if (selectedRelation == null) {	
		        	textAreaEtat.setText("Vous n'avez insérer aucune ville.");
		        }
		        else {
		        	addMap(selectedRelation);
		        }
			});
			
			
			//sauvegarder Click
			sauvegarder.setOnAction((ActionEvent e) -> {
				Document docxml1 = null;
				//Document docxml2 = null;
				try {
					docxml1 = Itineraire.convertStringToXMLDocument(departWay.getNom(),arriveeWay.getNom(),textAreaIti.getText(), pathPolyline.getPoints(),calculeMode,rechercheType);
					//docxml2 = Itineraire.addPathToXMLDocument(docxml1, pathPolyline.getPoints());
				} catch (ParserConfigurationException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				FileChooser fileChooser = new FileChooser();
	            
	           
	 
	            //Show save file dialog
	            File file = fileChooser.showSaveDialog(primaryStage);
	            
	            /*pathPolyline.setStrokeWidth(2);
	    		pathPolyline.setStroke(Color.RED);
	    		mapPane.getChildren().add(pathPolyline);*/
	 
	            if (file != null) {
	                try {
						Itineraire.saveTextToFile(docxml1, file);
					} catch (TransformerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        });
			
			//importer Click
			importer.setOnAction((ActionEvent e) -> {    
					
				FileChooser fileChooser = new FileChooser();
				arret.setDisable(false);
	           
	 
	            //Show save file dialog
	            File file = fileChooser.showOpenDialog(primaryStage);
	            
	            if (file != null) {
                    String c;
					try {
						c = Itineraire.LoadFile(file);
						textAreaIti.setText(c);
					} catch (SAXException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    
                }
			        
			});
				
			
			
			root.setBottom(barre_etat);
	        root.setTop(split);
	       
	        primaryStage.widthProperty().addListener(e -> {
	        	
	            if(primaryStage.getWidth() > 1200) {
	            	vBox1.getChildren().clear();
	            	vBox1.setPadding(new Insets(10));
	                vBox1.getChildren().addAll(ville, labelD, depart, labelA, arrivee,labelmodedecalcule,modecalcule,labelmoyendetransport,moyendetransport,labelmoyendetransport1,filtremoyendetransport,checkBox,checkBox1,lancerR, labelI, itineraire, XML ,resultat);
	                barre_etat.getChildren().clear();
	                
	                textAreaEtat.setMinWidth(primaryStage.getWidth());
	                textAreaEtat.setMinHeight(primaryStage.getHeight()-vBox1.getHeight()+100);	  
	                barre_etat.getChildren().add(textAreaEtat);
	            }
	            else {	        
	            	vBox1.getChildren().clear();	
	            	vBox1.setPadding(new Insets(10));
	            	vBox1.getChildren().addAll(ville, labelD, depart, labelA, arrivee,labelmodedecalcule,modecalcule,labelmoyendetransport,moyendetransport,labelmoyendetransport1,filtremoyendetransport,checkBox,checkBox1,lancerR, labelI, itineraire, XML ,resultat );          	            	
	            	barre_etat.getChildren().clear();
	            	
	            	textAreaEtat.setMinWidth(primaryStage.getWidth());
	                textAreaEtat.setMinHeight(primaryStage.getHeight()-vBox1.getHeight());	
	                barre_etat.getChildren().add(textAreaEtat);
	                
	            	resultatsInfo.setMaxWidth(textAreaIti.getWidth());
	            }
	        });

			depBas.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			depHaut.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			depGauche.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			depDroite.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			zoomIn.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			zoomOut.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
			reset.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
	        System.out.println(vBox1.getHeight()+" "+vBox1.getWidth());
	        System.out.println(map.getHeight()+" "+map.getWidth());
	        System.out.println(scene.getHeight()+" "+scene.getWidth());
	        primaryStage.sizeToScene();
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void deplacer(Deplacement type, double mult) {
		double layoutX = mapPane.getLayoutX();
		double layoutY = mapPane.getLayoutY();
		if(type== Deplacement.BAS) {
			mapPane.setLayoutY(layoutY - mult);
			OverPassWay.pixelCenterY += mult;
		}
		if(type== Deplacement.HAUT) {
			mapPane.setLayoutY(layoutY + mult);
			OverPassWay.pixelCenterY -= mult;
		}
		if(type== Deplacement.GAUCHE) {
			OverPassWay.pixelCenterX -= mult;
			mapPane.setLayoutX(layoutX + mult); 
		}
		if(type== Deplacement.DROIT) {
			OverPassWay.pixelCenterX += mult;
			mapPane.setLayoutX(layoutX - mult); 
		}
		if(type== Deplacement.ZOOM_OUT) {
			mapZone.setScaleX(mapZone.getScaleX()/mult);
			mapZone.setScaleY(mapZone.getScaleY()/mult);
		}
		if(type== Deplacement.ZOOM_IN) {

			mapZone.setScaleX(mapZone.getScaleX()*mult);
			mapZone.setScaleY(mapZone.getScaleY()*mult);
		}
		if(type== Deplacement.RESET) {

			mapZone.setScaleX(1);
			mapZone.setScaleY(1);
			mapPane.setLayoutX(0);
			mapPane.setLayoutY(0);
		}
		//drawMap();
		
	}
	public int moveStep = 100;
	@Override
	public void handle(MouseEvent arg0) {
		Button button = (Button)arg0.getSource();
		if(button == this.depBas) {
			System.out.println("Bas");
			deplacer(Deplacement.BAS,this.moveStep);
		}
		if(button == this.depHaut) {
			System.out.println("Haut");
			deplacer(Deplacement.HAUT,this.moveStep);
		}
		if(button == this.depDroite) {
			System.out.println("Droite");
			deplacer(Deplacement.DROIT,this.moveStep);
		}
		if(button == this.depGauche) {
			System.out.println("Gauche");
			deplacer(Deplacement.GAUCHE,this.moveStep);
		}
		if(button == this.zoomIn) {
			deplacer(Deplacement.ZOOM_IN,2);
		}
		if(button == this.zoomOut) {
			deplacer(Deplacement.ZOOM_OUT,2);
		}
		if(button== this.reset) {
			deplacer(Deplacement.RESET,0);
		}
		//drawMap();
	}
}
