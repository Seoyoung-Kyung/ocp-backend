package com.ocp.ocp_finalproject.work.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class WebhookTimeParser {

    private WebhookTimeParser() {
    }

    public static LocalDateTime toUtcOrNow(OffsetDateTime source) {
        return source != null
                ? source.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime()
                : LocalDateTime.now(ZoneOffset.UTC);
    }
}
