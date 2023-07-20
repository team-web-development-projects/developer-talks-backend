package com.dtalks.dtalks.base.config;

import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Configuration
public class FirebaseInitializer {

    @Value("${firebase.firebaseConfigPath}")
    private String firebaseConfigPath;

    @Value("${firebase.scope}")
    private String scope;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            ClassPathResource resource = new ClassPathResource(firebaseConfigPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream())
                            .createScoped(Collections.singleton(scope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("[FCM] - Firebase init start");
            }
        } catch (IOException ex){
            throw new CustomException(ErrorCode.FIREBASE_INIT_ERROR, "[FCM] - firebase init error");
        }
    }
}
