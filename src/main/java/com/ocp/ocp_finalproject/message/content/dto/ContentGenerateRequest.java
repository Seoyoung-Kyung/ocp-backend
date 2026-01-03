package com.ocp.ocp_finalproject.message.content.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContentGenerateRequest {

    // ===== 공통 메타 정보 =====
    private Long workId;              // Airflow DAG run 식별용
    private Boolean hasCrawledItems;      // 크롤링된 쇼핑몰 상품 사용 여부 (true=크롤링된 상품 있음, false=url 기반)

    // ===== 1. 최근 사용된 트렌드 키워드 10개 =====
    private List<String> recentTrendKeywords;

    // ===== 2. 상품 목록 (크롤링된 상품만 해당) =====
    // gmarket / musinsa / ssadagu 등 쇼핑몰 식별 가능하도록
    private List<ProductInfo> crawledProducts;   // 요청 쇼핑몰이 지원대상이면 채움, 아니면 null

    // ===== 3. 최근 사용된 상품 정보 목록 (URL 기반 생성 방지용) =====
    // 크롤링된 쇼핑몰을 사용 중이면 null, 아니라면 최근 사용된 product 리스트
    private List<String> recentlyUsedProducts;

    // ===== 4. Webhook Secret =====
    private String webhookSecret;

    // ===== 5. Webhook URLs (각 단계별로 따로 존재) =====
    private WebhookUrls webhookUrls;

    // ===== 6. 쇼핑몰 URLs =====
    private String siteUrl;

    // ===== 7. 트렌드 카테고리 =====
    private TrendCategory trendCategory;

    // ===== 8. 테스트 여부 =====
    private Boolean isTest;

    // ===== 내부 DTO: 상품 정보 ===== //이건 크롤링 파트랑 상의 후 변경 예정
    @Data
    public static class ProductInfo {
        private Long productId;     // 내부 DB id (있는 경우)
        // 상품 고유 정보
        private String productName;         // 상품명
        private String productCode;         // 상품 코드 (쇼핑몰별 id 등)

        // 상세 정보
        private String productDetailUrl;    // 상품 상세 페이지 URL
        private Integer productPrice;       // 가격 (null 허용 → Integer)
        private String productImageUrl;     // 상품 이미지 URL
    }

    // ===== 내부 DTO: Webhook URL 모음 =====
    @Data
    public static class WebhookUrls {
        private String keywordSelect;
        private String productSelect;
        private String contentGenerate;
        private String airflowLog;
    }

    @Data
    public static class TrendCategory {
        private String category1;
        private String category2;
        private String category3;
    }
}
