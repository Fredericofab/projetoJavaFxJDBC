package model.dao;

import java.util.List;

import model.entities.Departamento;
import model.entities.Vendedor;

public interface VendedorDao {

	void inserir(Vendedor objeto);
	void atualizar(Vendedor objeto);
	void deletarId(Integer id);
	Vendedor pesquisarId(Integer id);
	List<Vendedor> pesquisarTodos();
	
	List<Vendedor> pesquisarPorDepartamento(Departamento departamento);

}
