package com.studio.scout.repository;

import com.studio.scout.model.AppliedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppliedJobRepository extends JpaRepository<AppliedJob, Long> {

    // Idempotency verification signature [cite: 60, 62]
    boolean existsByHrEmail(String hrEmail);

    // Fetches items ready for delivery batches
    List<AppliedJob> findByPipelineState(String pipelineState);

    // Chronological log extractor for your frontend UI dashboard
    List<AppliedJob> findAllByOrderBySourcedDateDesc();
}