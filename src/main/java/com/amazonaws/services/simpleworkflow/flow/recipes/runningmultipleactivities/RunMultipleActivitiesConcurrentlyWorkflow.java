package com.amazonaws.services.simpleworkflow.flow.recipes.runningmultipleactivities;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 120)
public interface RunMultipleActivitiesConcurrentlyWorkflow {

    @Execute(version = "1.0")
    public void runMultipleActivitiesConcurrently();

}
