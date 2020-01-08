package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.source.doctree.SinceTree;

import application.Main;
import gui.util.Alertas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		System.out.println("onMenuItemVendedorAction");
	}
	@FXML
	public void onMenuItemDepartamentoAction() {
		System.out.println("onMenuItemDepartamentoAction");
	}
	@FXML
	public void onMenuItemSobreAction() {
		carregarView("/gui/SobreView.fxml");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
			
	}

	private synchronized void carregarView(String caminhoDaView) {
		try {
			FXMLLoader carregar = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVbox = carregar.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVbox.getChildren());
		}
		catch (IOException e) {
			Alertas.mostrarAlertas("IOException", "Erro carregando a View",
								   e.getMessage(), AlertType.ERROR);
		}
	}
}
