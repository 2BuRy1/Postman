package com.example.postman.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Entity(name = "notification_producer")
@Data
@ToString
public class NotificationProducer {


    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<NotificationSubscriber> subscribers;





}
