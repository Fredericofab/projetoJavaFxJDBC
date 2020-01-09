package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDao;
import model.entities.Departamento;
import model.entities.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {

	private Connection conexao;
	public VendedorDaoJDBC(Connection conexao) {
		this.conexao = conexao;
	}
	
	@Override
	public void inserir(Vendedor objeto) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("INSERT INTO seller "
										+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
										+ "VALUES (?, ?, ?, ?, ?) ",
										+ Statement.RETURN_GENERATED_KEYS);
			st.setString(1, objeto.getNome());
			st.setString(2, objeto.getEmail());
			st.setDate(3, new java.sql.Date(objeto.getAniversario().getTime()));
			st.setDouble(4, objeto.getSalarioBase());
			st.setInt(5, objeto.getDepartamento().getId());
			
			int linhasAfetadas = st.executeUpdate();
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if ( rs.next()) {
					int id = rs.getInt(1);
					objeto.setId(id);
				}
				DB.fecharResultSet(rs);
			}
			else {
				throw new DbException("Erro na insercao - nenhuma linha foi afetada");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public void atualizar(Vendedor objeto) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("UPDATE seller "
										+ "SET Name = ?, Email = ?, BirthDate = ?, "
										+ "BaseSalary = ?, DepartmentId = ? "
										+ "WHERE Id = ? ");
			st.setString(1, objeto.getNome());
			st.setString(2, objeto.getEmail());
			st.setDate(3, new java.sql.Date(objeto.getAniversario().getTime()));
			st.setDouble(4, objeto.getSalarioBase());
			st.setInt(5, objeto.getDepartamento().getId());
			st.setInt(6, objeto.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public void deletarId(Integer id) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("DELETE FROM seller "
										+ "WHERE Id = ? ");
			st.setInt(1, id);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public Vendedor pesquisarId(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement("SELECT seller.*,department.Name as DepName "
										+ "FROM   seller, department "
										+ "WHERE  seller.DepartmentId = department.Id "
										+ "AND    seller.Id = ? ");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				Vendedor vendedor = instanciaVendedor(rs, departamento);
				return vendedor;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(st);
		}
	}

	@Override
	public List<Vendedor> pesquisarTodos() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement("SELECT seller.*,department.Name as DepName "  
										+ "FROM seller , department " 
										+ "WHERE seller.DepartmentId = department.Id "  
										+ "ORDER BY Name ");
			rs = st.executeQuery();
			
			List<Vendedor> lista = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento depto = map.get(rs.getInt("DepartmentId"));
				if ( depto == null ) {
					depto = instanciaDepartamento(rs);
					map.put(rs.getInt("DepartmentId"), depto);
				}
				Vendedor vendedor = instanciaVendedor(rs, depto);
				lista.add(vendedor);
			}
			return lista;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(st);
		}
	}

	@Override
	public List<Vendedor> pesquisarPorDepartamento(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement("SELECT seller.*,department.Name as DepName "  
										+ "FROM seller , department " 
										+ "WHERE seller.DepartmentId = department.Id "  
										+ "AND DepartmentId = ? "  
										+ "ORDER BY Name ");
			st.setInt(1, departamento.getId());
			rs = st.executeQuery();
			
			List<Vendedor> lista = new ArrayList<Vendedor>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento depto = map.get(rs.getInt("DepartmentId"));
				if ( depto == null ) {
					depto = instanciaDepartamento(rs);
					map.put(rs.getInt("DepartmentId"), depto);
				}
				Vendedor vendedor = instanciaVendedor(rs, depto);
				lista.add(vendedor);
			}
			return lista;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(st);
		}
	}

	private Vendedor instanciaVendedor(ResultSet rs, Departamento departamento) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setId(rs.getInt("Id"));
		vendedor.setNome(rs.getString("name"));
		vendedor.setEmail(rs.getNString("email"));
		vendedor.setAniversario(rs.getDate("BirthDate"));
		vendedor.setSalarioBase(rs.getDouble("BaseSalary"));
		vendedor.setDepartamento(departamento);
		return vendedor;
	}

	private Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(rs.getInt("DepartmentId"));
		departamento.setDescricao(rs.getString("DepName"));
		return departamento;
	}
	
}
