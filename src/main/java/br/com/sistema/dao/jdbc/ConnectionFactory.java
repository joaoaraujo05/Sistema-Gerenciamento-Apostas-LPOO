package br.com.sistema.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

    private static final String URL = "jdbc:h2:./apostas_db";
    private static final String usuario = "sa";
    private static final String senha = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,usuario,senha);
    }

    public static void createDatabase() {
        String sqlTimes = "CREATE TABLE IF NOT EXISTS times (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "nome VARCHAR(25) NOT NULL" +
                ");";

        String sqlCampeonatos = "CREATE TABLE IF NOT EXISTS campeonatos (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "nome VARCHAR(50) NOT NULL" +
                ");";

        String sqlCampeonatoTime = "CREATE TABLE IF NOT EXISTS campeonato_time (" +
                "campeonato_id BIGINT," +
                "time_id BIGINT," +
                "PRIMARY KEY (campeonato_id, time_id)" +
                ");";

        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "nome VARCHAR(100) NOT NULL," +
                "login VARCHAR(50) NOT NULL UNIQUE," +
                "senha VARCHAR(50) NOT NULL," +
                "perfil VARCHAR(20) NOT NULL," +
                "pontuacao_total INT" +
                ");";

        String sqlParticipanteGrupo = "CREATE TABLE IF NOT EXISTS participante_grupo (" +
                "participante_id BIGINT," +
                "grupo_id BIGINT," +
                "PRIMARY KEY (participante_id, grupo_id)" +
                ");";

        String sqlGrupos = "CREATE TABLE IF NOT EXISTS grupos (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "nome VARCHAR(50) NOT NULL" +
                ");";

        String sqlPartidas = "CREATE TABLE IF NOT EXISTS partidas (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "campeonato_id BIGINT," +
                "mandante_id BIGINT," +
                "visitante_id BIGINT," +
                "data_hora TIMESTAMP NOT NULL," +
                "gols_mandante INT," +
                "gols_visitante INT," +
                "finalizada BOOLEAN" +
                ");";

        String sqlApostas = "CREATE TABLE IF NOT EXISTS apostas (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "participante_id BIGINT," +
                "partida_id BIGINT," +
                "resultado VARCHAR(15)," +
                "gols_mandante INT," +
                "gols_visitante INT," +
                "pontos INT" +
                ");";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement()) {

            st.execute(sqlTimes);
            st.execute(sqlCampeonatos);
            st.execute(sqlCampeonatoTime);
            st.execute(sqlUsuarios);
            st.execute(sqlParticipanteGrupo);
            st.execute(sqlGrupos);
            st.execute(sqlPartidas);
            st.execute(sqlApostas);
            System.out.println("Tabelas garantidas com sucesso.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}