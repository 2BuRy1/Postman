package com.example.notification_service.services;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.encrypt.BouncyCastleAesCbcBytesEncryptor;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;

@Service
public class NotificationPushService {


    @Value("${key.private}")
    private String privateKey;


    @Getter
    @Value("${key.public}")
    private String publicKey;

    private PushService pushService;


    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }




    public void subscribe(Subscription subscription){
        System.out.println(subscription);
    }










}
