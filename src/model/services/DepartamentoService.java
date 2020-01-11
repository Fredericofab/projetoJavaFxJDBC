package model.services;

import java.util.List;

import model.dao.DaoFabrica;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoService {

	private DepartamentoDao dao = DaoFabrica.criarDepartamentoDao();
	
	public List<Departamento> pesquisarTodos(){
		return dao.pesquisarTodos();
	}
	
	public void salvarOuAtualizar(Departamento objeto) {
		if (objeto.getId() == null) {
			dao.inserir(objeto);
		}
		else {
			dao.atualizar(objeto);
		}
	}
	
	public void remover(Departamento objeto) {
		dao.deletarId(objeto.getId());
	}
}
