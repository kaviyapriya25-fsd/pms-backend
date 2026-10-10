package com.example.demo.service;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class EmailTemplateBuilder {

    private String getHeaderHtml(String subtitle) {
        String template = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    background-color: #f1f5f9;
                    margin: 0;
                    padding: 0;
                    -webkit-font-smoothing: antialiased;
                }
                .container {
                    max-width: 600px;
                    margin: 30px auto;
                    background-color: #ffffff;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
                    border: 1px solid #e2e8f0;
                }
                .header {
                    background: linear-gradient(135deg, #1e1b4b 0%, #4338ca 100%);
                    color: #ffffff;
                    padding: 32px 24px;
                    text-align: center;
                }
                .header h1 {
                    margin: 0;
                    font-size: 24px;
                    font-weight: 800;
                    letter-spacing: 0.5px;
                }
                .header p {
                    margin: 6px 0 0 0;
                    font-size: 13px;
                    color: #c7d2fe;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    font-weight: 600;
                }
                .body-content {
                    padding: 32px 28px;
                    color: #334155;
                    line-height: 1.6;
                    font-size: 15px;
                }
                .greeting {
                    font-size: 18px;
                    font-weight: 700;
                    color: #0f172a;
                    margin-bottom: 16px;
                }
                .badge {
                    display: inline-block;
                    padding: 4px 12px;
                    border-radius: 20px;
                    font-size: 12px;
                    font-weight: 700;
                    text-transform: uppercase;
                }
                .badge-primary { background-color: #e0e7ff; color: #3730a3; }
                .badge-success { background-color: #dcfce7; color: #166534; }
                .badge-warning { background-color: #fef3c7; color: #92400e; }
                .badge-danger { background-color: #fee2e2; color: #991b1b; }
                .card-box {
                    background-color: #f8fafc;
                    border: 1px solid #e2e8f0;
                    border-left: 4px solid #4338ca;
                    border-radius: 8px;
                    padding: 20px;
                    margin: 20px 0;
                }
                .card-row {
                    display: flex;
                    margin-bottom: 10px;
                }
                .card-label {
                    font-weight: 600;
                    width: 130px;
                    color: #64748b;
                }
                .card-value {
                    font-weight: 500;
                    color: #0f172a;
                }
                .otp-box {
                    background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 100%);
                    border: 2px dashed #6366f1;
                    border-radius: 12px;
                    padding: 24px;
                    text-align: center;
                    margin: 24px 0;
                }
                .otp-code {
                    font-family: 'Courier New', Courier, monospace;
                    font-size: 38px;
                    font-weight: 900;
                    letter-spacing: 8px;
                    color: #312e81;
                    margin: 8px 0;
                }
                .btn {
                    display: inline-block;
                    background: linear-gradient(135deg, #4f46e5 0%, #3730a3 100%);
                    color: #ffffff !important;
                    text-decoration: none;
                    font-weight: 700;
                    padding: 14px 28px;
                    border-radius: 8px;
                    margin-top: 20px;
                    box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
                }
                .footer {
                    background-color: #f8fafc;
                    border-top: 1px solid #e2e8f0;
                    padding: 20px;
                    text-align: center;
                    font-size: 12px;
                    color: #94a3b8;
                }
                .footer p { margin: 4px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div className="header">
                    <h1>Project Management System</h1>
                    <p>{{SUBTITLE}}</p>
                </div>
                <div className="body-content">
        """;
        return template.replace("{{SUBTITLE}}", subtitle);
    }

    private String getFooterHtml() {
        int year = Year.now().getValue();
        String template = """
                </div>
                <div class="footer">
                    <p>This is an automated notification from Project Management System.</p>
                    <p>Please do not reply directly to this email.</p>
                    <p>&copy; {{YEAR}} Project Management System. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """;
        return template.replace("{{YEAR}}", String.valueOf(year));
    }

    // 1. WELCOME EMAIL
    public String buildWelcomeEmail(String userName, String role, String loginUrl) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "Valued User";
        String safeRole = (role != null) ? role.toUpperCase() : "MEMBER";

        return getHeaderHtml("Welcome Notification")
                + "<div class=\"greeting\">Welcome to Project Management System, " + safeName + "! 👋</div>"
                + "<p>Your account has been successfully created. We are excited to have you onboard.</p>"
                + "<div class=\"card-box\">"
                + "  <div style=\"margin-bottom: 8px;\"><span class=\"card-label\">Account Name:</span> <span class=\"card-value\">" + safeName + "</span></div>"
                + "  <div style=\"margin-bottom: 8px;\"><span class=\"card-label\">Role Assigned:</span> <span class=\"badge badge-primary\">" + safeRole + "</span></div>"
                + "  <div><span class=\"card-label\">System Status:</span> <span class=\"badge badge-success\">ACTIVE</span></div>"
                + "</div>"
                + "<p>You can log into the platform using your registered email address and credentials.</p>"
                + "<div style=\"text-align: center;\">"
                + "  <a href=\"" + (loginUrl != null ? loginUrl : "http://localhost:5173/login") + "\" class=\"btn\">Login to Workspace Portal</a>"
                + "</div>"
                + getFooterHtml();
    }

    // 2. FORGOT PASSWORD OTP EMAIL
    public String buildOtpEmail(String userName, String otpCode, int expiryMinutes) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "User";

        return getHeaderHtml("Security Verification")
                + "<div class=\"greeting\">Hello " + safeName + ",</div>"
                + "<p>We received a request to reset your password for your Project Management System account.</p>"
                + "<p>Use the following 6-digit Verification OTP code to complete your password reset request:</p>"
                + "<div class=\"otp-box\">"
                + "  <div style=\"font-size: 13px; color: #475569; font-weight: 600; text-transform: uppercase;\">Your Verification Code</div>"
                + "  <div class=\"otp-code\">" + otpCode + "</div>"
                + "  <div style=\"font-size: 14px; color: #dc2626; font-weight: 700; margin-top: 6px;\">⏰ Valid for " + expiryMinutes + " minutes only (Strict Expiry)</div>"
                + "</div>"
                + "<p style=\"color: #b91c1c; font-weight: 600; font-size: 13px;\">⚠️ Note: This OTP is strictly valid for " + expiryMinutes + " minutes. After " + expiryMinutes + " minutes, it will expire and cannot be used.</p>"
                + "<p>If you did not request a password reset, please ignore this email or contact system administration immediately.</p>"
                + getFooterHtml();
    }

    // 2b. COMPANY REGISTRATION ADMIN EMAIL VERIFICATION OTP
    public String buildRegistrationOtpEmail(String userName, String otpCode, int expiryMinutes) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "Administrator";

        return getHeaderHtml("Company Registration Verification")
                + "<div class=\"greeting\">Hello " + safeName + ", 👋</div>"
                + "<p>Thank you for initiating company registration on Project Management System.</p>"
                + "<p>Please verify your administrator email address using the following 6-digit OTP code:</p>"
                + "<div class=\"otp-box\">"
                + "  <div style=\"font-size: 13px; color: #475569; font-weight: 600; text-transform: uppercase;\">Admin Verification OTP</div>"
                + "  <div class=\"otp-code\">" + otpCode + "</div>"
                + "  <div style=\"font-size: 14px; color: #dc2626; font-weight: 700; margin-top: 6px;\">⏰ Valid for " + expiryMinutes + " minutes only (Strict Expiry)</div>"
                + "</div>"
                + "<p style=\"color: #b91c1c; font-weight: 600; font-size: 13px;\">⚠️ Note: This OTP will automatically expire after " + expiryMinutes + " minutes. If expired, please request a fresh OTP from the registration page.</p>"
                + "<p>If you did not initiate this registration, please disregard this email.</p>"
                + getFooterHtml();
    }

    // 3. PASSWORD CHANGED EMAIL
    public String buildPasswordChangedEmail(String userName) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "User";

        return getHeaderHtml("Security Notification")
                + "<div class=\"greeting\">Password Updated Successfully 🔐</div>"
                + "<p>Hello " + safeName + ",</p>"
                + "<p>This is a confirmation that the password for your Project Management System account has been updated successfully.</p>"
                + "<div class=\"card-box\" style=\"border-left-color: #16a34a;\">"
                + "  <div style=\"margin-bottom: 8px;\"><span class=\"card-label\">Account:</span> <span class=\"card-value\">" + safeName + "</span></div>"
                + "  <div style=\"margin-bottom: 8px;\"><span class=\"card-label\">Action:</span> <span class=\"badge badge-success\">Password Reset Complete</span></div>"
                + "  <div><span class=\"card-label\">Status:</span> <span class=\"card-value\">Secured</span></div>"
                + "</div>"
                + "<p style=\"color: #b91c1c; font-weight: 600;\">⚠️ If you did not make this change, please reset your password immediately or alert your administrator.</p>"
                + getFooterHtml();
    }

    // 4. TASK ASSIGNED EMAIL
    public String buildTaskAssignedEmail(String userName, String taskTitle, String projectName, String priority, String dueDate, String description) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "Team Member";
        String safeTask = (taskTitle != null) ? taskTitle : "Untitled Task";
        String safeProject = (projectName != null && !projectName.isBlank()) ? projectName : "General";
        String safePriority = (priority != null) ? priority.toUpperCase() : "MEDIUM";
        String safeDueDate = (dueDate != null && !dueDate.isBlank()) ? dueDate : "Not Specified";
        String safeDesc = (description != null && !description.isBlank()) ? description : "No additional description provided.";

        String priorityBadgeClass = switch (safePriority) {
            case "HIGH", "URGENT" -> "badge-danger";
            case "MEDIUM" -> "badge-warning";
            default -> "badge-primary";
        };

        return getHeaderHtml("New Task Assignment")
                + "<div class=\"greeting\">New Task Assigned to You! 📋</div>"
                + "<p>Hello " + safeName + ",</p>"
                + "<p>A new task has been assigned to you in <strong>Project Management System</strong>. Here are the details:</p>"
                + "<div class=\"card-box\">"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Task Title:</span> <span class=\"card-value\" style=\"font-weight: 700; color: #4338ca;\">" + safeTask + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Project:</span> <span class=\"card-value\">" + safeProject + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Priority:</span> <span class=\"badge " + priorityBadgeClass + "\">" + safePriority + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Due Date:</span> <span class=\"card-value\" style=\"font-weight: 600;\">" + safeDueDate + "</span></div>"
                + "  <div><span class=\"card-label\">Description:</span> <div style=\"margin-top: 6px; background: #ffffff; padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 14px;\">" + safeDesc + "</div></div>"
                + "</div>"
                + "<p>Please log in to your dashboard to review and track progress on this task.</p>"
                + getFooterHtml();
    }

    // 5. PROJECT ASSIGNED EMAIL
    public String buildProjectAssignedEmail(String userName, String projectName, String description, String priority, String startDate, String endDate) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "Team Leader";
        String safeProject = (projectName != null) ? projectName : "Untitled Project";
        String safePriority = (priority != null) ? priority.toUpperCase() : "MEDIUM";
        String safeStart = (startDate != null && !startDate.isBlank()) ? startDate : "TBD";
        String safeEnd = (endDate != null && !endDate.isBlank()) ? endDate : "TBD";
        String safeDesc = (description != null && !description.isBlank()) ? description : "No description available.";

        String priorityBadgeClass = switch (safePriority) {
            case "HIGH", "CRITICAL" -> "badge-danger";
            case "MEDIUM" -> "badge-warning";
            default -> "badge-primary";
        };

        return getHeaderHtml("Project Assignment")
                + "<div class=\"greeting\">Project Assignment Alert 🚀</div>"
                + "<p>Hello " + safeName + ",</p>"
                + "<p>You have been assigned as the project leader for <strong>" + safeProject + "</strong>.</p>"
                + "<div class=\"card-box\" style=\"border-left-color: #0284c7;\">"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Project Name:</span> <span class=\"card-value\" style=\"font-size: 16px; font-weight: 700; color: #0369a1;\">" + safeProject + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Priority:</span> <span class=\"badge " + priorityBadgeClass + "\">" + safePriority + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Start Date:</span> <span class=\"card-value\">" + safeStart + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">End Date:</span> <span class=\"card-value\">" + safeEnd + "</span></div>"
                + "  <div><span class=\"card-label\">Overview:</span> <div style=\"margin-top: 6px; background: #ffffff; padding: 10px; border-radius: 6px; border: 1px solid #cbd5e1; font-size: 14px;\">" + safeDesc + "</div></div>"
                + "</div>"
                + "<p>Access your leader workspace to assign tasks and manage team deliverables.</p>"
                + getFooterHtml();
    }

    // 6. MEMBER ACCOUNT CREATED EMAIL
    public String buildMemberCreatedEmail(String userName, String role, String setPasswordUrl) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "Team Member";
        String safeRole = (role != null) ? role.replace("_", " ") : "EMPLOYEE";

        return getHeaderHtml("Account Provisioned")
                + "<div class=\"greeting\">Welcome to Project Management System! 🎉</div>"
                + "<p>Hello <strong>" + safeName + "</strong>,</p>"
                + "<p>An organization account has been created for you as a <strong>" + safeRole + "</strong>.</p>"
                + "<div class=\"card-box\" style=\"border-left-color: #10b981;\">"
                + "  <p style=\"margin: 0 0 10px 0; font-weight: 700; color: #0f172a;\">🔒 Secure Password Setup Required</p>"
                + "  <p style=\"margin: 0; font-size: 14px; color: #475569;\">To ensure maximum privacy and security, your initial password is auto-generated and encrypted. You must set your own private password using OTP verification before logging in.</p>"
                + "</div>"
                + "<div style=\"text-align: center; margin: 28px 0;\">"
                + "  <a href=\"" + setPasswordUrl + "\" style=\"background-color: #4338ca; color: #ffffff; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: 700; font-size: 15px; display: inline-block;\">Set Password via Email OTP →</a>"
                + "</div>"
                + "<p style=\"font-size: 13px; color: #64748b;\">If the button above does not work, copy and paste this link into your browser:<br/><a href=\"" + setPasswordUrl + "\">" + setPasswordUrl + "</a></p>"
                + getFooterHtml();
    }

    // 7. LOGIN SECURITY ALERT EMAIL
    public String buildLoginAlertEmail(String userName, String role, String loginTimestamp) {
        String safeName = (userName != null && !userName.isBlank()) ? userName : "User";
        String safeRole = (role != null) ? role.replace("_", " ") : "USER";
        String safeTime = (loginTimestamp != null && !loginTimestamp.isBlank()) ? loginTimestamp : java.time.LocalDateTime.now().toString();

        return getHeaderHtml("Security Alert")
                + "<div class=\"greeting\">New Account Login Alert 🔐</div>"
                + "<p>Hello <strong>" + safeName + "</strong>,</p>"
                + "<p>We detected a successful login to your <strong>Project Management System</strong> account as <strong>" + safeRole + "</strong>.</p>"
                + "<div class=\"card-box\" style=\"border-left-color: #3b82f6;\">"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Account Name:</span> <span class=\"card-value\" style=\"font-weight: 700;\">" + safeName + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Access Role:</span> <span class=\"badge badge-primary\">" + safeRole + "</span></div>"
                + "  <div style=\"margin-bottom: 10px;\"><span class=\"card-label\">Login Time:</span> <span class=\"card-value\" style=\"font-weight: 700; color: #2563eb;\">" + safeTime + "</span></div>"
                + "  <div><span class=\"card-label\">Status:</span> <span class=\"badge badge-success\">AUTHORIZED ACCESS</span></div>"
                + "</div>"
                + "<div style=\"background-color: #fff1f2; border: 1px solid #fecdd3; border-radius: 8px; padding: 14px; margin-top: 20px; font-size: 13px; color: #be123c;\">"
                + "  <strong>Security Notice:</strong> If you did not log into your account at this time, please change your password immediately or contact your System Administrator."
                + "</div>"
                + getFooterHtml();
    }
}
