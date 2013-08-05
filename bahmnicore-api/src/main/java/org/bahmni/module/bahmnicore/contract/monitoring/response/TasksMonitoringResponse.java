package org.bahmni.module.bahmnicore.contract.monitoring.response;

import lombok.Data;

import java.util.Date;

@Data
public class TasksMonitoringResponse {
    private final Boolean started;
    private final String taskClass;
    private final Date lastExecutionTime;
    private final Date nextExecutionTime;

    public TasksMonitoringResponse(Boolean started, String taskClass, Date lastExecutionTime, Date nextExecutionTime) {
        this.started = started;
        this.taskClass = taskClass;
        this.lastExecutionTime = lastExecutionTime;
        this.nextExecutionTime = nextExecutionTime;
    }
}
