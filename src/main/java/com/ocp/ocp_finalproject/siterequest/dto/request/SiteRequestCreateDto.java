package com.ocp.ocp_finalproject.siterequest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteRequestCreateDto(
        @NotBlank(message = "사이트 URL은 필수입니다.")
        String siteUrl,

        @NotBlank(message = "사이트 이름은 필수입니다")
        @Size(max = 20, message = "사이트 이름은 20자를 초과할 수 없습니다.")
        String siteName,

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        String description
) { }
