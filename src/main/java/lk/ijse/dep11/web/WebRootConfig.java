package lk.ijse.dep11.web;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@PropertySource("/application.properties")
public class WebRootConfig {

    @Bean(destroyMethod = "close")
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
