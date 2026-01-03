package com.ocp.ocp_finalproject.message.blog;

import com.ocp.ocp_finalproject.common.config.rabbit.RabbitConfig;
import com.ocp.ocp_finalproject.message.blog.dto.BlogUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogUploadProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(BlogUploadRequest request) {
        rabbitTemplate.convertAndSend(RabbitConfig.BLOG_UPLOAD_QUEUE, request);
    }
}
