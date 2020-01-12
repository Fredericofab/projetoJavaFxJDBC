package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DadosAlteradosListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utilitarios;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor entidade;
	private VendedorService servico;
	private DepartamentoService departamentoService;
	private List<DadosAlteradosListener> dadosAlteradosListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpAniversario;
	@FXML
	private TextField txtSalarioBase;
	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;
	@FXML
	private Label labelErroNome;
	@FXML
	private Label labelErroEmail;
	@FXML
	private Label labelErroAniversario;
	@FXML
	private Label labelErroSalarioBase;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsLista;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}

	public void setServices(VendedorService servico, DepartamentoService departamentoService) {
		this.servico = servico;
		this.departamentoService = departamentoService;
	}

	public void inscreverDadosAlteradosListener(DadosAlteradosListener ouvinte) {
		dadosAlteradosListeners.add(ouvinte);
	}

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade estava nula");
		}
		if (servico == null) {
			throw new IllegalStateException("Servico estava nulo");
		}
		try {
			entidade = getDadosDoForm();
			servico.salvarOuAtualizar(entidade);
			notificarDadosAlteradosListeners();
			Utilitarios.atualStage(evento).close();
		} catch (ValidacaoException e) {
			setMensagensErros(e.getErros());
		} catch (DbException e) {
			Alertas.mostrarAlertas("Erro Salvando Objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notificarDadosAlteradosListeners() {
		for (DadosAlteradosListener ouvinte : dadosAlteradosListeners) {
			ouvinte.onDadosAlterados();
		}

	}

	private Vendedor getDadosDoForm() {
		Vendedor objeto = new Vendedor();
		ValidacaoException excecao = new ValidacaoException("Erros de validacao");

		objeto.setId(Utilitarios.tentarConverterParaInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.adicionarErro("nome", "O campo nao pode ser vazio");
		}
		objeto.setNome(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			excecao.adicionarErro("email", "O campo nao pode ser vazio");
		}
		objeto.setEmail(txtEmail.getText());
		
		if(dpAniversario.getValue() == null) {
			excecao.adicionarErro("aniversario", "O campo nao pode ser vazio");
		}
		else {
			Instant instant = Instant.from(dpAniversario.getValue().atStartOfDay(ZoneId.systemDefault()));
			objeto.setAniversario(Date.from(instant));
		}
		
		if (txtSalarioBase.getText() == null || txtSalarioBase.getText().trim().equals("")) {
			excecao.adicionarErro("salarioBase", "O campo nao pode ser vazio");
		}
		objeto.setSalarioBase(Utilitarios.tentarConverterParaDouble(txtSalarioBase.getText()));
		
		objeto.setDepartamento(comboBoxDepartamento.getValue());
		
		if (excecao.getErros().size() > 0) {
			throw excecao;
		}
		return objeto;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent evento) {
		Utilitarios.atualStage(evento).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Restricoes.validaTextFieldInteiro(txtId);
		Restricoes.validaTextFieldTamanhoMax(txtNome, 60);
		Restricoes.validaTextFieldDouble(txtSalarioBase);
		Restricoes.validaTextFieldTamanhoMax(txtEmail, 50);
		Utilitarios.formatarDatePicker(dpAniversario, "dd/MM/yyyy");
		initializeComboBoxDepartamento();
	}

	public void atualizarDadosForm() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade Esta Vazia");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());

		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));

//		dpAniversario.setValue(entidade.getAniversario()); erro de compilação Date x LocalDate
		if (entidade.getAniversario() != null) {
			dpAniversario.setValue(LocalDate.ofInstant(entidade.getAniversario().toInstant(), ZoneId.systemDefault()));
		}
		if (entidade.getDepartamento() == null ) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}
		else {
			comboBoxDepartamento.setValue(entidade.getDepartamento());
		}	
	}

	public void lerObjetosAssociados() {
		if (departamentoService == null) {
			throw new IllegalStateException("O DepartamentoService esta nulo");
		}
		List<Departamento> lista = departamentoService.pesquisarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		comboBoxDepartamento.setItems(obsLista);
	}

	private void setMensagensErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		labelErroNome.setText((campos.contains("nome") ? erros.get("nome") : "" ));
		labelErroEmail.setText((campos.contains("email") ? erros.get("email") : "" ));
		labelErroAniversario.setText((campos.contains("aniversario") ? erros.get("aniversario") : "" ));
		labelErroSalarioBase.setText((campos.contains("salarioBase") ? erros.get("salarioBase") : "" ));
	}

	private void initializeComboBoxDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDescricao());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}
}
