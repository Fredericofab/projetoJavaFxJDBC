package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {

	private Connection conexao;

	public DepartamentoDaoJDBC(Connection conexao) {
		this.conexao = conexao;
	}

	@Override
	public void inserir(Departamento objeto) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("INSERT INTO department "
										+ "(Name) VALUES (?) ",
										+ Statement.RETURN_GENERATED_KEYS);
			st.setString(1, objeto.getDescricao());

			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);
			objeto.setId(id);

			DB.fecharResultSet(rs);
		} catch (SQLException e) {
			throw new DbException("Erro na Insercao " + e.getMessage());
		} finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public void atualizar(Departamento objeto) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("UPDATE department SET name = ? " + "WHERE id = ? ");
			st.setString(1, objeto.getDescricao());
			st.setInt(2, objeto.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException("Erro na Atualizacao " + e.getMessage());
		} finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public void deletarId(Integer id) {
		PreparedStatement st = null;
		try {
			st = conexao.prepareStatement("DELETE FROM department " + "WHERE id = ? ");
			st.setInt(1, id);
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException("erro na delecao " + e.getMessage());
		}
		finally {
			DB.fecharStatement(st);
		}
	}

	@Override
	public Departamento pesquisarId(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement("SELECT * FROM department " + "WHERE id = ? ");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				return departamento;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException("erro na Pesquisa " + e.getMessage());
		}
		finally {
			DB.fecharStatement(st);
			DB.fecharResultSet(rs);
		}

	}

	@Override
	public List<Departamento> pesquisarTodos() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conexao.prepareStatement("SELECT * FROM department ");
			rs = st.executeQuery();
			List<Departamento> lista = new ArrayList<Departamento>();
			while (rs.next()) {
				Departamento departamento = instanciaDepartamento(rs);
				lista.add(departamento);
			}
			return lista;
		} 
		catch (SQLException e) {
			throw new DbException("erro na consulta todos - " + e.getMessage());
		}
		finally {
			DB.fecharResultSet(rs);
			DB.fecharStatement(st);
		}
	}

	private Departamento instanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(rs.getInt("id"));
		departamento.setDescricao(rs.getString("Name"));
		return departamento;
	}
}
