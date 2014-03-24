package com.amazonaws.services.simpleworkflow.flow.recipes.common;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

@Activities(version = "1.0")
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 120, defaultTaskStartToCloseTimeoutSeconds = 60)
public interface CommonRecipeActivities {

    void doNothing();

    int generateRandomNumber();

    boolean generateRandomBoolean();
    
    void delayActivity(long time);
    
}
