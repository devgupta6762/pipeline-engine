package com.studio.scout.service;

import com.studio.scout.model.AppliedJob;
import com.studio.scout.repository.AppliedJobRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ThrottledMailerService {

    private final JavaMailSender mailSender;
    private final AppliedJobRepository repository;

    public ThrottledMailerService(JavaMailSender mailSender, AppliedJobRepository repository) {
        this.mailSender = mailSender;
        this.repository = repository;
    }

    @Async // Runs in background so your trigger endpoints return responses instantly
    public void processQueueAndSend() {
        List<AppliedJob> pendingLeads = repository.findByPipelineState("PENDING_DISPATCH");
        int batchCycleCounter = 0;

        for (AppliedJob jobRecord : pendingLeads) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

                messageHelper.setTo(jobRecord.getHrEmail());
                messageHelper.setSubject("Application for Java Developer Position - 3+ Years Experience - Mayank Gaur");

                // 🔑 Direct back-channel customization rules
                messageHelper.setFrom("mayank.scout.apps@gmail.com", "Mayank Gaur");
                messageHelper.setReplyTo("devgupta67621@gmail.com");

                String htmlCoverLetter = "Dear Hiring Team / Talent Acquisition Specialist,<br><br>"
                        + "I hope this message finds you well.<br><br>"
                        + "I am writing to formally express my interest in the Java Developer position open at your organization. With over 3 years of experience specializing in building production-grade distributed backends with Spring Boot, Java 17, and PostgreSQL, I am confident I can add immediate value to your engineering team.<br><br>"
                        + "I have attached my comprehensive resume PDF to this message. You can respond directly to this email chain, or connect with me via my primary inbox at <strong>devgupta67621@gmail.com</strong>.<br><br>"
                        + "Thank you for your consideration.<br><br>"
                        + "Sincerely,<br>"
                        + "<strong>Mayank Gaur</strong><br>"
                        + "Software Developer | Backend Systems Engineer<br>"
                        + "📧 Primary: devgupta67621@gmail.com";

                messageHelper.setText(htmlCoverLetter, true);

                // Attaches the file directly out of your resources folder
                messageHelper.addAttachment("Resume_Mayank_Gaur_Backend_Java.pdf", new ClassPathResource("resume.pdf"));

                mailSender.send(message);

                // Advance state tracker to completed
                jobRecord.setPipelineState("BATCH_DELIVERED");
                repository.save(jobRecord);

                batchCycleCounter++;

                // Defensive Cooldown Gate: Sleep for 5 minutes after every 10 emails [cite: 68, 70, 71]
                if (batchCycleCounter % 10 == 0) {
                    Thread.sleep(300000); // 300,000 milliseconds = 5 Minutes
                }

            } catch (Exception e) {
                System.err.println("Mailing payload failure for address " + jobRecord.getHrEmail() + ": " + e.getMessage());
            }
        }
    }
}