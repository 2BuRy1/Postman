package com.example.worker.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Encoding;
import com.example.notification_service.models.NotificationSendDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;

@Service
public class WebPushService {



    private final ObjectMapper mapper;

    @Value("${key.private}")
    private String privateKey;


    @Getter
    @Value("${key.public}")
    private String publicKey;

    private PushService pushService;


    @Autowired
    public WebPushService(ObjectMapper mapper) {
        this.mapper = mapper;
    }




    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }

    @KafkaListener(topics = {"worker_topic"}, concurrency = "60")
    public void sendNotification(String data) throws IOException, JoseException, GeneralSecurityException, ExecutionException, InterruptedException {
        System.out.println(data);
        NotificationSendDTO not= mapper.readValue(data, NotificationSendDTO.class);

        ObjectNode payload = mapper.createObjectNode();
        payload.put("title", "Новое уведомление");
        payload.put("body", not.getMessage());
        String jsonPayload = mapper.writeValueAsString(payload);

        var response = pushService.send(new Notification(
                mapper.readValue(not.getSubscriptionJson(), Subscription.class),
                jsonPayload
        ));

        System.out.println(response.getStatusLine());


    }




}
