package com.amazonaws.services.simpleworkflow.flow.recipes.forloopinline;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 60)
public interface ForLoopInlineRecipeWorkflow {

    @Execute(version = "1.0")
    public void loop(int times);

}
