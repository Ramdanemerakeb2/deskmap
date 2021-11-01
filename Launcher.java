package fr.univavignon.ceri.deskmap;

import java.util.ArrayList;

import com.sun.javafx.css.converters.StringConverter;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * This class is used to launch the game.
 * 
 * @author 
 * @author 
 * @author 
 * @author 
 */
public class Launcher extends Application
{	
	/**
	 * Launches the game.
	 * 
	 * @param args
	 * 		Not used here.
	 */
	private Button rechercher;
	public static void main(String[] args){	
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("DeskMap");
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 1200, 900, Color.CADETBLUE);
			scene.getStylesheets().add(getClass().getResource("launcher.css").toExternalForm());
			primaryStage.setScene(scene);
			
			VBox vBox1 = new VBox();
	        vBox1.setPadding(new Insets(50));
	        vBox1.setId("vBox1");
	        VBox vBox2 = new VBox();
	        vBox2.setPadding(new Insets(10));
	        
	        
	        HBox ville = new HBox();
	        ville.setPadding(new Insets(10));
	        ville.setSpacing(10);
	        TextField champTexteVille = new TextField("Avignon");
	        rechercher = new Button ("OK");
	        ville.getChildren().addAll(champTexteVille, rechercher);

	       
	        HBox depart = new HBox();
	        depart.setPadding(new Insets(10));
	        depart.setSpacing(10);
	        Label labelD = new Label("Depart");
	        labelD.setPadding(new Insets(10));
	        TextField numVoieD = new TextField();
	        numVoieD.setMaxWidth(70);   
	        ComboBox<String> nomVoieD = new ComboBox<String>();
	        nomVoieD.setEditable(true);
	        nomVoieD.getItems().add("Choix 1");
	        nomVoieD.getItems().add("Choix 2");
	        nomVoieD.getItems().add("Choix 3");
	        depart.getChildren().addAll(numVoieD, nomVoieD);
	        


	        
	        HBox arrivee = new HBox();
	        arrivee.setPadding(new Insets(10));
	        arrivee.setSpacing(10);
	        Label labelA = new Label("Arrivée");
	        labelA.setPadding(new Insets(10));
	        TextField numVoieA = new TextField();
	        numVoieA.setMaxWidth(70);
	        ComboBox<String> nomVoieA = new ComboBox<String>();
	        nomVoieA.setEditable(true);
	        nomVoieA.getItems().add("Choix 1");
	        nomVoieA.getItems().add("Choix 2");
	        nomVoieA.getItems().add("Choix 3");
	        arrivee.getChildren().addAll(numVoieA, nomVoieA);
	        
	        
	        
	        
	        HBox lancerR = new HBox();
	        lancerR.setPadding(new Insets(20));
	        lancerR.setSpacing(20);
	        Button recherche = new Button ("Rechercher");
	        Button arret = new Button ("Arreter");
	        lancerR.getChildren().addAll(recherche, arret);
	        
	        
	        VBox itineraire = new VBox();
	        itineraire.setPadding(new Insets(10));
	        Label labelI = new Label("Itinéraire");
	        labelI.setPadding(new Insets(10));
	        TextArea textAreaIti = new TextArea();
	        textAreaIti.setEditable(false);
	        textAreaIti.setMaxWidth(300);
	        itineraire.getChildren().add(textAreaIti);
	        // Zone Carte
	        Pane map = new Pane();
	        vBox2.resize(1000, 1000);
	        vBox1.resize(180, 1000);
	        vBox1.getChildren().addAll(ville, labelD, depart, labelA, arrivee, lancerR, labelI, itineraire);
	        map.setMinSize(850, 900);
	        vBox1.setPrefSize(200, 900);
	        vBox1.setMaxSize(350, 900);
	        SplitPane split = new SplitPane(vBox1, map);
	        // Bouton déplacement
	        VBox deplacement = new VBox();
	        HBox haut = new HBox(200);
	        HBox bas = new HBox(20);
	        HBox centre = new HBox(20);
	        haut.setAlignment(Pos.CENTER);
	        bas.setAlignment(Pos.CENTER);
	        Button depHaut = new Button("H");
	        Button depBas = new Button("B");
	        Button depGauche = new Button("<");
	        Button depDroite = new Button(">");
	        haut.getChildren().add(depHaut);
	        bas.getChildren().add(depBas);
	        centre.getChildren().addAll(depGauche, depDroite);
	        deplacement.getChildren().addAll(haut,centre,bas);
	        deplacement.setLayoutX(scene.getWidth()-450);
	        deplacement.setLayoutY(10);
	        deplacement.setSpacing(5);
	        // Bouton Zoom
	        VBox zoom = new VBox(10);
	        Button zoomIn = new Button("+");
	        Button zoomOut = new Button("-");
	        zoomIn.getStyleClass().add("zoom-button");
	        zoomOut.getStyleClass().add("zoom-button");
	        zoom.getChildren().add(zoomIn);
	        zoom.getChildren().add(zoomOut);
	        zoom.setLayoutX(scene.getWidth()-400);
	        zoom.setLayoutY(scene.getHeight()-100);
	        /*root.setLeft(vBox1);
	        root.setRight(vBox2);*/
	        root.setCenter(split);
	         System.out.println(vBox1.getHeight()+" "+vBox1.getWidth());
	         System.out.println(map.getHeight()+" "+map.getWidth());
	         System.out.println(scene.getHeight()+" "+scene.getWidth());
	        primaryStage.sizeToScene();
			primaryStage.show();
			Map carte = new Map(depHaut,depBas,depGauche,depDroite, zoomIn,zoomOut,null,null);
			
			
			Pane cartePan = carte.getMapPane();
			
			
			 map.getChildren().add(zoom);
			 map.getChildren().add(cartePan);
			 map.getChildren().add(deplacement);
			 VBox resultat = new VBox();
			 TextField resultatsInfo = new TextField("");
			 resultat.getChildren().add(resultatsInfo);
			 ChoiceBox resultCB = new ChoiceBox();
			 resultCB.setVisible(false);
			 resultat.getChildren().add(resultCB);
			 vBox1.getChildren().add(resultat);
			 resultCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					Searchable choix = (Searchable) resultCB.getItems().get(arg2.intValue());
					Map carte2 = Map.recherche(choix,depHaut,depBas,depGauche,depDroite, zoomIn,zoomOut);
					map.getChildren().clear();
					map.getChildren().add(carte2.getMapPane());
					map.getChildren().add(deplacement);
					map.getChildren().add(zoom);
				}
			    });
			 rechercher.setOnAction((ActionEvent e) -> {    
				 resultCB.setVisible(true);
				 resultCB.setItems(FXCollections.observableArrayList());
				 System.out.println("Recherche "+champTexteVille.getText() );
		        	if ((champTexteVille.getText() == null || champTexteVille.getText().isEmpty())) {
		        		numVoieA.setEditable(false);
		        		nomVoieA.setEditable(false);
		        	}
		        	else {
		        		ArrayList<Searchable> resultats = OverPassQuery.search(champTexteVille.getText(), carte.nodes, carte.ways);
		        		resultatsInfo.setText(resultats.size()+" Résultat(s)");
		        		ObservableList<Searchable> resultatsList = FXCollections.observableArrayList();
		        		for(int i=0;i<resultats.size();i++) {
		        			resultatsList.add(resultats.get(i));
		        		}
		        		resultCB.setItems(resultatsList);
		        	}
		        });
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
