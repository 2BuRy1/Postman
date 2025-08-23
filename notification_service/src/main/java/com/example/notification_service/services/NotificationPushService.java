package com.example.notification_service.services;

import com.example.notification_service.models.NotificationSendDTO;
import com.example.notification_service.models.NotificationSubscriber;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.Urgency;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationPushService {


    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper;

    @Value("${key.private}")
    private String privateKey;


    @Getter
    @Value("${key.public}")
    private String publicKey;

    private PushService pushService;


    @Autowired
    public NotificationPushService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }


    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }


    public void sendNotifications(ArrayList<NotificationSendDTO> list) throws JsonProcessingException {

        for(var e: list){
            kafkaTemplate.send("worker_topic", mapper.writeValueAsString(e));
        }


    }


    public void send(Subscription subscription) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException, JoseException {
        org.apache.http.HttpResponse send;
        System.out.println("lal");
        Notification notification = new Notification(
                subscription,
                "{\"title\":\"Привет\",\"body\":\"Сообщение из Java!\"}",
                Urgency.NORMAL
        );


        pushService.send(notification);
    }
}
