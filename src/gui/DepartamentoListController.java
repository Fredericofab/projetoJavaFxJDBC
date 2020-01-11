package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import model.entities.Departamento;
import model.services.DepartamentoService;

public class DepartamentoListController implements Initializable, DadosAlteradosListener {

	private DepartamentoService servico;

	private ObservableList<Departamento> obsLista;

	private List<DadosAlteradosListener> alteracaoDeDadosListeners = new ArrayList<>();

	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableColumnDepartamentoId;
	@FXML
	private TableColumn<Departamento, String> tableColumnDepartamentoDescricao;
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEDIT;
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnREMOVE;
	@FXML
	private Button btIncluir;

	@FXML
	public void onBtIncluirAction(ActionEvent evento) {
		Stage parentStage = Utilitarios.atualStage(evento);
		String caminhoDaView = "/gui/DepartamentoForm.fxml";
		Departamento objeto = new Departamento();
		criarDialogoForm(objeto, caminhoDaView, parentStage);
		;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tableColumnDepartamentoId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnDepartamentoDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void setDepartamentoService(DepartamentoService servico) {
		this.servico = servico;
	}

	public void seInscreverEmAlteracaoDeDadosListener(DadosAlteradosListener ouvinte) {
		alteracaoDeDadosListeners.add(ouvinte);
	}

	public void atualizarTableView() {
		if (servico == null) {
			throw new IllegalStateException("O service foi passado nulo!");
		}
		List<Departamento> lista = servico.pesquisarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tableViewDepartamento.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void criarDialogoForm(Departamento objeto, String caminhoDaView, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			Pane pane = loader.load();

			DepartamentoFormController controller = loader.getController();
			controller.setDepartamento(objeto);
			controller.setDepartamentoService(servico);
			controller.inscreverDadosAlteradosListener(this);
			controller.atualizarDadosForm();

			Stage dialogoStage = new Stage();
			dialogoStage.setTitle("Informe os Dados do Departamento");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarDialogoForm(obj, "/gui/DepartamentoForm.fxml", Utilitarios.atualStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
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
	private void removeEntity(Departamento objeto) {
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
