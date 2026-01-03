package com.ocp.ocp_finalproject.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    //성공 응답 (데이터 O)
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(true, message, data);
    }

    //성공 응답 (데이터 x)
    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(true, message, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(false, message, null);
    }

}
