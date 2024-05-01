//package com.FINAL.KIP.common.firebase;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import java.io.IOException;
//import java.io.InputStream;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FCMInitializer {
//
//	@Value("${fcm.certification}")
//	private String googleApplicationCredentials;
//
//	@PostConstruct
//	public void initialize() throws IOException {
//		ClassPathResource resource = new ClassPathResource(googleApplicationCredentials);
//
//		try (InputStream is = resource.getInputStream()) {
//			FirebaseOptions options = FirebaseOptions.builder()
//				.setCredentials(GoogleCredentials.fromStream(is))
//				.build();
//
//			if (FirebaseApp.getApps().isEmpty()) {
//				FirebaseApp.initializeApp(options);
//			}
//		}
//	}
//
//}