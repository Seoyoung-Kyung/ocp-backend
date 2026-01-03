package com.ocp.ocp_finalproject.siterequest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteRequestState {
    RECEIVED("접수완료"),
    APPROVED("승인"),
    REJECTED("거부");

    private final String description;
}
