package com.ocp.ocp_finalproject.crawling.service;

import com.ocp.ocp_finalproject.crawling.domain.ProductCrawl;
import com.ocp.ocp_finalproject.crawling.dto.ProductCrawlDto;
import com.ocp.ocp_finalproject.crawling.repository.ProductCrawlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCrawlService {
    private final ProductCrawlRepository productCrawlRepository;

    /**
     * 여러 상품 저장
     */
    @Transactional
    public void saveAll(List<ProductCrawlDto> dtos) {
        List<ProductCrawl> entities = dtos.stream()
                .map(ProductCrawlDto::toEntity)
                .toList();
        productCrawlRepository.saveAll(entities);
    }
}
