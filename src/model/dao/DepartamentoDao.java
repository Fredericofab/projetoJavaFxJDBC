package model.dao;

import java.util.List;

import model.entities.Departamento;

public interface DepartamentoDao {

	void inserir(Departamento objeto);
	void atualizar(Departamento objeto);
	void deletarId(Integer id);
	Departamento pesquisarId(Integer id);
	List<Departamento> pesquisarTodos();

}
