package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Integer>{

    long countByIsRead(boolean isRead);

}