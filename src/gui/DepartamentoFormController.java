package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Departamento;
import model.exceptions.ValidacaoException;
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {

	private Departamento entidade;
	private DepartamentoService servico;
	private List<DadosAlteradosListener> dadosAlteradosListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtDescricao;
	@FXML
	private Label labelErroDescricao;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	public void setDepartamento(Departamento entidade) {
		this.entidade = entidade;
	}
	public void setDepartamentoService(DepartamentoService servico) {
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
	private Departamento getDadosDoForm() {
		Departamento objeto = new Departamento();
		ValidacaoException excecao =new ValidacaoException("Erros de validacao");
		
		objeto.setId(Utilitarios.tentarConverterParaInt(txtId.getText()));
		
		if (txtDescricao.getText() == null || txtDescricao.getText().trim().equals("")) {
			excecao.adicionarErro("descricao", "O campo nao pode ser vazio");
		}
		objeto.setDescricao(txtDescricao.getText());
		
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
		Restricoes.validaTextFieldInteiro(txtId);
		Restricoes.validaTextFieldTamanhoMax(txtDescricao, 30);
	}

	public void atualizarDadosForm() {
		if ( entidade == null ) {
			throw new IllegalStateException("Entidade Esta Vazia");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtDescricao.setText(entidade.getDescricao());
	}
	
	private void setMensagensErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		if (campos.contains("descricao")) {
			labelErroDescricao.setText(erros.get("descricao"));
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
