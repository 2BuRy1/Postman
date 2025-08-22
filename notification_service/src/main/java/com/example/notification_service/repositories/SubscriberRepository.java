package com.example.notification_service.repositories;

import com.example.notification_service.models.NotificationSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<NotificationSubscriber, Long> {

}
