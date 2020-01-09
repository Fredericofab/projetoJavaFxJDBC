package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class DepartamentoListController implements Initializable {

	private DepartamentoService servico;
	
	private ObservableList<Departamento> obsLista;
	
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableColumnDepartamentoId;
	@FXML
	private TableColumn<Departamento, String> tableColumnDepartamentoDescricao;
	@FXML
	private Button btIncluir;

	@FXML
	public void onBtIncluir() {
		System.out.println("onBtIncluir");
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
	
	public void atualizarTableView() {
		if (servico == null ) {
			throw new IllegalStateException("O service foi passado nulo!");
		}
		List<Departamento> lista = servico.pesquisarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tableViewDepartamento.setItems(obsLista);
	}
}
