package br.com.interglobal.ged_camunda_service.notify;

import java.util.Set;

public record WorkflowTaskCreatedEvent(
    String tenantId,
    String taskId,
    String name,
    String taskDefinitionKey,
    Set<String> candidateGroups,
    String businessKey,
    String docId
) {}
