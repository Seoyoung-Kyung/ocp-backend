package com.ocp.ocp_finalproject.message.content;

import com.ocp.ocp_finalproject.common.config.rabbit.RabbitConfig;
import com.ocp.ocp_finalproject.message.content.dto.ContentGenerateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentGenerateProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(ContentGenerateRequest request) {
        rabbitTemplate.convertAndSend(RabbitConfig.CONTENT_GENERATE_QUEUE, request);
    }
}