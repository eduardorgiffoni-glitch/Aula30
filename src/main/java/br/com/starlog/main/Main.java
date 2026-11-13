package br.com.starlog.main;

import br.com.starlog.dao.ProdutoDAO;
import br.com.starlog.dao.ProdutoDAOSqlite;
import br.com.starlog.model.Produto;
import java.util.List;
import java.util.Locale;

/** Roteiro oficial da Aula 30: Create, Read, Update, Delete e estado final. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAOSqlite();

        Produto teclado = new Produto("Teclado Mecânico RGB", 349.90, 8);
        Produto mousepad = new Produto("Mousepad Speed XL", 69.90, 20);

        System.out.println("=== CREATE ===");
        dao.salvar(teclado);
        dao.salvar(mousepad);
        System.out.println("Teclado criado com ID: " + teclado.getId());
        System.out.println("Mousepad criado com ID: " + mousepad.getId());

        System.out.println("\n=== READ ===");
        imprimirCatalogo(dao.listarTodos());

        System.out.println("\n=== UPDATE ===");
        teclado.setEstoque(15);
        teclado.setPreco(289.90);
        System.out.println("Teclado atualizado? " + dao.atualizar(teclado));
        dao.buscarPorId(teclado.getId()).ifPresentOrElse(
            p -> System.out.println("Registro atualizado: " + p),
            () -> System.out.println("Teclado não encontrado."));

        System.out.println("\n=== DELETE ===");
        System.out.println("Mousepad excluído? " + dao.excluir(mousepad.getId()));
        System.out.println("ID 888 excluído? " + dao.excluir(888L)
            + " (false significa que não havia registro).");

        System.out.println("\n=== ESTADO FINAL ===");
        List<Produto> restantes = dao.listarTodos();
        imprimirCatalogo(restantes);
        System.out.println("Produtos restantes nesta execução: " + restantes.size());
        if (restantes.size() != 1) {
            System.out.println("Observação: o banco é persistente; registros de execuções anteriores permanecem.");
        }
    }

    private static void imprimirCatalogo(List<Produto> produtos) {
        System.out.printf("%-6s %-28s %14s %10s%n", "ID", "PRODUTO", "PREÇO (R$)", "ESTOQUE");
        System.out.println("-".repeat(65));
        for (Produto p : produtos) {
            System.out.printf(Locale.forLanguageTag("pt-BR"), "%-6d %-28s %14.2f %10d%n",
                p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
        }
    }
}
