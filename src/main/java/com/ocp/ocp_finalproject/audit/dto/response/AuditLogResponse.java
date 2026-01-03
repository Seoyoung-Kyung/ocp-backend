package com.ocp.ocp_finalproject.audit.dto.response;

import com.ocp.ocp_finalproject.audit.domain.AuditLog;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditAction;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditLogResponse {

    private Long id;
    private LocalDateTime occurredAt;

    private ActorType actorType;
    private Long actorId;

    private AuditAction action;
    private AuditResult result;

    private String targetType;
    private String targetId;
    private String failReason;

    public static AuditLogResponse from(AuditLog log) {
        if (log == null) {
            return null;
        }
        return AuditLogResponse.builder()
                .id(log.getId())
                .occurredAt(log.getOccurredAt())
                .actorType(log.getActorType())
                .actorId(log.getActorId())
                .action(log.getAction())
                .result(log.getResult())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .failReason(log.getFailReason())
                .build();
    }
}
