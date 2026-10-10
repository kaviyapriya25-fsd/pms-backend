package com.example.demo.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateBuilder templateBuilder;

    @Value("${app.mail.from:noreply@multicompanypms.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public EmailService(JavaMailSender mailSender, EmailTemplateBuilder templateBuilder) {
        this.mailSender = mailSender;
        this.templateBuilder = templateBuilder;
    }

    public boolean sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        if (toEmail == null || toEmail.isBlank()) {
            System.err.println("[EmailService] Cannot send email: recipient address is empty.");
            return false;
        }

        if (!mailEnabled) {
            System.out.println("[EmailService - Disabled] Mock email dispatch to: " + toEmail + " | Subject: " + subject);
            return true;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("[EmailService] Successfully dispatched HTML email to: " + toEmail + " | Subject: " + subject);
            return true;

        } catch (Exception e) {
            System.err.println("[EmailService Error] Failed to send email to " + toEmail + ": " + e.getMessage());
            // Log fallback message content for dev debugging
            System.out.println("=== EMAIL FALLBACK CONTENT ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println("==============================");
            return false;
        }
    }

    public void sendWelcomeEmail(String toEmail, String userName, String role, String loginUrl) {
        String subject = "Welcome to Project Management System!";
        String html = templateBuilder.buildWelcomeEmail(userName, role, loginUrl);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendOtpEmail(String toEmail, String userName, String otpCode, int expiryMinutes) {
        String subject = "Your Password Reset OTP Code (Valid for " + expiryMinutes + " Mins) - Project Management System";
        String html = templateBuilder.buildOtpEmail(userName, otpCode, expiryMinutes);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendRegistrationOtpEmail(String toEmail, String userName, String otpCode, int expiryMinutes) {
        String subject = "Verify Admin Email - Registration OTP Code (Valid for " + expiryMinutes + " Mins) | Project Management System";
        String html = templateBuilder.buildRegistrationOtpEmail(userName, otpCode, expiryMinutes);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendPasswordChangedEmail(String toEmail, String userName) {
        String subject = "Password Changed Notification - Project Management System";
        String html = templateBuilder.buildPasswordChangedEmail(userName);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendTaskAssignedEmail(String toEmail, String userName, String taskTitle, String projectName, String priority, String dueDate, String description) {
        String subject = "New Task Assigned: " + (taskTitle != null ? taskTitle : "Task Notification");
        String html = templateBuilder.buildTaskAssignedEmail(userName, taskTitle, projectName, priority, dueDate, description);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendProjectAssignedEmail(String toEmail, String userName, String projectName, String description, String priority, String startDate, String endDate) {
        String subject = "New Project Assigned: " + (projectName != null ? projectName : "Project Notification");
        String html = templateBuilder.buildProjectAssignedEmail(userName, projectName, description, priority, startDate, endDate);
        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendMemberCreatedEmail(String toEmail, String userName, String role) {
        String subject = "Account Provisioned - Set Your Password | Project Management System";
        try {
            String setPasswordUrl = "http://localhost:5173/forgot-password?email=" + java.net.URLEncoder.encode(toEmail, "UTF-8") + "&role=" + role;
            String html = templateBuilder.buildMemberCreatedEmail(userName, role, setPasswordUrl);
            sendHtmlEmail(toEmail, subject, html);
        } catch (Exception e) {
            System.err.println("[EmailService] Failed to encode setPasswordUrl: " + e.getMessage());
        }
    }

    public void sendLoginAlertEmail(String toEmail, String userName, String role, String loginTimestamp) {
        String subject = "Security Alert: Successful Login to Project Management System - " + (role != null ? role.replace("_", " ") : "User");
        String html = templateBuilder.buildLoginAlertEmail(userName, role, loginTimestamp);
        sendHtmlEmail(toEmail, subject, html);
    }
}
