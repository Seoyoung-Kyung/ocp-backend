package com.ocp.ocp_finalproject.content.producer;

import com.ocp.ocp_finalproject.common.config.rabbit.RabbitConfig;
import com.ocp.ocp_finalproject.content.dto.ContentRequestDto;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentTaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTask(String jobId, ContentRequestDto requestDto) {
        Map<String, Object> message = new HashMap<>();
        message.put("jobId", jobId);
        message.put("taskType", requestDto.getTaskType());
        message.put("title", requestDto.getTitle());
        message.put("content", requestDto.getContent());

        log.info("Sending content task to queue {}: jobId={}, taskType={}", RabbitConfig.CONTENT_GENERATE_QUEUE, jobId, requestDto.getTaskType());
        rabbitTemplate.convertAndSend(RabbitConfig.CONTENT_GENERATE_QUEUE, message);
    }
}
