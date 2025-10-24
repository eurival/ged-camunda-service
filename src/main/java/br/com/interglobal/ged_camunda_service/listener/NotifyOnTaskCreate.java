package br.com.interglobal.ged_camunda_service.listener;

import br.com.interglobal.ged_camunda_service.notify.WorkflowNotificationPublisher;
import br.com.interglobal.ged_camunda_service.notify.WorkflowTaskCreatedEvent;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component("notifyOnTaskCreate")
@RequiredArgsConstructor
public class NotifyOnTaskCreate implements TaskListener {

    private final WorkflowNotificationPublisher publisher;

    @Override
    public void notify(DelegateTask task) {
        if (!TaskListener.EVENTNAME_CREATE.equals(task.getEventName())) {
            return;
        }

        Set<String> groups = task.getCandidates().stream()
            .filter(link -> link.getGroupId() != null)
            .map(link -> link.getGroupId())
            .collect(Collectors.toSet());

        String tenantId = task.getTenantId();
        String taskId = task.getId();
        String name = task.getName();
        String taskKey = task.getTaskDefinitionKey();
        String businessKey = toStringOrNull(task.getExecution().getProcessBusinessKey());
        Object docIdVar = task.getExecution().getVariable("docId");
        String docId = toStringOrNull(docIdVar);

        WorkflowTaskCreatedEvent event = new WorkflowTaskCreatedEvent(
            tenantId,
            taskId,
            name,
            taskKey,
            groups,
            businessKey,
            docId
        );

        log.debug("Publicando criação da task {} do tenant {}", taskId, tenantId);
        publisher.publishTaskCreated(event);
    }

    private String toStringOrNull(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
