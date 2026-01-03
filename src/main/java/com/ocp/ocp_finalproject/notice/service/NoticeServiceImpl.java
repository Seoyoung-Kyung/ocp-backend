package com.ocp.ocp_finalproject.notice.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.notice.domain.Notice;
import com.ocp.ocp_finalproject.notice.domain.NoticeFile;
import com.ocp.ocp_finalproject.notice.dto.request.NoticeCreateRequest;
import com.ocp.ocp_finalproject.notice.dto.request.NoticeUpdateRequest;
import com.ocp.ocp_finalproject.notice.dto.response.NoticeResponse;
import com.ocp.ocp_finalproject.notice.repository.NoticeFileRepository;
import com.ocp.ocp_finalproject.notice.repository.NoticeRepository;
import com.ocp.ocp_finalproject.user.domain.User;
import com.ocp.ocp_finalproject.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final UserRepository userRepository;

    @Override
    public List<NoticeResponse> getAllNotice() {
        // 1. Notice 전체 조회
        List<Notice> notices = noticeRepository.findAllWithFiles();

        // 2. authorId 추출 (중복 제거, null 필터링)
        Set<Long> authorIds = notices.stream()
                .map(Notice::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. User 일괄 조회 (N+1 방지)
        Map<Long, String> authorNameMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        // 4. Notice -> NoticeResponse 변환
        return notices.stream()
                .map(notice -> {
                    String authorName = authorNameMap.getOrDefault(
                            notice.getAuthorId(),
                            "알 수 없음"
                    );
                    return NoticeResponse.of(notice, authorName);
                })
                .toList();
    }

    @Override
    public NoticeResponse getNotice(Long noticeId) {
        // 1. Notice 조회
        Notice notice = noticeRepository.findByIdWithFiles(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 2. User 조회 (authorId null 체크)
        String authorName = notice.getAuthorId() != null
                ? userRepository.findById(notice.getAuthorId())
                        .map(User::getName)
                        .orElse("알 수 없음")
                : "알 수 없음";

        // 3. Response 생성
        return NoticeResponse.of(notice, authorName);
    }

    /**
     * 공지사항 등록
     */
    @Override
    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request, Long authorId) {

        // 1. 공지사항 엔티티 생성 (authorId 전달)
        Notice notice = request.toEntity(authorId);

        // 2. 공지사항 저장
        Notice savedNotice = noticeRepository.save(notice);

        // 3. 첨부파일이 있는 경우 NoticeFile 엔티티 생성 + 저장
        if (request.getFileName() != null) {
            NoticeFile file = NoticeFile.create(
                    savedNotice,
                    request.getFileName(),
                    request.getFileName(), // originalName 동일 처리
                    request.getFileUrl(),
                    request.getFileSize(),
                    request.getFileType()
            );

            noticeFileRepository.save(file);
            savedNotice.addNoticeFile(file);

        }

        // 4. User 조회 (authorId null 체크)
        String authorName = savedNotice.getAuthorId() != null
                ? userRepository.findById(savedNotice.getAuthorId())
                        .map(User::getName)
                        .orElse("알 수 없음")
                : "알 수 없음";

        // 5. 저장된 Notices -> DTO 변환 후 반환
        return NoticeResponse.of(savedNotice, authorName);
    }
    /**
     * 공지사항 삭제
     */
    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {

        // 1. 공지 + 파일까지 fetch join으로 조회
        Notice notice = noticeRepository.findByIdWithFiles(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 2. 삭제 (Cascade + orphanRemoval 로 파일도 함께 삭제)
        noticeRepository.delete(notice);
    }

    /**
     * 공지사항 수정
     */
    @Override
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, NoticeUpdateRequest request) {

        Notice notice = noticeRepository.findByIdWithFiles(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        // 1. 텍스트 필드 수정 (null인 값은 무시)
        notice.update(
                request.getTitle(),
                request.getContent(),
                request.getAnnouncementType(),
                request.getIsImportant()
        );

        // 2. 파일 수정 (client가 파일 관련 정보 하나라도 전달하면 전체 교체)
        boolean hasFileUpdate =
                request.getFileName() != null &&
                        request.getFileUrl() != null;

        if (hasFileUpdate) {
            // 기존 파일 전체 삭제
            notice.getNoticeFiles().clear();

            NoticeFile.create(
                    notice,
                    request.getFileName(),
                    request.getFileName(),  // originalName 동일 처리
                    request.getFileUrl(),
                    request.getFileSize(),
                    request.getFileType()
            );
        }

        // 3. User 조회 (authorId null 체크)
        String authorName = notice.getAuthorId() != null
                ? userRepository.findById(notice.getAuthorId())
                        .map(User::getName)
                        .orElse("알 수 없음")
                : "알 수 없음";

        // 4. Response 생성
        return NoticeResponse.of(notice, authorName);
    }
}
