package com.ocp.ocp_finalproject.crawling.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "product_crawl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCrawl extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_crawl_id")
    private Long id;

    @Column(name= "site_name", length = 100)
    private String siteName;

    @Column(name= "site_url", length = 3000)
    private String siteUrl;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "product_code", length = 100)
    private String productCode;

    @Column(name = "product_detail_url", length = 3000)
    private String productDetailUrl;

    @Column(name = "product_image_url", length = 1000)
    private String productImageUrl;

    @Column(name = "product_price", length = 10)
    // DB에서 NULL이 들어올 수 있으면 primitive 타입 사용하면 NPE 위험해서 Integer 사용
    private Integer productPrice;

    @Builder(builderMethodName = "createBuilder")
    public static ProductCrawl create(
            String siteName,
            String siteUrl,
            String productName,
            String productCode,
            String productDetailUrl,
            Integer productPrice,
            String productImageUrl
    ) {
        ProductCrawl crawl = new ProductCrawl();
        crawl.siteName = siteName;
        crawl.siteUrl = siteUrl;
        crawl.productName = productName;
        crawl.productCode = productCode;
        crawl.productDetailUrl = productDetailUrl;
        crawl.productPrice = productPrice;
        crawl.productImageUrl = productImageUrl;
        return crawl;
    }
}
