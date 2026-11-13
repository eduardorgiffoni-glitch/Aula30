# Aula 30 — StarLog Almoxarifado (CRUD completo)

Solução do laboratório **Aula 30 — Prática DAO Completa & CRUD de Negócio**, em Java 17, Maven e SQLite.

## Como executar

Requisitos: **JDK 17+** e **Maven 3.8+**.

```bash
mvn clean compile exec:java
```

Na primeira execução, o SQLite cria automaticamente a base `almoxarifado.db` e a tabela `produtos`. O programa demonstra:

1. **Create:** insere Teclado Mecânico RGB e Mousepad Speed XL, mostrando os IDs gerados.
2. **Read:** lista o catálogo em colunas, ordenado por `nome ASC`.
3. **Update:** altera o preço do teclado para **R$ 289,90** e estoque para **15**, e consulta o resultado.
4. **Delete:** exclui o mousepad e tenta excluir o ID 888, demonstrando retorno `false` quando não encontrado.
5. **Final State:** mostra o catálogo após a exclusão.

**Importante:** em um banco inicialmente vazio sobra exatamente um produto. O banco é persistente: executar novamente insere outros registros sem apagar os anteriores. Para repetir a demonstração do zero, exclua localmente `almoxarifado.db` antes da execução.

## Organização MVC / DAO

```text
src/main/java/br/com/starlog/
├── model/Produto.java           # Invariantes e dados de domínio
├── dao/ProdutoDAO.java          # Contrato com 5 operações
├── dao/ProdutoDAOSqlite.java    # JDBC e CRUD seguro
├── db/ConexaoFactory.java       # Fábrica de conexões
└── main/Main.java               # Simulação de negócio no terminal
src/test/java/br/com/starlog/
└── ProdutoDAOSqliteTest.java    # Testes com banco temporário
```

A implementação usa `PreparedStatement` com parâmetros, `try-with-resources`, `WHERE id = ?` no UPDATE e DELETE, `executeUpdate() > 0`, `Optional<Produto>` e `mapearLinha(ResultSet)`.

## Executar testes

```bash
mvn test
```

Os testes criam uma base SQLite temporária, sem modificar a base principal.
