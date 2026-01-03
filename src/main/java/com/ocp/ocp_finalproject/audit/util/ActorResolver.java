package com.ocp.ocp_finalproject.audit.util;

import com.ocp.ocp_finalproject.audit.enums.ActorType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

/**
 * SecurityContext의 principal에서 actor 정보를 최대한 추출한다.
 *
 * <p>프로젝트별로 principal 구현이 다를 수 있어서 아래 케이스를 순서대로 시도한다.</p>
 * <ul>
 *   <li>Case A: 커스텀 principal - getUserId/getId 또는 userId/id 필드(리플렉션)</li>
 *   <li>Case B: UserDetails - username이 숫자면 actorId로 사용</li>
 *   <li>Case C: OAuth2User - attributes에서 userId/id/memberId/sub</li>
 *   <li>Case D: 문자열/anonymousUser - null 처리</li>
 * </ul>
 *
 * <p>어떤 케이스든 예외가 발생하면 null로 처리한다(비즈니스 로직을 절대 깨지 않게).</p>
 */
public final class ActorResolver {

    private ActorResolver() {
    }

    public static Resolution resolve(ActorType actorType, Authentication authentication) {
        if (actorType == ActorType.SYSTEM) {
            return Resolution.empty();
        }
        if (authentication == null) {
            return Resolution.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return Resolution.empty();
        }
        if (principal instanceof String s) {
            // 예: "anonymousUser"
            return Resolution.empty()
                    .withExtra("principalType", "String")
                    .withExtra("principal", s);
        }

        Resolution byReflection = tryResolveByReflection(principal);
        if (byReflection.actorId != null) {
            return byReflection.withExtra("principalType", principal.getClass().getName());
        }

        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Long numeric = parseLongOrNull(username);
            if (numeric != null) {
                return Resolution.of(numeric).withExtra("principalType", principal.getClass().getName());
            }
            return Resolution.empty()
                    .withExtra("principalType", principal.getClass().getName())
                    .withExtra("username", username);
        }

        if (principal instanceof OAuth2User oAuth2User) {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Object raw = firstNonNull(attributes, List.of("userId", "id", "memberId", "sub"));
            Long numeric = coerceToLongOrNull(raw);
            if (numeric != null) {
                return Resolution.of(numeric)
                        .withExtra("principalType", principal.getClass().getName())
                        .withExtra("oauth2Key", resolvedKey(attributes, List.of("userId", "id", "memberId", "sub")));
            }
            return Resolution.empty()
                    .withExtra("principalType", principal.getClass().getName())
                    .withExtra("oauth2Key", resolvedKey(attributes, List.of("userId", "id", "memberId", "sub")))
                    .withExtra("oauth2IdRaw", raw != null ? String.valueOf(raw) : null);
        }

        // 마지막: 타입은 있는데 ID 추출에 실패
        return Resolution.empty().withExtra("principalType", principal.getClass().getName());
    }

    private static Resolution tryResolveByReflection(Object principal) {
        // 1) 메서드 탐색: getUserId(), getId()
        for (String methodName : List.of("getUserId", "getId")) {
            Object value = invokeNoArg(principal, methodName);
            Long numeric = coerceToLongOrNull(value);
            if (numeric != null) {
                return Resolution.of(numeric).withExtra("resolvedBy", "method:" + methodName);
            }
        }

        // 2) 필드 탐색: userId, id
        for (String fieldName : List.of("userId", "id")) {
            Object value = readField(principal, fieldName);
            Long numeric = coerceToLongOrNull(value);
            if (numeric != null) {
                return Resolution.of(numeric).withExtra("resolvedBy", "field:" + fieldName);
            }
        }

        // 3) "getUser()"로 엔티티를 들고 있는 경우: principal.getUser().getId()
        Object user = invokeNoArg(principal, "getUser");
        if (user != null) {
            Object value = invokeNoArg(user, "getId");
            Long numeric = coerceToLongOrNull(value);
            if (numeric != null) {
                return Resolution.of(numeric).withExtra("resolvedBy", "method:getUser.getId");
            }
            value = readField(user, "id");
            numeric = coerceToLongOrNull(value);
            if (numeric != null) {
                return Resolution.of(numeric).withExtra("resolvedBy", "field:getUser.id");
            }
        }

        return Resolution.empty();
    }

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Long parseLongOrNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Long coerceToLongOrNull(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return parseLongOrNull(String.valueOf(value));
    }

    private static Object firstNonNull(Map<String, Object> map, List<String> keys) {
        if (map == null) {
            return null;
        }
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return map.get(key);
            }
        }
        return null;
    }

    private static String resolvedKey(Map<String, Object> map, List<String> keys) {
        if (map == null) {
            return null;
        }
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return key;
            }
        }
        return null;
    }

    public static final class Resolution {
        private final Long actorId;
        private final Map<String, Object> extra;

        private Resolution(Long actorId, Map<String, Object> extra) {
            this.actorId = actorId;
            this.extra = extra;
        }

        public static Resolution of(Long actorId) {
            return new Resolution(actorId, new LinkedHashMap<>());
        }

        public static Resolution empty() {
            return new Resolution(null, new LinkedHashMap<>());
        }

        public Long actorId() {
            return actorId;
        }

        public Map<String, Object> extra() {
            return extra;
        }

        private Resolution withExtra(String key, Object value) {
            if (StringUtils.hasText(key) && value != null) {
                extra.put(key, value);
            }
            return this;
        }
    }
}

