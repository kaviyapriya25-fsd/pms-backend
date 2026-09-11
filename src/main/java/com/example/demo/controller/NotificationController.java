package com.example.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Notification;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public NotificationController(NotificationService notificationService, SecurityUtil securityUtil) {
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public List<Notification> getAll(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        return notificationService.getNotificationsForUser(activeCompanyId, email, role);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        long count = notificationService.getUnreadCountForUser(activeCompanyId, email, role);
        return Collections.singletonMap("unreadCount", count);
    }

    @PostMapping
    public Notification add(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Notification notification) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, notification.getCompanyId());
        if (activeCompanyId != null) {
            notification.setCompanyId(activeCompanyId);
        }
        return notificationService.save(notification);
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable int id) {
        return notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) Integer companyId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        Integer activeCompanyId = securityUtil.extractCompanyId(authHeader, companyId);
        notificationService.markAllAsRead(activeCompanyId, email, role);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public Notification update(
            @PathVariable int id,
            @RequestBody Notification payload) {

        Notification existing = notificationService.markAsRead(id);
        if (existing != null) {
            if (payload.getTitle() != null) existing.setTitle(payload.getTitle());
            if (payload.getMessage() != null) existing.setMessage(payload.getMessage());
            if (payload.getType() != null) existing.setType(payload.getType());
            if (payload.getTime() != null) existing.setTime(payload.getTime());
            if (payload.getRecipientEmail() != null) existing.setRecipientEmail(payload.getRecipientEmail());
            if (payload.getRecipientRole() != null) existing.setRecipientRole(payload.getRecipientRole());
            existing.setRead(payload.isRead());
            return notificationService.save(existing);
        } else {
            payload.setId(id);
            return notificationService.save(payload);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        notificationService.delete(id);
    }

}