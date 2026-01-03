package com.ocp.ocp_finalproject.crawling.repository;

import com.ocp.ocp_finalproject.crawling.domain.ProductCrawl;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCrawlRepository extends JpaRepository<ProductCrawl, Long> {

    List<ProductCrawl> findBySiteNameIgnoreCase(String siteName);
}
