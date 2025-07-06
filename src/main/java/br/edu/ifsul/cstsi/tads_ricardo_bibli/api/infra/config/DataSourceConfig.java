package br.edu.ifsul.cstsi.tads_ricardo_bibli.api.infra.config; // Ajuste o pacote conforme sua estrutura

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Value("${JAWSDB_MARIA_URL}")
    private String jawsdbMariaUrl;

    @Bean
    @Primary // Indica que este é o DataSource principal a ser usado
    public DataSource dataSource() throws URISyntaxException {
        HikariConfig config = new HikariConfig();

        // Parse a URL do Heroku para extrair os componentes
        URI dbUri = new URI(jawsdbMariaUrl);

        // Reconstrua a URL com o prefixo correto para MariaDB
        // Exemplo: mysql://user:pass@host:port/dbname -> jdbc:mariadb://host:port/dbname
        String jdbcUrl = "jdbc:mariadb://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUri.getUserInfo().split(":")[0]); // Extrai o usuário
        config.setPassword(dbUri.getUserInfo().split(":")[1]); // Extrai a senha
        config.setDriverClassName("org.mariadb.jdbc.Driver"); // Garante o driver correto

        // Opcional: Adicione parâmetros de conexão que você tinha no .properties
        if (dbUri.getQuery() != null) {
            config.setJdbcUrl(jdbcUrl + "?" + dbUri.getQuery());
        } else {
            // Se não houver query na URL do Heroku, adicione seus parâmetros padrão aqui
            config.setJdbcUrl(jdbcUrl + "?autoReconnect=true&useSSL=false&serverTimezone=UTC");
        }

        // Configurações adicionais do HikariCP (opcional, pode ser do seu .properties)
        config.setConnectionTestQuery("SELECT 1"); // Query simples para testar a conexão
        config.setMaximumPoolSize(10); // Tamanho máximo do pool
        config.setMinimumIdle(2); // Mínimo de conexões ociosas
        config.setIdleTimeout(600000); // Tempo para fechar conexões ociosas (10 minutos)
        config.setMaxLifetime(1800000); // Tempo máximo de vida de uma conexão (30 minutos)

        return new HikariDataSource(config);
    }
}