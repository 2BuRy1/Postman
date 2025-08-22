package com.example.postman.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriber")
public class NotificationSubscriber {



    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private NotificationProducer producer;

    private String name;

    private String token;


}
