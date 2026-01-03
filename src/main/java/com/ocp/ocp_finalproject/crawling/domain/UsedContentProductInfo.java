package com.ocp.ocp_finalproject.crawling.domain;

import com.ocp.ocp_finalproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "used_content_product_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsedContentProductInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "used_content_product_info_id")
    private Long id;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "product_code", length = 100)
    private String productCode;

    @Column(name = "product_detail_url", length = 1000)
    private String productDetailUrl;

    @Column(name = "product_price")
    // DB에서 NULL이 들어올 수 있으면 primitive 타입 사용하면 NPE 위험해서 Integer 사용
    private Integer productPrice;

    @Builder(builderMethodName = "createBuilder")
    public static UsedContentProductInfo create(String productName, String productCode, String productDetailUrl, Integer productPrice) {
        UsedContentProductInfo usedContentProductInfo = new UsedContentProductInfo();
        usedContentProductInfo.productName = productName;
        usedContentProductInfo.productCode = productCode;
        usedContentProductInfo.productDetailUrl = productDetailUrl;
        usedContentProductInfo.productPrice = productPrice;
        return usedContentProductInfo;
    }
}
