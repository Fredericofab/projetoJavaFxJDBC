package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Departamento;

public class DepartamentoService {

	public List<Departamento> pesquisarTodos(){
		List<Departamento> lista = new ArrayList<Departamento>();
		lista.add(new Departamento(1,"Administracao"));
		lista.add(new Departamento(2,"Gerencia"));
		lista.add(new Departamento(3,"Informatica"));
		lista.add(new Departamento(4,"Engenharia"));
		
		return lista;
	}
}
