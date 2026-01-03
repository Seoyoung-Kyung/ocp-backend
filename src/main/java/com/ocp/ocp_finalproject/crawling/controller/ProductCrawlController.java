package com.ocp.ocp_finalproject.crawling.controller;

import com.ocp.ocp_finalproject.common.response.ApiResult;
import com.ocp.ocp_finalproject.crawling.dto.ProductCrawlDto;
import com.ocp.ocp_finalproject.crawling.service.ProductCrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crawling")
public class ProductCrawlController {

    private final ProductCrawlService productCrawlService;

    // 여러 상품 한 번에 저장
    @PostMapping("/products")
    public ApiResult<String> saveProducts(@RequestBody List<ProductCrawlDto> products) {
        productCrawlService.saveAll(products);
        return ApiResult.success("크롤링 데이터 저장 완료");
    }
}
