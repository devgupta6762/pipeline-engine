package com.studio.scout.service;

import com.studio.scout.model.AppliedJob;
import com.studio.scout.repository.AppliedJobRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AutomatedScoutEngine {

    private final AppliedJobRepository repository;
    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=site:linkedin.com/posts+%22Java+Developer%22+%22hiring%22+(%22gmail.com%22+OR+%22hr@%22)&tbs=qdr:w";

    public AutomatedScoutEngine(AppliedJobRepository repository) {
        this.repository = repository;
    }

    public void runMorningScan() {
        try {
            // Unattended connection masking the JVM signature to look like a normal web browser [cite: 64, 66]
            Document searchPage = Jsoup.connect(GOOGLE_SEARCH_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            String pageContentText = searchPage.text();

            // Standard text extraction regular expression compilation
            Pattern emailRegexPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcher = emailRegexPattern.matcher(pageContentText);

            while (matcher.find()) {
                String extractedEmail = matcher.group().toLowerCase();

                // Gatekeeper database check to skip existing leads [cite: 60, 63]
                if (!repository.existsByHrEmail(extractedEmail)) {
                    AppliedJob targetEntry = new AppliedJob();
                    targetEntry.setHrEmail(extractedEmail);
                    targetEntry.setCompanyName("Sourced Vacancy Node");
                    repository.save(targetEntry);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing text harvesting thread: " + e.getMessage());
        }
    }
}