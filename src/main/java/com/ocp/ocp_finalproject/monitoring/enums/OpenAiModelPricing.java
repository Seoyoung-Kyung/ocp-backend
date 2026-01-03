package com.ocp.ocp_finalproject.monitoring.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;

/**
 * 지원 모델 단가(USD, 1M tokens 기준).
 */
public enum OpenAiModelPricing {
    GPT_4O_MINI("gpt-4o-mini", new BigDecimal("0.15"), new BigDecimal("0.60")),
    GPT_4O("gpt-4o", new BigDecimal("5.00"), new BigDecimal("15.00"));

    private static final BigDecimal MILLION = new BigDecimal("1000000");
    private static final int SCALE = 6;

    private final String modelNamePrefix;
    private final BigDecimal inputPricePerMillion;
    private final BigDecimal outputPricePerMillion;

    OpenAiModelPricing(String modelNamePrefix, BigDecimal inputPricePerMillion, BigDecimal outputPricePerMillion) {
        this.modelNamePrefix = modelNamePrefix;
        this.inputPricePerMillion = inputPricePerMillion;
        this.outputPricePerMillion = outputPricePerMillion;
    }

    public static Optional<OpenAiModelPricing> fromModel(String model) {
        if (model == null || model.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(it -> model.startsWith(it.modelNamePrefix))
                .findFirst();
    }

    public BigDecimal calculateCost(Integer promptTokens, Integer completionTokens) {
        BigDecimal inputCost = inputPricePerMillion
                .multiply(BigDecimal.valueOf(promptTokens))
                .divide(MILLION, SCALE, RoundingMode.HALF_UP);

        BigDecimal outputCost = outputPricePerMillion
                .multiply(BigDecimal.valueOf(completionTokens))
                .divide(MILLION, SCALE, RoundingMode.HALF_UP);

        return inputCost.add(outputCost);
    }
}
