package com.ocp.ocp_finalproject.audit.domain;

import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 20)
    private ActorType actorType;

    @Column(name = "actor_id")
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private AuditAction action;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "target_id")
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private AuditResult result;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Lob
    @Column(name = "extra_json")
    private String extraJson;

    @Builder(builderMethodName = "createBuilder")
    private static AuditLog create(ActorType actorType,
                                   Long actorId,
                                   AuditAction action,
                                   String targetType,
                                   String targetId,
                                   AuditResult result,
                                   String failReason,
                                   String ip,
                                   String userAgent,
                                   String requestId,
                                   String extraJson) {
        AuditLog log = new AuditLog();
        log.actorType = actorType;
        log.actorId = actorId;
        log.action = action;
        log.targetType = targetType;
        log.targetId = targetId;
        log.result = result;
        log.failReason = failReason;
        log.ip = ip;
        log.userAgent = userAgent;
        log.requestId = requestId;
        log.extraJson = extraJson;
        return log;
    }

    @PrePersist
    void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}
