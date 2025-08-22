package com.example.notification_service.repositories;

import com.example.notification_service.models.NotificationSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<NotificationSubscriber, Long> {
    Optional<NotificationSubscriber> findBySubscriptionJson(String subscription);


}
