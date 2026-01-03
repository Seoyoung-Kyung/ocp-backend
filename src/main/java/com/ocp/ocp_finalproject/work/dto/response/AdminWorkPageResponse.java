package com.ocp.ocp_finalproject.work.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminWorkPageResponse {

    private final List<AdminWorkListResponse> works;

    private final int page;

    private final int size;

    private final long totalElements;

    private final int totalPages;
    
    private final boolean last;
}