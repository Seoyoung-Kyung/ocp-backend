package com.ocp.ocp_finalproject.monitoring.service;

import com.ocp.ocp_finalproject.integration.openai.dto.OpenAiUsage;
import com.ocp.ocp_finalproject.monitoring.domain.AiUsageLog;
import com.ocp.ocp_finalproject.monitoring.enums.AiFeatureType;
import com.ocp.ocp_finalproject.monitoring.repository.AiUsageLogRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiUsageLogService {

    private final AiUsageLogRepository aiUsageLogRepository;
    private final OpenAiCostCalculator openAiCostCalculator;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AiUsageLog saveUsage(AiFeatureType featureType, String model, OpenAiUsage usage, Long workId, Long userId) {
        Assert.notNull(workId, "workId must not be null");
        Assert.notNull(userId, "userId must not be null");

        Integer promptTokens = usage != null && usage.promptTokens() != null ? usage.promptTokens() : 0;
        Integer completionTokens = usage != null && usage.completionTokens() != null ? usage.completionTokens() : 0;
        Integer totalTokens = usage != null && usage.totalTokens() != null ? usage.totalTokens() : 0;

        if (usage == null || usage.promptTokens() == null || usage.completionTokens() == null || usage.totalTokens() == null) {
            log.warn("OpenAI usage 필드가 비어 있어 0으로 대체합니다. featureType={}, workId={}, userId={}, usage={}",
                    featureType, workId, userId, usage);
        }

        BigDecimal estimatedCost = openAiCostCalculator.calculate(model, promptTokens, completionTokens);

        AiUsageLog aiUsageLog = AiUsageLog.createBuilder()
                .featureType(featureType != null ? featureType.name() : null)
                .model(model)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .estimatedCost(estimatedCost)
                .workId(workId)
                .userId(userId)
                .build();

        return aiUsageLogRepository.save(aiUsageLog);
    }
}
