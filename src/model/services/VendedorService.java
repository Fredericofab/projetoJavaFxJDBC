package model.services;

import java.util.List;

import model.dao.DaoFabrica;
import model.dao.VendedorDao;
import model.entities.Vendedor;

public class VendedorService {

	private VendedorDao dao = DaoFabrica.criarVendedorDao();
	
	public List<Vendedor> pesquisarTodos(){
		return dao.pesquisarTodos();
	}
	
	public void salvarOuAtualizar(Vendedor objeto) {
		if (objeto.getId() == null) {
			dao.inserir(objeto);
		}
		else {
			dao.atualizar(objeto);
		}
	}
	
	public void remover(Vendedor objeto) {
		dao.deletarId(objeto.getId());
	}
}
