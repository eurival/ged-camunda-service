package br.com.interglobal.ged_camunda_service.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowNotificationPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${workflow.notifications.task-created-topic:workflow-task-created}")
    private String taskCreatedTopic;

    public void publishTaskCreated(WorkflowTaskCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(taskCreatedTopic, event.taskId(), payload);
            log.debug("Task {} publicada no tópico {}", event.taskId(), taskCreatedTopic);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar evento de criação de task", e);
        }
    }
}
