package lk.ijse.dep11.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("/apllication.properties")
public class WebRootConfig {
    @Bean
    public HikariDataSource dataSource(Environment env){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(env.getRequiredProperty("spring-data-source-url"));
        hikariConfig.setUsername(env.getRequiredProperty("spring-data-source-username"));
        hikariConfig.setPassword(env.getRequiredProperty("spring-data-source-password"));
        hikariConfig.setDriverClassName(env.getRequiredProperty("spring-data-source-driver-class-name"));
        hikariConfig.setMaximumPoolSize(env.getRequiredProperty("spring-data-source-hikari.maximum-pool-size",Integer.class));
        return new HikariDataSource(hikariConfig);
    }
}
