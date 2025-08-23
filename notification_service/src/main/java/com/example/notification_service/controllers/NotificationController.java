package com.example.notification_service.controllers;


import com.example.notification_service.models.NotificationProducer;
import com.example.notification_service.models.NotificationRequestDTO;
import com.example.notification_service.models.NotificationSendDTO;
import com.example.notification_service.models.NotificationSubscriber;
import com.example.notification_service.repositories.ProducerRepository;
import com.example.notification_service.repositories.SubscriberRepository;
import com.example.notification_service.services.NotificationPushService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class NotificationController {

    private final ProducerRepository producerRepository;

    private final SubscriberRepository subscriberRepository;

    private final ObjectMapper mapper;

    private final NotificationPushService pushService;

    @Autowired
    public NotificationController(ProducerRepository producerRepository, SubscriberRepository subscriberRepository, ObjectMapper mapper, NotificationPushService pushService) {
        this.producerRepository = producerRepository;
        this.subscriberRepository = subscriberRepository;
        this.mapper = mapper;
        this.pushService = pushService;
    }


    @PostMapping("/notificate")
    public ResponseEntity<Map<String, String>> notificate(@RequestBody NotificationRequestDTO dto,
                                                          @RequestParam("id") String id) throws JsonProcessingException {

        Optional<NotificationProducer> optional = producerRepository.findByProviderId(id);

        if(optional.isPresent()){

            NotificationProducer producer = optional.get();



            ArrayList<NotificationSubscriber> list = findIntersection(subscriberRepository.getAllByProducer(producer), dto.getNames());


            ArrayList<NotificationSendDTO> toSend = new ArrayList<>();

            list.forEach(el -> {
                    toSend.add(new NotificationSendDTO(el.getSubscriptionJson(),
                            dto.getMessage()));


            });
            if(!toSend.isEmpty())  pushService.sendNotifications(toSend);


        }




        return ResponseEntity.ok(Map.of("status", "successfully sent to all available users"));
    }


    private ArrayList<NotificationSubscriber> findIntersection(ArrayList<NotificationSubscriber> list, String[] array){


         ArrayList<String> array1 = new ArrayList<>(Arrays.stream(array).toList());

         list = list.stream().filter(el -> array1.contains(el.getName())).collect(Collectors.toCollection(ArrayList::new));

        return list;

    }







}
