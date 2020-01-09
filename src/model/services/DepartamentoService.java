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
}
