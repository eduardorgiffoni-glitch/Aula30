package br.com.starlog;

import br.com.starlog.dao.ProdutoDAO;
import br.com.starlog.dao.ProdutoDAOSqlite;
import br.com.starlog.model.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDAOSqliteTest {
    private Path banco;
    private ProdutoDAO dao;

    @BeforeEach
    void iniciar() throws IOException {
        banco = Files.createTempFile("aula30-test-", ".db");
        System.setProperty("starlog.jdbc.url", "jdbc:sqlite:" + banco.toAbsolutePath());
        dao = new ProdutoDAOSqlite();
    }

    @AfterEach
    void finalizar() throws IOException {
        System.clearProperty("starlog.jdbc.url");
        Files.deleteIfExists(banco);
    }

    @Test
    void fluxoCrudCompleto() {
        Produto teclado = new Produto("Teclado Mecânico RGB", 349.90, 8);
        Produto mousepad = new Produto("Mousepad Speed XL", 69.90, 20);
        dao.salvar(teclado);
        dao.salvar(mousepad);
        assertEquals(1L, teclado.getId());
        assertEquals(2L, mousepad.getId());
        assertEquals(2, dao.listarTodos().size());

        teclado.setPreco(289.90);
        teclado.setEstoque(15);
        assertTrue(dao.atualizar(teclado));
        Produto encontrado = dao.buscarPorId(teclado.getId()).orElseThrow();
        assertEquals(289.90, encontrado.getPreco(), 0.001);
        assertEquals(15, encontrado.getEstoque());

        assertTrue(dao.excluir(mousepad.getId()));
        assertFalse(dao.excluir(888L));
        assertTrue(dao.buscarPorId(mousepad.getId()).isEmpty());
        assertEquals(1, dao.listarTodos().size());
    }

    @Test
    void listagemOrdenadaPorNome() {
        dao.salvar(new Produto("Zebra", 10.0, 0));
        dao.salvar(new Produto("Abacaxi", 20.0, 2));
        List<Produto> lista = dao.listarTodos();
        assertEquals("Abacaxi", lista.get(0).getNome());
        assertEquals("Zebra", lista.get(1).getNome());
    }

    @Test
    void idInexistenteNaoAlteraDados() {
        Produto p = new Produto(999L, "Produto Falso", 5.0, 1);
        assertFalse(dao.atualizar(p));
        assertFalse(dao.excluir(999L));
        assertTrue(dao.buscarPorId(999L).isEmpty());
    }

    @Test
    void invariantesRejeitamDadosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Produto("ab", 10, 0));
        assertThrows(IllegalArgumentException.class, () -> new Produto(null, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> new Produto("Válido", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Produto("Válido", Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> new Produto("Válido", 10, -1));
        Produto p = new Produto("Válido", 10, 0);
        assertThrows(IllegalArgumentException.class, () -> p.setEstoque(-5));
        assertThrows(IllegalArgumentException.class, () -> p.setPreco(-1));
    }
}
