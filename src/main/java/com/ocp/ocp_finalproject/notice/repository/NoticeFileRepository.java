package com.ocp.ocp_finalproject.notice.repository;

import com.ocp.ocp_finalproject.notice.domain.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
}
