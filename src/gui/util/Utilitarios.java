package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;


public class Utilitarios {

	public static Stage atualStage(ActionEvent evento) {
		return (Stage) ((Node) evento.getSource()).getScene().getWindow();
	}
	
}
