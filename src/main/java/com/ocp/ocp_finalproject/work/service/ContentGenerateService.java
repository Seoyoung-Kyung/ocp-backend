package com.ocp.ocp_finalproject.work.service;

import com.ocp.ocp_finalproject.common.exception.CustomException;
import com.ocp.ocp_finalproject.common.exception.ErrorCode;
import com.ocp.ocp_finalproject.content.domain.AiContent;
import com.ocp.ocp_finalproject.content.enums.ContentStatus;
import com.ocp.ocp_finalproject.content.repository.AiContentRepository;
import com.ocp.ocp_finalproject.crawling.domain.ProductCrawl;
import com.ocp.ocp_finalproject.crawling.repository.ProductCrawlRepository;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest.ProductInfo;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest.TrendCategory;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest.WebhookUrls;
import com.ocp.ocp_finalproject.work.config.AirflowLogProperties;
import com.ocp.ocp_finalproject.work.config.ContentGenerateProperties;
import com.ocp.ocp_finalproject.work.config.KeywordSelectProperties;
import com.ocp.ocp_finalproject.work.config.ProductSelectProperties;
import com.ocp.ocp_finalproject.work.domain.Work;
import com.ocp.ocp_finalproject.work.enums.WorkExecutionStatus;
import com.ocp.ocp_finalproject.work.repository.WorkRepository;
import com.ocp.ocp_finalproject.workflow.domain.Workflow;
import com.ocp.ocp_finalproject.workflow.enums.WorkflowStatus;
import com.ocp.ocp_finalproject.workflow.repository.WorkflowRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentGenerateService {

    private static final int RECENT_KEYWORD_LIMIT = 10;
    private static final Set<String> SUPPORTED_CRAWL_DOMAINS = Set.of("gmarket", "musinsa", "ssadagu");

    private final WorkflowRepository workflowRepository;
    private final WorkRepository workRepository;
    private final AiContentRepository aiContentRepository;
    private final ProductCrawlRepository productCrawlRepository;
    private final KeywordSelectProperties keywordSelectProperties;
    private final ProductSelectProperties productSelectProperties;
    private final ContentGenerateProperties contentGenerateProperties;
    private final AirflowLogProperties airflowLogProperties;

    @Transactional
    public ContentGenerateRequest createRequest(Long workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKFLOW_NOT_FOUND));

        boolean isTestWorkflow = workflow.getStatus() == WorkflowStatus.PRE_REGISTERED;

        LocalDateTime now = LocalDateTime.now();
        Work work = workRepository.save(
                Work.createBuilder()
                        .workflow(workflow)
                        .status(WorkExecutionStatus.PENDING)
                        .startedAt(now)
                        .completedAt(null)
                        .build()
        );

        AiContent aiContent = AiContent.createBuilder()
                .title(null)
                .content(null)
                .choiceProduct(null)
                .choiceTrendKeyword(null)
                .status(ContentStatus.PENDING)
                .startedAt(now)
                .completedAt(null)
                .work(work)
                .build();
        aiContentRepository.save(aiContent);

        ContentGenerateRequest request = new ContentGenerateRequest();
        request.setWorkId(work.getId());
        request.setSiteUrl(workflow.getSiteUrl());
        request.setTrendCategory(convertTrendCategory(workflow));
        request.setIsTest(isTestWorkflow);

        request.setRecentTrendKeywords(fetchRecentTrendKeywords(workflow.getId()));

        List<ProductInfo> crawledProducts = fetchCrawledProducts(workflow);
        boolean hasCrawled = !crawledProducts.isEmpty();
        request.setHasCrawledItems(hasCrawled);
        request.setCrawledProducts(hasCrawled ? crawledProducts : null);

        request.setRecentlyUsedProducts(hasCrawled ? null : fetchRecentlyUsedProducts(workflow.getId()));

        return request;
    }

    public ContentGenerateRequest applyWebhookSettings(ContentGenerateRequest request) {
        request.setWebhookSecret(contentGenerateProperties.getWebhookSecret());
        WebhookUrls webhookUrls = new WebhookUrls();
        webhookUrls.setKeywordSelect(keywordSelectProperties.getWebhookUrl());
        webhookUrls.setProductSelect(productSelectProperties.getWebhookUrl());
        webhookUrls.setContentGenerate(contentGenerateProperties.getWebhookUrl());
        webhookUrls.setAirflowLog(airflowLogProperties.getWebhookUrl());
        request.setWebhookUrls(webhookUrls);
        return request;
    }

    @Transactional
    public void markWorkRequested(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_NOT_FOUND, "워크를 찾을 수 없습니다. workId=" + workId));
        work.markRequested();
    }

    private List<String> fetchRecentTrendKeywords(Long workflowId) {
        List<String> keywords = aiContentRepository.findRecentTrendKeywordsByWorkflowId(
                workflowId,
                PageRequest.of(0, RECENT_KEYWORD_LIMIT)
        );
        return keywords.isEmpty() ? Collections.emptyList() : keywords;
    }

    private List<String> fetchRecentlyUsedProducts(Long workflowId) {
        List<String> products = aiContentRepository.findRecentChoiceProductsByWorkflowId(workflowId);
        return products.isEmpty() ? Collections.emptyList() : products;
    }

    private List<ProductInfo> fetchCrawledProducts(Workflow workflow) {
        // 1) site_url에서 지원 사이트 추출
        Optional<String> siteName = resolveSupportedSite(workflow.getSiteUrl());
        if (siteName.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) 최근 ai_content 의 choiceProduct 목록 조회 (이미 사용된 상품)
        List<String> usedProducts = aiContentRepository.findRecentChoiceProductsByWorkflowId(workflow.getId());
        Set<String> usedProductSet = usedProducts.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3) 해당 사이트의 모든 크롤링 데이터 조회 후
        //    이미 사용된 상품(choiceProduct) 목록은 제외
        return productCrawlRepository.findBySiteNameIgnoreCase(siteName.get()).stream()
                .filter(product -> !usedProductSet.contains(product.getProductName()))
                .map(this::toProductInfo)
                .collect(Collectors.toList());
    }

    private Optional<String> resolveSupportedSite(String siteUrl) {
        if (siteUrl == null || siteUrl.isBlank()) {
            return Optional.empty();
        }
        try {
            String host = new URI(siteUrl).getHost();
            if (host == null) {
                return Optional.empty();
            }
            String lowerHost = host.toLowerCase(Locale.ROOT);
            return SUPPORTED_CRAWL_DOMAINS.stream()
                    .filter(lowerHost::contains)
                    .findFirst();
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    private ProductInfo toProductInfo(ProductCrawl productCrawl) {
        ProductInfo info = new ProductInfo();
        info.setProductId(productCrawl.getId());
        info.setProductName(productCrawl.getProductName());
        info.setProductPrice(productCrawl.getProductPrice() != null ? productCrawl.getProductPrice() : null);
        info.setProductDetailUrl(productCrawl.getProductDetailUrl());
        info.setProductCode(productCrawl.getProductCode());
        info.setProductImageUrl(productCrawl.getProductImageUrl());
        return info;
    }

    private TrendCategory convertTrendCategory(Workflow workflow) {
        TrendCategory category = new TrendCategory();
        if (workflow.getTrendCategory() != null) {
            List<com.ocp.ocp_finalproject.trend.domain.TrendCategory> path = workflow.getTrendCategory().getFullPath();
            if (path.size() > 0) {
                category.setCategory1(path.get(0).getTrendCategoryName());
            }
            if (path.size() > 1) {
                category.setCategory2(path.get(1).getTrendCategoryName());
            }
            if (path.size() > 2) {
                category.setCategory3(path.get(2).getTrendCategoryName());
            }
        }
        return category;
    }
}
