package br.com.starlog.dao;

import java.util.List;
import java.util.Optional;
import br.com.starlog.model.Produto;

/** Contrato DAO usado pelas camadas de negócio, sem dependência de JDBC. */
public interface ProdutoDAO {
    void salvar(Produto produto);
    Optional<Produto> buscarPorId(Long id);
    List<Produto> listarTodos();
    boolean atualizar(Produto produto);
    boolean excluir(Long id);
}
