package com.studio.scout.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "applied_jobs")
public class AppliedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hr_email", nullable = false, unique = true)
    private String hrEmail;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "pipeline_state", nullable = false)
    private String pipelineState = "PENDING_DISPATCH";

    @Column(name = "sourced_date")
    private ZonedDateTime sourcedDate = ZonedDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public String getHrEmail() { return hrEmail; }
    public void setHrEmail(String hrEmail) { this.hrEmail = hrEmail; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getPipelineState() { return pipelineState; }
    public void setPipelineState(String pipelineState) { this.pipelineState = pipelineState; }
    public ZonedDateTime getSourcedDate() { return sourcedDate; }
    public void setSourcedDate(ZonedDateTime sourcedDate) { this.sourcedDate = sourcedDate; }
}