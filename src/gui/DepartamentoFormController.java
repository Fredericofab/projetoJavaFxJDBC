package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
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
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {

	private Departamento entidade;
	private DepartamentoService servico;
	
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
			Utilitarios.atualStage(evento).close();
		}
		catch (DbException e) {
			Alertas.mostrarAlertas("Erro Salvando Objeto", null, e.getMessage(),AlertType.ERROR);
		}
	}
	private Departamento getDadosDoForm() {
		Departamento objeto = new Departamento();
		objeto.setId(Utilitarios.tentarConverterParaInt(txtId.getText()));
		objeto.setDescricao(txtDescricao.getText());
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
}
