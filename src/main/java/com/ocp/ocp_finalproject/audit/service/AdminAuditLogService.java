package com.ocp.ocp_finalproject.audit.service;

import com.ocp.ocp_finalproject.audit.domain.AuditLog;
import com.ocp.ocp_finalproject.audit.dto.response.AuditLogResponse;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import com.ocp.ocp_finalproject.audit.repository.AuditLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuditLogService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(
            ActorType actorType,
            AuditAction action,
            AuditResult result,
            String targetType,
            Long actorId,
            LocalDateTime from,
            LocalDateTime to,
            Integer page,
            Integer size
    ) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "occurredAt"));

        // 시작점을 conjunction()으로 두어 .and 체인에서 NullPointerException이 나지 않도록 한다.
        Specification<AuditLog> spec = Specification.where((root, query, cb) -> cb.conjunction());
        spec = spec.and(actorTypeEquals(actorType));
        spec = spec.and(actionEquals(action));
        spec = spec.and(resultEquals(result));
        spec = spec.and(targetTypeEquals(targetType));
        spec = spec.and(actorIdEquals(actorId));
        spec = spec.and(occurredAtFrom(from));
        spec = spec.and(occurredAtTo(to));

        return auditLogRepository.findAll(spec, pageable).map(AuditLogResponse::from);
    }

    private Specification<AuditLog> actorTypeEquals(ActorType actorType) {
        return (root, query, cb) -> {
            if (actorType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("actorType"), actorType);
        };
    }

    private Specification<AuditLog> actionEquals(AuditAction action) {
        return (root, query, cb) -> {
            if (action == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("action"), action);
        };
    }

    private Specification<AuditLog> resultEquals(AuditResult result) {
        return (root, query, cb) -> {
            if (result == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("result"), result);
        };
    }

    private Specification<AuditLog> targetTypeEquals(String targetType) {
        return (root, query, cb) -> {
            if (targetType == null || targetType.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("targetType"), targetType);
        };
    }

    private Specification<AuditLog> actorIdEquals(Long actorId) {
        return (root, query, cb) -> {
            if (actorId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("actorId"), actorId);
        };
    }

    private Specification<AuditLog> occurredAtFrom(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("occurredAt"), from);
        };
    }

    private Specification<AuditLog> occurredAtTo(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("occurredAt"), to);
        };
    }
}
