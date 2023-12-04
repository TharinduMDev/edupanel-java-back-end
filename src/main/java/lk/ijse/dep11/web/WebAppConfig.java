package lk.ijse.dep11.web;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@ComponentScan
@EnableWebMvc
public class WebAppConfig {
    public Bucket defaultBucket() throws IOException {
        InputStream serviceAccount = getClass().getResourceAsStream("/dep11edupanel-firebase-adminsdk-dlhzh-53ffc3bc2f.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("dep11edupanel.appspot.com")         //link from firebase bucket
                .build();

        FirebaseApp.initializeApp(options);
        return StorageClient.getInstance().bucket();

    }
    @Bean
    public StandardServletMultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();                  //it is neaccesary otherwith we can't convert multypart form data request to TO Object
    }
}
