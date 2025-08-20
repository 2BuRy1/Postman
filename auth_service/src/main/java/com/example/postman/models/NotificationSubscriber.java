package com.example.postman.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSubscriber {



    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private NotificationProducer producer;

    private String name;

    private String token;


}
