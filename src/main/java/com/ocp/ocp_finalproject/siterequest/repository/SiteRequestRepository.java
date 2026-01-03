package com.ocp.ocp_finalproject.siterequest.repository;

import com.ocp.ocp_finalproject.siterequest.domain.SiteRequest;
import com.ocp.ocp_finalproject.siterequest.enums.SiteRequestState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRequestRepository extends JpaRepository<SiteRequest, Long> {
    Page<SiteRequest> findByUserId(Long userId, Pageable pageable);

    Page<SiteRequest> findByState(SiteRequestState state, Pageable pageable);

    boolean existsBySiteUrl(String siteUrl);
}
