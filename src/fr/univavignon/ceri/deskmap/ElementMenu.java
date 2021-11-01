package fr.univavignon.ceri.deskmap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ElementMenu extends ContextMenu {
	private Clickable element;
	public ElementMenu(Clickable element) {
		super();
		this.element = element;
		 MenuItem item1 = new MenuItem("Nouveau point de départ");
	        item1.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	            	System.out.println(((OverPassWay)element).toString());
	            	Launcher.numVoieDText.setText(((OverPassWay)element).toString());
	            	Launcher.departWay = ((OverPassWay)element);
	            	Launcher.nomVoieD.getSelectionModel().select(((OverPassWay)element));
	            }
	        });
	        MenuItem item2 = new MenuItem("Nouveau point d'arrivée");
	        item2.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	            	System.out.println(((OverPassWay)element).toString());
	            	Launcher.numVoieAText.setText(((OverPassWay)element).toString());
	            	Launcher.arriveeWay = ((OverPassWay)element);
	            	Launcher.nomVoieA.getSelectionModel().select(((OverPassWay)element));
	            }
	        });
	        item1.setDisable(Launcher.rechercheActive);
	        item2.setDisable(Launcher.rechercheActive);
	        MenuItem item3 = new MenuItem("ProprietÃ©");
	        item3.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	            	System.out.println(element.getNom());
	            	 new ElementPopUp(element).showAndWait();
	            }
	        });
	        this.getItems().addAll(item1, item2, item3);
	}
	

}
