package com.ocp.ocp_finalproject.crawling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ocp.ocp_finalproject.crawling.domain.ProductCrawl;
import lombok.Getter;

@Getter
public class ProductCrawlDto {

    @JsonProperty("site_name")
    private String siteName;

    @JsonProperty("site_url")
    private String siteUrl;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("product_detail_url")
    private String productDetailUrl;

    @JsonProperty("product_price")
    private String productPrice;

    @JsonProperty("image_url")
    private String productImageUrl;

    /**
     *  DTO -> Entity 변환
     */
    public ProductCrawl toEntity() {
        return ProductCrawl.createBuilder()
                .siteName(siteName)
                .siteUrl(siteUrl)
                .productName(productName)
                .productCode(productCode)
                .productDetailUrl(productDetailUrl)
                .productPrice(parsePrice(productPrice))
                .productImageUrl(productImageUrl)
                .build();
    }

    private Integer parsePrice(String price) {
        if (price == null) return null;
        String digits = price.replaceAll("[^0-9]",""); // 숫자만 추출
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }
}
