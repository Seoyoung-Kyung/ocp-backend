package com.ocp.ocp_finalproject.audit.aspect;

import com.ocp.ocp_finalproject.audit.annotation.Audit;
import com.ocp.ocp_finalproject.audit.enums.ActorType;
import com.ocp.ocp_finalproject.audit.enums.AuditResult;
import com.ocp.ocp_finalproject.audit.service.AuditLogService;
import com.ocp.ocp_finalproject.audit.util.ActorResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    private final ExpressionParser spelParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(audit)")
    public Object around(ProceedingJoinPoint pjp, Audit audit) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        HttpServletRequest request = currentRequest();
        String ip = request != null ? resolveClientIp(request) : null;
        String userAgent = request != null ? request.getHeader("User-Agent") : null;
        String requestId = MDC.get("requestId");

        ActorResolver.Resolution actor = resolveActor(audit.actorType());
        Long actorId = actor.actorId();
        String extraJson = toExtraJsonOrNull(actor.extra());

        try {
            Object result = pjp.proceed();

            // ✅ proceed()가 정상 종료되면 result가 null이더라도 항상 SUCCESS 저장한다.
            String targetId = resolveTargetId(method, pjp.getArgs(), audit.targetIdSpel(), result);
            auditLogService.save(
                    audit.actorType(),
                    actorId,
                    audit.action(),
                    audit.targetType(),
                    targetId,
                    AuditResult.SUCCESS,
                    null,
                    ip,
                    userAgent,
                    requestId,
                    extraJson
            );
            return result;
        } catch (Exception e) {
            String targetId = resolveTargetId(method, pjp.getArgs(), audit.targetIdSpel(), null);
            auditLogService.save(
                    audit.actorType(),
                    actorId,
                    audit.action(),
                    audit.targetType(),
                    targetId,
                    AuditResult.FAIL,
                    summarizeException(e),
                    ip,
                    userAgent,
                    requestId,
                    extraJson
            );
            throw e;
        } catch (Throwable t) {
            // Error 등도 비즈니스 흐름을 깨지 않게 audit 저장은 시도한다(저장 실패는 내부에서 swallow).
            String targetId = resolveTargetId(method, pjp.getArgs(), audit.targetIdSpel(), null);
            auditLogService.save(
                    audit.actorType(),
                    actorId,
                    audit.action(),
                    audit.targetType(),
                    targetId,
                    AuditResult.FAIL,
                    summarizeException(t),
                    ip,
                    userAgent,
                    requestId,
                    extraJson
            );
            throw t;
        }
    }

    /**
     * actorId 추출 우선순위(요구사항):
     * - Case A) 커스텀 principal: getUserId/getId 또는 userId/id 필드 (리플렉션)
     * - Case B) UserDetails: username이 숫자면 actorId
     * - Case C) OAuth2User: attributes(userId/id/memberId/sub)
     * - Case D) 문자열(anonymousUser): null
     *
     * <p>프로젝트에서 principal이 {@code com.ocp.ocp_finalproject.user.domain.UserPrincipal}이면
     * 내부 getUser().getId()가 Case A에서 잡힌다.</p>
     */
    private ActorResolver.Resolution resolveActor(ActorType actorType) {
        if (actorType == ActorType.SYSTEM) {
            return ActorResolver.Resolution.empty();
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return ActorResolver.resolve(actorType, auth);
        } catch (Exception e) {
            log.warn("actor_id 추출 실패. actorType={} msg={}", actorType, e.getMessage());
            return ActorResolver.Resolution.empty();
        }
    }

    private String summarizeException(Throwable ex) {
        String message = ex != null ? ex.getMessage() : null;
        if (!StringUtils.hasText(message) && ex != null) {
            message = ex.getClass().getSimpleName();
        }
        if (message != null && message.length() > 500) {
            return message.substring(0, 500);
        }
        return message;
    }

    private String evaluateTargetIdIfPresent(Method method, Object[] args, String targetIdSpel, Object result) {
        if (!StringUtils.hasText(targetIdSpel)) {
            return null;
        }
        try {
            StandardEvaluationContext ctx = new StandardEvaluationContext();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
            if (paramNames != null) {
                for (int i = 0; i < Math.min(paramNames.length, args.length); i++) {
                    ctx.setVariable(paramNames[i], args[i]);
                }
            }
            ctx.setVariable("result", result);

            Expression expression = spelParser.parseExpression(targetIdSpel);
            Object value = expression.getValue(ctx);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            log.debug("targetIdSpel 평가 실패 spel={} method={} msg={}", targetIdSpel, method.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * targetIdSpel은 케이스에 따라 args만 쓰기도 하고(#workflowId, #request.workId),
     * 응답(result)까지 타고 들어가기도 한다(#result.body.data.workflowId).
     */
    private String resolveTargetId(Method method, Object[] args, String targetIdSpel, Object result) {
        return evaluateTargetIdIfPresent(method, args, targetIdSpel, result);
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            int comma = xff.indexOf(',');
            return comma > 0 ? xff.substring(0, comma).trim() : xff.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * extraJson은 선택 사항이므로, 값이 없으면 null로 저장한다.
     *
     * <p>예시(로그인 사용자 principal이 UserDetails이고 username이 숫자가 아닐 때):</p>
     * <pre>
     * {"principalType":"org.springframework.security.core.userdetails.User","username":"alice@example.com"}
     * </pre>
     *
     * <p>예시(OAuth2User attributes에서 sub가 "12345"일 때):</p>
     * <pre>
     * {"principalType":"org.springframework.security.oauth2.core.user.DefaultOAuth2User","oauth2Key":"sub"}
     * </pre>
     */
    private String toExtraJsonOrNull(Map<String, Object> extra) {
        if (extra == null || extra.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(extra);
        } catch (Exception e) {
            log.debug("extraJson 생성 실패 msg={}", e.getMessage());
            return null;
        }
    }
}
