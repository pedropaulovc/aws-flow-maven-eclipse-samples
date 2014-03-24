package com.amazonaws.services.simpleworkflow.flow.recipes.retryactivity;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;

@Activities(version = "1.0")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 30, defaultTaskStartToCloseTimeoutSeconds = 30)
public interface ExponentialRetryAnnotationActivities {

    /**
     * use @ExponentialRetry annotation to define the retry policy. The
     * parameter initialRetryIntervalSeconds indicates the time period to wait
     * for the first retry attempt. The parameter maximumAttempts specifies the
     * number of attempts after which the retry will stop. The exceptionsToRetry
     * parameter indicates that only exceptions in the exceptionsToRetry list
     * will be retried.
     */
    @ExponentialRetry(initialRetryIntervalSeconds = 5, maximumAttempts = 5, exceptionsToRetry = IllegalStateException.class)
    public void unreliableActivity();

}
