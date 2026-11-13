package br.com.starlog.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoFactory {
    private static final String URL_PADRAO = "jdbc:sqlite:almoxarifado.db";

    private ConexaoFactory() {}

    public static Connection obterConexao() throws SQLException {
        String url = System.getProperty("starlog.jdbc.url", URL_PADRAO);
        return DriverManager.getConnection(url);
    }
}
