package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartamentoService;
import model.services.VendedorService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		String caminhoDaView = "/gui/VendedorList.fxml";
		Consumer<VendedorListController> acaoDeInicializao = 
				(VendedorListController controller) -> {
					controller.setVendedorService(new VendedorService());
					controller.atualizarTableView();					
				};
		carregarView(caminhoDaView,acaoDeInicializao);
	}
	@FXML
	public void onMenuItemDepartamentoAction() {
		String caminhoDaView = "/gui/DepartamentoList.fxml";
		Consumer<DepartamentoListController> acaoDeInicializao = 
				(DepartamentoListController controller) -> {
					controller.setDepartamentoService(new DepartamentoService());
					controller.atualizarTableView();					
				};
		carregarView(caminhoDaView,acaoDeInicializao);
	}
	@FXML
	public void onMenuItemSobreAction() {
		String caminhoDaView = "/gui/Sobre.fxml";
		Consumer<Initializable> acaoDeInicializacao = x -> {};
		carregarView(caminhoDaView, acaoDeInicializacao);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
			
	}

	private synchronized <T> void carregarView(String caminhoDaView, Consumer<T> acaoDeInicializacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVbox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVbox.getChildren());
			
			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
		}
		catch (IOException e) {
			Alertas.mostrarAlertas("IOException", "Erro carregando a View",
								   e.getMessage(), AlertType.ERROR);
		}
	}
	
}
