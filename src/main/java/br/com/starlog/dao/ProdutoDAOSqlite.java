package br.com.starlog.dao;

import br.com.starlog.db.ConexaoFactory;
import br.com.starlog.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementação SQLite do CRUD: SQL restrito à camada DAO. */
public class ProdutoDAOSqlite implements ProdutoDAO {
    public ProdutoDAOSqlite() {
        String sql = "CREATE TABLE IF NOT EXISTS produtos ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nome TEXT NOT NULL, preco REAL NOT NULL, estoque INTEGER NOT NULL);";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao inicializar o banco: " + e.getMessage(), e);
        }
    }

    @Override
    public void salvar(Produto produto) {
        Objects.requireNonNull(produto, "O produto é obrigatório.");
        if (produto.getId() != null) {
            throw new IllegalArgumentException("Produto já possui ID: use atualizar.");
        }
        String sql = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?);";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Não foi possível inserir o produto.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("A chave gerada não foi retornada.");
                }
                produto.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        validarId(id);
        String sql = "SELECT id, nome, preco, estoque FROM produtos WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearLinha(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT id, nome, preco, estoque FROM produtos ORDER BY nome ASC;";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearLinha(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar catálogo: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean atualizar(Produto produto) {
        Objects.requireNonNull(produto, "O produto é obrigatório.");
        validarId(produto.getId());
        String sql = "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setLong(4, produto.getId());
            int afetadas = stmt.executeUpdate();
            return afetadas > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(Long id) {
        validarId(id);
        String sql = "DELETE FROM produtos WHERE id = ?;";
        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int afetadas = stmt.executeUpdate();
            return afetadas > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    private Produto mapearLinha(ResultSet rs) throws SQLException {
        return new Produto(rs.getLong("id"), rs.getString("nome"),
                           rs.getDouble("preco"), rs.getInt("estoque"));
    }

    private static void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }
    }
}
