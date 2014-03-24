package com.amazonaws.services.simpleworkflow.flow.recipes.waitforsignal;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;


@Activities(version="1.0")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 30, defaultTaskStartToCloseTimeoutSeconds = 30)
public interface WaitForSignalActivities {
    void processOrder(int amount);
}
