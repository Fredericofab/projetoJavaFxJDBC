package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegridadeException;
import gui.listeners.DadosAlteradosListener;
import gui.util.Alertas;
import gui.util.Utilitarios;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Vendedor;
import model.services.VendedorService;

public class VendedorListController implements Initializable, DadosAlteradosListener {

	private VendedorService servico;

	private ObservableList<Vendedor> obsLista;

	private List<DadosAlteradosListener> alteracaoDeDadosListeners = new ArrayList<>();

	@FXML
	private TableView<Vendedor> tableViewVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> tableColumnVendedorId;
	@FXML
	private TableColumn<Vendedor, String> tableColumnVendedorNome;
	@FXML
	private TableColumn<Vendedor, String> tableColumnVendedorEmail;
	@FXML
	private TableColumn<Vendedor, Date> tableColumnVendedorAniversario;
	@FXML
	private TableColumn<Vendedor, Double> tableColumnVendedorSalarioBase;

	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEDIT;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnREMOVE;
	@FXML
	private Button btIncluir;

	@FXML
	public void onBtIncluirAction(ActionEvent evento) {
		Stage parentStage = Utilitarios.atualStage(evento);
		String caminhoDaView = "/gui/VendedorForm.fxml";
		Vendedor objeto = new Vendedor();
		criarDialogoForm(objeto, caminhoDaView, parentStage);
		;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnVendedorId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnVendedorNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnVendedorEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnVendedorAniversario.setCellValueFactory(new PropertyValueFactory<>("aniversario"));
		Utilitarios.formatarTableColumnDate(tableColumnVendedorAniversario, "dd/MM/yyyy");
		tableColumnVendedorSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utilitarios.formatarTableColumnDouble(tableColumnVendedorSalarioBase, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void setVendedorService(VendedorService servico) {
		this.servico = servico;
	}

	public void seInscreverEmAlteracaoDeDadosListener(DadosAlteradosListener ouvinte) {
		alteracaoDeDadosListeners.add(ouvinte);
	}

	public void atualizarTableView() {
		if (servico == null) {
			throw new IllegalStateException("O service foi passado nulo!");
		}
		List<Vendedor> lista = servico.pesquisarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tableViewVendedor.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void criarDialogoForm(Vendedor objeto, String caminhoDaView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			Pane pane = loader.load();

			VendedorFormController controller = loader.getController();
			controller.setVendedor(objeto);
			controller.setVendedorService(servico);
			controller.inscreverDadosAlteradosListener(this);
			controller.atualizarDadosForm();

			Stage dialogoStage = new Stage();
			dialogoStage.setTitle("Informe os Dados do Vendedor");
			dialogoStage.setScene(new Scene(pane));
			dialogoStage.setResizable(false);
			dialogoStage.initOwner(parentStage);
			dialogoStage.initModality(Modality.WINDOW_MODAL);
			dialogoStage.showAndWait();
		} catch (IOException e) {
			Alertas.mostrarAlertas("IOException", "Erro carregando View", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDadosAlterados() {
		atualizarTableView();
	}

	private void initEditButtons() {
		// codigo adaptado da material do curso Udemy
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarDialogoForm(obj, "/gui/VendedorForm.fxml", Utilitarios.atualStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	private void removeEntity(Vendedor objeto) {
		Optional<ButtonType> clicado = Alertas.mostrarConfirmacao("Confirmacao", "Tem certeza da delecao?");
		if (clicado.get() == ButtonType.OK) {
			if ( servico == null ) {
				throw new IllegalStateException("O service foi passado nulo!");
			}
			try {
				servico.remover(objeto);
				atualizarTableView();
			}
			catch (DbIntegridadeException e) {	
				Alertas.mostrarAlertas("Erro removendo Objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
