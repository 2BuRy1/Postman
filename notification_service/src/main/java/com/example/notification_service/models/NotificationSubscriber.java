package com.example.notification_service.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String subscriptionJson;

    @ManyToOne
    @JoinColumn(name = "producer_id", nullable = false)
    private NotificationProducer producer;
}