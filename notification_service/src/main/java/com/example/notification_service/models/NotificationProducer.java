package com.example.notification_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "producer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationProducer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    private String providerId;

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    private List<NotificationSubscriber> list;



}
