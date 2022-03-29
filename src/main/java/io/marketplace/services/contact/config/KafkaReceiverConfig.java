package io.marketplace.services.contact.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.util.StringUtils;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaReceiverConfig {

    private final ExtraConfigLoader configLoader;
    private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaReceiverConfig(ExtraConfigLoader appProps, KafkaProperties kafkaProperties) {
        this.configLoader = appProps;
        this.kafkaProperties = kafkaProperties;
    }

    @Value("${kafka.consumer.groupid}")
    private String consumerGroupId;

    @Value("${kafka.consumer.maxPollRecords:1000}")
    private int maxPollRecords;

    @Value("${kafka.consumer.numberProcessor:1}")
    private int numberProcessor;

    private String KAFKA_TIMEOUT = "60000";
    private String KAFKA_COMMIT_INTERVAL = "5000";
    private String HEARTBEAT_INTERVAL_MS_CONFIG = "20000";

    @Bean
    @SuppressWarnings("Duplicates")
    public Map<String, Object> consumerConfigs() {
        
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, KAFKA_TIMEOUT);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, HEARTBEAT_INTERVAL_MS_CONFIG);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        String jaasConfig = configLoader.getJaasConfig();
        if (!StringUtils.isEmpty(jaasConfig)) {
            props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put("sasl.jaas.config", jaasConfig);
        }
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(numberProcessor);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
