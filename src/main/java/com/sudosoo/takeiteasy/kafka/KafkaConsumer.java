package com.sudosoo.takeiteasy.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    @Value("${devsoo.kafka.notice.topic}")
    private String kafkaTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

}
