package com.ocp.ocp_finalproject.notice.repository;

import com.ocp.ocp_finalproject.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**\
     * 공지사항 + 공지파일(NoticeFile)을 fetch join 으로 한 번에 가져오는 메서드
     * N+1 문제 방지용
     */
    @Query("SELECT DISTINCT n FROM Notice n LEFT JOIN FETCH n.noticeFiles")
    List<Notice> findAllWithFiles();

    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.noticeFiles WHERE n.id = :noticeId")
    Optional<Notice> findByIdWithFiles(Long noticeId);
}
