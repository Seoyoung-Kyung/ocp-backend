package com.ocp.ocp_finalproject.content.repository;

import com.ocp.ocp_finalproject.content.domain.ContentJob;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentJobRepository extends JpaRepository<ContentJob, Long> {
    Optional<ContentJob> findByJobId(String jobId);
}
