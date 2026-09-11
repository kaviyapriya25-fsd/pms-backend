package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByCompanyId(Integer companyId);

    List<Notification> findByCompanyIdOrderByIdDesc(Integer companyId);

    @Query("SELECT n FROM Notification n WHERE n.companyId = :companyId AND " +
           "(n.recipientEmail IS NULL OR n.recipientEmail = '' OR LOWER(n.recipientEmail) = LOWER(:email)) AND " +
           "(n.recipientRole IS NULL OR n.recipientRole = '' OR n.recipientRole = 'ALL' OR LOWER(n.recipientRole) = LOWER(:role)) " +
           "ORDER BY n.id DESC")
    List<Notification> findForUser(
            @Param("companyId") Integer companyId,
            @Param("email") String email,
            @Param("role") String role);

    long countByIsRead(boolean isRead);

    long countByCompanyIdAndIsRead(Integer companyId, boolean isRead);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.companyId = :companyId AND n.isRead = false AND " +
           "(n.recipientEmail IS NULL OR n.recipientEmail = '' OR LOWER(n.recipientEmail) = LOWER(:email)) AND " +
           "(n.recipientRole IS NULL OR n.recipientRole = '' OR n.recipientRole = 'ALL' OR LOWER(n.recipientRole) = LOWER(:role))")
    long countUnreadForUser(
            @Param("companyId") Integer companyId,
            @Param("email") String email,
            @Param("role") String role);

}