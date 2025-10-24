package br.com.interglobal.ged_camunda_service.notify;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
 
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = { "workflow-task-created" })
class WorkflowNotificationPublisherIT {

    @Autowired
    private WorkflowNotificationPublisher publisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private Consumer<String, String> consumer;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close(Duration.ZERO);
        }
    }

    @Test
    void publishesMessageToKafka() {
        consumer = createConsumer();

        WorkflowTaskCreatedEvent event = new WorkflowTaskCreatedEvent(
            "tenant-1",
            "task-123",
            "Aprovar documento",
            "taskDefinitionKey",
            Set.of("financeiro"),
            "BUS-1",
            "DOC-9"
        );

        publisher.publishTaskCreated(event);

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "workflow-task-created");

        assertThat(record.key()).isEqualTo(event.taskId());
        assertThat(record.value()).contains("\"taskId\":\"task-123\"");
        assertThat(record.value()).contains("\"tenantId\":\"tenant-1\"");
    }

    private Consumer<String, String> createConsumer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("workflow-test-consumer", "true", embeddedKafka);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new StringDeserializer()
        ).createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "workflow-task-created");
        return consumer;
    }
}
