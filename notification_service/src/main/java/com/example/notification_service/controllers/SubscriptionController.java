package com.example.notification_service.controllers;

import com.example.notification_service.models.BasicUser;
import com.example.notification_service.models.NotificationProducer;
import com.example.notification_service.models.NotificationSubscriber;
import com.example.notification_service.models.OAuthUser;
import com.example.notification_service.repositories.ProducerRepository;
import com.example.notification_service.repositories.SubscriberRepository;
import com.example.notification_service.services.NotificationPushService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.PathParam;
import jdk.jshell.JShell;
import lombok.Getter;
import nl.martijndwars.webpush.Subscription;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.Map;

@RestController
public class SubscriptionController {

    private final ProducerRepository producerRepository;

    private final SubscriberRepository subscriberRepository;

    private final NotificationPushService pushService;

    private final ObjectMapper mapper;

    @Autowired
    public SubscriptionController(ProducerRepository repository, ProducerRepository producerRepository, SubscriberRepository subscriberRepository, NotificationPushService pushService, ObjectMapper mapper) {
        this.producerRepository = producerRepository;
        this.subscriberRepository = subscriberRepository;
        this.pushService = pushService;
        this.mapper = mapper;
    }


    @GetMapping("/get_key")
    public ResponseEntity<Map<String ,String>> getKey(){

        return ResponseEntity.ok(Map.of("key", pushService.getPublicKey()));


   }


   @GetMapping("/get_currentId")
    public ResponseEntity<Map<String, String>> getId(){


       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

       ;

       Object principal = authentication.getPrincipal();

       System.out.println(principal.toString());


       boolean saved = false;

        if(principal instanceof OAuthUser u){

            if(producerRepository.findByProviderId(u.getProviderId()).isEmpty()){
                producerRepository.save(NotificationProducer.builder().providerId(u.getProviderId()).build());
                saved=true;
            }


            return ResponseEntity.ok(Map.of("id", u.getProviderId()));
        }
        else if( principal instanceof BasicUser u){
            if(!saved){
                if(producerRepository.findByProviderId(u.getUsername()).isEmpty()){
                    producerRepository.save(NotificationProducer.builder().providerId(u.getUsername()).build());
                    saved=true;
                }
            }
            return ResponseEntity.ok(Map.of("id", u.getUsername()));

        }

        return ResponseEntity.status(401).body(Map.of("status", "unauthorized"));

   }

  @PostMapping("save-subscription/{id}")
  public ResponseEntity<String> saveSubscription(@RequestBody Subscription subscription,
                                                 @PathVariable("id") String id,
                                                 @RequestParam String name
                                                 ) throws JsonProcessingException {
        var optional = producerRepository.findByProviderId(id);
      if (optional.isPresent() && name!=null && !name.isEmpty()) {
          var producer = optional.get();

          NotificationSubscriber subscriber = new NotificationSubscriber();
          subscriber.setProducer(producer);
          subscriber.setName(name);
          subscriber.setSubscriptionJson(mapper.writeValueAsString(subscription));
          subscriberRepository.save(subscriber);

          System.out.println(subscription.endpoint + " " + id);
          return ResponseEntity.ok("saved");

      }
      return ResponseEntity.badRequest().body("no such user");
  }


}
