package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor entidade;
	private VendedorService servico;
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

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}
	public void setVendedorService(VendedorService servico) {
		this.servico = servico;
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
		}
		catch (ValidacaoException e) {
			setMensagensErros(e.getErros());
		}
		catch (DbException e) {
			Alertas.mostrarAlertas("Erro Salvando Objeto", null, e.getMessage(),AlertType.ERROR);
		}
	}
	private void notificarDadosAlteradosListeners() {
		for (DadosAlteradosListener ouvinte : dadosAlteradosListeners) {
			ouvinte.onDadosAlterados();
		}
		
	}
	private Vendedor getDadosDoForm() {
		Vendedor objeto = new Vendedor();
		ValidacaoException excecao =new ValidacaoException("Erros de validacao");
		
		objeto.setId(Utilitarios.tentarConverterParaInt(txtId.getText()));
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			excecao.adicionarErro("nome", "O campo nao pode ser vazio");
		}
		objeto.setNome(txtNome.getText());
		
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
	}
	public void atualizarDadosForm() {
		if ( entidade == null ) {
			throw new IllegalStateException("Entidade Esta Vazia");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		
		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", entidade.getSalarioBase()));
		
//		dpAniversario.setValue(entidade.getAniversario()); erro de compilação Date x LocalDate
		if ( entidade.getAniversario() != null ) {
			dpAniversario.setValue(LocalDate.ofInstant(entidade.getAniversario().toInstant(),ZoneId.systemDefault()));
		}
	}
	
	private void setMensagensErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		if (campos.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}
	}
}
