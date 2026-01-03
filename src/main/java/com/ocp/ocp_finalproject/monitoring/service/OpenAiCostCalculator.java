package com.ocp.ocp_finalproject.monitoring.service;

import com.ocp.ocp_finalproject.monitoring.enums.OpenAiModelPricing;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class OpenAiCostCalculator {

    public BigDecimal calculate(String model, Integer promptTokens, Integer completionTokens) {
        Assert.notNull(promptTokens, "promptTokens must not be null");
        Assert.notNull(completionTokens, "completionTokens must not be null");

        Optional<OpenAiModelPricing> pricing = OpenAiModelPricing.fromModel(model);
        if (pricing.isEmpty()) {
            log.warn("OpenAI 단가가 등록되지 않은 모델입니다. model={}", model);
            return null;
        }
        return pricing.get().calculateCost(promptTokens, completionTokens);
    }
}
